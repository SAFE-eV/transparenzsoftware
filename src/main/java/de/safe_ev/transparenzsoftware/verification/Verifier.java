package de.safe_ev.transparenzsoftware.verification;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.EncodedData;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;
import de.safe_ev.transparenzsoftware.verification.xml.SignedData;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

/**
 * Main verification class which will call various parsers to verify the data
 * The class tries different formats
 */
public class Verifier {

	private final static Logger LOGGER = LogManager.getLogger(Verifier.class);
	private final VerificationParserFactory factory;

	/**
	 * Initiates the console file processor
	 *
	 * @param factory factory of parser which will be used to create the results of
	 *                the parsing
	 */
	public Verifier(VerificationParserFactory factory) {
		this.factory = factory;
	}

	/**
	 * Process an list of values and adds it to a result list
	 *
	 * @param values
	 * @return
	 */
	public List<VerificationResult> verifyValues(Values values) {
		final ArrayList<VerificationResult> resultList = new ArrayList<>();
		for (final Value value : values.getValues()) {
			resultList.add(verify(value));
		}
		return resultList;
	}

	/**
	 * Process one single input value and tries to verify it
	 *
	 * @param value value to process
	 * @return VerificationResult
	 */
	public VerificationResult verify(Value value) {
		VerificationResult result = null;
		try {
			value.validate(false);

			// if format is set we will just use that parser
			final String publicKey = value.getPublicKey() != null ? value.getPublicKey().getValue() : null;
			result = verify(value, publicKey);
			// if (value.getSignedData().getEncoding() != null &&
			// value.getSignedData().getFormat() != null) {
		} catch (final InvalidInputException e) {
			final Error error = new Error(Error.Type.INPUT, e.getMessage(), e.getLocalizedMessageKey());
			result = new VerificationResult(error);
		} catch (final VerificationTypeNotImplementedException e) {
			final Error error = new Error(null, "Format not found", "error.format.unknown");
			result = new VerificationResult(error);
		}
		return result;
	}

	/**
	 * Tries to find a parser with help of the factory and create a verification
	 * result out of it. The first parser that verifies with success will be be
	 * used. If no parser can verify the data an verification error will be returned
	 *
	 * @param publicKey decoded public key
	 * @return a verification result
	 * @throws VerificationTypeNotImplementedException if not type at all could be
	 *                                                 found for a parser
	 */
	VerificationResult verify(Value data, String publicKey) throws VerificationTypeNotImplementedException {
		final AtomicReference<IntrinsicVerified> maybeIntrinsicVerified = new AtomicReference<>();
		final String decodedData = decodeData(data, publicKey, maybeIntrinsicVerified);

		final List<VerificationParser> possibleParser = factory.getParserWithData(decodedData);
		final List<String> parserNames = new ArrayList<>();
		for (final VerificationParser parser : possibleParser) {
			parserNames.add(parser.getVerificationType().name());
		}
		LOGGER.info(String.format("%s parser found to try formats: %s", possibleParser.size(),
				String.join(",", parserNames)));

		return verify(possibleParser, data, publicKey);
	}

	private String decodeData(Value data, String publicKey, AtomicReference<IntrinsicVerified> intrinsicVerified) {
		if (data.getSignedData() != null) {
			return data.getSignedData().getValue();
		}
		return decodeData(data, Utils.hexStringToByteArray(publicKey), intrinsicVerified);
	}

	private String decodeData(Value data, byte[] publicKey, AtomicReference<IntrinsicVerified> intrinsicVerified) {
		if (data.getSignedData() != null && !data.getSignedData().getValue().isEmpty()) {
			return data.getSignedData().getValue();
		}
		final EncryptedDataDecoder decoder = new EncryptedDataDecoder(publicKey);
		return decoder.decode(data, intrinsicVerified);
	}

	public VerificationResult verify(VerificationParser parser, Value data, String publicKey) {
		return verify(Collections.singletonList(parser), data, publicKey);
	}

	/**
	 * Verifies a list of transaction values.
	 *
	 * @param parser            parser to use (not list here as we want only to have
	 *                          verifications with the same parser obj)
	 * @param transactionValues transaction values to use
	 * @param publicKey         public key to use
	 * @return verification result
	 * @throws TransactionValidationException if the validation on the transaction
	 *                                        fails (like too many start values etc)
	 */
	public VerificationResult verifyTransaction(VerificationParser parser, List<Value> transactionValues,
			String publicKey) throws TransactionValidationException {
		int startCount = 0;
		int stopCount = 0;
		Value startValue = null;
		Value stopValue = null;
		BigInteger transactionId = null;
		for (final Value value : transactionValues) {
			if (value.getContext() != null && value.getContext().trim().equals(Value.CONTEXT_BEGIN)) {
				startCount++;
				startValue = value;
			}
			if (value.getContext() != null && value.getContext().trim().equals(Value.CONTEXT_END)) {
				stopCount++;
				stopValue = value;
			}
			transactionId = value.getTransactionId();
		}
		if (startCount == 0) {
			throw new TransactionValidationException("No start value for transaction found", "error.values.no.start");
		}
		if (stopCount == 0) {
			throw new TransactionValidationException("No stop value for transaction found", "error.values.no.stop");
		}
		if (startCount > 1) {
			throw new TransactionValidationException("Too many start values for transaction found",
					"error.values.toomany.start");
		}
		if (stopCount > 1) {
			throw new TransactionValidationException("Too many stop values for transaction found",
					"error.values.toomany.stop");
		}
		final VerificationResult verificationResultStart = verify(parser, startValue, publicKey);
		final VerificationResult verificationResultStop = verify(parser, stopValue, publicKey);
		if (verificationResultStart == null || verificationResultStop == null) {
			// if no error message was set yet we set at least an unknown error
			throw new TransactionValidationException("Unknown error on verification results", "app.view.error.generic");
		}
		LOGGER.debug("Verify transaction " + startValue.getTransactionId() + " now with");
		LOGGER.debug("Result 1: " + verificationResultStart.isVerified());
		LOGGER.debug("Result 2: " + verificationResultStop.isVerified());
		VerificationResult result = null;
		try {
			result = VerificationResult.mergeVerificationData(verificationResultStart, verificationResultStop,
					transactionId);
		} catch (final ValidationException e) {
			throw new TransactionValidationException(e.getMessage(), e.getLocalizedMessageKey(), e);
		}
		if (result.isVerified()) {
			try {
				final List<Meter> startMeters = Meter.filterLawRelevant(verificationResultStart.getMeters());
				final List<Meter> stopMeters = Meter.filterLawRelevant(verificationResultStop.getMeters());
				Meter.validateListStartStop(startMeters, stopMeters);
			} catch (final ValidationException e) {
				throw new TransactionValidationException(e.getMessage(), e.getLocalizedMessageKey(), e);
			}
		}
		return result;

	}

	/**
	 * Tries to verify a raw data content against a given list of parser
	 *
	 * @param parsers   - list of parser which will be tried
	 * @param data      - data which will be used (raw string data)
	 * @param publicKey public key data as string
	 * @return verification result
	 */
	public VerificationResult verify(List<VerificationParser> parsers, Value data, String publicKey) {
		VerificationResult result = null;
		Error lastError = null;

		// lets first check if we have a parser who can handle this
		if (parsers.isEmpty()) {
			lastError = new Error(Error.Type.INPUT, "Could not find a parser for this format.", "error.parse.payload");
			return new VerificationResult(lastError);
		}

		Error error = null;
		for (final VerificationParser parser : parsers) {
			// lets check if public key is also contained and might
			// be different from provided one, if so provide an erro

			final List<String> publicKeysToUse = new ArrayList<>();
			if (publicKey != null) {
				publicKeysToUse.add(publicKey);
			}
			try {
				final String embeddedPublicKey = checkForEmbeddedPublicKey(parser, publicKey, data);
				// this can be the case if we have no public key provided and we only want
				// to use the embedded one
				if (publicKey == null && embeddedPublicKey != null) {
					publicKeysToUse.add(embeddedPublicKey);
				}
			} catch (final InvalidInputException e) {
				if (publicKey != null) {
					error = new Error(Error.Type.VERIFICATION, "Public key does not match with public key in data",
							"app.view.error.publickeynotmatchdata");
				}
				// second try, ignore the given key
				try {
					final String embeddedPublicKey = checkForEmbeddedPublicKey(parser, null, data);
					if (embeddedPublicKey != null) {
						publicKeysToUse.add(embeddedPublicKey);
					}
				} catch (final InvalidInputException e1) {
					// no op we might wanna try to use the provided public key
				}
			}
			if (publicKeysToUse.isEmpty()) {
				return new VerificationResult(new Error(Error.Type.INPUT, "Could not find a public key to use",
						"error.no.publickeysfoundduringverify"));
			}
			for (final String keyToTry : publicKeysToUse) {
				try {
					result = tryParser(parser, keyToTry, data);
					if (result != null && result.isVerified()) {
						// we have found a hit we will stop now
						break;
					}
				} catch (final VerificationException e) {
					lastError = e.getError();
				}
			}
			if (result != null && result.isVerified()) {
				// we have found a hit we will stop now
				break;
			}

		}
		if (result == null) {
			result = new VerificationResult(lastError);
		}
		if (error != null) {
			result.addError(error);
		}
		return result;
	}

	/**
	 * Verifies one single public key and one parser
	 *
	 * @param parser         - parser which will
	 * @param publicKeyToUse - public key which will be used
	 * @param data           data to verify
	 * @return a verification result
	 * @throws VerificationException when an error during verification happens
	 */
	public VerificationResult tryParser(VerificationParser parser, String publicKeyToUse, Value data)
			throws VerificationException {
		// check if there is at least something in the public key
		// if create an verification error
		if (publicKeyToUse == null || publicKeyToUse.trim().isEmpty()) {
			final Error error = new Error(Error.Type.VALIDATION, "Could not find a parser for this public key.",
					"error.values.publickey.cannot.encode");
			throw new VerificationException(error);
		}

		// build a list of possible encodings and try them out
		final List<EncodingType> keyTypes = EncodingType.guessType(publicKeyToUse);
		if (keyTypes.isEmpty()) {
			final Error error = new Error(Error.Type.INPUT, "no encoding found for key",
					"error.values.publickey.cannot.encode");
			throw new VerificationException(error);
		}

		VerificationResult result = null;
		Error lastError = null;
		for (final EncodingType encodingType : keyTypes) {
			try {
				final byte[] decodedPublicKey = EncodingType.decode(encodingType, publicKeyToUse);
				final AtomicReference<IntrinsicVerified> intrinsicVerified = new AtomicReference<>(
						IntrinsicVerified.NOT_VERIFIED);
				final String decodedData = decodeData(data, decodedPublicKey, intrinsicVerified);
				if (decodedData != null) {
					final VerificationResult newResult = parser.parseAndVerify(decodedData, decodedPublicKey,
							intrinsicVerified.get());
					if (newResult != null && newResult.isVerified()) {
						result = newResult;
						break;
					} else {
						if (result == null || !result.containsErrorOfType(Error.Type.VERIFICATION)) {
							result = newResult;
						}
					}
				}
			} catch (final DecodingException e) {
				if (lastError == null) {
					lastError = new Error(Error.Type.INPUT, "Could not decode public key",
							"error.values.publickey.cannot.encode");
				}
			}
		}
		return result;
	}

	/**
	 * Checks if the value contains a embedded public key. If so it will be compared
	 * with the given public key.
	 * <p>
	 * - If the given public key is null or equal the decoded public key will be
	 * returned. - If parser is not aware of a contained public key the original
	 * provided key will be returned - If the given public key is not equal the
	 * embedded public key an exception will be thrown
	 *
	 * @param parser           parser to check with
	 * @param decodedPublicKey decoded public key can be null
	 * @param payloadData      data which might contain the public key
	 * @return string of the decoded public key
	 * @throws InvalidInputException if the public key does not match the embedded
	 */
	public String checkForEmbeddedPublicKey(VerificationParser parser, String decodedPublicKey, Value payloadData)
			throws InvalidInputException {
		if (parser instanceof ContainedPublicKeyParser && payloadData.hasSignedData()) {

			final String containedPublicKey = ((ContainedPublicKeyParser) parser)
					.parsePublicKey(payloadData.getSignedData().getValue());
			if (decodedPublicKey == null) {
				return containedPublicKey;
			} else if (!Utils.compareEncodedStrings(decodedPublicKey, containedPublicKey)) {
				throw new InvalidInputException("Public key of does not match with public key in data",
						"app.view.error.publickeynotmatchdata");
			}
		}
		return decodedPublicKey;
	}

	/**
	 * Verify with raw data from a Text field in the GUI.
	 */
	public VerificationResult verifyUnknown(VerificationParser parser, String rawDataContent, String publicKeyContent) {

		final Value value = new Value();
		final EncodedData encodedData = new EncodedData();
		encodedData.setValue(rawDataContent);
		value.setEncodedData(encodedData);
		final AtomicReference<IntrinsicVerified> maybeIntrinsicVerified = new AtomicReference<>(
				IntrinsicVerified.NOT_VERIFIED);
		try {
			final EncryptedDataDecoder decoder = new EncryptedDataDecoder(publicKeyContent);
			decoder.decode(value, maybeIntrinsicVerified);
		} catch (final Exception e) {
			//
		}
		if (!maybeIntrinsicVerified.get().ok()) {
			value.setEncodedData(null);
			final SignedData signedData = new SignedData();
			signedData.setValue(rawDataContent);
			value.setSignedData(signedData);
		}
		return verify(parser, value, publicKeyContent);
	}

}
