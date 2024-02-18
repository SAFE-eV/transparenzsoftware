package de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLVerificationParserBase;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public class IsaEDL40VerificationParser extends SMLVerificationParserBase {

    private static final Logger LOGGER = LogManager.getLogger(IsaEDL40VerificationParser.class);

    private final IsaSMLReader smlReader;

    public IsaEDL40VerificationParser() {
	super();
	smlReader = new IsaSMLReader();
	verifier = new IsaSMLSignatureVerifier();
    }

    @Override
    public VerificationType getVerificationType() {
	return VerificationType.ISA_EDL_40_P;
    }

    @Override
    public boolean canParseData(String data) {
	final List<EncodingType> encodingTypes = EncodingType.guessType(data);
	if (encodingTypes.size() == 0) {
	    LOGGER.info("Data not matching for " + VerificationType.ISA_EDL_40_P + ". Not base64.");
	    return false;
	}
	boolean match = false;
	for (final EncodingType dataTypes : encodingTypes) {

	    try {
		final SMLSignature smlSignature = smlReader.parsePayloadData(dataTypes.decode(data));
		if (smlSignature.getProvidedSignature() == null) {
		    LOGGER.info("Data not matching for " + VerificationType.ISA_EDL_40_P + " and encoding " + dataTypes
			    + ". Not a full sml data set.");
		} else {
		    match = true;
		    break;
		}
	    } catch (ValidationException | DecodingException e) {
		LOGGER.info("Data not matching for " + VerificationType.ISA_EDL_40_P + " and encoding " + dataTypes
			+ ". Invalid sml data.");
	    }
	}
	if (match) {
	    LOGGER.info("Match for " + VerificationType.ISA_EDL_40_P + " detected");
	}
	return match;
    }

    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey, IntrinsicVerified intrinsicVerified) {
	LOGGER.info("Starting...");
	final List<EncodingType> typeList = EncodingType.guessType(data);
	VerificationResult verificationResult = null;
	for (final EncodingType encodingType : typeList) {
	    try {
		final SMLSignature smlSignature = smlReader.parsePayloadData(encodingType.decode(data));
		verificationResult = parseAndVerifyWithSmlData(smlSignature, VerificationType.ISA_EDL_40_P,
			EncodingType.HEX, publicKey, intrinsicVerified);
		return verificationResult;
	    } catch (final ValidationException e) {
		verificationResult = new VerificationResult(Error.withValidationException(e));
	    } catch (final DecodingException e) {
		verificationResult = new VerificationResult(Error.withDecodingSignatureFailed());
	    }
	}

	return verificationResult;
    }

    @Override
    public VerificationResult parseAndVerifyWithSmlData(SMLSignature smlSignature, VerificationType verificationType,
	    EncodingType encodingType, byte[] publicKey, IntrinsicVerified intrinsicVerified)
	    throws ValidationException {

	final IsaSMLVerifiedData verifiedData = new IsaSMLVerifiedData((IsaEDL40Signature) smlSignature,
		verificationType, encodingType, Utils.toFormattedHex(publicKey));
	if (intrinsicVerified.ok() || verifier.verify(publicKey, smlSignature)) {
	    return new VerificationResult(verifiedData, true, intrinsicVerified);
	}
	return new VerificationResult(verifiedData, Error.withVerificationFailed());
    }
}
