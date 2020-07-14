package com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes.embedded;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLValidationException;
import com.hastobe.transparenzsoftware.verification.xml.OffsetDateTimeAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;

@XmlRootElement(name = "Measurement")
@XmlAccessorType(XmlAccessType.FIELD)
public class Measurement {

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    @XmlElement(name = "TimestampCustomerIdent")
    private OffsetDateTime timestampCustomerIdent;

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    @XmlElement(name = "Timestamp")
    private OffsetDateTime timestamp;

    @XmlElement(name = "Signature")
    private String signature;

    @XmlElement(name = "EventCounter")
    private Long eventCounter;

    @XmlElement(name = "MeterStatus")
    private Integer meterStatus;

    @XmlElement(name = "Value")
    private Long value;

    @XmlElement(name = "Scaler")
    private int scaler;

    @XmlElement(name = "Pagination")
    private int pagination;

    @XmlElement(name = "SecondIndex")
    private int secondIndex;

    public OffsetDateTime getTimestampCustomerIdent() {
        return timestampCustomerIdent;
    }

    public void setTimestampCustomerIdent(OffsetDateTime timestampCustomerIdent) {
        this.timestampCustomerIdent = timestampCustomerIdent;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature != null ? Utils.clearString(signature) : null;
    }

    public byte[] getSignatureAsBytes() throws SMLValidationException {
        try {
            if(getSignature() == null){
                throw new SMLValidationException("Invalid signature provided", "error.sml.mennekes.invalid.signature");
            }
            return EncodingType.hexDecode(getSignature());
        } catch (DecodingException e) {
            throw new SMLValidationException("Invalid signature provided", "error.sml.mennekes.invalid.signature");
        }
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Long getEventCounter() {
        return eventCounter;
    }

    public void setEventCounter(Long eventCounter) {
        this.eventCounter = eventCounter;
    }

    public Integer getMeterStatus() {
        return meterStatus;
    }

    public void setMeterStatus(Integer meterStatus) {
        this.meterStatus = meterStatus;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public int getScaler() {
        return scaler;
    }

    public void setScaler(int scaler) {
        this.scaler = scaler;
    }

    public int getPagination() {
        return pagination;
    }

    public void setPagination(int pagination) {
        this.pagination = pagination;
    }

    public int getSecondIndex() {
        return secondIndex;
    }

    public void setSecondIndex(int secondIndex) {
        this.secondIndex = secondIndex;
    }
}
