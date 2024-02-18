package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.embedded;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "publicKey")
@XmlAccessorType(XmlAccessType.FIELD)
public class PublicKey extends EncodingValue {

    @XmlAttribute
    private String encoding;

    @XmlValue
    private String value;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getValue() {
        return value != null ? value.trim() : null;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
