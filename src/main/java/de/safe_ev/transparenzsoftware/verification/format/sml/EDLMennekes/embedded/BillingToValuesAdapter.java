package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.xml.PublicKey;
import de.safe_ev.transparenzsoftware.verification.xml.SignedData;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

import java.io.StringWriter;
import java.util.ArrayList;

public class BillingToValuesAdapter {

    public static Values convertBilling(Marshaller marshaller, Billing billing) throws BillingAdapterException {
        Values values = new Values();
        values.setValues(new ArrayList<>());
        for (BillingPeriod billingPeriod : billing.getBillingPeriods()) {
            for (ChargingProcess chargingProcess : billingPeriod.getChargingProcesses()) {
                StringWriter stringWriter = new StringWriter();
                try {
                    marshaller.marshal(chargingProcess, stringWriter);
                    Value value = new Value();
                    value.setSignedData(new SignedData(VerificationType.EDL_40_MENNEKES, EncodingType.PLAIN, stringWriter.toString()));
                    value.setPublicKey(new PublicKey(EncodingType.HEX, chargingProcess.getPublicKey()));
                    values.getValues().add(value);
                } catch (JAXBException e) {
                    throw new BillingAdapterException("Could not transform Mennekes format to values", "error.xml.mennekes.transform");
                }
            }
        }
        return values;
    }
}
