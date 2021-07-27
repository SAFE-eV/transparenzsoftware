package com.hastobe.transparenzsoftware.verification.format.alfen;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.OffsetDateTimeAdapter;
import com.hastobe.transparenzsoftware.verification.xml.VerifiedData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class AlfenVerifiedData extends VerifiedData {

    public static final String MESSAGE_CODE_METER_ERROR = "app.verify.alfen.status.metererroroccurred";
    private boolean meterError;
    private boolean adapterError;
    private boolean meterStartMismatch;

    private String identifier;
    private String type;
    private String blobVersion;
    private String publicKey;

    private String adapterId;
    private String adapterFirmwareVersion;
    private String adapterFirmwareChecksum;
    private String meterId;
    private String status;
    private long secondIndex;
    private OffsetDateTime timestamp;
    private String obisId;
    private String unit;
    private String scalar;
    private long value;
    private String uid;
    private long sessionId;
    private long paging;
    private String signature;
    private EncodingType encodingType;
    private List<Meter> meters;

    /**
     * Necessary for JAXB
     */
    public AlfenVerifiedData() {
    }

    public AlfenVerifiedData(AlfenSignature signatureData, EncodingType encodingType) {
        identifier = signatureData.getIdentifier();
        type = signatureData.getType();
        blobVersion = signatureData.getBlobVersion();
        publicKey = signatureData.getPublicKey();

        adapterId = Utils.toFormattedHex(signatureData.getAdapterId());
        adapterFirmwareVersion = new String(signatureData.getAdapterFirmwareVersion());
        adapterFirmwareChecksum = Utils.toFormattedHex(signatureData.getAdapterFirmwareChecksum());
        meterId = Utils.toFormattedHex(signatureData.getMeterId());
        status = Utils.toFormattedHex(signatureData.getStatus());
        if (signatureData.getStatus() != null && signatureData.getStatus().length == 4) {
            meterError = Utils.areBitSet(signatureData.getStatus(), new int[]{1, 2});
            adapterError = Utils.areBitSet(signatureData.getStatus(), new int[]{16, 30, 31});
            meterStartMismatch = Utils.areBitSet(signatureData.getStatus(), new int[]{26});
        } else {
            adapterError = false;
            meterError = false;
        }
        secondIndex = signatureData.getSecondIndexAsLong();
        timestamp = signatureData.getTimestampAsLocalDate();
        obisId = Utils.toFormattedHex(signatureData.getObisId());
        unit = Utils.toFormattedHex(signatureData.getUnit());
        scalar = Utils.toFormattedHex(signatureData.getScalar());
        value = signatureData.getValueAsLong();
        uid = new String(signatureData.getUid(), StandardCharsets.UTF_8);
        sessionId = signatureData.getSessionIdAsLong();
        paging = signatureData.getPagingAsLong();
        signature = Utils.toFormattedHex(signatureData.getSignature());
        this.encodingType = encodingType;
        meters = new ArrayList<>();
        double kwhMeterValue = value != 0 ? value * Math.pow(10, signatureData.getScalar()) / 1000 : 0;
        meters.add(new Meter(kwhMeterValue, timestamp, signatureData.getScalar()));
    }

    @Override
    public List<Meter> getMeters() {
        return meters;
    }

    @Override
    public String getFormat() {
        return VerificationType.ALFEN.name();
    }

    @Override
    public HashMap<String, Object> getAdditionalData() {
        HashMap<String, Object> addData = new HashMap<>();
        String publicKey = getPublicKey() != null ? getPublicKey().toLowerCase() : "";
        addData.put(Translator.get("app.verify.alfen.publicKey"), Utils.splitStringToGroups(publicKey, 4));
        addData.put(Translator.get("app.verify.alfen.adapterId"), getAdapterId());
        addData.put(Translator.get("app.verify.alfen.adapterFirmwareVersion"), getAdapterFirmwareVersion());
        addData.put(Translator.get("app.verify.alfen.adapterFirmwareChecksum"), getAdapterFirmwareChecksum());
        addData.put(Translator.get("app.verify.alfen.meterId"), getMeterId());
        addData.put(Translator.get("app.verify.alfen.status"), getStatus());
        if (getMeters().size() > 0) {
            OffsetDateTime offsetDateTime = getMeters().get(0).getTimestamp();
            addData.put(Translator.get("app.view.timestamp"),
                    OffsetDateTimeAdapter.formattedDateTime(offsetDateTime));
        }

        //show errors of status info
        try {
            calculateMeterError();
            calculateAdapterError();
            calculateMeterreadingMismatchError();
        } catch (RegulationLawException e) {
            addData.put(Translator.get("app.verify.alfen.statusinfo"), e.getLocalizedMessage());
        }

        addData.put(Translator.get("app.verify.alfen.secondIndex"), getSecondIndex());
        addData.put(Translator.get("app.verify.alfen.obisId"), getObisId());
        addData.put(Translator.get("app.verify.alfen.unit"), getUnit());
        addData.put(Translator.get("app.verify.alfen.scalar"), getScalar());
        addData.put(Translator.get("app.verify.alfen.UID"), getUid());
        addData.put(Translator.get("app.verify.alfen.sessionId"), getSessionId());
        addData.put(Translator.get("app.verify.alfen.paging"), getPaging());
        addData.put(Translator.get("app.verify.alfen.signature"), getSignature());
        return addData;
    }

    @Override
    public boolean lawConform(VerifiedData other) throws RegulationLawException, ValidationException {
        if (!(other instanceof AlfenVerifiedData)) {
            throw new FormatComparisonException();
        }
        AlfenVerifiedData otherData = (AlfenVerifiedData) other;

        if (getSessionId() != otherData.getSessionId()) {
            throw new RegulationLawException("Session id differs between values", "app.verify.law.conform.session.id.differs");
        }
        if (getPaging() >= otherData.getPaging()) {
            throw new RegulationLawException("Pagination is of start is higher or equal than the pagination of stop", "app.verify.law.conform.pagination.wrong");
        }
        if (value > otherData.value) {
            throw new RegulationLawException("Meter value of start is higher than meter value of stop", "app.verify.law.conform.meter.wrong");
        }
        return true;
    }


    /**
     * Internal helper method to check if a meter error has happened
     */
    public void calculateMeterError() throws RegulationLawException {
        if (meterError) {
            throw new RegulationLawException(
                    String.format("Meter error occured"),
                    MESSAGE_CODE_METER_ERROR
            );
        }
    }

    /**
     * Internal helper method to check if a adapter error has happened
     */
    public void calculateAdapterError() throws RegulationLawException {
        if (adapterError) {
            throw new RegulationLawException(
                    String.format("Adapter error occured"),
                    MESSAGE_CODE_METER_ERROR
            );
        }
    }

    /**
     * Internal helper method to check if a adapter error has happened
     */
    public void calculateMeterreadingMismatchError() throws RegulationLawException {
        if (meterStartMismatch) {
            throw new RegulationLawException(
                    String.format("Meterreading mismatch error occured"),
                    MESSAGE_CODE_METER_ERROR
            );
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    public String getBlobVersion() {
        return blobVersion;
    }

    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public String getEncoding() {
        return encodingType.getCode();
    }

    public String getAdapterId() {
        return adapterId;
    }

    public String getMeterId() {
        return meterId;
    }

    public String getStatus() {
        return status;
    }

    public long getSecondIndex() {
        return secondIndex;
    }

    public String getObisId() {
        return obisId;
    }

    public String getUnit() {
        return unit;
    }

    public String getScalar() {
        return scalar;
    }

    public String getUid() {
        return uid;
    }

    public long getSessionId() {
        return sessionId;
    }

    public long getPaging() {
        return paging;
    }

    public String getSignature() {
        return signature;
    }

    public String getAdapterFirmwareVersion() {
        return adapterFirmwareVersion;
    }

    public String getAdapterFirmwareChecksum() {
        return adapterFirmwareChecksum;
    }
}
