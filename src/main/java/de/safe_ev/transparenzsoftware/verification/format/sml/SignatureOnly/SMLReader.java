package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.embedded.SignedMeterValue;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;

public class SMLReader {

    private final static Logger LOGGER = LogManager.getLogger(SMLReader.class);

    private Unmarshaller unmarshaller;

    public SMLReader() {
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(SignedMeterValue.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            unmarshaller = null;
            LOGGER.error("Error on creating marshaller", e);
        }
    }

    /**
     * Parses a base 64 string containing signature data for a sml
     * message in the Lastenheft specified order (page 48).
     * of the raw
     *
     * @param signedMeterValue xml data
     * @return SMLSignature out of the BaseArray
     * @throws ValidationException
     */
    public SMLSignature parseSMLSigXml(SignedMeterValue signedMeterValue) throws ValidationException {


        try {
            byte[] payloadBytes = signedMeterValue.getEncodedMeterValue().getValueEncoded();
            byte[] signatureBytes = signedMeterValue.getMeterValueSignature().getValueEncoded();

            SMLSignatureOnly smlData = new SMLSignatureOnly(payloadBytes);
            smlData.setProvidedSignature(signatureBytes);
            return smlData;
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid argument exception in sml base 64", e);
            throw new ValidationException("Invalid base 64 data applied", "error.encoding.base64.invalid", e);
        }
    }


    public SignedMeterValue readFromString(String xml) throws ValidationException {
        StringReader reader = new StringReader(xml);
        try {
            return (SignedMeterValue) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new ValidationException("Could not read sml data", e);
        }
    }

}
