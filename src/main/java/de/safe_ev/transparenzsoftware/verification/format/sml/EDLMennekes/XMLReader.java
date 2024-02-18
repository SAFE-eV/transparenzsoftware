package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes;

import java.io.StringReader;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Constants;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.Billing;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.ChargingProcess;

public class XMLReader {

	private final static Logger LOGGER = LogManager.getLogger(XMLReader.class);

	private Unmarshaller unmarshaller;

	public XMLReader() {
		JAXBContext jc = null;
		try {
			jc = JAXBContext.newInstance(Billing.class, ChargingProcess.class);
			unmarshaller = jc.createUnmarshaller();
		} catch (final JAXBException e) {
			unmarshaller = null;
			LOGGER.error("Error on creating unmarshaller", e);
		}
	}

	public Billing readFromString(String xml) throws ValidationException {
		final StringReader reader = new StringReader(enforceXMLStartingString(xml));
		try {
			return (Billing) unmarshaller.unmarshal(reader);
		} catch (final JAXBException e) {
			LOGGER.error("JAXB error on loading mennekes file", e);
			throw new ValidationException("Could not read mennekes xml data");
		}
	}

	public ChargingProcess readChargingProcessFromString(String xml, boolean verbose) throws ValidationException {
		String enforcedNamespace = enforceXMLStartingString(xml);
		if (!enforcedNamespace.contains(Constants.NAMESPACE_MENNEKES)) {
			final String replacement = String.format("<ChargingProcess xmlns=\"%s\"", Constants.NAMESPACE_MENNEKES);
			enforcedNamespace = enforcedNamespace.replace("<ChargingProcess", replacement);
		}
		final StringReader reader = new StringReader(enforcedNamespace);
		try {
			return (ChargingProcess) unmarshaller.unmarshal(reader);
		} catch (final JAXBException e) {
			if (verbose) {
				LOGGER.debug("JAXB error on loading mennekes file", e);
			}
			throw new ValidationException("Could not read mennekes xml data");
		} catch (final ClassCastException e) {
			if (verbose) {
				LOGGER.debug("Class cast error in loading mennekes file", e);
			}
			throw new ValidationException("Could not read mennekes xml data");
		}
	}

	private String enforceXMLStartingString(String xml) {
		if (!xml.trim().startsWith("<?xml")) {
			xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
		}
		return xml;
	}
}
