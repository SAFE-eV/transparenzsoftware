package com.hastobe.transparenzsoftware.output;

import com.hastobe.transparenzsoftware.TestUtils;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.format.alfen.AlfenSignature;
import com.hastobe.transparenzsoftware.verification.format.alfen.AlfenVerifiedData;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.*;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class OutputTest {

    private static String value1 = "94 3D 00 00 00 00 00 00";
    private static String value2 = "94 3E 00 00 00 00 00 00";

    @Test
    public void test_create_results_single_value() throws JAXBException, DecodingException, ValidationException {
        VerificationParserFactory factory = new VerificationParserFactory();

        AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value1));
        AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{0, 1, 0, 0}, EncodingType.hexDecode(value1));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
        VerificationResult resultStart = new VerificationResult(start);
        VerificationResult resultStop = new VerificationResult(start);

        Values values = new Values();
        Value valueStart = new Value();
        valueStart.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
        values.getValues().add(valueStart);
        Value valueStop = new Value();
        valueStop.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
        values.getValues().add(valueStop);


        List<VerificationResult> ts = Arrays.asList(resultStart, resultStop);
        Output output = new Output(factory.getVerifiedDataClasses(), ts, values);
        Results results = output.createResults();
        Assert.assertEquals(2, results.getResults().size());
        Result result1 = results.getResults().get(0);
        Assert.assertEquals("Verified", result1.getStatus());
        Assert.assertNotNull(result1.getVerifiedData());
        Assert.assertEquals(1, result1.getMeters().size());

        Result result2 = results.getResults().get(0);
        Assert.assertEquals("Verified", result2.getStatus());
        Assert.assertNotNull(result2.getVerifiedData());
        Assert.assertEquals(1, result2.getMeters().size());
    }

    @Test
    public void test_create_results_transaction() throws JAXBException, DecodingException, ValidationException {
        VerificationParserFactory factory = new VerificationParserFactory();

        AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value1));
        AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[]{0, 0, 0, 0}, new byte[]{1, 0, 0, 0}, EncodingType.hexDecode(value2));
        AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
        AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
        VerificationResult resultStart = new VerificationResult(start);
        VerificationResult resultStop = new VerificationResult(stop);

        Values values = new Values();
        Value valueStart = new Value();
        valueStart.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
        values.getValues().add(valueStart);
        Value valueStop = new Value();
        valueStop.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
        values.getValues().add(valueStop);


        List<VerificationResult> ts = Arrays.asList(resultStart, resultStop, VerificationResult.mergeVerificationData(resultStart, resultStop, BigInteger.ONE));
        Output output = new Output(factory.getVerifiedDataClasses(), ts, values);
        Results results = output.createResults();
        Assert.assertEquals(3, results.getResults().size());
        Result result1 = results.getResults().get(0);
        Assert.assertEquals("Verified", result1.getStatus());
        Assert.assertNotNull(result1.getVerifiedData());
        Assert.assertEquals(1, result1.getMeters().size());

        Result result2 = results.getResults().get(1);
        Assert.assertEquals("Verified", result2.getStatus());
        Assert.assertNotNull(result2.getVerifiedData());
        Assert.assertEquals(1, result2.getMeters().size());

        Result result3 = results.getResults().get(2);
        Assert.assertEquals("Failed", result3.getStatus());
        Assert.assertNull(result3.getVerifiedData());
        Assert.assertNotNull(result3.getTimeDiff());
        Assert.assertNotNull(result3.getMeterDiff());
        Assert.assertEquals(2, result3.getMeters().size());
        String e = result3.getErrorMessage();
        Assert.assertTrue(e.indexOf("Paginierung von Startwert ist gr") >= 0);
        Assert.assertTrue(e.indexOf("oder gleich dem Endwert") >= 0);
    }
}
