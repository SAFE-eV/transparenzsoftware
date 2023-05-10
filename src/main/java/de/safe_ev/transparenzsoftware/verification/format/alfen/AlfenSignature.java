package de.safe_ev.transparenzsoftware.verification.format.alfen;

import java.time.OffsetDateTime;
import java.util.Arrays;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;

public class AlfenSignature {

    private final static String IDENTIFIER_STRING = "AP";

    private final static int ADAPTER_ID_OFFSET = 0;
    private final static int ADAPTER_ID_LENGTH = 10;

    private final static int ADAPTER_FIRMWARE_VERSION_OFFSET = 10;
    private final static int ADAPTER_FIRMWARE_VERSION_LENGTH = 4;

    private final static int ADAPTER_FIRMWARE_CHECKSUM_OFFSET = 14;
    private final static int ADAPTER_FIRMWARE_CHECKSUM_LENGTH = 2;

    private final static int METER_ID_OFFSET = 16;
    private final static int METER_ID_LENGTH = 10;

    private final static int STATUS_OFFSET = 26;
    private final static int STATUS_LENGTH = 4;

    private final static int SECOND_INDEX_OFFSET = 30;
    private final static int SECOND_INDEX_LENGTH = 4;

    private final static int TIMESTAMP_OFFSET = 34;
    private final static int TIMESTAMP_LENGTH = 4;

    private final static int OBIS_ID_OFFSET = 38;
    private final static int OBIS_ID_LENGTH = 6;

    private final static int UNIT_OFFSET = 44;
    private final static int SCALAR_OFFSET = 45;

    private final static int VALUE_OFFSET = 46;
    private final static int VALUE_LENGTH = 8;

    private final static int UID_OFFSET = 54;
    private final static int UID_LENGTH = 20;

    private final static int SESSIONID_OFFSET = 74;
    private final static int SESSIONID_LENGTH = 4;

    private final static int PAGING_OFFSET = 78;
    private final static int PAGING_LENGTH = 4;

    private final static int DATASET_LENGTH = 82;

    private static final String[] POSSIBLE_TYPE_VALUES = {"0", "1", "2"};

    /**
     * Value: “AP”. Alfen personalized data set, includes UID.
     * 2 bytes long
     */
    private String identifier;
    /**
     * Possible values:
     * “0”: start meter value
     * “1”: stop meter value
     * “2”: regular (intermediate) meter value
     * 1 byte long
     */
    private String type;
    /**
     * Value "2"
     * 1 byte long
     */
    private String blobVersion;

    /**
     * Base32 encoded public key
     * 64 bytes long
     */
    private String publicKey;

    private byte[] adapterId;
    private byte[] adapterFirmwareVersion;
    private byte[] adapterFirmwareChecksum;
    private byte[] meterId;
    private byte[] status;
    private byte[] secondIndex;
    private byte[] timestamp;
    private byte[] obisId;
    private byte unit;
    private byte scalar;
    private byte[] value;
    private byte[] uid;
    private byte[] sessionId;
    private byte[] paging;
    private byte[] dataset;

    private byte[] signature;

    public AlfenSignature() {

    }

    public AlfenSignature(String identifier, String type, String blobVersion, String publicKey, byte[] dataset, byte[] signature) throws ValidationException {
        setIdentifier(identifier);
        setType(type);
        setBlobVersion(blobVersion);

        if (dataset.length != DATASET_LENGTH) {
            throw new AlfenValidationException("Invalid length of dataset data applied", "error.alfen.invaliddatablocklength");
        }
        setAdapterId(Utils.copyFromWithLength(dataset, ADAPTER_ID_OFFSET, ADAPTER_ID_LENGTH));
        setAdapterFirmwareVersion(Utils.copyFromWithLength(dataset, ADAPTER_FIRMWARE_VERSION_OFFSET, ADAPTER_FIRMWARE_VERSION_LENGTH));
        setAdapterFirmwareChecksum(Utils.copyFromWithLength(dataset, ADAPTER_FIRMWARE_CHECKSUM_OFFSET, ADAPTER_FIRMWARE_CHECKSUM_LENGTH));
        setMeterId(Utils.copyFromWithLength(dataset, METER_ID_OFFSET, METER_ID_LENGTH));
        setStatus(Utils.copyFromWithLength(dataset, STATUS_OFFSET, STATUS_LENGTH));
        setSecondIndex(Utils.copyFromWithLength(dataset, SECOND_INDEX_OFFSET, SECOND_INDEX_LENGTH));
        setTimestamp(Utils.copyFromWithLength(dataset, TIMESTAMP_OFFSET, TIMESTAMP_LENGTH));
        setObisId(Utils.copyFromWithLength(dataset, OBIS_ID_OFFSET, OBIS_ID_LENGTH));
        setUnit(dataset[UNIT_OFFSET]);
        setScalar(dataset[SCALAR_OFFSET]);
        setValue(Utils.copyFromWithLength(dataset, VALUE_OFFSET, VALUE_LENGTH));
        setUid(Utils.copyFromWithLength(dataset, UID_OFFSET, UID_LENGTH));
        setSessionId(Utils.copyFromWithLength(dataset, SESSIONID_OFFSET, SESSIONID_LENGTH));
        setPaging(Utils.copyFromWithLength(dataset, PAGING_OFFSET, PAGING_LENGTH));

        setPublicKey(publicKey);
        setSignature(signature);
        this.dataset = dataset;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) throws ValidationException {
        if (!IDENTIFIER_STRING.equals(identifier)) {
            throw new ValidationException("Identifier not AP");
        }
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws ValidationException {
        if (type.length() != 1) {
            throw new ValidationException("Type not 1 characters long");
        }
        if (!Arrays.asList(POSSIBLE_TYPE_VALUES).contains(type)) {
            throw new ValidationException(String.format("Invalid type %s given", type));
        }
        this.type = type;
    }

    public String getBlobVersion() {
        return blobVersion;
    }

    public void setBlobVersion(String blobVersion) throws ValidationException {
        if (blobVersion.length() != 1) {
            throw new ValidationException("Blob version not 1 characters long");
        }
        this.blobVersion = blobVersion;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getAdapterId() {
        return adapterId;
    }

    public void setAdapterId(byte[] adapterId) throws ValidationException {
        if (adapterId.length != ADAPTER_ID_LENGTH) {
            throw new ValidationException(String.format("Adapter id length not %s", ADAPTER_ID_LENGTH));
        }
        this.adapterId = adapterId;
    }

    public byte[] getMeterId() {
        return meterId;
    }

    public long getMeterIdAsLong() {
        return Utils.parseUint8Chain(adapterId);
    }

    public void setMeterId(byte[] meterId) throws ValidationException {
        if (meterId.length != METER_ID_LENGTH) {
            throw new ValidationException("Invalid length of meter id");
        }
        this.meterId = meterId;
    }

    public byte[] getStatus() {
        return status;
    }

    public void setStatus(byte[] status) throws ValidationException {
        if (status.length != STATUS_LENGTH) {
            throw new ValidationException("Invalid length of status");
        }
        this.status = status;
    }

    public byte[] getSecondIndex() {
        return secondIndex;
    }

    public long getSecondIndexAsLong() {
        return Utils.parseUint32(secondIndex, true);
    }

    public void setSecondIndex(byte[] secondIndex) throws ValidationException {
        if (secondIndex.length != SECOND_INDEX_LENGTH) {
            throw new ValidationException("Invalid second index length");
        }
        this.secondIndex = secondIndex;
    }

    public byte[] getTimestamp() {
        return timestamp;
    }

    public OffsetDateTime getTimestampAsLocalDate() {
        return Utils.timeBytesToTimestamp(timestamp);
    }

    public long getTimestampAsLong() {
        return Utils.parseUint32(timestamp, true);
    }

    public void setTimestamp(byte[] timestamp) throws ValidationException {
        if (timestamp.length != TIMESTAMP_LENGTH) {
            throw new ValidationException("Invalid timestamp length");
        }
        this.timestamp = timestamp;
    }

    public byte[] getObisId() {
        return obisId;
    }

    public void setObisId(byte[] obisId) throws ValidationException {
        if (obisId.length != OBIS_ID_LENGTH) {
            throw new ValidationException("Invalid length of obis id");
        }
        this.obisId = obisId;
    }

    public byte getUnit() {
        return unit;
    }

    public void setUnit(byte unit) {
        this.unit = unit;
    }

    public byte getScalar() {
        return scalar;
    }

    public void setScalar(byte scalar) {
        this.scalar = scalar;
    }

    public byte[] getValue() {
        return value;
    }

    public long getValueAsLong() {
        return Utils.bytesToLong(Utils.reverseByteOrder(value));
    }

    public void setValue(byte[] value) throws ValidationException {
        if (value.length != VALUE_LENGTH) {
            throw new ValidationException("Invalid length for value given");
        }
        this.value = value;
    }

    public byte[] getUid() {
        return uid;
    }


    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    public byte[] getSessionId() {
        return sessionId;
    }

    public long getSessionIdAsLong() {
        return Utils.parseUint32(sessionId, true);
    }

    public byte[] getPaging() {
        return paging;
    }

    public long getPagingAsLong() {
        return Utils.parseUint32(paging, true);
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSessionId(byte[] sessionId) throws ValidationException {
        if (sessionId.length != SESSIONID_LENGTH) {
            throw new ValidationException("Invalid length for session id");
        }
        this.sessionId = sessionId;
    }

    public void setPaging(byte[] paging) throws ValidationException {
        if (paging.length != PAGING_LENGTH) {
            throw new ValidationException("Invalid length for paging");
        }
        this.paging = paging;
    }

    public byte[] getAdapterFirmwareVersion() {
        return adapterFirmwareVersion;
    }

    public void setAdapterFirmwareVersion(byte[] adapterFirmwareVersion) throws ValidationException {
        if (adapterFirmwareVersion.length != ADAPTER_FIRMWARE_VERSION_LENGTH) {
            throw new ValidationException("Invalid length for adapter firmware version");
        }
        this.adapterFirmwareVersion = adapterFirmwareVersion;
    }

    public byte[] getAdapterFirmwareChecksum() {
        return adapterFirmwareChecksum;
    }

    public void setAdapterFirmwareChecksum(byte[] adapterFirmwareChecksum) throws ValidationException {
        if (adapterFirmwareChecksum.length != ADAPTER_FIRMWARE_CHECKSUM_LENGTH) {
            throw new ValidationException("Invalid length for adapter firmware checksum");
        }
        this.adapterFirmwareChecksum = adapterFirmwareChecksum;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getDataset() {
        return dataset;
    }
}
