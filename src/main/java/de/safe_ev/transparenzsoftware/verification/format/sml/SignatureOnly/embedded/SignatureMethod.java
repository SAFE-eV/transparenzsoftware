package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.embedded;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "signatureMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignatureMethod {

    @XmlValue
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
