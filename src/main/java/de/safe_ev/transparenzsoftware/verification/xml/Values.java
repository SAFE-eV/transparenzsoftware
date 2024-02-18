package de.safe_ev.transparenzsoftware.verification.xml;

import java.beans.Transient;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;

@XmlRootElement(name = "values")
@XmlAccessorType(XmlAccessType.FIELD)
public class Values {

    @XmlElement(name = "value")
    private List<Value> values = null;

    private String rawContent;

    public List<Value> getValues() {
        if (values == null) {
            values = new ArrayList<>();
        }
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    /**
     * Validates the given values
     *
     * @param enforceTypeChecking if true also encoding types will be checked
     * @throws InvalidInputException
     */
    public void validate(boolean enforceTypeChecking) throws InvalidInputException {
        if (values == null || values.isEmpty()) {
            throw new InvalidInputException("No values supplied", "error.values.no.values");
        }
        for (Value value : values) {
            value.validate(enforceTypeChecking);
        }

    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getRawContent() {
        return rawContent;
    }

    @Transient
    public Value findSecondTransaction(int indexOrigin, BigInteger transactionId) {
        for (int i = 0; i < values.size(); i++) {
            Value value = values.get(i);
            if (i != indexOrigin && transactionId.equals(value.getTransactionId())) {
                return value;
            }
        }
        return null;
    }


}
