package com.hastobe.transparenzsoftware.verification.format.sml.IsaEDL40;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLSignature;
import com.hastobe.transparenzsoftware.verification.format.sml.IsaEDL40.IsaSMLSignatureVerifier;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLVerificationParserBase;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLVerifiedData;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class IsaEDL40VerificationParser extends SMLVerificationParserBase {

    private static final Logger LOGGER = LogManager.getLogger(IsaEDL40VerificationParser.class);

    private IsaSMLReader smlReader;

    public IsaEDL40VerificationParser() {
        super();
        smlReader = new IsaSMLReader();
        this.verifier = new IsaSMLSignatureVerifier();
    }


    @Override
    public VerificationType getVerificationType() {
        return VerificationType.ISA_EDL_40_P;
    }

    @Override
    public boolean canParseData(String data) {
        List<EncodingType> encodingTypes = EncodingType.guessType(data);
        if (encodingTypes.size() == 0) {
            LOGGER.info("Data not matching for " + VerificationType.ISA_EDL_40_P + ". Not base64.");
            return false;
        }
        boolean match = false;
        for (EncodingType dataTypes : encodingTypes) {

            try {
                SMLSignature smlSignature = smlReader.parsePayloadData(dataTypes.decode(data));
                if (smlSignature.getProvidedSignature() == null) {
                    LOGGER.info("Data not matching for " + VerificationType.ISA_EDL_40_P + " and encoding " + dataTypes + ". Not a full sml data set.");
                } else {
                    match = true;
                    break;
                }
            } catch (ValidationException | DecodingException e) {
                LOGGER.info("Data not matching for " + VerificationType.ISA_EDL_40_P + " and encoding " + dataTypes + ". Invalid sml data.");
            }
        }
        if (match) {
            LOGGER.info("Match for " + VerificationType.ISA_EDL_40_P + " detected");
        }
        return match;
    }

    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey) {
        List<EncodingType> typeList = EncodingType.guessType(data);
        VerificationResult verificationResult = null;
        for (EncodingType encodingType : typeList) {
            try {
                SMLSignature smlSignature = smlReader.parsePayloadData(encodingType.decode(data));
                verificationResult = parseAndVerifyWithSmlData(smlSignature, VerificationType.ISA_EDL_40_P, EncodingType.HEX, publicKey);
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
    public VerificationResult parseAndVerifyWithSmlData(SMLSignature smlSignature, VerificationType verificationType, EncodingType encodingType, byte[] publicKey) throws ValidationException {

        IsaSMLVerifiedData verifiedData = new IsaSMLVerifiedData((IsaEDL40Signature)smlSignature, verificationType, encodingType, Utils.toFormattedHex(publicKey));
        if (verifier.verify(publicKey, smlSignature)) {
            return new VerificationResult(verifiedData);
        }
        return new VerificationResult(verifiedData, Error.withVerificationFailed());
    }
}
