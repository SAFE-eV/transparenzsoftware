package com.hastobe.transparenzsoftware.verification.format.sml;


import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.xml.LocalDateTimeAdapter;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.VerifiedData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMLVerifiedData extends VerifiedData {

    private String secondsIndex;
    private VerificationType verificationType;
    private EncodingType encodingType;
    private String publicKey;
    private String serverId;
    private String status;
    private String obisId;
    private String unit;
    private String scaler;
    private double meter;
    private String logbookHigh;
    private String logbookLow;
    private String signature;
    private String customerId;
    private int pagination;
    private OffsetDateTime timestampCustomerId;
    private List<Meter> meters;

    /**
     * Necessary for JAXB
     */
    public SMLVerifiedData() {

    }

    public SMLVerifiedData(SMLSignature smlSignature, VerificationType verificationType, EncodingType encodingType, String publicKey) {
        this.verificationType = verificationType;
        this.encodingType = encodingType;
        this.publicKey = publicKey;
        if (smlSignature != null) {
            serverId = Utils.toFormattedHex(smlSignature.getServerId());
            meters = new ArrayList<>();
            meter = smlSignature.getMeterPositionAsLong();

            //calculate kWh
            meter = meter != 0 ? meter * Math.pow(10, smlSignature.getScaler()) / 1000 : 0;

            meters.add(new Meter(meter, smlSignature.getTimestampAsDate()));
            status = Utils.toFormattedHex(smlSignature.getStatus());
            secondsIndex = Utils.toFormattedHex(smlSignature.getSecondsIndex());
            pagination = new BigInteger(Utils.reverseByteOrder(smlSignature.getPagination())).intValue();
            obisId = Utils.toFormattedHex(smlSignature.getObisNr());
            unit = Utils.toFormattedHex(smlSignature.getUnit());
            scaler = Utils.toFormattedHex(smlSignature.getScaler());
            logbookHigh = Integer.toString(smlSignature.getBytesLog()[0]);
            logbookLow = Integer.toString(smlSignature.getBytesLog()[1]);
            signature = Utils.toFormattedHex(smlSignature.getProvidedSignature());
            customerId = Utils.toFormattedHex(Utils.trimPaddingAtEnd(smlSignature.getContractId()));
            timestampCustomerId = smlSignature.getTimestampContractIdAsDate();
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
    public HashMap<String, Object> getAdditionalData() {
        HashMap<String, Object> additionalData = new HashMap<>();
        additionalData.put(Translator.get("app.verify.sml.customerId"), getCustomerId());
        additionalData.put(Translator.get("app.verify.sml.secondsIndex"), getSecondsIndex());
        additionalData.put(Translator.get("app.verify.sml.logbook"), getLogbookHigh() + " " + getLogbookLow());
        additionalData.put(Translator.get("app.verify.sml.unit"), getUnit());
        additionalData.put(Translator.get("app.verify.sml.obisId"), getObisId());
        additionalData.put(Translator.get("app.verify.sml.scaler"), getScaler());
        additionalData.put(Translator.get("app.verify.sml.serverId"), getServerId());
        additionalData.put(Translator.get("app.verify.sml.signature"), getSignature());
        additionalData.put(Translator.get("app.verify.sml.status"), getStatus());
//        additionalData.put(Translator.get("app.verify.sml.timestamp"), OffsetDateTimeAdapter.formattedDateTime(getTimestamp()));
        additionalData.put(Translator.get("app.verify.sml.timestampCustomer"), LocalDateTimeAdapter.formattedDateTime(getTimestampCustomerId().toLocalDateTime()));
        additionalData.put(Translator.get("app.verify.sml.pagination"), getPagination());

        return additionalData;
    }

    @Override
    public boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException {
        if (!(stopValue instanceof SMLVerifiedData)) {
            throw new FormatComparisonException();
        }
        SMLVerifiedData stopData = (SMLVerifiedData) stopValue;
        if (getLogbookHigh() == null || stopData.getLogbookHigh() == null) {
            throw new RegulationLawException("Logbook differs between transactions", "app.verify.law.conform.logbook.differs");
        }
        if (getLogbookLow() == null || stopData.getLogbookLow() == null) {
            throw new RegulationLawException("Logbook differs between transactions", "app.verify.law.conform.logbook.differs");
        }
        if (!getLogbookHigh().equals(stopData.getLogbookHigh()) || !getLogbookLow().equals(stopData.getLogbookLow())) {
            throw new RegulationLawException("Logbook differs between transactions", "app.verify.law.conform.logbook.differs");
        }
        if (getCustomerId() == null || !getCustomerId().equals(stopData.getCustomerId())) {
            throw new RegulationLawException("Customer ids differs between two values", "app.verify.law.conform.customer.id.differs");
        }
        if (getPagination() >= stopData.getPagination()) {
            throw new RegulationLawException("Pagination is of start is higher or equal than the pagination of stop", "app.verify.law.conform.pagination.wrong");
        }
        if (getMeter() > stopData.getMeter()) {
            throw new RegulationLawException("Meter value of start is higher than meter value of stop", "app.verify.law.conform.meter.wrong");
        }
        return true;
    }

    public String getServerId() {
        return serverId;
    }

    public String getStatus() {
        return status;
    }

    public String getObisId() {
        return obisId;
    }

    public String getUnit() {
        return unit;
    }

    public String getScaler() {
        return scaler;
    }

    public double getMeter() {
        return meter;
    }

    public String getLogbookHigh() {
        return logbookHigh;
    }

    public String getLogbookLow() {
        return logbookLow;
    }

    public String getSignature() {
        return signature;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OffsetDateTime getTimestampCustomerId() {
        return timestampCustomerId;
    }

    public String getSecondsIndex() {
        return secondsIndex;
    }

    public int getPagination() {
        return pagination;
    }
}
