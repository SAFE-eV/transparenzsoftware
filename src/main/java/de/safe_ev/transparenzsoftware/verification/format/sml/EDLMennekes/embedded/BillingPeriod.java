package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.safe_ev.transparenzsoftware.verification.xml.OffsetDateTimeAdapter;

import java.time.OffsetDateTime;
import java.util.List;

@XmlRootElement(name = "BillingPeriod")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingPeriod {

    @XmlElement(name = "BillingNo")
    private String billingNo;

    @XmlElement(name = "ObisCode")
    private String obisCode;

    @XmlElement(name = "Title")
    private String title;

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    @XmlElement(name = "Begin")
    private OffsetDateTime begin;

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    @XmlElement(name = "End")
    private OffsetDateTime end;

    @XmlElementWrapper(name = "ChargingProcesses")
    @XmlElement(name = "ChargingProcess")
    private List<ChargingProcess> chargingProcesses;

    public String getBillingNo() {
        return billingNo;
    }

    public void setBillingNo(String billingNo) {
        this.billingNo = billingNo;
    }

    public String getObisCode() {
        return obisCode;
    }

    public void setObisCode(String obisCode) {
        this.obisCode = obisCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OffsetDateTime getBegin() {
        return begin;
    }

    public void setBegin(OffsetDateTime begin) {
        this.begin = begin;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }

    public List<ChargingProcess> getChargingProcesses() {
        return chargingProcesses;
    }

    public void setChargingProcesses(List<ChargingProcess> chargingProcesses) {
        this.chargingProcesses = chargingProcesses;
    }
}
