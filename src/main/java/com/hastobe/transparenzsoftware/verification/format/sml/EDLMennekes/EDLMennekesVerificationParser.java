package com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.ContainedPublicKeyParser;
import com.hastobe.transparenzsoftware.verification.RegulationLawException;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.ChargingProcess;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLVerificationParserBase;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EDLMennekesVerificationParser extends SMLVerificationParserBase implements ContainedPublicKeyParser {

    private static final Logger LOGGER = LogManager.getLogger(EDLMennekesVerificationParser.class);

    private XMLReader xmlReader;

    public EDLMennekesVerificationParser() {
        super();
        xmlReader = new XMLReader();
    }


    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey) {
        VerificationResult verificationResult;
        try {
            ChargingProcess chargingProcess = xmlReader.readChargingProcessFromString(data);
            EDLMennekesSignature mennekesSignatureStart = new EDLMennekesSignature(chargingProcess, EDLMennekesSignature.ReadingType.MEASUREMENT_START);
            EDLMennekesSignature mennekesSignatureEnd = new EDLMennekesSignature(chargingProcess, EDLMennekesSignature.ReadingType.MEASUREMENT_END);
            EDLMennekesVerifiedData verifiedData = new EDLMennekesVerifiedData(chargingProcess, mennekesSignatureStart, mennekesSignatureEnd);
            if (!verifier.verify(publicKey, mennekesSignatureStart)) {
                return new VerificationResult(verifiedData, Error.withVerificationFailed());
            }
            if (!verifier.verify(publicKey, mennekesSignatureEnd)) {
                return new VerificationResult(verifiedData, Error.withVerificationFailed());
            }
            verificationResult = new VerificationResult(verifiedData, true);
            try {
                verifiedData.checkLawIntegrityForTransaction();
            } catch (RegulationLawException e) {
                verificationResult.addError(Error.withRegulationLawException(e));
            }
        } catch (ValidationException e) {
            verificationResult = new VerificationResult(Error.withValidationException(e));
        }
        return verificationResult;
    }

    @Override
    public VerificationType getVerificationType() {
        return VerificationType.EDL_40_MENNEKES;
    }

    @Override
    public boolean canParseData(String data) {
        try {
            ChargingProcess chargingProcess = xmlReader.readChargingProcessFromString(data);
            if (chargingProcess == null || chargingProcess.getPublicKey() == null) {
                LOGGER.info("Data not matching for " + VerificationType.EDL_40_MENNEKES + ". Not a full xml data set.");
                return false;
            }
        } catch (ValidationException e) {
            LOGGER.info("Data not matching for " + VerificationType.EDL_40_MENNEKES + ". Invalid xml data.");
            return false;
        }
        LOGGER.info("Match for " + VerificationType.EDL_40_MENNEKES + " detected");
        return true;
    }

    @Override
    public String parsePublicKey(String data) {
        try {
            ChargingProcess chargingProcess = xmlReader.readChargingProcessFromString(data);
            return Utils.clearString(chargingProcess.getPublicKey());
        } catch (ValidationException e) {
            return null;
        }
    }

    @Override
    public String createFormattedKey(String data) {
        String publicKey = parsePublicKey(data);
        if (publicKey == null) {
            return null;
        }
        return Utils.splitStringToGroups(publicKey, 4);
    }

    @Override
    public Class getVerfiedDataClass() {
        return EDLMennekesVerifiedData.class;
    }
}
