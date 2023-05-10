package de.safe_ev.transparenzsoftware.gui.views.helper;

import java.math.BigInteger;

public class NodeHeadingHolder {

    private final BigInteger transactionId;
    private final String label;

    public NodeHeadingHolder(BigInteger transactionId, String label) {
        this.transactionId = transactionId;
        this.label = label;
    }

    public BigInteger getTransactionId() {
        return transactionId;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
