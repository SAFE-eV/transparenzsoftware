package de.safe_ev.transparenzsoftware.verification.result;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.ocmf.OCMFVerifiedData;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;
import de.safe_ev.transparenzsoftware.verification.xml.VerifiedData;

public class VerificationResult {

    private List<Meter> meters;
    private boolean verified;
    private IntrinsicVerified intrinsicVerified;
    private DetailsList additionalVerificationData;
    private VerifiedData verifiedData;

    private final List<Error> errorMessages;
    private boolean transactionResult;
    private BigInteger transactionId;

    private VerificationResult() {
	errorMessages = new ArrayList<>();
	meters = new ArrayList<>();
	transactionResult = false;
    }

    public VerificationResult(VerifiedData verifiedData, boolean transactionResult,
	    IntrinsicVerified intrinsicVerified) {
	this();
	meters = verifiedData.getMeters();
	additionalVerificationData = verifiedData.getAdditionalData();
	this.verifiedData = verifiedData;
	verified = true;
	this.transactionResult = transactionResult;
	this.intrinsicVerified = intrinsicVerified;
    }

    public VerificationResult(VerifiedData verifiedData, IntrinsicVerified intrinsicVerified) {
	this(verifiedData, false, intrinsicVerified);
    }

    private VerificationResult(VerifiedData verifiedData, List<Error> errors) {
	this();
	if (verifiedData != null) {
	    meters = verifiedData.getMeters();
	    additionalVerificationData = verifiedData.getAdditionalData();
	} else {
	    additionalVerificationData = new DetailsList();
	}
	verified = false;
	errorMessages.addAll(errors);
	this.verifiedData = verifiedData;
    }

    public VerificationResult(VerifiedData verifiedData, Error error) {
	this(verifiedData, Collections.singletonList(error));
    }

    public VerificationResult(Error error) {
	this(null, error);
    }

    public void addError(Error error) {
	errorMessages.add(error);
	verified = false;
    }

    public DetailsList getAdditionalVerificationData() {
	return additionalVerificationData;
    }

    public VerifiedData getVerifiedData() {
	return verifiedData;
    }

    public boolean isVerified() {
	return verified;
    }

    public List<Error> getErrorMessages() {
	return errorMessages;
    }

    public boolean containsErrorOfType(Error.Type type) {
	for (final Error errorMessage : errorMessages) {
	    if (errorMessage != null && errorMessage.getType().equals(type)) {
		return true;
	    }
	}
	return false;
    }

    public List<Meter> getMeters() {
	return meters;
    }

    /**
     * Merges two verification results into one object. Also takes care to check if
     * the status has changed and we need to create a warning that the data has
     * changed status values and though is not Eichrechtskonform anymore
     *
     * @param start         VerificationResult of start
     * @param stop          VerificationResult of stop
     * @param transactionId
     * @return merged verification result
     */
    public static VerificationResult mergeVerificationData(VerificationResult start, VerificationResult stop,
	    BigInteger transactionId) throws ValidationException {
	final VerificationResult verificationResult = new VerificationResult();
	verificationResult.verified = start.isVerified() && stop.isVerified();
	final DetailsList additionalDataMerged = new DetailsList();
	for (final String key : start.getAdditionalVerificationData().keySet()) {
	    final Object o1 = start.getAdditionalVerificationData().get(key);
	    final Object o2 = stop.getAdditionalVerificationData().get(key);
	    if (o1 != null && o1.equals(o2)) {
		additionalDataMerged.put(key, o1);
	    } else {
		additionalDataMerged.put(String.format("%s (start)", key), o1);
		additionalDataMerged.put(String.format("%s (stop)", key), o2);
	    }
	}
	verificationResult.additionalVerificationData = additionalDataMerged;

	for (final Error error : start.getErrorMessages()) {
	    verificationResult.addError(error);
	}
	for (final Error error : stop.getErrorMessages()) {
	    verificationResult.addError(error);
	}

	if (!(start.getVerifiedData() instanceof OCMFVerifiedData)) {
	    for (final Meter meter : start.getMeters()) {
		verificationResult.meters.add(new Meter(meter.getValue(), meter.getTimestamp(), Meter.Type.START,
			meter.getTimeSyncType(), meter.getScaling(), meter.isCompensated()));
	    }

	    for (final Meter meter : stop.getMeters()) {
		verificationResult.meters.add(new Meter(meter.getValue(), meter.getTimestamp(), Meter.Type.STOP,
			meter.getTimeSyncType(), meter.getScaling(), meter.isCompensated()));
	    }
	} else {
	    verificationResult.meters.addAll(start.getMeters());
	    verificationResult.meters.addAll(stop.getMeters());
	}

	verificationResult.transactionResult = true;
	verificationResult.transactionId = transactionId;
	// only check law regulations if result is verified
	if (verificationResult.isVerified() && start.getVerifiedData() != null && stop.getVerifiedData() != null) {
	    try {
		start.getVerifiedData().lawConform(stop.getVerifiedData());
	    } catch (final RegulationLawException e) {
		final Error lawViolation = new Error(Error.Type.VERIFICATION, e.getMessage(),
			e.getLocalizedMessageKey());
		verificationResult.addError(lawViolation);
	    }
	}
	return verificationResult;
    }

    public boolean isTransactionResult() {
	return transactionResult;
    }

    public BigInteger getTransactionId() {
	return transactionId;
    }
}
