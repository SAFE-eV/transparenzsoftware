package de.safe_ev.transparenzsoftware.verification.xml;

import jakarta.xml.bind.annotation.*;

import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;

@XmlRootElement(name = "signedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignedData {

    @XmlAttribute
    private String format;

    @XmlAttribute
    private String encoding;

    @XmlValue
    private String value;

    public SignedData() {
    }

    public SignedData(VerificationType format, EncodingType encoding, String value) {
        this.format = format.name();
        this.encoding = encoding.getCode();
        this.value = value;
    }

    public String getFormat() {
        return format;
    }

    public VerificationType getFormatAsVerificationType() {
        if (format == null) {
            return null;
        }
        try {
            return VerificationType.valueOf(format.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public EncodingType getEncodingType() {
        return EncodingType.fromCode(encoding);
    }

    public String getValue() {
        return value != null ? value.trim() : null;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void validate(boolean enforceTypeChecking) throws InvalidInputException {
        if (enforceTypeChecking) {
            try {
                VerificationType.valueOf(format);
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid encoding supplied", "error.values.signeddata.invalid.encoding", e);
            }
            if (EncodingType.fromCode(encoding) == null) {
                throw new InvalidInputException("Invalid encoding type supplied", "error.values.signeddata.invalid.format");
            }
        }
        if (this.value == null || value.trim().isEmpty()) {
            throw new InvalidInputException("Empty value provided", "error.values.signeddata.empty.value");
        }
    }
}
