package com.hastobe.transparenzsoftware.verification.xml;

import com.hastobe.transparenzsoftware.verification.input.InvalidInputException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;

@XmlRootElement(name = "value")
@XmlAccessorType(XmlAccessType.FIELD)
public class Value {

    public static final String CONTEXT_BEGIN = "Transaction.Begin";
    public static final String CONTEXT_END = "Transaction.End";

    @XmlAttribute
    private String context;

    @XmlAttribute
    private BigInteger transactionId;


    private PublicKey publicKey;
    private SignedData signedData;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public SignedData getSignedData() {
        return signedData;
    }

    public void setSignedData(SignedData signedData) {
        this.signedData = signedData;
    }

    public void validate(boolean enforceTypeChecking) throws InvalidInputException {
        if (this.publicKey != null) {
            this.publicKey.validate(enforceTypeChecking);
        }
        if (this.signedData == null) {
            throw new InvalidInputException("No signed data supplied", "error.values.no.publickey");
        } else {
            this.signedData.validate(enforceTypeChecking);
        }
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public BigInteger getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(BigInteger transactionId) {
        this.transactionId = transactionId;
    }
}
