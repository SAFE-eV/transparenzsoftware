package de.safe_ev.transparenzsoftware.verification.xml;

import jakarta.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.List;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

    private String status;
    private SignedData signedData;
    private PublicKey publicKey;
    private VerifiedData verifiedData;
    private String errorMessage;
    private String meterDiff;
    private String timeDiff;

    @XmlElementWrapper(name = "meters")
    @XmlElement(name = "meter")
    public List<Meter> meters;


    @XmlAttribute
    private BigInteger transactionId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SignedData getSignedData() {
        return signedData;
    }

    public void setSignedData(SignedData signedData) {
        this.signedData = signedData;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public VerifiedData getVerifiedData() {
        return verifiedData;
    }

    public void setVerifiedData(VerifiedData verifiedData) {
        this.verifiedData = verifiedData;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public BigInteger getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(BigInteger transactionId) {
        this.transactionId = transactionId;
    }

    public String getMeterDiff() {
        return meterDiff;
    }

    public void setMeterDiff(String meterDiff) {
        this.meterDiff = meterDiff;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public List<Meter> getMeters() {
        return meters;
    }

    public void setMeters(List<Meter> meters) {
        this.meters = meters;
    }
}
