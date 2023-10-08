package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.embedded;

import jakarta.xml.bind.annotation.XmlTransient;

import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;

@XmlTransient
public abstract class EncodingValue {

    public abstract String getEncoding();
    public abstract String getValue();

    /**
     * Cleanup a value string from whitespaces, newlines and tabs
     * @return cleaned string
     */
    public String getCleanedValue(){
        String value = getValue();
        value = value.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "");
        return value;
    }

    public byte[] getValueEncoded() throws ValidationException {
        EncodingType encoding = EncodingType.fromCode(getEncoding());
        if(encoding == null || getValue() == null){
            throw new ValidationException("Could not find a decoder", "error.unknown.encoding");
        }
        String value = getCleanedValue();
        byte[] payloadBytes = new byte[0];
        try {
            payloadBytes = EncodingType.decode(encoding, value);
        } catch (DecodingException e) {
            throw new ValidationException("Could not decode a data", "error.unknown.encoding");
        }
        return payloadBytes;
    }
}
