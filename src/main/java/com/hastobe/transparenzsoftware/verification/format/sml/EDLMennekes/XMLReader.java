package com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes;

import com.hastobe.transparenzsoftware.Constants;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.Billing;
import com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.ChargingProcess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class XMLReader {

    private final static Logger LOGGER = LogManager.getLogger(XMLReader.class);

    private Unmarshaller unmarshaller;

    public XMLReader() {
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(Billing.class, ChargingProcess.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            unmarshaller = null;
            LOGGER.error("Error on creating unmarshaller", e);
        }
    }

    public Billing readFromString(String xml) throws ValidationException {
        StringReader reader = new StringReader(enforceXMLStartingString(xml));
        try {
            return (Billing) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            LOGGER.error("JAXB error on loading mennekes file", e);
            throw new ValidationException("Could not read mennekes xml data");
        }
    }

    public ChargingProcess readChargingProcessFromString(String xml) throws ValidationException {
        String enforcedNamespace = enforceXMLStartingString(xml);
        if(!enforcedNamespace.contains(Constants.NAMESPACE_MENNEKES)){
            String replacement = String.format("<ChargingProcess xmlns=\"%s\"", Constants.NAMESPACE_MENNEKES );
            enforcedNamespace = enforcedNamespace.replace("<ChargingProcess", replacement);
        }
        StringReader reader = new StringReader(enforcedNamespace);
        try {
            return (ChargingProcess) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            LOGGER.error("JAXB error on loading mennekes file", e);
            throw new ValidationException("Could not read mennekes xml data");
        } catch (ClassCastException e) {
            LOGGER.error("Class cast error in loading mennekes file", e);
            throw new ValidationException("Could not read mennekes xml data");
        }
    }

        private String enforceXMLStartingString(String xml){
            if (!xml.trim().startsWith("<?xml")) {
                xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
            }
        return xml;
    }
}
