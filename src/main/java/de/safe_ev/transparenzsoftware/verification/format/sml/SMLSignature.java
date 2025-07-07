package de.safe_ev.transparenzsoftware.verification.format.sml;

import java.nio.ByteBuffer;
import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Utils;

/**
 * Class keep book of signature data fetched by sml until byte 33 everything is
 * only compliant to edl 40 basic.
 * <p>
 * Everything that starts after byte 33 is the extended signature format.
 */
public abstract class SMLSignature {

    public final static int LENGTH_SIGNATURE = 320;

    public final static int SERVER_ID_OFFSET = 0;
    public final static int SERVER_ID_LENGTH = 10;

    public final static int TIMESTAMP_OFFSET = 10;
    public final static int TIMESTAMP_LENGTH = 4;

    public final static int STATUS_OFFSET = 14;

    public final static int SECONDS_INDEX_OFFSET = 15;
    public final static int SECONDS_INDEX_LENGTH = 4;

    public final static int PAGINATION_INDEX_OFFSET = 19;
    public final static int PAGINATION_INDEX_LENGTH = 4;

    public final static int OBIS_ID_OFFSET = 23;
    public final static int OBIS_ID_LENGTH = 6;

    public final static int UNIT_OFFSET = 29;

    public final static int SCALER_OFFSET = 30;

    public final static int METER_POSITION_OFFSET = 31;
    public final static int METER_POSITION_LENGTH = 8;

    public final static int LOGBUCH_OFFSET = 39;
    public final static int LOGBUCH_LENGTH = 2;

    public final static int CONTRACT_ID_OFFSET = 41;
    public final static int CONTRACT_ID_LENGTH = 128;

    public final static int CONTRACT_ID_TIMESTAMP_OFFSET = 169;
    public final static int CONTRACT_ID_TIMESTAMP_LENGTH = 4;

    public final static int HISTORICAL_VALUE_OFFSET = 173;
    public final static int HISTORICAL_VALUE_LENGTH = 8;

    public final static int HISTORICAL_VALUE_COMP_OFFSET = 181;
    public final static int HISTORICAL_VALUE_COMP_LENGTH = 8;

    public final static int POWERLINE_RESIST_OFFSET = 189;
    public final static int POWERLINE_RESIST_LENGTH = 2;

    public final static int STOPWATCH_OFFSET = 191;
    public final static int STOPWATCH_LENGTH = 4;

    public final static int PRICE_OFFSET = 195;
    public final static int PRICE_LENGTH = 2;

    public final static int RESERVED_BYTES_OFFSET = 197;
    public final static int RESERVED_BYTES_LENGTH = LENGTH_SIGNATURE - RESERVED_BYTES_OFFSET;

    private static final Logger LOGGER = LogManager.getLogger(SMLSignature.class);

    private int version;
    /**
     * Stated in spec as Geräteeinzelidentifikation (<=> Server-ID) 10 bytes long
     */
    private byte[] serverId;

    /**
     * Stated in spec as Statuswort
     */
    private byte status;

    /**
     * Index of second at the time of creating the signature
     * <p>
     * - first byte is the lsb of the seconds index - last byte ist the msb of the
     * seconds index
     */
    private byte[] secondsIndex;

    /**
     * Pagination - first byte is lsb of pagination - last byte msb of pagination
     */
    private byte[] pagination;

    /**
     * Stated in spec as OBIS-Kennzahl 5 bytes long
     */
    private byte[] obisNr;

    /**
     * Unit
     */
    private byte unit;

    /**
     * Scaler
     */
    private byte scaler;

    /**
     * Zählerstand 8 bytes ( 8byte unsigned integer)
     */
    private byte[] meterPosition;

    /**
     * Byte at pos 31 -> high byte of logbook entry index Byte at pos 32 -> low byte
     * of logbook entry index
     */
    private byte[] bytesLog;

    /**
     * customer id (Kunden-ID) 128 bytes long if LSB at the beginning the end has to
     * be filled up with 0x00
     */
    private byte[] contractId;

    /**
     * 8 bytes historical counter value
     */
    private long historicalValue;

    /**
     * 8 bytes historical counter compensated value
     */
    private long historicalValueComp;

    /**
     * 2 byte power line resistance
     */
    private int powerlineResistance;

    /**
     * 4 byte time (duration)
     */
    private int stopwatch;

    /**
     * 2 byte price indication
     */
    private int price;

    private boolean compensated;

    public SMLSignature() {
    }

    /**
     * @return Server id in bytes 10 bytes long
     */
    public byte[] getServerId() {
	return serverId;
    }

    /**
     * Sets the serverId bytes
     *
     * @param serverId 10 bytes long array
     * @throws SMLValidationException if serverId is not 10 bytes long
     */
    public void setServerId(byte[] serverId) throws SMLValidationException {
	if (serverId.length != SERVER_ID_LENGTH) {
	    throw new SMLValidationException("Server Id was not 10 bytes long");
	}
	this.serverId = serverId;
    }

    /**
     * @return in 4 bytes long
     */
    public abstract byte[] getTimestamp();

    /**
     * Timestamp as time object
     *
     * @return Date
     */
    public abstract OffsetDateTime getTimestampAsDate();

    /**
     * @return status byte
     */
    public byte getStatus() {
	return status;
    }

    /**
     * @param status status byte
     */
    public void setStatus(byte status) {
	this.status = status;
    }

    /**
     * Takes the status as int will only have a look at the LSB
     *
     * @param status status information as int will be transformed to 1 byte
     */
    public void setStatus(int status) {
	this.status = (byte) (status & 0xFF); // Least significant "byte";
    }

    /**
     * @return returns the obis nr as a 5 byte long array
     */
    public byte[] getObisNr() {
	return obisNr;
    }

    /**
     * @param obisNr obisNr as 6 bytes long array
     * @throws SMLValidationException if obisNr is not 6 bytes long
     */
    public void setObisNr(byte[] obisNr) throws SMLValidationException {
	if (obisNr.length != OBIS_ID_LENGTH) {
	    throw new SMLValidationException("Obis id was not 6 bytes long");
	}
	this.obisNr = obisNr;
    }

    /**
     * @return unit byte
     */
    public byte getUnit() {
	return unit;
    }

    /**
     * @param unit unit byte
     */
    public void setUnit(byte unit) {
	this.unit = unit;
    }

    /**
     * Will set the unit byte with an integer. We only care about the LSB.
     *
     * @param unit unit as int will be transformed to a byte
     */
    public void setUnit(int unit) {
	this.unit = (byte) (unit & 0xFF);
    }

    /**
     * @return scaler byte
     */
    public byte getScaler() {
	return scaler;
    }

    /**
     * @param scaler scaler byte
     */
    public void setScaler(byte scaler) {
	this.scaler = scaler;
    }

    public byte[] getMeterPosition() {
	return meterPosition;
    }

    /**
     * Sets the meter position switching the order of the byte array so that the LSB
     * becomes the MSB
     *
     * @param meterPosition meter position as a 8 bytes long array
     * @throws SMLValidationException if the meter position is not 8 bytes long
     */
    public void setMeterPosition(byte[] meterPosition) throws SMLValidationException {
	setMeterPosition(meterPosition, true);
    }

    /**
     * Sets the meter position switching the order of the byte array so that the LSB
     * becomes the MSB
     *
     * @param meterPosition meter position as a 8 bytes long array
     * @param reverse       if true order of bytes will reversed
     * @throws SMLValidationException if the meter position is not 8 bytes long
     */
    public void setMeterPosition(byte[] meterPosition, boolean reverse) throws SMLValidationException {
	if (meterPosition.length != METER_POSITION_LENGTH) {
	    throw new SMLValidationException("Meter position was not 89 bytes long");
	}
	if (reverse) {
	    this.meterPosition = Utils.reverseByteOrder(meterPosition);
	} else {
	    this.meterPosition = meterPosition;
	}

    }

    /**
     * Sets the meter position with long value which will be transferred to a byte
     * array
     *
     * @param meterPosition meterPosition as long
     * @throws SMLValidationException if meter position cannot be transferred to a
     *                                23 bytes long array
     */
    public void setMeterPosition(long meterPosition) throws SMLValidationException {
	setMeterPosition(longToBytes(meterPosition));
    }

    /**
     * returns either the meter position value or the compensated value (if set) as
     * long
     *
     * @return meter position
     */
    public long getLawRelevantMeterAsLong() {
	if (historicalValueComp != 0) {
	    return historicalValueComp;
	}
	return Utils.bytesToLong(Utils.reverseByteOrder(meterPosition));
    }

    /**
     * Log bytes 2 bytes long
     *
     * @return logbook bytes 2 bytes long
     */
    public byte[] getBytesLog() {
	return bytesLog;
    }

    /**
     * Sets the logbook bytes
     *
     * @param bytesLog logbook bytes 2 bytes long array
     * @throws SMLValidationException if logbook bytes are not 2 bytes long
     */
    public void setBytesLog(byte[] bytesLog) throws SMLValidationException {
	if (bytesLog.length != 2) {
	    throw new SMLValidationException("Logbook bytes not 2 bytes long");
	}
	this.bytesLog = bytesLog;
    }

    /**
     * Sets the logbook bytes with the lowbyte and the highbyte
     *
     * @param lowByte  LSB logbook byte
     * @param highByte MSB logbook byte to a 2 bytes long array
     */
    public void setBytesLog(byte lowByte, byte highByte) {
	bytesLog = new byte[] { lowByte, highByte };
    }

    /**
     * @return byte array containing the customer id
     */
    public byte[] getContractId() {
	return contractId;
    }

    /**
     * sets the customer id will reverting the order of the bytes
     *
     * @param contractId customer id in bytes 128 bytes long
     * @throws SMLValidationException if customer id is not 128 bytes long
     */
    public void setContractId(byte[] contractId) throws SMLValidationException {
	setContractId(contractId, false);
    }

    /**
     * sets the customer id will reverting the order of the bytes
     *
     * @param contractId customer id in bytes 128 bytes long
     * @param fillUp     controls if bytes will be filled up starting at the
     *                   beginning
     * @throws SMLValidationException if customer id is not 128 bytes long
     */
    public void setContractId(byte[] contractId, boolean fillUp) throws SMLValidationException {
	if (contractId.length < CONTRACT_ID_LENGTH && fillUp) {
	    final byte[] fillUpData = new byte[CONTRACT_ID_LENGTH];
	    System.arraycopy(contractId, 0, fillUpData, 0, contractId.length);
	    this.contractId = fillUpData;
	} else if ((fillUp && contractId.length > CONTRACT_ID_LENGTH) || contractId.length != CONTRACT_ID_LENGTH) {
	    throw new SMLValidationException("base signature not 128 bytes long");
	} else {
	    this.contractId = contractId;
	}

    }

    /**
     * Sets the seconds index value with a int value will be transformed to byte
     * array
     *
     * @param index index in int
     * @throws SMLValidationException
     */
    public void setSecondsIndex(int index) throws SMLValidationException {
	setSecondsIndex(Utils.reverseByteOrder(intToBytes(index)));
    }

    /**
     * set the seconds index value needs to be 4 bytes long
     *
     * @param data byte array
     * @throws SMLValidationException if data provides is not 4 bytes long
     */
    public void setSecondsIndex(byte[] data) throws SMLValidationException {
	if (data.length != SECONDS_INDEX_LENGTH) {
	    throw new SMLValidationException("Seconds index was not 4 bytes long");
	}
	secondsIndex = data;
    }

    /**
     * @return byte array containing the 4 bytes of the seconds index
     */
    public byte[] getSecondsIndex() {
	return secondsIndex;
    }

    /**
     * @return byte array containing the 4 bytes pagination
     */
    public byte[] getPagination() {
	return pagination;
    }

    /**
     * @param pagination pagination data in a byte array 4 bytes long
     * @throws SMLValidationException if not in the correct length
     */
    public void setPagination(byte[] pagination) throws SMLValidationException {
	if (pagination.length != PAGINATION_INDEX_LENGTH) {
	    throw new SMLValidationException("Pagination index was not 4 bytes long");
	}
	this.pagination = pagination;
    }

    /**
     * @param pagination pagination data in a byte array 4 bytes long
     * @throws SMLValidationException if not in the correct length
     */
    public void setPagination(int pagination) throws SMLValidationException {
	setPagination(Utils.reverseByteOrder(intToBytes(pagination)));
    }

    /**
     * @return byte array containing the timestamp the customer id was set
     */
    public abstract byte[] getTimestampContractId();

    /**
     * Customer id timestamp as time object
     *
     * @return Date
     */
    public abstract OffsetDateTime getTimestampContractIdAsDate();

    /**
     * Creates the array ready for the hashing and signature creation ehz SML
     * Kundenidentifikation 2 page 6 and 7
     * <p>
     * Takes the basic signature data and starts adding data on SIGNATURE_OFFSET
     *
     * @return byte array
     */
    public byte[] buildExtendedSignatureData() {
	final byte[] signedData = new byte[LENGTH_SIGNATURE];

	System.arraycopy(serverId, 0, signedData, SERVER_ID_OFFSET, SERVER_ID_LENGTH);
	System.arraycopy(getTimestamp(), 0, signedData, TIMESTAMP_OFFSET, TIMESTAMP_LENGTH);
	signedData[STATUS_OFFSET] = status;
	System.arraycopy(pagination, 0, signedData, PAGINATION_INDEX_OFFSET, PAGINATION_INDEX_LENGTH);
	System.arraycopy(secondsIndex, 0, signedData, SECONDS_INDEX_OFFSET, SECONDS_INDEX_LENGTH);
	System.arraycopy(obisNr, 0, signedData, OBIS_ID_OFFSET, OBIS_ID_LENGTH);
	signedData[UNIT_OFFSET] = unit;
	signedData[SCALER_OFFSET] = (byte) (scaler & 0xFF);

	System.arraycopy(meterPosition, 0, signedData, METER_POSITION_OFFSET, METER_POSITION_LENGTH);
	System.arraycopy(bytesLog, 0, signedData, LOGBUCH_OFFSET, LOGBUCH_LENGTH);
	// fill up empty bytes at the end is done by java itself
	// arrays are initialised wit the default value which is 0 on bytes
	// https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
	System.arraycopy(contractId, 0, signedData, CONTRACT_ID_OFFSET, CONTRACT_ID_LENGTH);
	System.arraycopy(getTimestampContractId(), 0, signedData, CONTRACT_ID_TIMESTAMP_OFFSET,
		CONTRACT_ID_TIMESTAMP_LENGTH);

	System.arraycopy(Utils.reverseByteOrder(longToBytes(historicalValue)), 0, signedData, HISTORICAL_VALUE_OFFSET,
		HISTORICAL_VALUE_LENGTH);
	System.arraycopy(Utils.reverseByteOrder(longToBytes(getHistoricalValueComp())), 0, signedData,
		HISTORICAL_VALUE_COMP_OFFSET, HISTORICAL_VALUE_COMP_LENGTH);
	System.arraycopy(Utils.reverseByteOrder(intTo2Bytes(getPowerlineResistance())), 0, signedData,
		POWERLINE_RESIST_OFFSET, POWERLINE_RESIST_LENGTH);

	System.arraycopy(timeToBytes(stopwatch), 0, signedData, STOPWATCH_OFFSET, STOPWATCH_LENGTH);

	System.arraycopy(intTo2Bytes(price), 0, signedData, PRICE_OFFSET, PRICE_LENGTH);

	// fill up empty bytes at the end is done by java itself
	// arrays are initialised wit the default value which is 0 on bytes
	// https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
	return signedData;
    }

    /**
     * Calculates a long in a byte array
     *
     * @param meterPosition value in long
     * @return byte array
     */
    private static byte[] longToBytes(long meterPosition) {
	return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(meterPosition).array();
    }

    /**
     * Takes the time in as an int will do the byte order switching
     *
     * @param time time
     * @return time 4 bytes long switched
     */
    protected static byte[] timeToBytes(int time) {
	return Utils.reverseByteOrder(intToBytes(time));
    }

    /**
     * Takes the time in as an int will do the byte order switching
     *
     * @param dateTime time
     * @return time 4 bytes long switched
     */
    protected static byte[] timeToBytes(OffsetDateTime dateTime) {
	final int seconds = (int) (dateTime.toEpochSecond() + dateTime.getOffset().getTotalSeconds());
	return timeToBytes(seconds);
    }

    /**
     * Takes the time in as an int will do the byte order switching
     *
     * @param time time
     * @return time 4 bytes long switched
     */
    protected static byte[] intToBytes(int time) {
	int pos1 = time & 0xFF000000;
	pos1 >>= 24;
	int pos2 = time & 0x00FF0000;
	pos2 >>= 16;
	int pos3 = time & 0x0000FF00;
	pos3 >>= 8;
	final int pos4 = time & 0x000000FF;
	return new byte[] { (byte) pos1, (byte) pos2, (byte) pos3, (byte) pos4 };
    }

    /**
     * Takes the time in as an int will do the byte order switching
     *
     * @param time time
     * @return time 4 bytes long switched
     */
    protected static byte[] intTo2Bytes(int theInt) {
	int pos1 = theInt & 0x0000FF00;
	pos1 >>= 8;
	final int pos2 = theInt & 0x000000FF;
	return new byte[] { (byte) pos1, (byte) pos2 };
    }

    public abstract byte[] getProvidedSignature();

    /**
     * Checks if all necessary data has been filled in
     *
     * @return true if complete otherwise false
     */
    public boolean isDataComplete() throws SMLValidationException {
	if (getServerId() == null) {
	    throw new SMLValidationException("SML field server id missing", "error.sml.missing.serverid");
	}
	if (getTimestamp() == null) {
	    throw new SMLValidationException("SML field timestamp missing", "error.sml.missing.timestamp");
	}
	if (getSecondsIndex() == null) {
	    throw new SMLValidationException("SML field seconds index missing", "error.sml.missing.secondsindex");
	}
	if (getPagination() == null) {
	    throw new SMLValidationException("SML field pagination missing", "error.sml.missing.pagination");
	}
	if (getObisNr() == null) {
	    throw new SMLValidationException("SML field obis id missing", "error.sml.missing.obisnr");
	}
	if (getMeterPosition() == null) {
	    throw new SMLValidationException("SML field meter position missing", "error.sml.missing.meterposition");
	}
	if (getBytesLog() == null) {
	    throw new SMLValidationException("SML log bytes missing", "error.sml.missing.logbytes");
	}
	if (getContractId() == null) {
	    throw new SMLValidationException("SML field contract id missing", "error.sml.missing.contractid");
	}
	if (getTimestampContractId() == null) {
	    throw new SMLValidationException("SML field timestamp contract id missing",
		    "error.sml.missing.timestampcontractid");
	}
	if (getProvidedSignature() == null) {
	    throw new SMLValidationException("SML field signature", "error.sml.missing.signature");
	}
	return true;
    }

    public long getHistoricalValue() {
	return historicalValue;
    }

    public void setHistoricalValue(long historicalValue) {
	this.historicalValue = historicalValue;
    }

    public int getStopwatch() {
	return stopwatch;
    }

    public void setStopwatch(int stopwatch) {
	this.stopwatch = stopwatch;
    }

    public int getPrice() {
	return price;
    }

    public void setPrice(int price) {
	this.price = price;
    }

    public long getHistoricalValueComp() {
	return historicalValueComp;
    }

    public void setHistoricalValueComp(long historicalValueComp) {
	this.historicalValueComp = historicalValueComp;
    }

    public int getPowerlineResistance() {
	return powerlineResistance;
    }

    public void setPowerlineResistance(int powerlineResistance) {
	this.powerlineResistance = powerlineResistance;
	if (powerlineResistance != 0) {
	    compensated = true;
	}
    }

    public int getVersion() {
	return version;
    }

    public void setVersion(int version) {
	this.version = version;
    }

    public boolean isCompensated() {
	return compensated;
    }
}
