package de.safe_ev.transparenzsoftware.verification.format.sml.EDL40;

import java.time.OffsetDateTime;

import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;

public class EDL40Signature extends SMLSignature {

    private OffsetDateTime timestamp;
    private OffsetDateTime timestampContractId;
    private byte[] providedSignature;
    private boolean isEmoc;

    @Override
    public byte[] getTimestamp() {
	return timeToBytes(timestamp);
    }

    @Override
    public OffsetDateTime getTimestampAsDate() {
	return timestamp;
    }

    @Override
    public byte[] getTimestampContractId() {
	return timeToBytes(timestampContractId);
    }

    @Override
    public OffsetDateTime getTimestampContractIdAsDate() {
	return timestampContractId;
    }

    /**
     * @param timestampCustomerId unix timestamp in seconds
     * @throws SMLValidationException if it cannot be transferred to a 4 byte long
     *                                array
     */
    public void setTimestampContractId(OffsetDateTime timestampCustomerId) throws SMLValidationException {
	timestampContractId = timestampCustomerId;
    }

    /**
     * Sets the time in seconds will transfered to a 4 bytes long string where the
     * byte order will be switched (MSB becomes LSB)
     *
     * @param timestamp timestamp in seconds
     * @throws SMLValidationException if time cannot be transferred to a 4 bytes
     *                                long array
     */
    public void setTimestamp(OffsetDateTime timestamp) throws SMLValidationException {
	this.timestamp = timestamp;
    }

    public void setProvidedSignature(byte[] providedSignature) {
	this.providedSignature = providedSignature;
	if (providedSignature.length == 66) {
	    isEmoc = true;
	}
    }

    @Override
    public byte[] getProvidedSignature() {
	return providedSignature;
    }

    public boolean isEmoc() {
	return isEmoc;
    }

}
