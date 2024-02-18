package de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmuc.jsml.structures.*;
import org.openmuc.jsml.structures.responses.SmlGetListRes;
import org.openmuc.jsml.structures.responses.SmlPublicCloseRes;
import org.openmuc.jsml.structures.responses.SmlPublicOpenRes;
import org.openmuc.jsml.transport.MessageExtractor;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLConfig;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLUtils;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40.IsaEDL40Signature;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IsaSMLReader {
    private final static Logger LOGGER = LogManager.getLogger(IsaSMLReader.class);

    private static final String TYPE_CONTRACT_ID = "81 82 81 54 01 FF";             //Contract-ID
    private static final String TYPE_START_EC_VALUE = "01 00 01 08 00 80";          //Start EC
    private static final String TYPE_PAGINATION = "81 80 C7 F0 40 FF";              //Pagination
    private static final String TYPE_ACTUAL_EC_VALUE = "01 00 01 08 00 FF";         //Actual EC value (Signed value is item)
    private static final String TYPE_ESTH = "81 80 81 61 01 FF";                    //ESTH

    private static final String TYPE_MANUFACTURER_SPECIFIC = "81 80 81 61 01 FF";           //???
    private static final String TYPE_SECONDS_INDEX = "81 00 60 08 00 01";                   //???


    /**
     * Parses a single asn object to a signature data object,
     * we are only interested in the SmlGetListRes objects
     * as those are the ones holding the information about
     * a meter value and the according additional data
     *
     * @param asnObject holds the byte information of the sml message
     * @return SMLSignature or null if it cannot be parsed
     */
    private SMLSignature parseASNObject(ASNObject asnObject) throws ValidationException {
        IsaEDL40Signature parsedSml = null;
        if (asnObject instanceof SmlPublicOpenRes) {
            SmlPublicOpenRes openRes = (SmlPublicOpenRes) asnObject;
            if (SMLConfig.DEBUG_SML_MESSAGES) {
                LOGGER.debug("Found SmlPublicOpenRes message");
            }
        } else if (asnObject instanceof SmlGetListRes) {
            if (SMLConfig.DEBUG_SML_MESSAGES) {
                LOGGER.debug("Found SmlGetListRes message");
            }

            parsedSml = new IsaEDL40Signature();
            SmlGetListRes getListRes = (SmlGetListRes) asnObject;

            if (SMLConfig.DEBUG_SML_MESSAGES) {
                showDebugInformation(getListRes);
            }

            //fetch the server id 10 bytes long
            parsedSml.setServerId(getListRes.getServerId().getValue());
            parsedSml.setListNameOfRes(getListRes.getListName().getValue());
            byte[] originalSignature = getListRes.getListSignature().getValue();
            //take the last two bytes as those are logbook bytes FNN spec page 42
            parsedSml.setBytesLog(originalSignature[originalSignature.length - 2], originalSignature[originalSignature.length - 1]);
            byte[] croppedSignature = Arrays.copyOfRange(originalSignature, 0, originalSignature.length-2);
            parsedSml.setDataSignature(croppedSignature);

            for (SmlListEntry smlEntry : getListRes.getValList().getValListEntry()) {
                LOGGER.debug("verify smlEntry " + smlEntry.getObjName());
                if (smlEntry.getObjName() == null) {
                    LOGGER.warn("Entry without an asnObject name found");
                    continue;
                }

                switch (smlEntry.getObjName().toHexString()) {
                    case TYPE_CONTRACT_ID: //1
                        if (smlEntry.getValue().getChoice() instanceof OctetString) {
                            OctetString contractId = (OctetString) smlEntry.getValue().getChoice();
                            parsedSml.setContractId(contractId.getValue(), true);
                        }

                        break;
                    case TYPE_START_EC_VALUE: //2
                        if (smlEntry.getValTime().getChoice() instanceof SmlTimestampLocal) {
                            parsedSml.setStartEcTimestamp(SMLUtils.parseSmlTimestamp((SmlTimestampLocal) smlEntry.getValTime().getChoice()));
                        }
                        if (smlEntry.getValTime().getChoice() instanceof SmlTimestamp) {
                            parsedSml.setStartEcTimestamp(SMLUtils.parseSmlTimestamp((SmlTimestamp) smlEntry.getValTime().getChoice()));
                        }

                        ASNObject strtStatus = smlEntry.getStatus().getChoice();
                        if (strtStatus instanceof Unsigned64) {
                            byte[] sArr = Utils.longToByteArray(((Unsigned64) strtStatus).getVal());
                            parsedSml.setStartEcStatus(sArr);
                        }

                        parsedSml.setStartEcObisId(smlEntry.getObjName().getValue());
                        //unit
                        parsedSml.setStartEcUnit(smlEntry.getUnit().getVal());
                        //scaler 1 byte
                        parsedSml.setStartlEcScaler(smlEntry.getScaler().getVal());

                        //EnergyCounter -> the actual value
                        Integer64 sEcWh = (Integer64) smlEntry.getValue().getChoice();
                        parsedSml.setStartEcValue(sEcWh.getVal());

                        break;
                    case TYPE_ACTUAL_EC_VALUE: //2
                        OctetString objName2 = smlEntry.getObjName();
                        byte[] data =  smlEntry.getObjName().getValue();
                        parsedSml.setActualEcObisId(smlEntry.getObjName().getValue());
                        if (smlEntry.getValTime().getChoice() instanceof SmlTimestamp) {
                            parsedSml.setActualEcTimestamp(SMLUtils.parseSmlTimestamp((SmlTimestamp) smlEntry.getValTime().getChoice()));
                        }
                        ASNObject actStatus = smlEntry.getStatus().getChoice();
                        if (actStatus instanceof Unsigned64) {
                            byte[] sArr = Utils.longToByteArray(((Unsigned64) actStatus).getVal());
                            parsedSml.setActualEcStatus(sArr);
                        }
                        //unit
                        parsedSml.setActualEcUnit(smlEntry.getUnit().getVal());
                        //scaler 1 byte
                        parsedSml.setActualEcScaler(smlEntry.getScaler().getVal());

                        //EnergyCounter -> the actual value
                        Integer64 wh2 = (Integer64) smlEntry.getValue().getChoice();
                        parsedSml.setActualEcValue(wh2.getVal());

                        //Signture
                        parsedSml.setActualEcSignature(smlEntry.getValueSignature().getValue());

                        break;
                    case TYPE_PAGINATION:
                        OctetString objName3 = smlEntry.getObjName();
                        if(smlEntry.getValue().getChoice() instanceof Unsigned32){
                            Unsigned32 paginationData = (Unsigned32) smlEntry.getValue().getChoice();
                            parsedSml.setPagination(paginationData.getVal());
                        } else {
                            throw new SMLValidationException("pagination data does not contain a unsigned integer");
                        }
                        break;
                    case TYPE_ESTH:
                        if (smlEntry.getValue().getChoice() instanceof OctetString) {
                            OctetString esth = (OctetString) smlEntry.getValue().getChoice();
                            parsedSml.setEsth(esth.getValue());
                        }

                        break;
                    default:
                        LOGGER.warn("Unknown element " + smlEntry.getObjName().toHexString());

                }
            }

        } else if (asnObject instanceof SmlPublicCloseRes) {
            SmlPublicCloseRes closeRes = (SmlPublicCloseRes) asnObject;
            if (SMLConfig.DEBUG_SML_MESSAGES) {
                LOGGER.debug("Found SmlPublicCloseRes message");
            }
        } else {
            LOGGER.warn("Unknown sml object found: " + asnObject.getClass().getSimpleName());
        }

        return parsedSml;
    }

    private void showDebugInformation(SmlGetListRes getListRes) {
        LOGGER.debug("ServerID:\t" + getListRes.getServerId());
        LOGGER.debug("ListName:\t" + getListRes.getListName());
        LOGGER.debug("Signature:\t" + getListRes.getListSignature());
        SmlTime time = getListRes.getActSensorTime();
        LOGGER.debug("ActSensorTime:\t" + time.getChoice());
        for (SmlListEntry smlEntry : getListRes.getValList().getValListEntry()) {
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
        MessageExtractor messageExtractor = createMessageExtractor(payloadBytes);
        DataInputStream smlFileDis = convertMessageExtractorToSmlMessages(messageExtractor);
        // loop over sml messages as their might be several message parts in it
        List<SmlMessage> messages = new ArrayList<SmlMessage>();
        while (smlFileDis.available() > 0) {
            //create a sml message out of a byte array
            SmlMessage message = new SmlMessage();
            message.decode(smlFileDis);
            messages.add(message);
            //the actual object is stored in the choice object
            ASNObject obj = message.getMessageBody().getChoice();
            SMLSignature SMLSignature = parseASNObject(obj);
            if (SMLSignature != null) {
                return SMLSignature;
            }

        }
        return null;
    }

    private DataInputStream convertMessageExtractorToSmlMessages(MessageExtractor messageExtractor) throws IOException {
        byte[] smlFile = messageExtractor.getSmlMessage();
        ByteArrayInputStream bais = new ByteArrayInputStream(smlFile);
        return new DataInputStream(bais);
    }

    /**
     * Creates a MessageExtractor out of a byte array (potential SML message)
     *
     * @param payloadBytes data as a byte array
     * @return MessageExtractor instance holding sml data
     * @throws IOException if message cannot be read a IOException will be thrown (Timeout)
     */
    private MessageExtractor createMessageExtractor(byte[] payloadBytes) throws IOException {
        // build the according data streams for java
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(payloadBytes));
        DataInputStream dis = new DataInputStream(bis);

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
            SMLSignature smlSignature = parseRawSMLBytes(smlData);
            if(smlSignature == null){
                throw new SMLValidationException("Could not read sml data incomplete data", "error.sml.incomplete");
            }
            //will throw an sml validation exception if check fails
            smlSignature.isDataComplete();

            if(smlSignature.getUnit() != 30){
                throw new SMLValidationException("Invalid unit present in sml data", "error.sml.invalid.unit");
            }
            return smlSignature;
        } catch (IOException e) {
            throw new SMLValidationException("Could not read sml data, invalid format", e);
        }
    }

}
