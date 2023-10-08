package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.embedded;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.safe_ev.transparenzsoftware.verification.ValidationException;

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
