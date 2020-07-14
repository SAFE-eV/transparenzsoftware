package com.hastobe.transparenzsoftware.verification.format.ocmf;

import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class OCMFVerificationParserTest {

    private final String ABL_TEST_DATA = "OCMF|{\"FV\":\"0.1\",\"VI\":\"ABL\",\"VV\":\"1.4p3\",\"PG\":\"T12345\",\"MV\":\"Phoenix Contact\",\"MM\":\"EEM-350-D-MCB\",\"MS\":\"BQ27400330016\",\"MF\":\"1.0\",\"IS\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],\"IT\":\"ISO14443\",\"ID\":\"1F2D3A4F5506C7\",\"RD\":[{\"TM\":\"2018-07-24T13:22:04,000+0200 S\",\"TX\":\"B\",\"RV\":2935.6,\"RI\":\"1-b:1.8.e\",\"RU\":\"kWh\",\"EI\":567,\"ST\":\"G\"}]}|{\"SA\":\"ECDSA-secp256k1-SHA256\",\"SD\":\"3046022100A7F1FD39278A88432E1AB81229C34CE1066885D0EAD8810DB900018A4960888302210089004420623749BF75561F29685CD87D6853EC08E83BD1A15C5DAFF9F03F4115\"}";
    private final String ABL_TEST_INVALID_VERSION_DATA = "OCMF|{\"FV\":\"0.0\",\"VI\":\"ABL\",\"VV\":\"1.4p3\",\"PG\":\"T12345\",\"MV\":\"Phoenix Contact\",\"MM\":\"EEM-350-D-MCB\",\"MS\":\"BQ27400330016\",\"MF\":\"1.0\",\"IS\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],\"IT\":\"ISO14443\",\"ID\":\"1F2D3A4F5506C7\",\"RD\":[{\"TM\":\"2018-07-24T13:22:04,000+0200 S\",\"TX\":\"B\",\"RV\":2935.6,\"RI\":\"1-b:1.8.e\",\"RU\":\"kWh\",\"EI\":567,\"ST\":\"G\"}]}|{\"SA\":\"ECDSA-secp256k1-SHA256\",\"SD\":\"3046022100A7F1FD39278A88432E1AB81229C34CE1066885D0EAD8810DB900018A4960888302210089004420623749BF75561F29685CD87D6853EC08E83BD1A15C5DAFF9F03F4115\"}";
    private final String ABL_TEST_DATA_NULL_VALUES = "OCMF|{\"FV\":\"0.1\",\"VI\":\"ABL\",\"VV\":\"1.4p3\",\"PG\":\"T12345\",\"MV\":\"Phoenix Contact\",\"MM\":\"EEM-350-D-MCB\",\"MS\":\"BQ27400330016\",\"MF\":\"1.0\",\"IS\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],\"IT\":\"ISO14443\",\"ID\":\"1F2D3A4F5506C7\",\"RD\":[{\"TM\":\"2018-07-24T13:22:04,000+0200 S\",\"RV\":2935.6,\"RI\":\"1-b:1.8.e\",\"RU\":\"kWh\",\"EI\":567,\"ST\":\"G\"}]}|{\"SA\":\"ECDSA-secp256k1-SHA256\",\"SD\":\"3046022100A7F1FD39278A88432E1AB81229C34CE1066885D0EAD8810DB900018A4960888302210089004420623749BF75561F29685CD87D6853EC08E83BD1A15C5DAFF9F03F4115\"}";
    private final String INVALID_FORMAT_1 = "OCMA|{\"FV\":\"0.1\",\"VI\":\"ABL\",\"VV\":\"1.4p3\",\"PG\":\"T12345\",\"MV\":\"Phoenix Contact\",\"MM\":\"EEM-350-D-MCB\",\"MS\":\"BQ27400330016\",\"MF\":\"1.0\",\"IS\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],\"IT\":\"ISO14443\",\"ID\":\"1F2D3A4F5506C7\",\"RD\":[{\"TM\":\"2018-07-24T13:22:04,000+0200 S\",\"TX\":\"B\",\"RV\":2935.6,\"RI\":\"1-b:1.8.e\",\"RU\":\"kWh\",\"EI\":567,\"ST\":\"G\"}]}|{\"SA\":\"ECDSA-secp256k1-SHA256\",\"SD\":\"3046022100A7F1FD39278A88432E1AB81229C34CE1066885D0EAD8810DB900018A4960888302210089004420623749BF75561F29685CD87D6853EC08E83BD1A15C5DAFF9F03F4115\"}";

    private final String ABL_PUBLIC_KEY = "3056301006072a8648ce3d020106052b8104000a034200044e4970098eeff5e0e286e3a38552679771b89315a49dddf66ebac6f176fb02df9841091010e6850510540dad0cf967fd8de0ab25198282b39597ddce09edf459";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFormatWrongName(){
        OCMFVerificationParser parser = new OCMFVerificationParser();
        Assert.assertFalse(parser.canParseData(INVALID_FORMAT_1));
    }

    @Test
    public void testVerify() throws DecodingException {
        OCMFVerificationParser parser = new OCMFVerificationParser();
        VerificationResult verificationResult = parser.parseAndVerify(ABL_TEST_DATA, EncodingType.hexDecode(ABL_PUBLIC_KEY));
        Assert.assertTrue(verificationResult.isVerified());

    }


    @Test
    public void testAblData() throws ValidationException {
        OCMFVerificationParser parser = new OCMFVerificationParser();
        Assert.assertTrue(parser.canParseData(ABL_TEST_DATA));
        OCMF ocmf = parser.parseString(ABL_TEST_DATA);
        OCMFPayloadData data = ocmf.getData();
        Assert.assertEquals("0.1", data.getFV());
        Assert.assertEquals("ABL", data.getGI());
        Assert.assertEquals("1.4p3", data.getGV());
        Assert.assertEquals("T12345", data.getPG());
        Assert.assertEquals("Phoenix Contact", data.getMV());
        Assert.assertEquals("EEM-350-D-MCB", data.getMM());
        Assert.assertEquals("BQ27400330016", data.getMS());
        Assert.assertEquals("1.0", data.getMF());
        Assert.assertEquals("VERIFIED", data.getIdLevel());
        Assert.assertEquals(2, data.getIF().size());
        Assert.assertEquals("RFID_PLAIN", data.getIF().get(0));
        Assert.assertEquals("OCPP_RS_TLS", data.getIF().get(1));
        Assert.assertEquals("ISO14443", data.getIT());
        Assert.assertEquals("1F2D3A4F5506C7", data.getID());

        List<? extends Reading> dataRD = data.getRD();
        Assert.assertEquals(1, dataRD.size());
        Reading rd = dataRD.get(0);
        Assert.assertEquals("2018-07-24T13:22:04,000+0200 S", rd.getTM());
        Assert.assertEquals("B", rd.getTX());
        Assert.assertEquals(new Double(2935.6), rd.getRV());
        Assert.assertEquals("1-b:1.8.e", rd.getRI());
        Assert.assertEquals("kWh", rd.getRU());
        Assert.assertEquals(new Double(567), rd.getEI());
        Assert.assertEquals("G", rd.getST());

        Assert.assertNotNull(ocmf.getSignature());
        Assert.assertEquals("3046022100A7F1FD39278A88432E1AB81229C34CE1066885D0EAD8810DB900018A4960888302210089004420623749BF75561F29685CD87D6853EC08E83BD1A15C5DAFF9F03F4115", ocmf.getSignature().getSD());
        Assert.assertEquals("ECDSA-secp256k1-SHA256", ocmf.getSignature().getSA());
        Assert.assertNull(ocmf.getSignature().getSE());
        Assert.assertNull(ocmf.getSignature().getSM());
        Assert.assertNotNull(ocmf.getSignature().getSignatureMimeType());
    }

    @Test
    public void testInvalidVersion() throws ValidationException {
        expectedException.expect(OCMFValidationException.class);
        expectedException.expectMessage("Not compatible with OCMF version 0.0");
        OCMFVerificationParser parser = new OCMFVerificationParser();
        OCMF ocmf = parser.parseString(ABL_TEST_INVALID_VERSION_DATA);

    }

    @Test
    public void testAblDataNullValues() throws ValidationException {
        OCMFVerificationParser parser = new OCMFVerificationParser();
        Assert.assertTrue(parser.canParseData(ABL_TEST_DATA_NULL_VALUES));
        OCMF ocmf = parser.parseString(ABL_TEST_DATA_NULL_VALUES);
        OCMFPayloadData data = ocmf.getData();
        Assert.assertEquals("0.1", data.getFV());
        Assert.assertEquals("ABL", data.getGI());
        Assert.assertEquals("1.4p3", data.getGV());
        Assert.assertEquals("T12345", data.getPG());
        Assert.assertEquals("Phoenix Contact", data.getMV());
        Assert.assertEquals("EEM-350-D-MCB", data.getMM());
        Assert.assertEquals("BQ27400330016", data.getMS());
        Assert.assertEquals("1.0", data.getMF());
        Assert.assertEquals("VERIFIED", data.getIdLevel());
        Assert.assertEquals(2, data.getIF().size());
        Assert.assertEquals("RFID_PLAIN", data.getIF().get(0));
        Assert.assertEquals("OCPP_RS_TLS", data.getIF().get(1));
        Assert.assertEquals("ISO14443", data.getIT());
        Assert.assertEquals("1F2D3A4F5506C7", data.getID());

        List<? extends Reading> dataRD = data.getRD();
        Assert.assertEquals(1, dataRD.size());
        Reading rd = dataRD.get(0);
        Assert.assertEquals("2018-07-24T13:22:04,000+0200 S", rd.getTM());
        Assert.assertNull(rd.getTX());
        Assert.assertEquals(new Double(2935.6), rd.getRV());
        Assert.assertEquals("1-b:1.8.e", rd.getRI());
        Assert.assertEquals("kWh", rd.getRU());
        Assert.assertEquals(new Double(567), rd.getEI());
        Assert.assertEquals("G", rd.getST());
    }

    @Test
    public void testVerificationResult() throws DecodingException {
        OCMFVerificationParser parser = new OCMFVerificationParser();
        VerificationResult verificationResult = parser.parseAndVerify(ABL_TEST_DATA, EncodingType.hexDecode(ABL_PUBLIC_KEY));
        Assert.assertTrue(verificationResult.isVerified());
        Assert.assertEquals(2935.6, verificationResult.getMeters().get(0).getValue(), 0);
        //2018-07-24T13:22:04,000+0200 S
        LocalDateTime localDateTime = LocalDateTime.of(2018, Month.JULY, 24, 13, 22, 4);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(2));
        Assert.assertEquals(offsetDateTime, verificationResult.getMeters().get(0).getTimestamp());

        Assert.assertNotNull(verificationResult.getAdditionalVerificationData());
        Assert.assertFalse(verificationResult.getAdditionalVerificationData().isEmpty());
    }
}
