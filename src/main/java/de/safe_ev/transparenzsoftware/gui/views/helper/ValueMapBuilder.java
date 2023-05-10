package de.safe_ev.transparenzsoftware.gui.views.helper;

import java.beans.Transient;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

public class ValueMapBuilder {

    public final static BigInteger NO_TRANSACTION_KEY = BigInteger.valueOf(-1);

    @Transient
    public static Map<BigInteger, List<ValueIndexHolder>> buildTransactionMap(Values values) throws InvalidInputException {
        HashMap<BigInteger, List<ValueIndexHolder>> result = new HashMap<>();
        int index = 0;
        for (Value value : values.getValues()) {
            if (value.getTransactionId() == null) {
                if (!result.containsKey(BigInteger.valueOf(-1))) {
                    result.put(BigInteger.valueOf(-1), new ArrayList<>());
                }
                result.get(BigInteger.valueOf(-1)).add(new ValueIndexHolder(value, index));
            } else {
                if (!result.containsKey(value.getTransactionId())) {
                    result.put(value.getTransactionId(), new ArrayList<>());
                }
                for (ValueIndexHolder otherValue : result.get(value.getTransactionId())) {
                    if (otherValue.getValue().getSignedData().getFormatAsVerificationType() != value.getSignedData().getFormatAsVerificationType()) {
                        String errMsg = String.format("Same transaction id %d for different formats on", value.getTransactionId());
                        throw new InvalidInputException(errMsg, "app.view.error.same.transaction.id.diff.format");
                    }
                }
                result.get(value.getTransactionId()).add(new ValueIndexHolder(value, index));
            }
            index++;
        }
        return result;
    }


}
