package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.safe_ev.transparenzsoftware.verification.xml.OffsetDateTimeAdapter;

import java.time.OffsetDateTime;

@XmlRootElement(name = "ChargingProcess")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargingProcess {

    @XmlElement(name = "ServerId")
    private String serverId;

    @XmlElement(name = "PublicKey")
    private String publicKey;

    @XmlElement(name = "MeteringPoint")
    private String meteringPoint;

    @XmlElement(name = "SiteAddress")
    private SiteAddress siteAddress;

    @XmlElement(name = "CustomerIdent")
    private String customerIdent;

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    @XmlElement(name = "TimestampCustomerIdent")
    private OffsetDateTime timestampCustomerIdent;

    @XmlElement(name = "MeasurementStart")
    private Measurement measurementStart;

    @XmlElement(name = "MeasurementEnd")
    private Measurement measurementEnd;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMeteringPoint() {
        return meteringPoint;
    }

    public void setMeteringPoint(String meteringPoint) {
        this.meteringPoint = meteringPoint;
    }

    public SiteAddress getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getCustomerIdent() {
        return customerIdent;
    }

    public void setCustomerIdent(String customerIdent) {
        this.customerIdent = customerIdent;
    }

    public OffsetDateTime getTimestampCustomerIdent() {
        return timestampCustomerIdent;
    }

    public void setTimestampCustomerIdent(OffsetDateTime timestampCustomerIdent) {
        this.timestampCustomerIdent = timestampCustomerIdent;
    }

    public Measurement getMeasurementStart() {
        return measurementStart;
    }

    public void setMeasurementStart(Measurement measurementStart) {
        this.measurementStart = measurementStart;
    }

    public Measurement getMeasurementEnd() {
        return measurementEnd;
    }

    public void setMeasurementEnd(Measurement measurementEnd) {
        this.measurementEnd = measurementEnd;
    }
}

