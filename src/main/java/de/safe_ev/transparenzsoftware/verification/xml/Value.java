package de.safe_ev.transparenzsoftware.verification.xml;

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;

@XmlRootElement(name = "value")
@XmlAccessorType(XmlAccessType.FIELD)
public class Value {

	public static final String CONTEXT_BEGIN = "Transaction.Begin";
	public static final String CONTEXT_END = "Transaction.End";

	@XmlAttribute
	private String context;

	@XmlAttribute
	private BigInteger transactionId;

	private EncodedData encodedData;

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
		if (publicKey != null) {
			publicKey.validate(enforceTypeChecking);
		}
		if (signedData == null) {
			if (encodedData == null) {
				throw new InvalidInputException("No signed or encoded data supplied", "error.values.no.publickey");
			}
			encodedData.validate(enforceTypeChecking);
		} else {
			if (encodedData != null) {
				throw new InvalidInputException("Both signed and encoded data supplied",
						"error.values.both.encoded.and.signed");
			}
			signedData.validate(enforceTypeChecking);
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

	public EncodedData getEncodedData() {
		return encodedData;
	}

	public void setEncodedData(EncodedData encodedData) {
		this.encodedData = encodedData;
	}

	public boolean hasSignedData() {
		return signedData != null;
	}
}
