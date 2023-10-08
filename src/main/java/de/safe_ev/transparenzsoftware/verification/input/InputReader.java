package de.safe_ev.transparenzsoftware.verification.input;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.Billing;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.BillingAdapterException;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.BillingToValuesAdapter;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Reads in our defined input data
 */
public class InputReader {

    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public InputReader() {
        try {
            JAXBContext jc = JAXBContext.newInstance(Values.class, Billing.class);
            unmarshaller = jc.createUnmarshaller();
            marshaller = jc.createMarshaller();
        } catch (JAXBException e) {
            // wrap it in runtime this exceptions happens
            // only if an invalid object was passed.
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the defined input data from a file
     *
     * @param file
     * @return
     * @throws InvalidInputException if an invalid xml was delivered
     */
    public Values readFile(File file) throws InvalidInputException {
        try {
            Object unmarshalled = unmarshaller.unmarshal(file);
            Values value;
            if (unmarshalled instanceof Billing) {
                value = BillingToValuesAdapter.convertBilling(marshaller, (Billing) unmarshalled);
            } else {
                value = (Values) unmarshalled;
            }
            String fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            value.setRawContent(fileContent);
            return value;
        } catch (JAXBException | IOException e) {
            throw new InvalidInputException("Cannot read input file", "error.input.string.noxml", e);
        } catch (BillingAdapterException e) {
            throw new InvalidInputException(e.getMessage(), e.getLocalizedMessageKey(), e);
        }
    }

    /**
     * Reads the defined input data from a string
     *
     * @param data
     * @return
     * @throws InvalidInputException if an invalid xml was delivered
     */
    public Values readString(String data) throws InvalidInputException {
        try {
            StringReader stringReader = new StringReader(data);
            Values value = (Values) unmarshaller.unmarshal(stringReader);
            value.setRawContent(data);
            return value;
        } catch (Exception e) {
            throw new InvalidInputException("Cannot read input string", "error.input.string.noxml", e);
        }
    }
}
