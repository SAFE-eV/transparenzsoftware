package com.hastobe.transparenzsoftware.verification.format.ocmf;

import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.FormatComparisonException;
import com.hastobe.transparenzsoftware.verification.RegulationLawException;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.VerifiedData;

import java.util.*;

public class OCMFVerifiedData extends VerifiedData {

    private String publicKey;
    private String encoding;

    private String formatVersion;
    private String vendorIdentification;
    private String vendorVersion;
    private String pagination;
    private String meterVendor;
    private String meterModel;
    private String meterSerialNumber;
    private String meterFirmwareVersion;
    private String identificationStatus;
    private String identificationLevel;
    private String identificationFlags;
    private String identificationType;
    private String identificationData;
    private List<Meter> meters;
    private OCMFPayloadData ocmfPayloadData;

    public OCMFVerifiedData() {

    }

    public OCMFVerifiedData(OCMF ocmf, String publicKey, String encoding) {
        if (ocmf == null) {
            return;
        }
        ocmfPayloadData = ocmf.getData();
        this.meters = new ArrayList<>();

        for (Reading reading : ocmfPayloadData.getRD()) {
            Meter.Type type = null;
            if (reading.isStartTransaction()) {
                type = Meter.Type.START;
            }
            if (reading.isStopTransaction()) {
                type = Meter.Type.STOP;
            }
            Double rv = reading.getRV();
            if (rv != null && reading.getRU() != null && reading.getRU().trim().toLowerCase().equals("wh")) {
                rv = rv / 1000;
            }
            //rv might be null here
            if (rv == null) {
                rv = (double) 0;
            }
            Meter.TimeSyncType timeSyncType = reading.isTimeInformativeOnly() ? Meter.TimeSyncType.INFORMATIVE : Meter.TimeSyncType.SYNCHRONIZED;
            int digits = reading.getRVDigits();
            
            meters.add(new Meter(rv, reading.getTimestamp(), type, timeSyncType, digits));
        }
        this.publicKey = publicKey;
        this.encoding = encoding;
        this.formatVersion = ocmfPayloadData.getFV();

        this.vendorIdentification = ocmfPayloadData.getGI();
        this.vendorVersion = ocmfPayloadData.getGV();
        this.pagination = ocmfPayloadData.getPG();
        this.meterVendor = ocmfPayloadData.getMV();
        this.meterModel = ocmfPayloadData.getMM();
        this.meterSerialNumber = ocmfPayloadData.getMS();
        this.meterFirmwareVersion = ocmfPayloadData.getMF();
        this.identificationStatus = ocmfPayloadData.getIdStatus();
        this.identificationLevel = ocmfPayloadData.getIdLevel();


        if (ocmfPayloadData.getIF() != null) {
            this.identificationFlags = String.join(", ", ocmfPayloadData.getIF());
        }
        this.identificationType = ocmfPayloadData.getIT();
        this.identificationData = ocmfPayloadData.getID();

    }

    @Override
    public List<Meter> getMeters() {
        return meters;
    }

    @Override
    public String getFormat() {
        return VerificationType.OCMF.name();
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public HashMap<String, Object> getAdditionalData() {
        HashMap<String, Object> addData = new HashMap<>();
        addData.put(Translator.get("app.verify.ocmf.version"), getFormatVersion());
        addData.put(Translator.get("app.verify.ocmf.vendorIdentifier"), getVendorIdentification());
        addData.put(Translator.get("app.verify.ocmf.vendorVersion"), getVendorVersion());
        addData.put(Translator.get("app.verify.ocmf.pagination"), getPagination());
        if (getMeterVendor() != null) {
            addData.put(Translator.get("app.verify.ocmf.meterVendor"), getMeterVendor());
        }
        if (getMeterModel() != null) {
            addData.put(Translator.get("app.verify.ocmf.meterModel"), getMeterModel());
        }
        if (getMeterSerialNumber() != null) {
            addData.put(Translator.get("app.verify.ocmf.meterSerialNumber"), getMeterSerialNumber());
        }
        if (getMeterFirmwareVersion() != null) {
            addData.put(Translator.get("app.verify.ocmf.meterFirmwareVersion"), getMeterFirmwareVersion());
        }
        if (getIdentificationStatus() != null) {
            addData.put(Translator.get("app.verify.ocmf.identificationStatus"), getIdentificationStatus());
        }
        if (getIdentificationLevel() != null) {
            addData.put(Translator.get("app.verify.ocmf.identificationLevel"), getIdentificationLevel());
        }
        if (getIdentificationFlags() != null) {
            addData.put(Translator.get("app.verify.ocmf.identificationFlags"), getIdentificationFlags());
        }
        if (getIdentificationType() != null) {
            addData.put(Translator.get("app.verify.ocmf.identificationType"), getIdentificationType());
        }
        if (getIdentificationData() != null) {
            addData.put(Translator.get("app.verify.ocmf.identificationData"), getIdentificationData());
        }
        if (this.ocmfPayloadData.getRD() != null) {
            int count = 1;
            for (Reading reading : this.ocmfPayloadData.getRD()) {
                if (reading.getTimeSynchronicity() != null) {
                    String label = Translator.get("app.verify.ocmf.timesynchronicity");
                    addData.put(String.format("%s %s", label, count), Translator.get(reading.getLabelForTimeFlag()));
                }
                count++;
            }
            addData.put(Translator.get("app.verify.ocmf.identificationData"), getIdentificationData());
        }


        return addData;
    }

    @Override
    public boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException {

        if (!(stopValue instanceof OCMFVerifiedData)) {
            throw new FormatComparisonException();
        }
        if(this.getMeters() == null){
            throw new OCMFValidationException("OCMF entry marked as start does not contain any meter values", "app.verify.ocmf.startcontainsnometers");
        }
        if(stopValue.getMeters() == null){
            throw new OCMFValidationException("OCMF entry marked ast stop does not contain any meter values", "app.verify.ocmf.stopcontainsnometers");
        }
        OCMFVerifiedData otherOCMFData = (OCMFVerifiedData) stopValue;
        boolean stopContainsStart = stopValue.getMeters().stream().anyMatch(meter -> meter.getType() != null && meter.getType().equals(Meter.Type.START));
        if (stopContainsStart) {
            throw new OCMFValidationException("OCMF entry marked as stop contains a start value", "app.verify.ocmf.stopcontainsstart");
        }
        boolean startContainsStop = this.getMeters().stream().anyMatch(meter -> meter.getType() != null && meter.getType().equals(Meter.Type.STOP));
        if (startContainsStop) {
            throw new OCMFValidationException("OCMF entry marked as start contains a stop value", "app.verify.ocmf.startcontainsstop");
        }
        List<Reading> combinedReadings = new ArrayList<>();
        combinedReadings.addAll(this.ocmfPayloadData.getRD());
        combinedReadings.addAll(otherOCMFData.ocmfPayloadData.getRD());
        return checkLawIntegrityForReadings(combinedReadings, this.ocmfPayloadData.getIdLevel(), otherOCMFData.ocmfPayloadData.getIdLevel());
    }


    public boolean checkLawIntegrityForTransaction() throws ValidationException, RegulationLawException {
        return checkLawIntegrityForReadings(this.ocmfPayloadData.getRD(), ocmfPayloadData.getIdLevel(), null);
    }

    private boolean checkLawIntegrityForReadings(List<? extends Reading> readings, String idLevelStart, String idLevelStop ) throws OCMFValidationException, RegulationLawException {
        Reading startValue = null;
        Reading stopValue = null;

        String previousST = null;
        String previousRU = null;
        for (Reading reading : readings) {
            if (reading.getST() == null) {
                if (previousST == null) {
                    throw new OCMFValidationException("Missing mandatory ST parameter for first reading.", "error.values.missing.st");
                } else {
                    reading.setST(previousST);
                }
            } else {
                previousST = reading.getST();
            }

            if (reading.getRU() == null) {
                if (previousRU == null) {
                    throw new OCMFValidationException("Missing mandatory RU parameter for first reading.", "error.values.missing.ru");
                } else {
                    reading.setRU(previousRU);
                }
            } else {
                previousRU = reading.getRU();
            }

            if (reading.isStartTransaction()) {
                if (startValue != null) {
                    throw new OCMFValidationException("Cannot verify contains multiple start values", "error.values.toomany.start");
                }
                startValue = reading;
            }
            if (reading.isStopTransaction()) {
                if (stopValue != null) {
                    throw new OCMFValidationException("Cannot verify contains multiple stop values", "error.values.toomany.stop");
                }
                stopValue = reading;
            }
        }
        if (startValue == null) {
            throw new OCMFValidationException("Cannot verify contains no start values in reading", "error.values.no.start.meter.values");
        }
        if (stopValue == null) {
            throw new OCMFValidationException("Cannot verify contains no stop values in readings", "error.values.no.stop.meter.values");
        }

        if (startValue.getRV() > stopValue.getRV()) {
            throw new RegulationLawException("Meter value of start is higher than meter value of stop", "app.verify.law.conform.meter.wrong");
        }
        if(startValue.getTimestamp() != null && stopValue.getTimestamp() != null && startValue.getTimestamp().isAfter(stopValue.getTimestamp())){
            throw new RegulationLawException("Meter timestamp of start is after timestamp of stop", "app.verify.law.conform.meter.time.wrong");
        }
        List<String> contractStatusErrors = Arrays.asList("MISMATCH", "INVALID", "OUTDATED", "UNKNOWN");
        if (idLevelStart != null && contractStatusErrors.contains(idLevelStart.trim())) {
            throw new RegulationLawException("Error on reading contract id", "app.verify.law.conform.timesynchronicity.wrong");
        }
        if (idLevelStop != null && contractStatusErrors.contains(idLevelStop.trim())) {
            throw new RegulationLawException("Error on reading contract id", "app.verify.law.conform.timesynchronicity.wrong");
        }
        if (startValue.getST() == null || (!startValue.getST().equals("G") || !stopValue.getST().equals("G"))) {
            throw new RegulationLawException("Meter error code present", "app.verify.law.conform.meterstatus.wrong");
        }
        if (startValue.getTimeSynchronicity() == null || (startValue.getTimeSynchronicity().equals("R") && !stopValue.getTimeSynchronicity().equals("R"))) {
            throw new RegulationLawException("Time synchronicity wrong", "app.verify.law.conform.timesynchronicity.wrong");
        }
        if (stopValue.getEI() != null && !stopValue.getEI().equals(startValue.getEI())) {
            throw new RegulationLawException("Event counter of start and stop differs", "app.verify.law.conform.eventcounter.differs");
        }
        checkErrorFlag(startValue);
        checkErrorFlag(stopValue);

        return true;
    }

    private void checkErrorFlag(Reading startValue) throws RegulationLawException {
        if (startValue.getEF() != null) {
            if (startValue.getEF().equals("E")) {
                throw new RegulationLawException("Error flag set on energy", "app.verify.law.conform.error.flag.energy");
            }
            if (startValue.getEF().equals("t")) {
                throw new RegulationLawException("Error flag set on time", "app.verify.law.conform.error.flag.time");
            }
        }
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public String getVendorIdentification() {
        return vendorIdentification;
    }

    public String getVendorVersion() {
        return vendorVersion;
    }

    public String getPagination() {
        return pagination;
    }

    public String getMeterVendor() {
        return meterVendor;
    }

    public String getMeterModel() {
        return meterModel;
    }

    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    public String getMeterFirmwareVersion() {
        return meterFirmwareVersion;
    }

    public String getIdentificationStatus() {
        return identificationStatus;
    }

    public String getIdentificationFlags() {
        return identificationFlags;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public String getIdentificationData() {
        return identificationData;
    }

    public String getIdentificationLevel() {
        return identificationLevel;
    }

    public boolean containsCompleteTransaction() {
        return ocmfPayloadData.containsCompleteTransaction();
    }
}
