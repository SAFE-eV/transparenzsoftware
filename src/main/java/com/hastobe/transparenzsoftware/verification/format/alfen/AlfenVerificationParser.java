package com.hastobe.transparenzsoftware.verification.format.alfen;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;

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

            AlfenSignature signatureData = reader.parseString(data);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey) {
        //we do not need the public key
        AlfenSignature signatureData;
        try {
            signatureData = reader.parseString(data);
        } catch (ValidationException e) {
            return new VerificationResult(Error.withValidationException(e));
        }
        boolean verified = false;
        AlfenVerifiedData verifiedData = null;
        Error error = null;
        try {
            verified = verifier.verify(publicKey, signatureData.getSignature(), signatureData.getDataset());
            verifiedData = new AlfenVerifiedData(signatureData, EncodingType.PLAIN);
            if (!verified) {
                error = Error.withVerificationFailed();
            }
        } catch (ValidationException e) {
            error = Error.withValidationException(e);
        }

        VerificationResult result = verified ? new VerificationResult(verifiedData) : new VerificationResult(verifiedData, error);
        if(verifiedData != null && verified){
            try {
                verifiedData.calculateAdapterError();
                verifiedData.calculateMeterError();
            } catch (RegulationLawException e) {
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
            AlfenSignature signatureData = reader.parseString(data);
            return signatureData.getPublicKey();
        } catch (ValidationException e) {
            // no op
        }
        return null;
    }

    @Override
    public String createFormattedKey(String data) {
        String parsedKey = parsePublicKey(data);
        return Utils.splitStringToGroups(parsedKey, 4);
    }
}
