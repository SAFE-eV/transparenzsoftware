package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Billing", namespace = "http://www.mennekes.de/Mennekes.EdlVerification.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class Billing {

    @XmlElement(name = "Customer")
    private Customer customer;

    @XmlElement(name = "Supplier")
    private Supplier supplier;

    @XmlElementWrapper(name = "BillingPeriods")
    @XmlElement(name = "BillingPeriod")
    private List<BillingPeriod> billingPeriods;

//    @XmlElementWrapper(name = "EventLogItems")
//    @XmlElement(name = "EventLogItem")
//    private List<EventLogItem> eventLogItems;


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<BillingPeriod> getBillingPeriods() {
        return billingPeriods;
    }

    public void setBillingPeriods(List<BillingPeriod> billingPeriods) {
        this.billingPeriods = billingPeriods;
    }
}
