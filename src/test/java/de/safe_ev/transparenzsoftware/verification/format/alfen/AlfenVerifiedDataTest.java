package de.safe_ev.transparenzsoftware.verification.format.alfen;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenSignature;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenVerifiedData;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class AlfenVerifiedDataTest {
//byte[] value1 = EncodingType.hexDecode("94 3D 00 00 00 00 00 00"); //15764
    private static String value1 = "94 3D 00 00 00 00 00 00";
    private static String value2 = "94 3E 00 00 00 00 00 00";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_law_conform() throws RegulationLawException, ValidationException, DecodingException {
        AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value1));
        AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{0, 1, 0, 0}, EncodingType.hexDecode(value1));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
        Assert.assertTrue(start.lawConform(stop));
    }


    @Test
    public void test_law_conform_adapter_error_1() throws RegulationLawException, ValidationException, DecodingException {
        expectedException.expectMessage("Adapter error occured");
        byte[] status = Hex.decode("00 00 01 00");
        AlfenSignature startData = TestUtils.createAlfenSignatureData(status, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value1));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        start.calculateMeterError();
        start.calculateAdapterError();
    }

    @Test
    public void test_law_conform_adapter_error_2() throws RegulationLawException, ValidationException, DecodingException {
        expectedException.expectMessage("Adapter error occured");
        byte[] status = Hex.decode("00 00 00 80");
        AlfenSignature startData = TestUtils.createAlfenSignatureData(status, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value1));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        start.calculateMeterError();
        start.calculateAdapterError();
    }

    @Test
    public void test_law_conform_meter_error_1() throws RegulationLawException, ValidationException, DecodingException {
        expectedException.expectMessage("Meter error occured");
        byte[] status = Hex.decode("02 00 00 00");
        AlfenSignature startData = TestUtils.createAlfenSignatureData(status, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value1));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        start.calculateAdapterError();
        start.calculateMeterError();
    }

    @Test
    public void test_law_conform_meter_error_2() throws RegulationLawException, ValidationException, DecodingException {
        expectedException.expectMessage("Meter error occured");
        byte[] status = Hex.decode("04 00 00 00");
        AlfenSignature startData = TestUtils.createAlfenSignatureData(status, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value1));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        start.calculateAdapterError();
        start.calculateMeterError();
    }

    @Test
    public void test_law_conform_paging_wrong() throws RegulationLawException, ValidationException, DecodingException {
        expectedException.expectMessage("Pagination");
        AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{1, 1, 0, 0}, EncodingType.hexDecode(value1));
        AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{0, 1, 0, 0}, EncodingType.hexDecode(value2));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
        Assert.assertTrue(start.lawConform(stop));
    }

    @Test
    public void test_law_conform_meter_wrong() throws RegulationLawException, ValidationException, DecodingException {
        expectedException.expectMessage("Meter");
        AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value2));
        AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{0, 1, 0, 0}, EncodingType.hexDecode(value1));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
        Assert.assertTrue(start.lawConform(stop));
    }

    @Test
    public void test_law_conform_session() throws RegulationLawException, ValidationException, DecodingException {
        expectedException.expectMessage("Session");
        AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value2));
        AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{0, 1, 0, 0}, EncodingType.hexDecode(value1), new byte[]{1, 1, 1, 1});
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
        Assert.assertTrue(start.lawConform(stop));
    }

    private static byte[] bitStringConverter(String b){
        return new BigInteger(b.trim().replaceAll(" ", ""), 2).toByteArray();
    }


}
