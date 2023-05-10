package de.safe_ev.transparenzsoftware.verification.format.alfen;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ContainedPublicKeyParser;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public class AlfenVerificationParser implements VerificationParser, ContainedPublicKeyParser {

	private final AlfenReader reader;
	private final AlfenSignatureVerifier verifier;

	public AlfenVerificationParser() {
		reader = new AlfenReader();
		verifier = new AlfenSignatureVerifier();
	}

	@Override
	public VerificationType getVerificationType() {
		return VerificationType.ALFEN;
	}

	@Override
	public boolean canParseData(String data) {
		try {

			final AlfenSignature signatureData = reader.parseString(data);
			return true;
		} catch (final ValidationException e) {
			return false;
		}
	}

	@Override
	public VerificationResult parseAndVerify(String data, byte[] publicKey, IntrinsicVerified intrinsicVerified) {
		// we do not need the public key
		AlfenSignature signatureData;
		try {
			signatureData = reader.parseString(data);
		} catch (final ValidationException e) {
			return new VerificationResult(Error.withValidationException(e));
		}
		boolean verified = false;
		AlfenVerifiedData verifiedData = null;
		Error error = null;
		try {
			verified = intrinsicVerified.ok()
					|| verifier.verify(publicKey, signatureData.getSignature(), signatureData.getDataset());
			verifiedData = new AlfenVerifiedData(signatureData, EncodingType.PLAIN);
			if (!verified) {
				error = Error.withVerificationFailed();
			}
		} catch (final ValidationException e) {
			error = Error.withValidationException(e);
		}

		final VerificationResult result = verified ? new VerificationResult(verifiedData, intrinsicVerified)
				: new VerificationResult(verifiedData, error);
		if (verifiedData != null && verified) {
			try {
				verifiedData.calculateAdapterError();
				verifiedData.calculateMeterError();
			} catch (final RegulationLawException e) {
				result.addError(new Error(Error.Type.VERIFICATION, "Meter error happened", e.getLocalizedMessageKey()));
			}
		}
		return result;
	}

	@Override
	public Class getVerfiedDataClass() {
		return AlfenVerifiedData.class;
	}

	@Override
	public String parsePublicKey(String data) {
		try {
			final AlfenSignature signatureData = reader.parseString(data);
			return signatureData.getPublicKey();
		} catch (final ValidationException e) {
			// no op
		}
		return null;
	}

	@Override
	public String createFormattedKey(String data) {
		final String parsedKey = parsePublicKey(data);
		return Utils.splitStringToGroups(parsedKey, 4);
	}
}
