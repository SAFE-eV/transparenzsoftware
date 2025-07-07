package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.FormatComparisonException;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;
import de.safe_ev.transparenzsoftware.verification.xml.VerifiedData;

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
    private String chargeControllerFirmwareVersion;
    private String identificationStatus;
    private String identificationLevel;
    private String identificationFlags;
    private String identificationType;
    private String identificationData;
    private List<Meter> meters;
    private OCMFPayloadData ocmfPayloadData;
    private String tariffText;
    private String lossCompensation;

    public OCMFVerifiedData() {

    }

    public OCMFVerifiedData(OCMF ocmf, String publicKey, String encoding) {
	if (ocmf == null) {
	    return;
	}
	ocmfPayloadData = ocmf.getData();
	meters = new ArrayList<>();

	for (final Reading reading : ocmfPayloadData.getRD()) {
	    Meter.Type type = null;
	    int digits = reading.getRVDigits();
	    if (reading.isStartTransaction()) {
		type = Meter.Type.START;
	    }
	    if (reading.isStopTransaction()) {
		type = Meter.Type.STOP;
	    }
	    Double rv = reading.getRV();
	    if (rv != null && reading.getRU() != null && reading.getRU().trim().toLowerCase().equals("wh")) {
		rv = rv / 1000;
		digits -= 3;
	    }
	    // rv might be null here
	    if (rv == null) {
		rv = (double) 0;
	    }
	    final Meter.TimeSyncType timeSyncType = reading.getTimeSyncType();
	    final Meter m = new Meter(rv, reading.getTimestamp(), type, timeSyncType, digits, isCompensated(reading));
	    m.setLawRelevant(isLawRelevant(reading));
	    meters.add(m);
	}
	this.publicKey = publicKey;
	this.encoding = encoding;
	formatVersion = ocmfPayloadData.getFV();

	vendorIdentification = ocmfPayloadData.getGI();
	vendorVersion = ocmfPayloadData.getGV();
	pagination = ocmfPayloadData.getPG();
	meterVendor = ocmfPayloadData.getMV();
	meterModel = ocmfPayloadData.getMM();
	meterSerialNumber = ocmfPayloadData.getMS();
	meterFirmwareVersion = ocmfPayloadData.getMF();
	chargeControllerFirmwareVersion = ocmfPayloadData.getCF();
	identificationStatus = ocmfPayloadData.getIdStatus();
	identificationLevel = ocmfPayloadData.getIdLevel();

	if (ocmfPayloadData.getIF() != null) {
	    identificationFlags = String.join(", ", ocmfPayloadData.getIF());
	}
	identificationType = ocmfPayloadData.getIT();
	identificationData = ocmfPayloadData.getID();
	tariffText = ocmfPayloadData.getTT();

	final LossCompensation lc = ocmfPayloadData.getLC();
	if (lc != null) {
	    lossCompensation = lc.LR + " " + lc.LU;
	    if (lc.LN != null && lc.LN.length() > 0) {
		lossCompensation += " (" + lc.LN + ")";
	    }
	}
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
    public DetailsList getAdditionalData() {
	final DetailsList addData = new DetailsList();
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
	if (getChargeControllerFirmwareVersion() != null) {
	    addData.put(Translator.get("app.verify.ocmf.chargeControllerFirmwareVersion"),
		    getChargeControllerFirmwareVersion());
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
	if (getTariffText() != null) {
	    addData.put(Translator.get("app.verify.ocmf.tariffText"), getTariffText());
	}
	if (lossCompensation != null) {
	    addData.put(Translator.get("app.verify.powerline.resistance"), lossCompensation);
	}
	if (ocmfPayloadData.getRD() != null) {
	    int count = 1;
	    for (final Reading reading : ocmfPayloadData.getRD()) {
		final String time = reading.getTM() != null ? reading.getTM() : "-";
		final String val = time + " " + reading.getRV() + " " + reading.getRU();
		addData.put(Translator.get("app.view.single.value") + " " + count, val);
		if (reading.getTimeSynchronicity() != null) {
		    final String label = Translator.get("app.verify.ocmf.timesynchronicity");
		    String value = "";
		    if (reading.getEF() != null && reading.getEF().indexOf("t") >= 0) {
			value = Translator.get("app.verify.ocmf.timesynchronicity.error");
		    } else {
			value = Translator.get(reading.getLabelForTimeFlag());
		    }
		    addData.put(String.format("%s %s", label, count), value);
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
	if (this.getMeters() == null) {
	    throw new OCMFValidationException("OCMF entry marked as start does not contain any meter values",
		    "app.verify.ocmf.startcontainsnometers");
	}
	if (stopValue.getMeters() == null) {
	    throw new OCMFValidationException("OCMF entry marked as stop does not contain any meter values",
		    "app.verify.ocmf.stopcontainsnometers");
	}
	final OCMFVerifiedData otherOCMFData = (OCMFVerifiedData) stopValue;
	final boolean stopContainsStart = stopValue.getMeters().stream()
		.anyMatch(meter -> meter.getType() != null && meter.getType().equals(Meter.Type.START));
	if (stopContainsStart) {
	    throw new OCMFValidationException("OCMF entry marked as stop contains a start value",
		    "app.verify.ocmf.stopcontainsstart");
	}
	final boolean startContainsStop = this.getMeters().stream()
		.anyMatch(meter -> meter.getType() != null && meter.getType().equals(Meter.Type.STOP));
	if (startContainsStop) {
	    throw new OCMFValidationException("OCMF entry marked as start contains a stop value",
		    "app.verify.ocmf.startcontainsstop");
	}
	final List<Reading> combinedReadings = new ArrayList<>();
	combinedReadings.addAll(ocmfPayloadData.getRD());
	combinedReadings.addAll(otherOCMFData.ocmfPayloadData.getRD());
	return checkLawIntegrityForReadings(combinedReadings, ocmfPayloadData.getIdLevel(),
		otherOCMFData.ocmfPayloadData.getIdLevel());
    }

    public boolean checkLawIntegrityForTransaction() throws ValidationException, RegulationLawException {
	return checkLawIntegrityForReadings(ocmfPayloadData.getRD(), ocmfPayloadData.getIdLevel(), null);
    }

    private boolean checkLawIntegrityForReadings(List<? extends Reading> readings, String idLevelStart,
	    String idLevelStop) throws OCMFValidationException, RegulationLawException {
	Reading startValue = null;
	Reading stopValue = null;

	String previousST = null;
	String previousRU = null;

	final List<? extends Reading> lawRelevantReadings = filterLawRelevantReadings(readings);
	for (final Reading reading : lawRelevantReadings) {
	    if (reading.getST() == null) {
		if (previousST == null) {
		    throw new OCMFValidationException("Missing mandatory ST parameter for first reading.",
			    "error.values.missing.st");
		} else {
		    reading.setST(previousST);
		}
	    } else {
		previousST = reading.getST();
	    }

	    if (reading.getRU() == null) {
		if (previousRU == null) {
		    throw new OCMFValidationException("Missing mandatory RU parameter for first reading.",
			    "error.values.missing.ru");
		} else {
		    reading.setRU(previousRU);
		}
	    } else {
		previousRU = reading.getRU();
	    }

	    if (reading.isStartTransaction()) {
		if (startValue != null) {
		    throw new OCMFValidationException("Cannot verify contains multiple start values",
			    "error.values.toomany.start");
		}
		startValue = reading;
	    }
	    if (reading.isStopTransaction()) {
		if (stopValue != null) {
		    throw new OCMFValidationException("Cannot verify contains multiple stop values",
			    "error.values.toomany.stop");
		}
		stopValue = reading;
	    }
	}
	if (startValue == null) {
	    throw new OCMFValidationException("Cannot verify contains no start values in reading",
		    "error.values.no.start.meter.values");
	}
	if (stopValue == null) {
	    throw new OCMFValidationException("Cannot verify contains no stop values in readings",
		    "error.values.no.stop.meter.values");
	}

	if (startValue.getRV() > stopValue.getRV()) {
	    throw new RegulationLawException("Meter value of start is higher than meter value of stop",
		    "app.verify.law.conform.meter.wrong");
	}
	if (startValue.getTimestamp() != null && stopValue.getTimestamp() != null
		&& startValue.getTimestamp().isAfter(stopValue.getTimestamp())) {
	    throw new RegulationLawException("Meter timestamp of start is after timestamp of stop",
		    "app.verify.law.conform.meter.time.wrong");
	}
	final List<String> contractStatusErrors = Arrays.asList("MISMATCH", "INVALID", "OUTDATED", "UNKNOWN");
	if (idLevelStart != null && contractStatusErrors.contains(idLevelStart.trim())) {
	    throw new RegulationLawException("Error on reading contract id",
		    "app.verify.law.conform.timesynchronicity.wrong");
	}
	if (idLevelStop != null && contractStatusErrors.contains(idLevelStop.trim())) {
	    throw new RegulationLawException("Error on reading contract id",
		    "app.verify.law.conform.timesynchronicity.wrong");
	}
	if (startValue.getST() == null || (!startValue.getST().equals("G") || !stopValue.getST().equals("G"))) {
	    throw new RegulationLawException("Meter error code present", "app.verify.law.conform.meterstatus.wrong");
	}
	if (startValue.getTimeSynchronicity() == null
		|| (startValue.getTimeSynchronicity().equals("R") && !stopValue.getTimeSynchronicity().equals("R"))) {
	    throw new RegulationLawException("Time synchronicity wrong",
		    "app.verify.law.conform.timesynchronicity.wrong");
	}
	if (stopValue.getEI() != null && !stopValue.getEI().equals(startValue.getEI())) {
	    throw new RegulationLawException("Event counter of start and stop differs",
		    "app.verify.law.conform.eventcounter.differs");
	}
	checkErrorFlag(startValue);
	checkErrorFlag(stopValue);

	return true;
    }

    private List<? extends Reading> filterLawRelevantReadings(List<? extends Reading> readings) {
	final List<? extends Reading> compensated = readings.stream().filter(this::isCompensated).toList();
	if (!compensated.isEmpty()) {
	    return compensated;
	}
	final List<? extends Reading> lawRelevant = readings.stream().filter(this::isLawRelevant).toList();
	return lawRelevant;
    }

    private boolean isLawRelevant(Reading reading) {
	return getObisCode(reading)
		.map(obis -> (obis.getA() == 1 && isCLawRelevant(obis.getC()) && obis.getD() == 8)
			&& (obis.getF() == 0 || obis.getF() == 0x200 || obis.getF() == 0x255 || obis.getF() == 0xFF))
		.orElse(false);
    }

    private boolean isCLawRelevant(int value) {
	return value == 1 || value == 0x98 || value == 0x9E || (value >= 0xB0 && value <= 0xC7);
    }

    private boolean isCompensated(Reading reading) {
	return getObisCode(reading).map(obis -> (obis.getA() == 1 && isCCompensated(obis.getC()) && obis.getD() == 8))
		.orElse(false);
    }

    private boolean isCCompensated(int value) {
	return value == 0x98 || value == 0xB1 || value == 0xB3 || value == 0xC1 || value == 0xC3;
    }

    private Optional<OBISCode> getObisCode(Reading reading) {
	final String ri = reading.getRI();
	if (ri != null) {
	    try {
		return Optional.of(new OBISCode(ri));
	    } catch (final Exception e) {

	    }
	}
	return Optional.empty();
    }

    private void checkErrorFlag(Reading reading) throws RegulationLawException {
	if (reading.getEF() != null) {
	    if (reading.getEF().indexOf("E") >= 0) {
		throw new RegulationLawException("Error flag set on energy",
			"app.verify.law.conform.error.flag.energy");
	    }
	    // Time "t" error flag is not law-relevant in this case.
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

    public String getChargeControllerFirmwareVersion() {
	return chargeControllerFirmwareVersion;
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

    public String getTariffText() {
	return tariffText;
    }

    public boolean containsCompleteTransaction() {
	return ocmfPayloadData.containsCompleteTransaction();
    }
}
