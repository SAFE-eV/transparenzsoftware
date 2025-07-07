package de.safe_ev.transparenzsoftware.verification.format.sml.EDL40;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmuc.jsml.structures.ASNObject;
import org.openmuc.jsml.structures.Integer64;
import org.openmuc.jsml.structures.OctetString;
import org.openmuc.jsml.structures.SmlListEntry;
import org.openmuc.jsml.structures.SmlListType;
import org.openmuc.jsml.structures.SmlMessage;
import org.openmuc.jsml.structures.SmlTime;
import org.openmuc.jsml.structures.SmlTimestamp;
import org.openmuc.jsml.structures.SmlTimestampLocal;
import org.openmuc.jsml.structures.Unsigned16;
import org.openmuc.jsml.structures.Unsigned32;
import org.openmuc.jsml.structures.Unsigned8;
import org.openmuc.jsml.structures.responses.SmlGetListRes;
import org.openmuc.jsml.structures.responses.SmlPublicCloseRes;
import org.openmuc.jsml.structures.responses.SmlPublicOpenRes;
import org.openmuc.jsml.transport.MessageExtractor;

import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLConfig;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLUtils;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;

public class SMLReader {

    private final static Logger LOGGER = LogManager.getLogger(SMLReader.class);

    private static final String TYPE_CONTRACT_ID = "81 82 81 54 01 FF";
    private static final String TYPE_SIGNED_VALUE = "01 00 01 11 00 FF";
    private static final String TYPE_SIGNED_VALUE_2 = "01 00 01 08 00 FF";
    private static final String TYPE_PAGINATION = "81 80 81 71 01 FF";
    private static final String TYPE_MANUFACTURER_SPECIFIC = "81 80 81 61 01 FF";
    private static final String TYPE_SECONDS_INDEX = "81 00 60 08 00 01";
    private static final String TYPE_HIST_VALUE = "01 00 01 11 00 64";
    private static final String TYPE_HIST_VALUE_COMP = "01 00 01 11 00 C8";
    private static final String TYPE_POWERLINE_REST = "00 AF 6C 6C 72 FF";
    private static final String TYPE_STOPWATCH = "00 AF 64 75 72 FF";
    private static final String TYPE_PRICE = "00 AF 70 72 63 FF";
    private static final String TYPE_SIGNATURE_VERSION = "00 AF 73 76 72 FF";

    private static class BitTranslator {
	int b;
	private final int from;

	BitTranslator(int from) {
	    this.from = from;
	}

	BitTranslator from(int bit, int otherBit) {
	    set(bit, from & (1 << otherBit));
	    return this;
	}

	BitTranslator set(int bit, int value) {
	    if (value != 0) {
		b |= (1 << bit);
	    }
	    return this;
	}

	int get() {
	    return b;
	}
    }

    /**
     * Parses a single asn object to a signature data object, we are only interested
     * in the SmlGetListRes objects as those are the ones holding the information
     * about a meter value and the according additional data
     *
     * @param asnObject holds the byte information of the sml message
     * @return SMLSignature or null if it cannot be parsed
     */
    private SMLSignature parseASNObject(ASNObject asnObject) throws ValidationException {
	EDL40Signature parsedSml = null;

	if (asnObject instanceof SmlPublicOpenRes) {
	    final SmlPublicOpenRes openRes = (SmlPublicOpenRes) asnObject;
	    if (SMLConfig.DEBUG_SML_MESSAGES) {
		LOGGER.debug("Found SmlPublicOpenRes message");
	    }
	} else if (asnObject instanceof SmlGetListRes) {
	    if (SMLConfig.DEBUG_SML_MESSAGES) {
		LOGGER.debug("Found SmlGetListRes message");
	    }

	    parsedSml = new EDL40Signature();
	    final SmlGetListRes getListRes = (SmlGetListRes) asnObject;

	    if (SMLConfig.DEBUG_SML_MESSAGES) {
		showDebugInformation(getListRes);
	    }
	    final byte[] originalSignature = getListRes.getListSignature().getValue();
	    // remove the last two bytes as those are logbook bytes FNN spec page 42
	    parsedSml.setBytesLog(originalSignature[originalSignature.length - 2],
		    originalSignature[originalSignature.length - 1]);

	    parsedSml.setProvidedSignature(originalSignature);

	    // fetch the server id 10 bytes long
	    parsedSml.setServerId(getListRes.getServerId().getValue());
	    // each listEntry should have 4 entries
	    for (final SmlListEntry smlEntry : getListRes.getValList().getValListEntry()) {
		LOGGER.debug("verify smlEntry " + smlEntry.getObjName());
		if (smlEntry.getObjName() == null) {
		    LOGGER.warn("Entry without an asnObject name found");
		    continue;
		}
		// have a look at the object name, we might need additional information from
		// that
		switch (smlEntry.getObjName().toHexString()) {
		case TYPE_CONTRACT_ID: // 1
		    if (smlEntry.getValue().getChoice() instanceof OctetString) {
			final OctetString contractId = (OctetString) smlEntry.getValue().getChoice();
			parsedSml.setContractId(contractId.getValue(), true);
		    }
		    if (smlEntry.getValTime().getChoice() instanceof SmlTimestampLocal) {
			parsedSml.setTimestampContractId(
				SMLUtils.parseSmlTimestamp((SmlTimestampLocal) smlEntry.getValTime().getChoice()));
		    }

		    break;
		case TYPE_SIGNED_VALUE: // 2
		    // the obis id is the name of the object name
		    final OctetString obisIDElement = smlEntry.getObjName();
		    parsedSml.setObisNr(obisIDElement.getValue());

		    // the actual value
		    final Long wh = getUIntOrLong(smlEntry.getValue().getChoice());
		    if (wh != null) {
			setScalerAndMeter(parsedSml, smlEntry, wh);
		    }
		    // the timestamp is stored without a the local time in account
		    // but in the calculation the timestamp is taken as it is
		    // on the station so add that to the value
		    // the setTimestamp method will take care of switching LSB and MSB
		    if (smlEntry.getValTime().getChoice() instanceof SmlTimestampLocal) {
			parsedSml.setTimestamp(
				SMLUtils.parseSmlTimestamp((SmlTimestampLocal) smlEntry.getValTime().getChoice()));
		    }
		    // the set status will take care of taking only the LSB
		    final ASNObject statusChoice = smlEntry.getStatus().getChoice();
		    if (statusChoice instanceof Unsigned32) {
			int status = ((Unsigned32) statusChoice).getVal();
			if (parsedSml.isEmoc()) {
			    status = transformStatus(status);
			}
			parsedSml.setStatus(status);
		    }
		    break;
		case TYPE_SIGNED_VALUE_2: // 2
		    // the obis id is the name of the object name
		    final OctetString obisIDElement2 = smlEntry.getObjName();
		    parsedSml.setObisNr(obisIDElement2.getValue());

		    // the actual value
		    final Integer64 wh2 = (Integer64) smlEntry.getValue().getChoice();

		    // the unit (What ours or similar) 1 byte
		    setScalerAndMeter(parsedSml, smlEntry, wh2.getVal());

		    // the timestamp is stored without a the local time in account
		    // but in the calculation the timestamp is taken as it is
		    // on the station so add that to the value
		    // the setTimestamp method will take care of switching LSB and MSB
		    if (smlEntry.getValTime().getChoice() instanceof SmlTimestampLocal) {
			parsedSml.setTimestamp(
				SMLUtils.parseSmlTimestamp((SmlTimestampLocal) smlEntry.getValTime().getChoice()));
		    } else if (smlEntry.getValTime().getChoice() instanceof SmlTimestamp) {
			parsedSml.setTimestamp(
				SMLUtils.parseSmlTimestamp((SmlTimestamp) smlEntry.getValTime().getChoice()));
		    }
		    final Unsigned32 secondsIndex2 = new Unsigned32(708606);// (Unsigned32) 708606;
		    parsedSml.setSecondsIndex(secondsIndex2.getVal());
		    final Unsigned32 paginationData2 = new Unsigned32(10);
		    parsedSml.setPagination(paginationData2.getVal());

		    // the set status will take care of taking only the LSB
		    final ASNObject statusChoice2 = smlEntry.getStatus().getChoice();
		    if (statusChoice2 instanceof Unsigned32) {
			int status = ((Unsigned32) statusChoice2).getVal();
			if (parsedSml.isEmoc()) {
			    status = transformStatus(status);
			}
			parsedSml.setStatus(status);
		    }
		    break;
		case TYPE_PAGINATION:
		    final Long pagination = getUIntOrLong(smlEntry.getValue().getChoice());
		    if (pagination != null) {
			parsedSml.setPagination(pagination.intValue());
		    } else {
			throw new SMLValidationException("pagination data does not contain a unsigned integer");
		    }
		    break;
		case TYPE_SECONDS_INDEX:
		    if (!(smlEntry.getValue().getChoice() instanceof SmlListType)) {
			throw new SMLValidationException("Time data entry does not contain a smlListType");
		    }
		    final SmlListType smlListType = (SmlListType) smlEntry.getValue().getChoice();
		    if (!(smlListType.getChoice() instanceof SmlTime)) {
			throw new SMLValidationException("SmlListtype data does not contain a time entry");
		    }
		    final SmlTime smlTime = (SmlTime) smlListType.getChoice();
		    if (!(smlTime.getChoice() instanceof Unsigned32)) {
			throw new SMLValidationException("SmlTime dose not contain secondsIndex");
		    }
		    final Unsigned32 secondsIndex = (Unsigned32) smlTime.getChoice();
		    parsedSml.setSecondsIndex(secondsIndex.getVal());
		    break;
		case TYPE_MANUFACTURER_SPECIFIC:
		    // we do not care about this type at the moment.
		    break;
		case TYPE_HIST_VALUE:
		    final Long histValue = getUIntOrLong(smlEntry.getValue().getChoice());
		    parsedSml.setHistoricalValue(histValue);
		    break;
		case TYPE_HIST_VALUE_COMP:
		    final Long histValueComp = getUIntOrLong(smlEntry.getValue().getChoice());
		    parsedSml.setHistoricalValueComp(histValueComp);
		    break;
		case TYPE_POWERLINE_REST:
		    final Long powerlineResistance = getUIntOrLong(smlEntry.getValue().getChoice());
		    parsedSml.setPowerlineResistance(powerlineResistance.intValue());
		    break;
		case TYPE_STOPWATCH:
		    final Long stopwatch = getUIntOrLong(smlEntry.getValue().getChoice());
		    parsedSml.setStopwatch(stopwatch.intValue());
		    break;
		case TYPE_PRICE:
		    final Long price = getUIntOrLong(smlEntry.getValue().getChoice());
		    parsedSml.setPrice(price.intValue());
		    break;
		case TYPE_SIGNATURE_VERSION:
		    final Long signatureVersion = getUIntOrLong(smlEntry.getValue().getChoice());
		    LOGGER.info("Signature Version: " + signatureVersion);
		    parsedSml.setVersion(signatureVersion.intValue());
		    break;
		default:
		    LOGGER.warn("Unknown element " + smlEntry.getObjName().toHexString() + " = " + smlEntry.getValue());

		}

	    }

	} else if (asnObject instanceof SmlPublicCloseRes) {
	    final SmlPublicCloseRes closeRes = (SmlPublicCloseRes) asnObject;
	    if (SMLConfig.DEBUG_SML_MESSAGES) {
		LOGGER.debug("Found SmlPublicCloseRes message");
	    }
	} else {
	    LOGGER.warn("Unknown sml object found: " + asnObject.getClass().getSimpleName());
	}
	return parsedSml;
    }

    private byte transformStatus(int val) {
	final BitTranslator b = new BitTranslator(val);
	b.from(0, 17).from(3, 31).from(4, 16).from(5, 11).from(6, 9).from(7, 8);
	return (byte) b.get();
    }

    private Long getUIntOrLong(ASNObject choice) {
	if (choice instanceof Integer64) {
	    return ((Integer64) choice).getVal();
	} else if (choice instanceof Unsigned8) {
	    return (long) ((Unsigned8) choice).getVal();
	} else if (choice instanceof Unsigned32) {
	    return ((Unsigned32) choice).getLongValue();
	} else if (choice instanceof Unsigned16) {
	    return Long.valueOf(((Unsigned16) choice).getVal());
	}
	return null;
    }

    private void setScalerAndMeter(EDL40Signature parsedSml, SmlListEntry smlEntry, long wh)
	    throws SMLValidationException {
	// the unit (What ours or similar) 1 byte
	parsedSml.setUnit(smlEntry.getUnit().getVal());
	// scaler 1 byte
	parsedSml.setScaler(smlEntry.getScaler().getVal());
	// the meter position is stored in the value
	parsedSml.setMeterPosition(wh);
    }

    private void showDebugInformation(SmlGetListRes getListRes) {
	LOGGER.debug("ServerID:\t" + getListRes.getServerId());
	LOGGER.debug("ListName:\t" + getListRes.getListName());
	LOGGER.debug("Signature:\t" + getListRes.getListSignature());
	final SmlTime time = getListRes.getActSensorTime();
	LOGGER.debug("ActSensorTime:\t" + time.getChoice());
	for (final SmlListEntry smlEntry : getListRes.getValList().getValListEntry()) {
	    LOGGER.debug("------");

	    LOGGER.debug("ObjName:\t" + smlEntry.getObjName());
	    LOGGER.debug("Value:\t" + smlEntry.getValue());
	    LOGGER.debug("Unit:\t" + smlEntry.getUnit());
	    LOGGER.debug("Signature:\t" + smlEntry.getValueSignature());
	    LOGGER.debug("Scaler:\t" + smlEntry.getScaler());
	    LOGGER.debug("Status:\t" + smlEntry.getStatus().getChoice());
	    if (smlEntry.getValTime() != null && smlEntry.getValTime().getChoice() instanceof SmlTimestampLocal) {
		LOGGER.debug("Timestamp:\t" + ((SmlTimestampLocal) smlEntry.getValTime().getChoice()).getTimestamp());
	    }
	}
    }

    /**
     * Parsing raw sml bytes
     *
     * @param payloadBytes - byte array containg the raw sml data
     * @return list of parsed signature data
     * @throws IOException if reading of sml failed
     */
    private SMLSignature parseRawSMLBytes(byte[] payloadBytes) throws IOException, ValidationException {
	final MessageExtractor messageExtractor = createMessageExtractor(payloadBytes);
	final DataInputStream smlFileDis = convertMessageExtractorToSmlMessages(messageExtractor);
	// loop over sml messages as their might be several message parts in it
	final List<SmlMessage> messages = new ArrayList<SmlMessage>();
	while (smlFileDis.available() > 0) {
	    // create a sml message out of a byte array
	    final SmlMessage message = new SmlMessage();
	    message.decode(smlFileDis);
	    messages.add(message);
	    // the actual object is stored in the choice object
	    final ASNObject obj = message.getMessageBody().getChoice();
	    final SMLSignature SMLSignature = parseASNObject(obj);
	    if (SMLSignature != null) {
		return SMLSignature;
	    }

	}
	return null;
    }

    private DataInputStream convertMessageExtractorToSmlMessages(MessageExtractor messageExtractor) throws IOException {
	final byte[] smlFile = messageExtractor.getSmlMessage();
	final ByteArrayInputStream bais = new ByteArrayInputStream(smlFile);
	return new DataInputStream(bais);
    }

    /**
     * Creates a MessageExtractor out of a byte array (potential SML message)
     *
     * @param payloadBytes data as a byte array
     * @return MessageExtractor instance holding sml data
     * @throws IOException if message cannot be read a IOException will be thrown
     *                     (Timeout)
     */
    private MessageExtractor createMessageExtractor(byte[] payloadBytes) throws IOException {
	// build the according data streams for java
	final BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(payloadBytes));
	final DataInputStream dis = new DataInputStream(bis);

	// message extractor for reading sml data
	// timeout is 300 as we do not have to wait for a network delay
	return new MessageExtractor(dis, 300);
    }

    /**
     * Parses a sml data to a sml signature
     *
     * @param smlData
     * @return
     * @throws ValidationException
     */
    public SMLSignature parsePayloadData(byte[] smlData) throws ValidationException {
	try {
	    final SMLSignature smlSignature = parseRawSMLBytes(smlData);
	    if (smlSignature == null) {
		throw new SMLValidationException("Could not read sml data incomplete data", "error.sml.incomplete");
	    }
	    // will throw an sml validation exception if check fails
	    smlSignature.isDataComplete();

	    if (smlSignature.getUnit() != 30) {
		throw new SMLValidationException("Invalid unit present in sml data", "error.sml.invalid.unit");
	    }
	    return smlSignature;
	} catch (final IOException e) {
	    throw new SMLValidationException("Could not read sml data, invalid format", e);
	}

    }

}
