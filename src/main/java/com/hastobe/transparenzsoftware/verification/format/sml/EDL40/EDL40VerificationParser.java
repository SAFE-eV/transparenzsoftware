package com.hastobe.transparenzsoftware.verification.format.sml.EDL40;

import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLSignature;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLValidationException;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLVerificationParserBase;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.List;

public class EDL40VerificationParser extends SMLVerificationParserBase {

    private static final Logger LOGGER = LogManager.getLogger(EDL40VerificationParser.class);

    private SMLReader smlReader;

    public EDL40VerificationParser() {
        super();
        smlReader = new SMLReader();
    }


    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey) {
        List<EncodingType> typeList = EncodingType.guessType(data);

        VerificationResult verificationResult = null;
        for (EncodingType encodingType : typeList) {
            try {
                SMLSignature smlSignature = smlReader.parsePayloadData(encodingType.decode(data));
                verificationResult = parseAndVerifyWithSmlData(smlSignature, VerificationType.EDL_40_P, EncodingType.BASE64, publicKey);
                return verificationResult;
            } catch (ValidationException e) {
                verificationResult = new VerificationResult(Error.withValidationException(e));
            } catch (DecodingException e) {
                verificationResult = new VerificationResult(Error.withDecodingSignatureFailed());
            }
        }
        return verificationResult;

    }

    @Override
    public VerificationType getVerificationType() {
        return VerificationType.EDL_40_P;
    }

    @Override
    public boolean canParseData(String data) {
        List<EncodingType> encodingTypes = EncodingType.guessType(data);
        if (encodingTypes.size() == 0) {
            LOGGER.info("Data not matching for " + VerificationType.EDL_40_P + ". Not base64.");
            return false;
        }
        boolean match = false;
        for (EncodingType dataTypes : encodingTypes) {

            try {
                SMLSignature smlSignature = smlReader.parsePayloadData(dataTypes.decode(data));
                if (smlSignature.getProvidedSignature() == null) {
                    LOGGER.info("Data not matching for " + VerificationType.EDL_40_P + " and encoding " + dataTypes + ". Not a full sml data set.");
                } else {
                    match = true;
                    break;
                }
            } catch (ValidationException | DecodingException e) {
                LOGGER.info("Data not matching for " + VerificationType.EDL_40_P + " and encoding " + dataTypes + ". Invalid sml data.");
            }
        }
        if (match) {
            LOGGER.info("Match for " + VerificationType.EDL_40_P + " detected");
        }
        return match;
    }
}
