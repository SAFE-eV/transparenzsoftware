package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SiteAddress")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteAddress {

    @XmlElement(name = "ZipCode")
    private String zipCode;

    @XmlElement(name = "Street")
    private String street;

    @XmlElement(name = "Town")
    private String town;

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
