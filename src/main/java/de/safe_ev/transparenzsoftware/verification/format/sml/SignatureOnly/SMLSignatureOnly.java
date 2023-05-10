package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly;

import java.time.OffsetDateTime;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;

public class SMLSignatureOnly extends SMLSignature {


    /**
     * Stated in spec as Zeitstempel
     * 4 bytes long
     */
    private byte[] timestamp;

    private byte[] timestampContractId;

    private byte[] providedSignature;

    protected SMLSignatureOnly(){

    }

    /**
     * Parses out of a byte array the positions of the specific fields of
     * sml message signature
     *
     * @param rawBytes payload bytes
     * @throws ValidationException if does not have the correct format or size
     */
    public SMLSignatureOnly(byte[] rawBytes) throws ValidationException {
        if (rawBytes.length != LENGTH_SIGNATURE) {
            throw new ValidationException("Invalid length of data applied for signature data", "error.invalid.signature.length");
        }
        setServerId(Utils.copyFromWithLength(rawBytes, SERVER_ID_OFFSET, SERVER_ID_LENGTH));
        setTimestamp(Utils.copyFromWithLength(rawBytes, TIMESTAMP_OFFSET, TIMESTAMP_LENGTH));
        setStatus(rawBytes[STATUS_OFFSET]);
        setSecondsIndex(Utils.copyFromWithLength(rawBytes, SECONDS_INDEX_OFFSET, SECONDS_INDEX_LENGTH));
        setPagination(Utils.copyFromWithLength(rawBytes, PAGINATION_INDEX_OFFSET, PAGINATION_INDEX_LENGTH));
        setObisNr(Utils.copyFromWithLength(rawBytes, OBIS_ID_OFFSET, OBIS_ID_LENGTH));
        setUnit(rawBytes[UNIT_OFFSET]);
        setScaler(rawBytes[SCALER_OFFSET]);
        setMeterPosition(Utils.copyFromWithLength(rawBytes, METER_POSITION_OFFSET, METER_POSITION_LENGTH), false);
        setBytesLog(Utils.copyFromWithLength(rawBytes, LOGBUCH_OFFSET, LOGBUCH_LENGTH));
        setContractId(Utils.copyFromWithLength(rawBytes, CONTRACT_ID_OFFSET, CONTRACT_ID_LENGTH), false);
        setTimestampContractId(Utils.copyFromWithLength(rawBytes, CONTRACT_ID_TIMESTAMP_OFFSET, CONTRACT_ID_TIMESTAMP_LENGTH));
    }

    /**
     * @param timestampContractId set the byte array of the customer id timestamp
     */
    public void setTimestampContractId(byte[] timestampContractId) throws SMLValidationException {
        if (timestampContractId.length != CONTRACT_ID_TIMESTAMP_LENGTH) {
            throw new SMLValidationException("Customer id timestamp is not 4 bytes long");
        }
        this.timestampContractId = timestampContractId;
    }

    /**
     * @param timestamp time in bytes 4 bytes long
     * @throws SMLValidationException will be thrown if timestamp length is not 4
     */
    public void setTimestamp(byte[] timestamp) throws SMLValidationException {
        if (timestamp.length != TIMESTAMP_LENGTH) {
            throw new SMLValidationException("Timestamp was not 4 bytes long");
        }
        this.timestamp = timestamp;
    }

    @Override
    public byte[] getTimestamp() {
        return this.timestamp;
    }

    @Override
    public OffsetDateTime getTimestampAsDate() {
        if(getTimestamp().length == 0){
            return null;
        }
        return Utils.timeBytesToTimestamp(getTimestamp());
    }

    @Override
    public byte[] getTimestampContractId() {
        return timestampContractId;
    }

    @Override
    public OffsetDateTime getTimestampContractIdAsDate() {
        if(getTimestampContractId().length == 0){
            return null;
        }
        return Utils.timeBytesToTimestamp(getTimestampContractId());
    }

    @Override
    public byte[] getProvidedSignature() {
        return providedSignature;
    }

    public void setProvidedSignature(byte[] providedSignature) {
        this.providedSignature = providedSignature;
    }
}
