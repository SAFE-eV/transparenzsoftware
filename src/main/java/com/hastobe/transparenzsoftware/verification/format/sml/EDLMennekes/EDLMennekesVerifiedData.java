package com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes;


import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.gui.views.helper.DetailsList;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.ChargingProcess;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.OffsetDateTimeAdapter;
import com.hastobe.transparenzsoftware.verification.xml.VerifiedData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class EDLMennekesVerifiedData extends VerifiedData {

    private VerificationType verificationType;
    private EncodingType encodingType;
    private String publicKey;

    private String serverId;
    private String contractId;

    private int secondIndexStart;
    private int secondIndexEnd;
    private String obisIdStart;
    private String obisIdEnd;
    private OffsetDateTime timestampContract;
    private String statusStart;
    private String statusEnd;
    private long eventCounterStart;
    private long eventCounterEnd;

    private int scalerStart;
    private int scalerEnd;
    private String signatureStart;
    private String signatureEnd;
    private int paginationStart;
    private int paginationEnd;
    private List<Meter> meters;
    private double meterStart;
    private double meterStop;

    /**
     * Necessary for JAXB
     */
    public EDLMennekesVerifiedData() {
    }

    public EDLMennekesVerifiedData(ChargingProcess chargingProcess, EDLMennekesSignature start, EDLMennekesSignature stop) {
        this.verificationType = VerificationType.EDL_40_MENNEKES;
        this.encodingType = EncodingType.PLAIN;
        this.publicKey = Utils.clearString(chargingProcess.getPublicKey());
        this.serverId = chargingProcess.getServerId();
        this.contractId = chargingProcess.getCustomerIdent();
        this.meters = new ArrayList<>();
        this.timestampContract = chargingProcess.getTimestampCustomerIdent();
        if (start != null) {
            //calculate kWh
            meterStart = start.getMeterPositionAsLong();
            meterStart = meterStart != 0 ? meterStart * Math.pow(10, start.getScaler()) / 1000 : 0;
            meters.add(new Meter(meterStart, start.getTimestampAsDate(), Meter.Type.START, Meter.TimeSyncType.INFORMATIVE, start.getScaler()));
            obisIdStart = Utils.toFormattedHex(start.getObisNr());
            statusStart = Utils.toFormattedHex(start.getStatus());
            paginationStart = chargingProcess.getMeasurementStart().getPagination();
            scalerStart = chargingProcess.getMeasurementStart().getScaler();
            signatureStart = chargingProcess.getMeasurementStart().getSignature();
            secondIndexStart = chargingProcess.getMeasurementStart().getSecondIndex();
            eventCounterStart = chargingProcess.getMeasurementStart().getEventCounter();
        }
        if (stop != null) {
            //calculate kWh
            meterStop = stop.getMeterPositionAsLong();
            meterStop = meterStop != 0 ? meterStop * Math.pow(10, stop.getScaler()) / 1000 : 0;
            meters.add(new Meter(meterStop, stop.getTimestampAsDate(), Meter.Type.STOP, Meter.TimeSyncType.INFORMATIVE, stop.getScaler()));
            obisIdEnd = Utils.toFormattedHex(stop.getObisNr());
            statusEnd = Utils.toFormattedHex(stop.getStatus());
            paginationEnd = chargingProcess.getMeasurementEnd().getPagination();
            scalerEnd = chargingProcess.getMeasurementEnd().getScaler();
            signatureEnd = chargingProcess.getMeasurementEnd().getSignature();
            secondIndexEnd = chargingProcess.getMeasurementEnd().getSecondIndex();
            eventCounterEnd = chargingProcess.getMeasurementStart().getEventCounter();
        }

    }

    @Override
    public List<Meter> getMeters() {
        return meters;
    }

    @Override
    public String getFormat() {
        return verificationType.name();
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public String getEncoding() {
        return encodingType.getCode();
    }

    @Override
    public DetailsList getAdditionalData() {
    	DetailsList additionalData = new DetailsList();
        additionalData.put(Translator.get("app.verify.sml.customerId"), getContractId());
        additionalData.put(Translator.get("app.verify.sml.timestampCustomer"), OffsetDateTimeAdapter.formattedDateTime(getTimestampContract()));
        additionalData.put(Translator.get("app.verify.sml.serverId"), getServerId());
        additionalData.put(Translator.get("app.verify.sml.publicKey"), getPublicKey());

        String startString = Translator.get("app.verify.start");
        String endString = Translator.get("app.verify.end");
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.obisId"), startString), getObisIdStart());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.secondsIndex"), startString), getSecondIndexStart());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.status"), startString), getStatusStart());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.pagination"), startString), getPaginationStart());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.scaler"), startString), getScalerStart());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.signature"), startString), getSignatureStart());

        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.obisId"), endString), getObisIdEnd());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.secondsIndex"), endString), getSecondIndexEnd());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.status"), endString), getStatusEnd());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.pagination"), endString), getPaginationEnd());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.scaler"), endString), getScalerEnd());
        additionalData.put(String.format("%s (%s)", Translator.get("app.verify.sml.signature"), endString), getSignatureEnd());

        return additionalData;
    }

    @Override
    public boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException {
        throw new ValidationException("EDL Mennekes transaction data can only be verified as one value", "app.verify.mennekes.law.conform.compare");
    }

    public boolean checkLawIntegrityForTransaction() throws RegulationLawException {
        if(getEventCounterStart() != getEventCounterEnd()) {
            throw new RegulationLawException("Event counter of start and stop differs", "app.verify.law.conform.eventcounter.differs");
        }
        if(getPaginationStart() >= getPaginationEnd()){
            throw new RegulationLawException("Pagination is of start is higher or equal than the pagination of stop", "app.verify.law.conform.pagination.wrong");
        }
        if(getMeterStart() > getMeterStop()) {
            throw new RegulationLawException("Meter value of start is higher than meter value of stop", "app.verify.law.conform.meter.wrong");
        }
        return true;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(VerificationType verificationType) {
        this.verificationType = verificationType;
    }

    public EncodingType getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public int getSecondIndexStart() {
        return secondIndexStart;
    }

    public void setSecondIndexStart(int secondIndexStart) {
        this.secondIndexStart = secondIndexStart;
    }

    public int getSecondIndexEnd() {
        return secondIndexEnd;
    }

    public void setSecondIndexEnd(int secondIndexEnd) {
        this.secondIndexEnd = secondIndexEnd;
    }

    public String getObisIdStart() {
        return obisIdStart;
    }

    public void setObisIdStart(String obisIdStart) {
        this.obisIdStart = obisIdStart;
    }

    public String getObisIdEnd() {
        return obisIdEnd;
    }

    public void setObisIdEnd(String obisIdEnd) {
        this.obisIdEnd = obisIdEnd;
    }


    public String getStatusStart() {
        return statusStart;
    }

    public void setStatusStart(String statusStart) {
        this.statusStart = statusStart;
    }

    public String getStatusEnd() {
        return statusEnd;
    }

    public void setStatusEnd(String statusEnd) {
        this.statusEnd = statusEnd;
    }

    public int getScalerStart() {
        return scalerStart;
    }

    public void setScalerStart(int scalerStart) {
        this.scalerStart = scalerStart;
    }

    public int getScalerEnd() {
        return scalerEnd;
    }

    public void setScalerEnd(int scalerEnd) {
        this.scalerEnd = scalerEnd;
    }

    public String getSignatureStart() {
        return signatureStart;
    }

    public void setSignatureStart(String signatureStart) {
        this.signatureStart = signatureStart;
    }

    public String getSignatureEnd() {
        return signatureEnd;
    }

    public void setSignatureEnd(String signatureEnd) {
        this.signatureEnd = signatureEnd;
    }

    public int getPaginationStart() {
        return paginationStart;
    }

    public void setPaginationStart(int paginationStart) {
        this.paginationStart = paginationStart;
    }

    public int getPaginationEnd() {
        return paginationEnd;
    }

    public void setPaginationEnd(int paginationEnd) {
        this.paginationEnd = paginationEnd;
    }

    public OffsetDateTime getTimestampContract() {
        return timestampContract;
    }

    public void setTimestampContract(OffsetDateTime timestampContract) {
        this.timestampContract = timestampContract;
    }

    protected long getEventCounterStart() {
        return eventCounterStart;
    }

    protected void setEventCounterStart(long eventCounterStart) {
        this.eventCounterStart = eventCounterStart;
    }

    protected long getEventCounterEnd() {
        return eventCounterEnd;
    }

    protected void setEventCounterEnd(long eventCounterEnd) {
        this.eventCounterEnd = eventCounterEnd;
    }

    protected double getMeterStart() {
        return meterStart;
    }

    protected void setMeterStart(double meterStart) {
        this.meterStart = meterStart;
    }

    protected double getMeterStop() {
        return meterStop;
    }

    protected void setMeterStop(double meterStop) {
        this.meterStop = meterStop;
    }
}
