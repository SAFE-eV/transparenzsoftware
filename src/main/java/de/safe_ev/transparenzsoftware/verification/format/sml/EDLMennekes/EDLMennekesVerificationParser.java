package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ContainedPublicKeyParser;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLVerificationParserBase;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.ChargingProcess;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public class EDLMennekesVerificationParser extends SMLVerificationParserBase implements ContainedPublicKeyParser {

    private static final Logger LOGGER = LogManager.getLogger(EDLMennekesVerificationParser.class);

    private final XMLReader xmlReader;

    public EDLMennekesVerificationParser() {
	super();
	xmlReader = new XMLReader();
    }

    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey, IntrinsicVerified intrinsicVerified) {
	LOGGER.info("Starting...");
	VerificationResult verificationResult;
	try {
	    final ChargingProcess chargingProcess = xmlReader.readChargingProcessFromString(data, true);
	    final EDLMennekesSignature mennekesSignatureStart = new EDLMennekesSignature(chargingProcess,
		    EDLMennekesSignature.ReadingType.MEASUREMENT_START);
	    final EDLMennekesSignature mennekesSignatureEnd = new EDLMennekesSignature(chargingProcess,
		    EDLMennekesSignature.ReadingType.MEASUREMENT_END);
	    final EDLMennekesVerifiedData verifiedData = new EDLMennekesVerifiedData(chargingProcess,
		    mennekesSignatureStart, mennekesSignatureEnd);
	    if (!intrinsicVerified.ok()) {
		if (!verifier.verify(publicKey, mennekesSignatureStart)) {
		    return new VerificationResult(verifiedData, Error.withVerificationFailed());
		}
		if (!verifier.verify(publicKey, mennekesSignatureEnd)) {
		    return new VerificationResult(verifiedData, Error.withVerificationFailed());
		}
	    }
	    verificationResult = new VerificationResult(verifiedData, true, intrinsicVerified);
	    try {
		verifiedData.checkLawIntegrityForTransaction();
	    } catch (final RegulationLawException e) {
		verificationResult.addError(Error.withRegulationLawException(e));
	    }
	} catch (final ValidationException e) {
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
	    final ChargingProcess chargingProcess = xmlReader.readChargingProcessFromString(data, false);
	    if (chargingProcess == null || chargingProcess.getPublicKey() == null) {
		return false;
	    }
	} catch (final Exception e) {
	    return false;
	}
	LOGGER.info("Match for " + VerificationType.EDL_40_MENNEKES + " detected");
	return true;
    }

    @Override
    public String parsePublicKey(String data) {
	try {
	    final ChargingProcess chargingProcess = xmlReader.readChargingProcessFromString(data, true);
	    return Utils.clearString(chargingProcess.getPublicKey());
	} catch (final ValidationException e) {
	    return null;
	}
    }

    @Override
    public String createFormattedKey(String data) {
	final String publicKey = parsePublicKey(data);
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
