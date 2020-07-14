package com.hastobe.transparenzsoftware.verification.format.ocmf;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;

import java.util.HashMap;

public class OCMFVerificationParser implements VerificationParser {


    public static final String DIVIDER = "\\|";
    public static final String SIGNATURE_METHOD_ECDSA = "ECDSA";
    public static final String HEADER_VALUE = "OCMF";

    public static final double MIN_VERSION = 0.1;
    public static final double MAX_VERSION = 1.0;

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
    public VerificationResult parseAndVerify(String data, byte[] publicKey) {
        OCMF ocmf;
        try {
            ocmf = parseString(data);
        } catch (ValidationException e) {
            return new VerificationResult(null, Error.withValidationException(e));
        } catch (JsonSyntaxException e) {
            return new VerificationResult(null, Error.withValidationException(new OCMFValidationException("Invalid ocmf data")));
        }
        OCMFVerifier verifier;
        try {
            verifier = new OCMFVerifier(ocmf.getSignature().getCurve());
        } catch (ValidationException e) {
            return new VerificationResult(null, Error.withValidationException(e));
        }


        EncodingType encoding = ocmf.getSignature().getEncoding();
        if (encoding == null) {
            return new VerificationResult(null, Error.withDecodingSignatureFailed());
        }
        byte[] signatureData;
        try {
            signatureData = EncodingType.decode(encoding, ocmf.getSignature().getSD());
        } catch (DecodingException e) {
            return new VerificationResult(null, Error.withDecodingSignatureFailed());
        }


        boolean verified = false;
        OCMFVerifiedData verifiedData = null;
        Error error = null;
        try {
            verified = verifier.verify(publicKey, signatureData, ocmf.getRawData().getBytes());
            verifiedData = new OCMFVerifiedData(ocmf, Utils.toFormattedHex(publicKey), EncodingType.PLAIN.name());
            if (!verified) {
                error = Error.withVerificationFailed();
            }
        } catch (ValidationException e) {
            error = Error.withValidationException(e);
        }
        VerificationResult verificationResult = verified ? new VerificationResult(verifiedData, ocmf.getData().containsCompleteTransaction()) : new VerificationResult(verifiedData, error);
        if (verifiedData != null && verified && ocmf.getData().containsCompleteTransaction()) {
            try {
                verifiedData.checkLawIntegrityForTransaction();
            } catch (ValidationException e) {
                verificationResult.addError(Error.withValidationException(e));
            } catch (RegulationLawException e) {
                verificationResult.addError(Error.withRegulationLawException(e));
            }
        }
        return verificationResult;
    }

    public OCMF parseString(String data) throws OCMFValidationException {
        Gson gson = new Gson();
        String[] splittedData = data.split(DIVIDER);
        if (splittedData.length < 3) {
            throw new OCMFValidationException("Invalid data format for OCMF given");
        }
        if (!splittedData[0].equals(HEADER_VALUE)) {
            throw new OCMFValidationException("Data not in OCMF format");
        }
        String rawData = splittedData[1];
        HashMap<String, Object> simpleData = gson.fromJson(rawData, HashMap.class);
        if (!simpleData.containsKey("FV")) {
            throw new OCMFValidationException("Invalid OCMF Version", "error.ocmf.invalid.version");
        }
        double version;
        try {
            version = Double.parseDouble(simpleData.get("FV").toString());
        } catch (NumberFormatException | NullPointerException e) {
            throw new OCMFValidationException(String.format("Not compatible with OCMF version %s", simpleData.get("FV").toString()), "error.ocmf.invalid.version");
        }

        if (MIN_VERSION > version || MAX_VERSION < version) {
            throw new OCMFValidationException(String.format("Not compatible with OCMF version %s", simpleData.get("FV").toString()), "error.ocmf.invalid.version");
        }
        OCMFPayloadData ocmfPayloadData = null;
        if (version < 0.5) {
            ocmfPayloadData = gson.fromJson(rawData, com.hastobe.transparenzsoftware.verification.format.ocmf.v02.OCMFPayloadData.class);
        } else if (version <= 1.0) {
            ocmfPayloadData = gson.fromJson(rawData, com.hastobe.transparenzsoftware.verification.format.ocmf.v05.OCMFPayloadData.class);
        } else {
            throw new OCMFValidationException(String.format("Not compatible with OCMF version %s", version), "error.ocmf.invalid.version");
        }
        OCMFSignature signature = gson.fromJson(splittedData[2], OCMFSignature.class);

        String publicKey = "";
        if (splittedData.length > 3) {
            publicKey = splittedData[3];
        }
        if (!SIGNATURE_METHOD_ECDSA.equals(signature.getSignatureMethod())) {
            throw new OCMFValidationException("Signature method not in OCMF format");
        }
        OCMF ocmf = new OCMF(ocmfPayloadData, rawData, signature, publicKey);
        return ocmf;
    }

    @Override
    public Class getVerfiedDataClass() {
        return OCMFVerifiedData.class;
    }

}
