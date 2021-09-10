package com.hastobe.transparenzsoftware.verification.result;

import com.hastobe.transparenzsoftware.verification.RegulationLawException;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.format.ocmf.OCMFVerifiedData;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.VerifiedData;

import java.math.BigInteger;
import java.util.*;

public class VerificationResult {

    private List<Meter> meters;
    private boolean verified;
    private Map<String, Object> additionalVerificationData;
    private VerifiedData verifiedData;

    private List<Error> errorMessages;
    private boolean transactionResult;
    private BigInteger transactionId;

    private VerificationResult() {
        this.errorMessages = new ArrayList<>();
        this.meters = new ArrayList<>();
        this.transactionResult = false;
    }

    public VerificationResult(VerifiedData verifiedData, boolean transactionResult) {
        this();
        this.meters = verifiedData.getMeters();
        this.additionalVerificationData = verifiedData.getAdditionalData();
        this.verifiedData = verifiedData;
        this.verified = true;
        this.transactionResult = transactionResult;
    }

    public VerificationResult(VerifiedData verifiedData) {
        this(verifiedData, false);
    }

    private VerificationResult(VerifiedData verifiedData, List<Error> errors) {
        this();
        if (verifiedData != null) {
            this.meters = verifiedData.getMeters();
            this.additionalVerificationData = verifiedData.getAdditionalData();
        } else {
            this.additionalVerificationData = new HashMap<>();
        }
        this.verified = false;
        this.errorMessages.addAll(errors);
        this.verifiedData = verifiedData;
    }

    public VerificationResult(VerifiedData verifiedData, Error error) {
        this(verifiedData, Collections.singletonList(error));
    }

    public VerificationResult(Error error) {
        this(null, error);
    }

    public void addError(Error error) {
        this.errorMessages.add(error);
        this.verified = false;
    }


    public Map<String, Object> getAdditionalVerificationData() {
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
        for (Error errorMessage : errorMessages) {
            if (errorMessage.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public List<Meter> getMeters() {
        return meters;
    }

    /**
     * Merges two verification results into one object. Also takes
     * care to check if the status has changed and we need
     * to create a warning that the data has changed status values
     * and though is not Eichrechtskonform anymore
     *
     * @param start VerificationResult of start
     * @param stop  VerificationResult of stop
     * @param transactionId
     * @return merged verification result
     */
    public static VerificationResult mergeVerificationData(VerificationResult start, VerificationResult stop, BigInteger transactionId) throws ValidationException {
        VerificationResult verificationResult = new VerificationResult();
        verificationResult.verified = start.isVerified() && stop.isVerified();
        Map<String, Object> additionalDataMerged = new HashMap<>();
        for (String key : start.getAdditionalVerificationData().keySet()) {
            Object o1 = start.getAdditionalVerificationData().get(key);
            Object o2 = stop.getAdditionalVerificationData().get(key);
            if (o1 == o2 || (o1 != null && o1.equals(o2))) {
                additionalDataMerged.put(key, o1);
            } else {
                additionalDataMerged.put(String.format("%s (start)", key), o1);
                additionalDataMerged.put(String.format("%s (stop)", key), o2);
            }
        }
        verificationResult.additionalVerificationData = additionalDataMerged;

        for (Error error : start.getErrorMessages()) {
            verificationResult.addError(error);
        }
        for (Error error : stop.getErrorMessages()) {
            verificationResult.addError(error);
        }

        if(!(start.getVerifiedData() instanceof OCMFVerifiedData)){
            for (Meter meter : start.getMeters()) {
                verificationResult.meters.add(new Meter(meter.getValue(), meter.getTimestamp(), Meter.Type.START, meter.getTimeSyncType()));
            }

            for (Meter meter : stop.getMeters()) {
                verificationResult.meters.add(new Meter(meter.getValue(), meter.getTimestamp(), Meter.Type.STOP, meter.getTimeSyncType()));
            }
        } else {
            verificationResult.meters.addAll(start.getMeters());
            verificationResult.meters.addAll(stop.getMeters());
        }

        verificationResult.transactionResult = true;
        verificationResult.transactionId = transactionId;
        //only check law regulations if result is verified
        if(verificationResult.isVerified() && start.getVerifiedData() != null && stop.getVerifiedData() != null) {
            try {
                start.getVerifiedData().lawConform(stop.getVerifiedData());
            } catch (RegulationLawException e) {
                Error lawViolation = new Error(Error.Type.VERIFICATION, e.getMessage(), e.getLocalizedMessage());
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
