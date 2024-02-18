package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public class OCMFVerificationParser implements VerificationParser {

	public static final String DIVIDER = "\\|";
	public static final String SIGNATURE_METHOD_ECDSA = "ECDSA";
	public static final String HEADER_VALUE = "OCMF";

	public static final double MIN_VERSION = 0.1;
	public static final double MAX_VERSION = 1.2;

	@Override
	public VerificationType getVerificationType() {
		return VerificationType.OCMF;
	}

	@Override
	public boolean canParseData(String data) {
		try {
			parseString(data);
			return true;
		} catch (JsonSyntaxException | OCMFValidationException e) {
			return false;
		}
	}

	@Override
	public VerificationResult parseAndVerify(String data, byte[] publicKey, IntrinsicVerified intrinsicVerified) {
		OCMF ocmf;
		try {
			ocmf = parseString(data);
		} catch (final ValidationException e) {
			return new VerificationResult(null, Error.withValidationException(e));
		} catch (final JsonSyntaxException e) {
			return new VerificationResult(null,
					Error.withValidationException(new OCMFValidationException("Invalid ocmf data")));
		}
		boolean verified = false;
		OCMFVerifiedData verifiedData = null;
		Error error = null;
		if (!intrinsicVerified.ok()) {
			OCMFVerifier verifier;
			try {
				verifier = new OCMFVerifier(ocmf.getSignature().getCurve());
			} catch (final ValidationException e) {
				return new VerificationResult(null, Error.withValidationException(e));
			}

			final EncodingType encoding = ocmf.getSignature().getEncoding();
			if (encoding == null) {
				return new VerificationResult(null, Error.withDecodingSignatureFailed());
			}
			byte[] signatureData;
			try {
				signatureData = EncodingType.decode(encoding, ocmf.getSignature().getSD());
			} catch (final DecodingException e) {
				return new VerificationResult(null, Error.withDecodingSignatureFailed());
			}

			try {
				verified = verifier.verify(publicKey, signatureData, ocmf.getRawData().getBytes());
				if (!verified) {
					error = Error.withVerificationFailed();
				} else {
					verifiedData = new OCMFVerifiedData(ocmf, Utils.toFormattedHex(publicKey), encoding.getCode());
				}
			} catch (final ValidationException e) {
				error = Error.withValidationException(e);
			}
		} else {
			verifiedData = new OCMFVerifiedData(ocmf, Utils.toFormattedHex(publicKey), EncodingType.PLAIN.name());
			verified = intrinsicVerified.ok();
		}
		final VerificationResult verificationResult = verified
				? new VerificationResult(verifiedData, ocmf.getData().containsCompleteTransaction(), intrinsicVerified)
				: new VerificationResult(verifiedData, error);
		if (verifiedData != null && verified && ocmf.getData().containsCompleteTransaction()) {
			try {
				verifiedData.checkLawIntegrityForTransaction();
			} catch (final ValidationException e) {
				verificationResult.addError(Error.withValidationException(e));
			} catch (final RegulationLawException e) {
				verificationResult.addError(Error.withRegulationLawException(e));
			}
		}
		return verificationResult;
	}

	public OCMF parseString(String data) throws OCMFValidationException {
		final Gson gson = new Gson();
		final String[] splittedData = data.split(DIVIDER);
		if (splittedData.length < 3) {
			throw new OCMFValidationException("Invalid data format for OCMF given");
		}
		if (!splittedData[0].equals(HEADER_VALUE)) {
			throw new OCMFValidationException("Data not in OCMF format");
		}
		final String rawData = splittedData[1];
		final HashMap<String, Object> simpleData = gson.fromJson(rawData, HashMap.class);
		if (!simpleData.containsKey("FV")) {
			throw new OCMFValidationException("Invalid OCMF Version", "error.ocmf.invalid.version");
		}
		double version;
		try {
			version = Double.parseDouble(simpleData.get("FV").toString());
		} catch (NumberFormatException | NullPointerException e) {
			throw new OCMFValidationException(
					String.format("Not compatible with OCMF version %s", simpleData.get("FV").toString()),
					"error.ocmf.invalid.version");
		}

		if (MIN_VERSION > version || MAX_VERSION < version) {
			throw new OCMFValidationException(
					String.format("Not compatible with OCMF version %s", simpleData.get("FV").toString()),
					"error.ocmf.invalid.version");
		}
		OCMFPayloadData ocmfPayloadData = null;
		if (version < 0.5) {
			ocmfPayloadData = gson.fromJson(rawData,
					de.safe_ev.transparenzsoftware.verification.format.ocmf.v02.OCMFPayloadData.class);
		} else if (version <= 1.1) {
			ocmfPayloadData = gson.fromJson(rawData,
					de.safe_ev.transparenzsoftware.verification.format.ocmf.v05.OCMFPayloadData.class);
		} else {
			throw new OCMFValidationException(String.format("Not compatible with OCMF version %s", version),
					"error.ocmf.invalid.version");
		}
		final OCMFSignature signature = gson.fromJson(splittedData[2], OCMFSignature.class);

		String publicKey = "";
		if (splittedData.length > 3) {
			publicKey = splittedData[3];
		}
		if (!SIGNATURE_METHOD_ECDSA.equals(signature.getSignatureMethod())) {
			throw new OCMFValidationException("Signature method not in OCMF format");
		}
		final OCMF ocmf = new OCMF(ocmfPayloadData, rawData, signature, publicKey);
		return ocmf;
	}

	@Override
	public Class getVerfiedDataClass() {
		return OCMFVerifiedData.class;
	}

}
