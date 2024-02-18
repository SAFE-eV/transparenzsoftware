package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes;

import org.bouncycastle.util.encoders.Hex;

import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.ChargingProcess;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.Measurement;

import java.time.OffsetDateTime;

public class EDLMennekesSignature extends SMLSignature {

    private OffsetDateTime timestamp;
    private OffsetDateTime timestampContractId;
    private byte[] providedSignature;
    private long eventCounter;

    public EDLMennekesSignature(ChargingProcess chargingProcess, ReadingType readingType) throws SMLValidationException {
        Measurement measurement = readingType == ReadingType.MEASUREMENT_START ? chargingProcess.getMeasurementStart() : chargingProcess.getMeasurementEnd();
        if (measurement == null) {
            throw new SMLValidationException("No measurements provided", "error.sml.mennekes.no.measurement");
        }
        if (measurement.getTimestamp() == null) {
            throw new SMLValidationException("No timestamp in measurement provided", "error.sml.mennekes.no.timestamp");
        }
        timestamp = measurement.getTimestamp();
        if (measurement.getTimestampCustomerIdent() == null) {
            timestampContractId = chargingProcess.getTimestampCustomerIdent();
        } else {
            timestampContractId = measurement.getTimestampCustomerIdent();
        }
        if (timestampContractId == null) {
            throw new SMLValidationException("No timestamp for customer timestamp provided", "error.sml.mennekes.no.timestampCustomer");
        }
        setMeterPosition(measurement.getValue());
        setSecondsIndex(measurement.getSecondIndex());
        setPagination(measurement.getPagination());
        setStatus(measurement.getMeterStatus());
        setScaler((byte) measurement.getScaler());
        setEventCounter(measurement.getEventCounter());
        try {
            setServerId(EncodingType.hexDecode(chargingProcess.getServerId()));
        } catch (DecodingException e) {
            throw new SMLValidationException("Invalid serverid provided", "error.sml.mennekes.invalid.serverid");
        }
        //the obis id must always be the one from the value
        setObisNr(new byte[]{1, 0, 1, 17, 0, -1});
        //we assume its always wh
        setUnit(30);
        byte[] signature = measurement.getSignatureAsBytes();
        if (signature.length > 48) {
        	setBytesLog(signature[signature.length - 2], signature[signature.length - 1]);
        } else {
        	setBytesLog((byte)((this.eventCounter >> 8) & 0xff), (byte)(this.eventCounter & 0xff));
        }
        setContractId(Hex.decode(chargingProcess.getCustomerIdent()), true);
        providedSignature = signature;
    }

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

    @Override
    public byte[] getProvidedSignature() {
        return providedSignature;
    }

    public long getEventCounter() {
        return eventCounter;
    }

    public void setEventCounter(long eventCounter) {
        this.eventCounter = eventCounter;
    }

    enum ReadingType {
        MEASUREMENT_START,
        MEASUREMENT_END
    }
}
