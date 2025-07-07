package de.safe_ev.transparenzsoftware.verification.format.sml;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public abstract class SMLVerificationParserBase implements VerificationParser {

    protected SMLSignatureVerifier verifier;
    protected SMLSignatureVerifier verifier2;

    protected SMLVerificationParserBase() {
	verifier = new SMLSignatureVerifier();
    }

    /**
     * @param smlSignature
     * @param publicKey
     * @return
     * @throws ValidationException
     */
    public VerificationResult parseAndVerifyWithSmlData(SMLSignature smlSignature, VerificationType verificationType,
	    EncodingType encodingType, byte[] publicKey, IntrinsicVerified intrinsicVerified)
	    throws ValidationException {

	final SMLVerifiedData verifiedData = new SMLVerifiedData(smlSignature, verificationType, encodingType,
		Utils.toFormattedHex(publicKey));
	if (intrinsicVerified.ok()) {
	    return new VerificationResult(verifiedData, intrinsicVerified);
	}

	try {
	    if (verifier.verify(publicKey, smlSignature)) {
		return new VerificationResult(verifiedData, intrinsicVerified);
	    }
	} catch (final ValidationException e) {
	    if (verifier2 != null && verifier2.verify(publicKey, smlSignature)) {
		return new VerificationResult(verifiedData, intrinsicVerified);
	    }
	}
	return new VerificationResult(verifiedData, Error.withVerificationFailed());
    }

    @Override
    public Class getVerfiedDataClass() {
	return SMLVerifiedData.class;
    }
}
