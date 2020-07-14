package com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLSignature;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLValidationException;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLVerificationParserBase;
import com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly.embedded.SignedMeterValue;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SignatureOnlyVerificationParser extends SMLVerificationParserBase implements ContainedPublicKeyParser {

    private static final Logger LOGGER = LogManager.getLogger(SignatureOnlyVerificationParser.class);
    private SMLReader smlReader;

    public SignatureOnlyVerificationParser() {
        super();
        smlReader = new SMLReader();
    }

    @Override
    public VerificationType getVerificationType() {
        return VerificationType.EDL_40_SIG;
    }

    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey) {

        try {
            SignedMeterValue signedMeterValue = smlReader.readFromString(Utils.unescapeXML(data));
            signedMeterValue.validate();
            SMLSignature smlSignature = smlReader.parseSMLSigXml(signedMeterValue);
            if(smlSignature.getUnit() != 30){
                throw new SMLValidationException("Invalid unit present in sml data", "error.sml.invalid.unit");
            }
            SMLVerifiedData verifiedData = new SMLVerifiedData(smlSignature, VerificationType.EDL_40_SIG, EncodingType.PLAIN, Utils.toFormattedHex(publicKey));

            if (verifier.verify(publicKey, smlSignature)) {
                return new VerificationResult(verifiedData);
            } else {
                return new VerificationResult(verifiedData, Error.withVerificationFailed());
            }
        } catch (ValidationException e) {
            return new VerificationResult(Error.withValidationException(e));
        }

    }

    @Override
    public boolean canParseData(String data) {

        try {
            SignedMeterValue signedMeterValue = smlReader.readFromString(Utils.unescapeXML(data));
            signedMeterValue.validate();
        } catch (ValidationException e) {
            LOGGER.info("Data not matching for " + VerificationType.EDL_40_SIG);
            return false;
        }
        LOGGER.info("Match for " + VerificationType.EDL_40_SIG + " detected");
        return true;
    }


    @Override
    public String parsePublicKey(String data) {
        try {
            SignedMeterValue signedMeterValue = smlReader.readFromString(Utils.unescapeXML(data));
            return signedMeterValue.getPublicKey() != null ? signedMeterValue.getPublicKey().getCleanedValue() : null;
        } catch (ValidationException e) {
            return null;
        }
    }

    @Override
    public String createFormattedKey(String data) {
        String parsedKey = parsePublicKey(data);
        String formattedKey = parsedKey;
        List<EncodingType> encodingTypes = EncodingType.guessType(parsedKey, false);
        if (encodingTypes.size() > 0) {
            try {
                formattedKey = Utils.toFormattedHex(encodingTypes.get(0).decode(parsedKey), 4);
            } catch (DecodingException e) {
                LOGGER.warn("Could not decode parsed key", e);
            }
        }
        return formattedKey;
    }
}
