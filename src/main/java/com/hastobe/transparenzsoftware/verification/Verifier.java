package com.hastobe.transparenzsoftware.verification;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.input.InvalidInputException;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.Value;
import com.hastobe.transparenzsoftware.verification.xml.Values;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * @param factory factory of parser which will be used to create the results
     *                of the parsing
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
        ArrayList<VerificationResult> resultList = new ArrayList<>();
        for (Value value : values.getValues()) {
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

            //if format is set we will just use that parser
            String publicKey = value.getPublicKey() != null ? value.getPublicKey().getValue() : null;
            result = verify(value.getSignedData().getValue(), publicKey);
            //if (value.getSignedData().getEncoding() != null && value.getSignedData().getFormat() != null) {
        } catch (InvalidInputException e) {
            Error error = new Error(Error.Type.INPUT, e.getMessage(), e.getLocalizedMessageKey());
            result = new VerificationResult(error);
        } catch (VerificationTypeNotImplementedException e) {
            Error error = new Error(null, "Format not found", "error.format.unknown");
            result = new VerificationResult(error);
        }
        return result;
    }


    /**
     * Tries to find a parser with help of the factory and create a verification
     * result out of it. The first parser that verifies with success will be
     * be used. If no parser can verify the data an verification error will
     * be returned
     *
     * @param publicKey decoded public key
     * @return a verification result
     * @throws VerificationTypeNotImplementedException if not type at all could be found for a parser
     */
    public VerificationResult verify(String data, String publicKey) throws VerificationTypeNotImplementedException {
        List<VerificationParser> possibleParser = factory.getParserWithData(data);
        List<String> parserNames = new ArrayList<>();
        for (VerificationParser parser : possibleParser) {
            parserNames.add(parser.getVerificationType().name());
        }
        LOGGER.info(String.format("%s parser found to try formats: %s", possibleParser.size(), String.join(",", parserNames)));

        return verify(possibleParser, data, publicKey);
    }

    public VerificationResult verify(VerificationParser parser, String data, String publicKey) {
        return verify(Collections.singletonList(parser), data, publicKey);
    }

    /**
     * Verifies a list of transaction values.
     *
     * @param parser parser to use (not list here as we want only to have verifications with the same parser obj)
     * @param transactionValues transaction values to use
     * @param publicKey public key to use
     * @return verification result
     * @throws TransactionValidationException if the validation on the transaction fails (like too many start values etc)
     */
    public VerificationResult verifyTransaction(VerificationParser parser, List<Value> transactionValues, String publicKey) throws TransactionValidationException {
        int startCount = 0;
        int stopCount = 0;
        Value startValue = null;
        Value stopValue = null;
        BigInteger transactionId = null;
        for (Value value : transactionValues) {
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
            throw new TransactionValidationException("Too many start values for transaction found", "error.values.toomany.start");
        }
        if (stopCount > 1) {
            throw new TransactionValidationException("Too many stop values for transaction found", "error.values.toomany.stop");
        }
        VerificationResult verificationResultStart = verify(parser, startValue.getSignedData().getValue(), publicKey);
        VerificationResult verificationResultStop = verify(parser, stopValue.getSignedData().getValue(), publicKey);
        if (verificationResultStart == null || verificationResultStop == null) {
            //if no error message was set yet we set at least an unknown error
            throw new TransactionValidationException("Unknown error on verification results", "app.view.error.generic");
        }
        LOGGER.debug("Verify transaction " + startValue.getTransactionId() + " now with");
        LOGGER.debug("Result 1: " + verificationResultStart.isVerified());
        LOGGER.debug("Result 2: " + verificationResultStop.isVerified());
        VerificationResult result = null;
        try {
            result = VerificationResult.mergeVerificationData(verificationResultStart, verificationResultStop, transactionId);
        } catch (ValidationException e) {
            throw new TransactionValidationException(e.getMessage(), e.getLocalizedMessageKey(), e);
        }
        if(result.isVerified()) {
            try {
                Meter.validateListStartStop(verificationResultStart.getMeters(), verificationResultStop.getMeters());
            } catch (ValidationException e) {
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
    public VerificationResult verify(List<VerificationParser> parsers, String data, String publicKey) {
        VerificationResult result = null;
        Error lastError = null;

        //lets first check if we have a parser who can handle this
        if (parsers.isEmpty()) {
            lastError = new Error(Error.Type.INPUT, "Could not find a parser for this format.", "error.parse.payload");
            return new VerificationResult(lastError);
        }

        Error error = null;
        for (VerificationParser parser : parsers) {
            // lets check if public key is also contained and might
            // be different from provided one, if so provide an erro

            List<String> publicKeysToUse = new ArrayList<>();
            if (publicKey != null) {
                publicKeysToUse.add(publicKey);
            }
            try {
                String embeddedPublicKey = checkForEmbeddedPublicKey(parser, publicKey, data);
                //this can be the case if we have no public key provided and we only want
                //to use the embedded one
                if (publicKey == null && embeddedPublicKey != null) {
                    publicKeysToUse.add(embeddedPublicKey);
                }
            } catch (InvalidInputException e) {
                if (publicKey != null) {
                    error = new Error(Error.Type.VERIFICATION, "Public key does not match with public key in data", "app.view.error.publickeynotmatchdata");
                }
                //second try, ignore the given key
                try {
                    String embeddedPublicKey = checkForEmbeddedPublicKey(parser, null, data);
                    if(embeddedPublicKey != null) {
                        publicKeysToUse.add(embeddedPublicKey);
                    }
                } catch (InvalidInputException e1) {
                    //no op we might wanna try to use the provided public key
                }
            }
            if(publicKeysToUse.isEmpty()){
                return new VerificationResult(new Error(Error.Type.INPUT, "Could not find a public key to use", "error.no.publickeysfoundduringverify"));
            }
            for (String keyToTry : publicKeysToUse) {
                try {
                    result = tryParser(parser, keyToTry, data);
                    if (result != null && result.isVerified()) {
                        //we have found a hit we will stop now
                        break;
                    }
                } catch (VerificationException e) {
                    lastError = e.getError();
                }
            }
            if (result != null && result.isVerified()) {
                //we have found a hit we will stop now
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
    public VerificationResult tryParser(VerificationParser parser, String publicKeyToUse, String data) throws VerificationException {
        //check if there is at least something in the public key
        //if create an verification error
        if (publicKeyToUse == null || publicKeyToUse.trim().isEmpty()) {
            Error error = new Error(Error.Type.VALIDATION, "Could not find a parser for this public key.", "error.values.publickey.cannot.encode");
            throw new VerificationException(error);
        }

        //build a list of possible encodings and try them out
        List<EncodingType> keyTypes = EncodingType.guessType(publicKeyToUse);
        if (keyTypes.isEmpty()) {
            Error error = new Error(Error.Type.INPUT, "no encoding found for key", "error.values.publickey.cannot.encode");
            throw new VerificationException(error);
        }

        VerificationResult result = null;
        Error lastError = null;
        for (EncodingType encodingType : keyTypes) {
            try {
                VerificationResult newResult = parser.parseAndVerify(data, EncodingType.decode(encodingType, publicKeyToUse));
                if (newResult != null && newResult.isVerified()) {
                    result = newResult;
                    break;
                } else {
                    if (result == null || !result.containsErrorOfType(Error.Type.VERIFICATION)) {
                        result = newResult;
                    }
                }
            } catch (DecodingException e) {
                if (lastError == null) {
                    lastError = new Error(Error.Type.INPUT, "Could not decode public key", "error.values.publickey.cannot.encode");
                }
            }
        }
        return result;
    }

    /**
     * Checks if the value contains a embedded public key. If so
     * it will be compared with the given public key.
     * <p>
     * - If the given public key is null or equal the decoded public key will be returned.
     * - If parser is not aware of a contained public key the original provided key will be returned
     * - If the given public key is not equal the embedded public key an exception will be thrown
     *
     * @param parser           parser to check with
     * @param decodedPublicKey decoded public key can be null
     * @param payloadData      data which might contain the public key
     * @return string of the decoded public key
     * @throws InvalidInputException if the public key does not match the embedded
     */
    public String checkForEmbeddedPublicKey(VerificationParser parser, String decodedPublicKey, String payloadData) throws InvalidInputException {
        if (parser instanceof ContainedPublicKeyParser) {
            String containedPublicKey = ((ContainedPublicKeyParser) parser).parsePublicKey(payloadData);
            if (decodedPublicKey == null) {
                return containedPublicKey;
            } else if (!Utils.compareEncodedStrings(decodedPublicKey, containedPublicKey)) {
                throw new InvalidInputException("Public key of does not match with public key in data", "app.view.error.publickeynotmatchdata");
            }
        }
        return decodedPublicKey;
    }


}
