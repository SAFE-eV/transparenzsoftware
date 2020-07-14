package com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly.embedded;

import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.ValidationException;

import javax.xml.bind.annotation.XmlTransient;

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
