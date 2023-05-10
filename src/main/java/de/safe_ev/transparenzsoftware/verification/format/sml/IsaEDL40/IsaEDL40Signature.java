package de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;

import java.nio.ByteBuffer;
import java.security.Security;
import java.time.OffsetDateTime;

public class IsaEDL40Signature extends SMLSignature {

    //List offsets in source array with length. It correlates with specification.
    public final static int SERVER_ID_OFFSET = 0;
    public final static int SERVER_ID_LENGTH = 10;

    public final static int TIMESTAMP_OFFSET = 10;
    public final static int TIMESTAMP_LENGTH = 4;

    public final static int ACTUAL_EC_LSB_SW_OFFSET = 14;   //LSB = 00

    public final static int ACTUAL_EC_OBISID_OFFSET = 15;
    public final static int ACTUAL_EC_OBISID_LENGTH = 6;

    public final static int ACTUAL_EC_UNIT_OFFSET = 21;
    public final static int ACTUAL_EC_SCALER_OFFSET = 22;

    public final static int METER_VALUE_OFFSET = 23;
    public final static int METER_VALUE_OFFSET_LENGTH = 8;

    public final static int LOG_ENTRY_INDEX_OFFSET = 31;
    public final static int LOG_ENTRY_INDEX_LENGTH = 2;

    public final static int ACTUAL_EC_SIGNATURE_OFFSET = 33;
    public final static int ACTUAL_EC_SIGNATURE_LENGTH = 66;

    public final static int CONTRACT_ID_OFFSET = 99;
    public final static int CONTRACT_ID_LENGTH = 128;

    public final static int START_EC_TIMESTAMP_OFFSET = 227;
    public final static int START_EC_TIMESTAMP_LENGTH = 4;

    public final static int ESTH_OFFSET = 231;
    public final static int ESTH_LENGTH = 20;

    public final static int START_EC_LSB_SW_OFFSET = 251;

    public final static int START_EC_OBISID_OFFSET = 252;
    public final static int START_EC_OBISID_LENGTH = 6;

    public final static int START_EC_UNIT_OFFSET = 258;
    public final static int START_EC_SCALER_OFFSET = 259;

    public final static int START_EC_VALUE_OFFSET = 260;
    public final static int START_EC_VALUE_LENGTH = 8;

    public final static int LIST_NAME_OFFSET = 268;
    public final static int LIST_NAME_LENGTH = 6;

    public final static int PAGINATION_OFFSET = 274;
    public final static int PAGINATION_LENGTH = 4;

    public final static int RESERVED_OFFSET = 278;
    public final static int RESERVED_LENGTH = 42;

    private byte[] embeddedSignature;
    private byte[] dataSinature;

    //************** Variables is taken from GetList.Res **********
    //SPos:0  L:10 EPos:9; Server-ID  10 bytes long; Line: 28
    private byte[] serverId;

    //SPos:10  L:4 EPos:13; Timestamp; Line: 88; It is taken from ListEntry with OBIS-ID:  0100010800FF (Actual EC)
    // ??? -> Line: 88   or  Line: 88
    private OffsetDateTime actualEcTimestamp;

    //SPos:14  L:1 EPos:14; Status; Line: 85; It is taken from ListEntry with OBIS-ID:  0100010800FF (Actual EC)
    private byte[] actualEcStatus;

    //SPos:15  L:6 EPos:20; OBIS-ID of Actual EC value; Line: 84; /01 00 01 08 00 FF
    private byte[] actualEcObisId;

    //SPos:21  L:1 EPos:21; Unit 621E; Line: 89;It is taken from ListEntry with OBIS-ID:  0100010800FF (Actual EC)
    private byte actualEcUnit;

    //SPos:22  L:1 EPos:22; Scaler 52FF; Line: 90;It is taken from ListEntry with OBIS-ID:  0100010800FF (Actual EC)
    private byte actualEcScaler;

    //SPos:23  L:8 EPos:30; Actual EC value; Line: 91;It is taken from ListEntry with OBIS-ID:  0100010800FF (Actual EC)
    private byte[] actualEcValue;

    //SPos:31  L:2 EPos:32; LogEntryIndex -> last 2 bytes of listSignature (SML1.04); Line: 97
    private byte[] logEntryIndex;

    //SPos:33  L:66 EPos:98; Signature of actual EcValue;  containing of 64 bytes plus Calibration Log Entry-Index
    private byte[] actualEcSignature;


    //************** Variables were taken from ListEntry with OBIS-ID:  81 82 81 54 01 FF (Contract-ID) **********
    //SPos:99  L:128 EPos:226; Contract-ID If the Contract-ID takes up less than 128 bytes, the missing Bytes at the end are to be filled with „0x00“.
    private byte[] contractId;

    //SPos:227  L:4 EPos:230; Timestamp of Contract-ID
    private OffsetDateTime startEcTimeStamp;

    //SPos:231  L:20 EPos:250; ESTH
    private byte[] esth;

    //SPos:251  L:1 EPos:251; LSB of status word @ setting Contract-ID
    private byte[] startEcStatus;

    //SPos:252  L:6 EPos:257; OBIS-ID: Sum Counter A+ @ setting Contract-ID.
    private byte[] startEcObisId;

    //SPos:258  L:1 EPos:258; from Starting EC; Line: 63
    private byte startEcUnit;

    //SPos:259  L:1 EPos:259; Starting EC; Line: 64
    private byte startEcScaler;

    //SPos:260  L:8 EPos:267; Starting EC -> EC value; Line: 65
    private byte[] startEcValue;

    //SPos:268  L:6 EPos:273; ListName -> OBIS-ID Context: 81 80 81 62 nn FF (02); Line: 29
    private byte[] listNameOfRes;

    //SPos:274  L:4 EPos:277; Pagination; Line: 73
    private byte[] pagination;

    //SPos:278  L:42 EPos:319; Reserved, should always be filled with „0x00“.
    private byte[] reserved;

    public IsaEDL40Signature() {

        reserved = new byte[42];
        for (int i = 0; i < 42; i++)
        {
            reserved[i] = 0x00;
        }
    }

    //**************** Set && Get Get.List_Res  *****************************************
    //Sets the serverId bytes; @param serverId 10 bytes long array; @throws SMLValidationException if serverId is not 10 bytes long
    public void setServerId(byte[] serverId) throws SMLValidationException {
        if (serverId.length != SERVER_ID_LENGTH) {
            throw new SMLValidationException("Server Id was not 10 bytes long");
        }
        this.serverId = serverId;
    }
    //   @return Server id in bytes 10 bytes long
    public byte[] getServerId() {
        return serverId;
    }

    public void setDataSignature(byte[] signature) { this.dataSinature = signature; }
    public byte[] getDataSignature() { return this.dataSinature; }

    //**************** Set && Get actual EC value  *****************************************

    public void setActualEcTimestamp(OffsetDateTime timestamp) throws SMLValidationException {
        this.actualEcTimestamp = timestamp;
    }
    public byte[] getActualEcTimestamp() {
        return timeToBytes(actualEcTimestamp);
        //int seconds = (int) (actualEcTimestamp.toEpochSecond() + actualEcTimestamp.getOffset().getTotalSeconds());
        //return intToBytes(seconds);
    }

    public void setActualEcStatus(byte[] status) { this.actualEcStatus = status; }
    public byte[] getActualEcStatus() {
        return actualEcStatus;
    }

    @Override
    public byte getUnit() { return actualEcUnit; }
    public byte getActualEcUnit() { return actualEcUnit; }
    public void setActualEcUnit(byte unit) {
        this.actualEcUnit = unit;
    }
    public void setActualEcUnit(int unit) {
        this.actualEcUnit = (byte) (unit & 0xFF);
    }

    public void setActualEcScaler(byte scaler) { this.actualEcScaler = scaler;    }
    public byte getActualEcScaler() {
        return actualEcScaler;
    }

    public void setActualEcObisId(byte[] obis) { this.actualEcObisId = obis; }
    public byte[] getActualEcObisId() { return actualEcObisId; }

    //long Actual EC
    public void setActualEcValue(long ecValue)
    {
        this.actualEcValue = longToBytes(ecValue);
    }
    public void setActualEcValue(byte[] ecValue)
    {
        this.actualEcValue = ecValue;
    }
    public byte[] getActualEcValue() {
        return actualEcValue;
    }

    public void setActualEcSignature(byte[] ecSignature) {
        this.actualEcSignature = ecSignature;
    }

    public byte[] getActualEcSignature() {
        return actualEcSignature;
    }
    @Override
    public byte[] getProvidedSignature() {
        return actualEcSignature;
    }

    //**************** Set && Get Contract-ID  *****************************************
    public byte[] getContractId() { return contractId; }
    public void setContractId(byte[] contractId) throws SMLValidationException {
        setContractId(contractId, false);
    }
    public void setContractId(byte[] contractId, boolean fillUp) throws SMLValidationException {
        if (contractId.length < CONTRACT_ID_LENGTH && fillUp) {
            byte[] fillUpData = new byte[CONTRACT_ID_LENGTH];
            System.arraycopy(contractId, 0, fillUpData, 0, contractId.length);
            this.contractId = fillUpData;
        } else if ((fillUp && contractId.length > CONTRACT_ID_LENGTH) || contractId.length != CONTRACT_ID_LENGTH) {
            throw new SMLValidationException("base signature not 128 bytes long");
        } else {
            this.contractId = contractId;
        }
    }

    //**************** Set && Get Start EC  *****************************************
    @Override
    public byte[] getTimestampContractId() {
        return timeToBytes(startEcTimeStamp);
    }
    @Override
    public OffsetDateTime getTimestampContractIdAsDate() {
        return startEcTimeStamp;
    }

    public void setTimestampContractId(OffsetDateTime timestampCustomerId) throws SMLValidationException {
        this.startEcTimeStamp = timestampCustomerId;
    }
    public void setStartEcTimestamp(OffsetDateTime timestamp) throws SMLValidationException {
        this.startEcTimeStamp = timestamp;
    }
    public byte[] getStartEcTimestamp() {
        return timeToBytes(startEcTimeStamp);
        //int seconds = (int) (startEcTimeStamp.toEpochSecond() + startEcTimeStamp.getOffset().getTotalSeconds());
        //return intToBytes(seconds);
    }

    public void setStartEcStatus(byte[] status) { this.startEcStatus = status; }
    public byte[] getStartEcStatus() { return startEcStatus; }

    public void setStartEcObisId(byte[] obis) { this.startEcObisId = obis; }
    public byte[] getStartEcObisId() { return startEcObisId; }

    public byte getStartEcUnit() { return startEcUnit; }
    public void setStartEcUnit(byte unit) {
        this.startEcUnit = unit;
    }
    public void setStartEcUnit(int unit) {
        this.startEcUnit = (byte) (unit & 0xFF);
    }

    public void setStartlEcScaler(byte scaler) { this.startEcScaler = scaler;    }
    public byte getStartEcScaler() { return startEcScaler; }

    public void setStartEcValue(long ecValue)
    {
        this.startEcValue = longToBytes(ecValue);
    }
    public void setStartEcValue(byte[] ecValue)
    {
        this.startEcValue = ecValue;
    }
    public byte[] getStartEcValue() {
        return startEcValue;
    }

    public long getStartEcValueAsLong() {
        return Utils.bytesToLong(Utils.reverseByteOrder(startEcValue));
    }
    //**************** Set && Get methods  *****************************************
    public byte[] getEsth() { return esth; }
    public void setEsth(byte[] esth)  { this.esth = esth;   }

    @Override
    public byte[] getPagination() { return pagination; }
    @Override
    public void setPagination(int pagination) throws SMLValidationException {
        setPagination(Utils.reverseByteOrder(intToBytes(pagination)));
    }
    @Override
    public void setPagination(byte[] pagination) throws SMLValidationException {
        if (pagination.length != PAGINATION_INDEX_LENGTH) {
            throw new SMLValidationException("Pagination index was not 4 bytes long");
        }
        this.pagination = pagination;
    }

    @Override
    public byte[] getBytesLog() { return logEntryIndex; }
    @Override
    public void setBytesLog(byte[] bytesLog) throws SMLValidationException {
        if (bytesLog.length != 2) {
            throw new SMLValidationException("Logbook bytes not 2 bytes long");
        }
        this.logEntryIndex = bytesLog;
    }
    @Override
    public void setBytesLog(byte lowByte, byte highByte) {
        this.logEntryIndex = new byte[]{lowByte, highByte};
    }

    public void setListNameOfRes(byte[] listName)  { this.listNameOfRes = listName;   }
    public byte[] getListNameOfRes() { return listNameOfRes; }
    //****************************************************************************************

    @Override
    public byte[] getTimestamp() {
    	if (actualEcTimestamp == null) return null;
        return timeToBytes(actualEcTimestamp);
    }

    @Override
    public OffsetDateTime getTimestampAsDate() {
        return actualEcTimestamp;
    }

    public void setLogEntryIndex(byte[] index)
    {
        this.logEntryIndex =index;
    }

    @Override
    public byte[] buildExtendedSignatureData() {
        byte[] signature = new byte[LENGTH_SIGNATURE];

        //SPos:0  L:10 EPos:9; Stated in spec as Geräteeinzelidentifikation (<=> Server-ID)  10 bytes long;
        System.arraycopy(serverId, 0, signature, SERVER_ID_OFFSET, SERVER_ID_LENGTH);
        //SPos:10  L:4 EPos:13; Timestamp at the time of recording of measurement value (4 Byte unsigned)
        System.arraycopy(getActualEcTimestamp(), 0, signature, TIMESTAMP_OFFSET, TIMESTAMP_LENGTH);
        //SPos:14  L:1 EPos:14; LSB of Status word @ recording the meter value -> Takes from 01 00 01 08 00 FF
        signature[ACTUAL_EC_LSB_SW_OFFSET] = getActualEcStatus()[7];
        //SPos:15  L:6 EPos:20; OBIS-ID: Sum Counter A+ /01 00 01 08 00 FF
        System.arraycopy(actualEcObisId, 0, signature, ACTUAL_EC_OBISID_OFFSET, ACTUAL_EC_OBISID_LENGTH);
        //SPos:21  L:1 EPos:21;
        signature[ACTUAL_EC_UNIT_OFFSET] = actualEcUnit;
        //SPos:22  L:1 EPos:22; Scaler
        signature[ACTUAL_EC_SCALER_OFFSET] = (byte) (actualEcScaler & 0xFF);
        //SPos:23  L:8 EPos:30; Actual EC value   Need to revert array
        System.arraycopy(Utils.reverseByteOrder(actualEcValue), 0, signature, METER_VALUE_OFFSET, METER_VALUE_OFFSET_LENGTH);
        //SPos:31  L:2 EPos:32; LogEntryIndex
        System.arraycopy(logEntryIndex, 0, signature, LOG_ENTRY_INDEX_OFFSET, LOG_ENTRY_INDEX_LENGTH);
        //SPos:33  L:66 EPos:98; Signature of actual EcValue;
        System.arraycopy(actualEcSignature, 0, signature, ACTUAL_EC_SIGNATURE_OFFSET, ACTUAL_EC_SIGNATURE_LENGTH);
        //SPos:99  L:128 EPos:226; Contract-ID
        System.arraycopy(contractId, 0, signature, CONTRACT_ID_OFFSET, CONTRACT_ID_LENGTH);
        //SPos:227  L:4 EPos:230; Timestamp of StartEC
        System.arraycopy(getStartEcTimestamp(), 0, signature, START_EC_TIMESTAMP_OFFSET, START_EC_TIMESTAMP_LENGTH);
        //SPos:231  L:20 EPos:250; ESTH
        System.arraycopy(esth, 0, signature, ESTH_OFFSET, ESTH_LENGTH);
        //SPos:251  L:1 EPos:251; LSB of SW of StartEC
        signature[START_EC_LSB_SW_OFFSET] = getStartEcStatus()[7];
        //SPos:252  L:6 EPos:257; OBIS-ID: OBIS-ID of StartEC
        System.arraycopy(startEcObisId, 0, signature, START_EC_OBISID_OFFSET, START_EC_OBISID_LENGTH);
        //SPos:258  L:1 EPos:258; from Starting EC; Line: 63
        signature[START_EC_UNIT_OFFSET] = startEcUnit;
        //SPos:259  L:1 EPos:259; Starting EC; Line: 64
        signature[START_EC_SCALER_OFFSET] = startEcScaler;
        //SPos:260  L:8 EPos:267; Starting EC -> EC value; Line: 65 Need to revert array
        System.arraycopy(Utils.reverseByteOrder(startEcValue), 0, signature, START_EC_VALUE_OFFSET, START_EC_VALUE_LENGTH);
        //SPos:268  L:6 EPos:273; ListName -> OBIS-ID Context: 81 80 81 62 nn FF (02); Line: 29
        System.arraycopy(listNameOfRes, 0, signature, LIST_NAME_OFFSET, LIST_NAME_LENGTH);
        //SPos:274  L:4 EPos:277; Pagination; Line: 73
        System.arraycopy(pagination, 0, signature, PAGINATION_OFFSET, PAGINATION_LENGTH);
        //SPos:278  L:42 EPos:319; Reserved, should always be filled with „0x00“.
        System.arraycopy(reserved, 0, signature, RESERVED_OFFSET, RESERVED_LENGTH);

        return signature;
    }

    @Override
    public boolean isDataComplete() throws SMLValidationException {
        if(getServerId() == null){
            throw new SMLValidationException("SML field server id missing", "error.sml.missing.serverid");
        }
        if(getTimestamp() == null){
            throw new SMLValidationException("SML field timestamp missing", "error.sml.missing.timestamp");
        }
        if(getPagination() == null){
            throw new SMLValidationException("SML field pagination missing", "error.sml.missing.pagination");
        }
        if(getBytesLog() == null){
            //throw new SMLValidationException("SML log bytes missing", "error.sml.missing.logbytes");
        }
        if(getContractId() == null){
            //throw new SMLValidationException("SML field contract id missing", "error.sml.missing.contractid");
        }
        //if(getTimestampContractId() == null){
            //throw new SMLValidationException("SML field timestamp contract id missing", "error.sml.missing.timestampcontractid");
        //}
        //if(getProvidedSignature() == null){
            //throw new SMLValidationException("SML field signature", "error.sml.missing.signature");
        //}

        return true;
    }

    private static byte[] longToBytes(long meterPosition) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(meterPosition).array();
    }

}
