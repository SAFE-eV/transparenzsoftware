package de.safe_ev.transparenzsoftware.verification.format.sml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.gui.listeners.OpenFileBtnListener;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public abstract class SMLVerificationParserBase implements VerificationParser {

	private static final Logger LOGGER = LogManager.getLogger(OpenFileBtnListener.class);

	protected SMLSignatureVerifier verifier;

	public SMLVerificationParserBase() {
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
		if (intrinsicVerified.ok() || verifier.verify(publicKey, smlSignature)) {
			return new VerificationResult(verifiedData, intrinsicVerified);
		}
		return new VerificationResult(verifiedData, Error.withVerificationFailed());
	}

	@Override
	public Class getVerfiedDataClass() {
		return SMLVerifiedData.class;
	}
}
