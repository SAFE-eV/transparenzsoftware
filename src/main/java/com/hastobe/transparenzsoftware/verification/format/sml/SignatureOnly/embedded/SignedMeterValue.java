package com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly.embedded;

import com.hastobe.transparenzsoftware.verification.ValidationException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "signedMeterValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignedMeterValue {

    @XmlElement
    private PublicKey publicKey;

    @XmlElement
    private SignatureMethod signatureMethod;

    @XmlElement
    private EncodedMeterValue encodedMeterValue;

    @XmlElement
    private MeterValueSignature meterValueSignature;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public SignatureMethod getSignatureMethod() {
        return signatureMethod;
    }

    public void setSignatureMethod(SignatureMethod signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    public EncodedMeterValue getEncodedMeterValue() {
        return encodedMeterValue;
    }

    public void setEncodedMeterValue(EncodedMeterValue encodedMeterValue) {
        this.encodedMeterValue = encodedMeterValue;
    }

    public MeterValueSignature getMeterValueSignature() {
        return meterValueSignature;
    }

    public void setMeterValueSignature(MeterValueSignature meterValueSignature) {
        this.meterValueSignature = meterValueSignature;
    }

    public boolean validate() throws ValidationException {
        boolean valueEncoded = getEncodedMeterValue() != null && getEncodedMeterValue().getValueEncoded() != null;
        boolean signatureEncoded = getMeterValueSignature() != null && getMeterValueSignature().getValueEncoded() != null;
        if (valueEncoded && signatureEncoded && getSignatureMethod() != null) {
            return true;
        }
        return false;
    }
}
