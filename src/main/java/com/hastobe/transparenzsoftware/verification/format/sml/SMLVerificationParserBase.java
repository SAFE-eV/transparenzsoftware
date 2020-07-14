package com.hastobe.transparenzsoftware.verification.format.sml;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.gui.listeners.OpenFileBtnListener;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.VerificationParser;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SMLVerificationParserBase implements VerificationParser {

    private static final Logger LOGGER = LogManager.getLogger(OpenFileBtnListener.class);

    protected SMLSignatureVerifier verifier;

    public SMLVerificationParserBase() {
        this.verifier = new SMLSignatureVerifier();
    }


    /**
     * @param smlSignature
     * @param publicKey
     * @return
     * @throws ValidationException
     */
    public VerificationResult parseAndVerifyWithSmlData(SMLSignature smlSignature, VerificationType verificationType, EncodingType encodingType, byte[] publicKey) throws ValidationException {

        SMLVerifiedData verifiedData = new SMLVerifiedData(smlSignature, verificationType, encodingType, Utils.toFormattedHex(publicKey));
        if (verifier.verify(publicKey, smlSignature)) {
            return new VerificationResult(verifiedData);
        }
        return new VerificationResult(verifiedData, Error.withVerificationFailed());
    }


    @Override
    public Class getVerfiedDataClass() {
        return SMLVerifiedData.class;
    }
}
