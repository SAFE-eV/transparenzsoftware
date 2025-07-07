package de.safe_ev.transparenzsoftware.output;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.*;

import java.io.StringWriter;
import java.time.Duration;
import java.util.List;

/**
 * Class for creating the according out put of a verification result
 */
public class Output {

    private final Marshaller marshaller;
    private final Values values;
    private List<VerificationResult> verificationResults;

    public Output(Class[] verifiedDataClasses, List<VerificationResult> verificationResults, Values values) throws JAXBException {
        this.values = values;
        //register classes to render otherwise jaxb does not know about child classes
        Class[] classes = new Class[verifiedDataClasses.length + 1];
        classes[0] = Results.class;
        System.arraycopy(verifiedDataClasses, 0, classes, 1, verifiedDataClasses.length);

        JAXBContext jc = JAXBContext.newInstance(classes);
        marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        this.verificationResults = verificationResults;
    }

    /**
     * Creates an xml based
     *
     * @return
     * @throws JAXBException
     */
    public String createXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        marshaller.marshal(createResults(), sw);
        return sw.toString();
    }

    protected Results createResults() {
        Results results = new Results();
        for (int i = 0; i < verificationResults.size(); i++) {
            VerificationResult verificationResult = this.verificationResults.get(i);
            Result result = new Result();

            if (values.getValues().size() < i) {
                Value value = values.getValues().get(i);
                result.setPublicKey(value.getPublicKey());
                result.setSignedData(value.getSignedData());
            }

            List<Meter> meters = verificationResult.getMeters();
            String preci = meters.get(0).getScalingFormat();
            if (verificationResult.isTransactionResult() && verificationResult.getTransactionId() != null) {
                result.setTransactionId(verificationResult.getTransactionId());
                result.setMeterDiff(String.format(preci+" kWh", Meter.getDifference(meters)));
                Duration timeDiff = Meter.getTimeDiff(meters);
                result.setTimeDiff(Utils.formatDuration(timeDiff));
            }
            result.setMeters(meters);
            result.setStatus(verificationResult.isVerified() ? "Verified" : "Failed");
            result.setVerifiedData(verificationResult.getVerifiedData());
            if (!verificationResult.getErrorMessages().isEmpty()) {
                result.setErrorMessage(verificationResult.getErrorMessages().get(0).getLocalizedMessage());
            }
            results.getResults().add(result);
        }
        return results;
    }
}
