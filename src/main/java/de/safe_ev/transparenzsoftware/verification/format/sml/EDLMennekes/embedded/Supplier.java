package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Supplier")
@XmlAccessorType(XmlAccessType.FIELD)
public class Supplier {

    @XmlElement(name = "ZipCode")
    private String zipCode;

    @XmlElement(name = "Name1")
    private String name1;

    @XmlElement(name = "Street")
    private String street;

    @XmlElement(name = "Phone")
    private String phone;

    @XmlElement(name = "Fax")
    private String fax;

    @XmlElement(name = "MailAddress")
    private String mailAddress;

    @XmlElement(name = "Town")
    private String town;

    @XmlElement(name = "MailAddressLinkText")
    private String mailAddressLinkText;

    @XmlElement(name = "MailSubject")
    private String mailSubject;

    @XmlElement(name = "MailBody")
    private String mailBody;

    @XmlElement(name = "WebAddress")
    private String webAddress;

    @XmlElement(name = "WebAddressLinkText")
    private String webAddressLinkText;

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getMailAddressLinkText() {
        return mailAddressLinkText;
    }

    public void setMailAddressLinkText(String mailAddressLinkText) {
        this.mailAddressLinkText = mailAddressLinkText;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getWebAddressLinkText() {
        return webAddressLinkText;
    }

    public void setWebAddressLinkText(String webAddressLinkText) {
        this.webAddressLinkText = webAddressLinkText;
    }
}
