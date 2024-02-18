package de.safe_ev.transparenzsoftware.verification.format.sml;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLVerifiedData;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDL40.EDL40Signature;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class SMLVerifiedDataTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_law_conform_logbook_start_null_1() throws RegulationLawException, ValidationException {
        expectedException.expectMessage("Logbook");
        EDL40Signature data = buildSignature((byte) 1, (byte) 2, 1, 1L, "01 04 05");
        SMLVerifiedData start = new SMLVerifiedData(data, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(null, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        start.lawConform(stop);
    }

    @Test
    public void test_law_conform_logbook_start_null_2() throws RegulationLawException, ValidationException {
        expectedException.expectMessage("Logbook");
        EDL40Signature data = buildSignature((byte) 1, (byte) 2, 1, 1L, "01 04 05");
        SMLVerifiedData start = new SMLVerifiedData(null, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(data, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        start.lawConform(stop);
    }

    @Test
    public void test_law_conform_logbook_different() throws RegulationLawException, ValidationException {
        expectedException.expectMessage("Logbook");
        EDL40Signature dataStart = buildSignature((byte) 1, (byte) 2, 1, 1L, "01 04 05");
        EDL40Signature dataStop = buildSignature((byte) 1, (byte) 3, 1, 1L, "01 04 05");
        SMLVerifiedData start = new SMLVerifiedData(dataStart, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(dataStop, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        start.lawConform(stop);
    }

    @Test
    public void test_law_conform_customer_id() throws RegulationLawException, ValidationException {
        expectedException.expectMessage("Customer id");
        EDL40Signature dataStart = buildSignature((byte) 1, (byte) 2, 3, 1L, "01 04 05");
        EDL40Signature dataStop = buildSignature((byte) 1, (byte) 2, 3, 1L, "01 04 06");
        SMLVerifiedData start = new SMLVerifiedData(dataStart, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(dataStop, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        start.lawConform(stop);
    }

    @Test
    public void test_law_conform_pagination_1() throws RegulationLawException, ValidationException {
        expectedException.expectMessage("Pagination");
        EDL40Signature dataStart = buildSignature((byte) 1, (byte) 2, 15, 1L, "01 04 05");
        EDL40Signature dataStop = buildSignature((byte) 1, (byte) 2, 15, 1L, "01 04 05");
        SMLVerifiedData start = new SMLVerifiedData(dataStart, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(dataStop, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        start.lawConform(stop);
    }

    @Test
    public void test_law_conform_pagination_2() throws RegulationLawException, ValidationException {
        expectedException.expectMessage("Pagination");
        EDL40Signature dataStart = buildSignature((byte) 1, (byte) 2, 15, 1L, "01 04 05");
        EDL40Signature dataStop = buildSignature((byte) 1, (byte) 2, 3, 1L, "01 04 05");
        SMLVerifiedData start = new SMLVerifiedData(dataStart, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(dataStop, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        start.lawConform(stop);
    }

    @Test
    public void test_law_conform_meter_value() throws RegulationLawException, ValidationException {
        expectedException.expectMessage("Meter");
        EDL40Signature dataStart = buildSignature((byte) 1, (byte) 2, 3, 2L, "01 04 05");
        EDL40Signature dataStop = buildSignature((byte) 1, (byte) 2, 4, 1L, "01 04 05");
        SMLVerifiedData start = new SMLVerifiedData(dataStart, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(dataStop, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        start.lawConform(stop);
    }

    @Test
    public void test_law_conform() throws RegulationLawException, ValidationException {
        EDL40Signature dataStart = buildSignature((byte) 1, (byte) 2, 3, 1L, "01 04 05");
        EDL40Signature dataStop = buildSignature((byte) 1, (byte) 2, 4, 2L, "01 04 05");
        SMLVerifiedData start = new SMLVerifiedData(dataStart, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        SMLVerifiedData stop = new SMLVerifiedData(dataStop, VerificationType.EDL_40_P, EncodingType.PLAIN, "");
        Assert.assertTrue(start.lawConform(stop));
    }

    private EDL40Signature buildSignature(Byte lowByte, Byte highByte, Integer pagination, Long meter, String customerId) throws SMLValidationException {
        EDL40Signature signatureData = new EDL40Signature();
        OffsetDateTime offsetDateTimeCid = OffsetDateTime.of(LocalDateTime.ofEpochSecond(1504275196, 0, ZoneOffset.UTC), ZoneOffset.UTC);
        OffsetDateTime offsetDateTimestamp = OffsetDateTime.of(LocalDateTime.ofEpochSecond(1504275196, 0, ZoneOffset.UTC), ZoneOffset.UTC);
        signatureData.setTimestampContractId(offsetDateTimeCid);
        signatureData.setTimestamp(offsetDateTimestamp);
        signatureData.setObisNr(new byte[]{0, 1, 2, 3, 4, 5});
        signatureData.setMeterPosition(1234);
        if (lowByte != null && highByte != null) {
            signatureData.setBytesLog(lowByte, highByte);
        }
        if (pagination != null) {
            signatureData.setPagination(pagination);
        }
        if (meter != null) {
            signatureData.setMeterPosition(meter);
        }
        signatureData.setSecondsIndex(10);
        String serverIDHex = "09 01 45 4D 48 00 00 6B B3 28";
        signatureData.setServerId(Hex.decode(serverIDHex));
        if (customerId != null) {
            signatureData.setContractId(Hex.decode(customerId), true);
        }
        signatureData.setProvidedSignature(Hex.decode("AA 00 FF AA 00 EE"));
        signatureData.setProvidedSignature(new byte[48]);
        return signatureData;
    }
}
