package com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly;

import com.hastobe.transparenzsoftware.TestUtils;
import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class SignatureOnlyVerificationParserTest  {

    public static String PUBLIC_KEY_BASE_64 = "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z";
    public static String PUBLIC_KEY_FALSE_BASE_64 = "XLyYACZ8/kaDQB+WzaTo7xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z";

    public static String TEST_SIG_ONLY_NO_XML = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "&lt;signedMeterValue>\n" +
            "&lt;publicKey encoding=\"base64\">\n" +
            "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z&lt;/publicKey>\n" +
            "&lt;meterValueSignature encoding=\"base64\">\n" +
            "5YP4W8xZDLC1Cr9uX5BiWYKFeBUP+t4i6fnt58dadMxk20zbgnxZgjrYH8Kl0xzbAAI=&lt;/meterValueSignature>\n" +
            "&lt;signatureMethod>\n" +
            "ECDSA192SHA256&lt;/signatureMethod>\n" +
            "&lt;encodingMethod>\n" +
            "EDL&lt;/encodingMethod>\n" +
            "&lt;encodedMeterValue encoding=\"base64\">\n" +
            "CQFFTUgAAH7gNd8UrVsItpUAAAcAAAABAAERAP8e/4oQAAAAAAAAAAIxYTdkNmE0MwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN4UrVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=&lt;/encodedMeterValue>\n";

    public static String TEST_SIG_ONLY_NO_KEY = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "&lt;signedMeterValue>\n" +
            "&lt;meterValueSignature encoding=\"base64\">\n" +
            "5YP4W8xZDLC1Cr9uX5BiWYKFeBUP+t4i6fnt58dadMxk20zbgnxZgjrYH8Kl0xzbAAI=&lt;/meterValueSignature>\n" +
            "&lt;signatureMethod>\n" +
            "ECDSA192SHA256&lt;/signatureMethod>\n" +
            "&lt;encodingMethod>\n" +
            "EDL&lt;/encodingMethod>\n" +
            "&lt;encodedMeterValue encoding=\"base64\">\n" +
            "CQFFTUgAAH7gNd8UrVsItpUAAAcAAAABAAERAP8e/4oQAAAAAAAAAAIxYTdkNmE0MwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN4UrVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=&lt;/encodedMeterValue>\n" +
            "&lt;/signedMeterValue>";

    /**
     * Verifies that a xml structure expected in sml xml format can be read
     */
    @Test
    public void test_xml_can_parse(){
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        Assert.assertTrue(parser.canParseData(TestUtils.TEST_SIG_ONLY));
    }

    /**
     * Verifies that an incomplete xml cannot be read by the app
     */
    @Test
    public void test_xml_can_not_parse(){
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        Assert.assertFalse(parser.canParseData(TEST_SIG_ONLY_NO_XML));
    }

    /**
     * Verifies that an embedded public key cannot be read
     */
    @Test
    public void test_read_public_key(){
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        String s = parser.parsePublicKey(TestUtils.TEST_SIG_ONLY);
        Assert.assertNotNull(s);
    }

    /**
     * Verifies that the verification type is set
     */
    @Test
    public void test_type_set(){
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        Assert.assertEquals(VerificationType.EDL_40_SIG, parser.getVerificationType());
    }

    /**
     * Verifies that the xml can be read and a result with verified
     * true will be returned. Also additional fields will be set
     */
    @Test
    public void verify_xml() throws DecodingException {
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        VerificationResult verificationResult = parser.parseAndVerify(TestUtils.TEST_SIG_ONLY, EncodingType.base64Decode(PUBLIC_KEY_BASE_64));
        Assert.assertTrue(verificationResult.isVerified());
        Assert.assertEquals(1, verificationResult.getMeters().size());
        Meter meter = verificationResult.getMeters().get(0);
        Assert.assertEquals(0.42340000000000005, meter.getValue(), 0);
        LocalDateTime dateTime = LocalDateTime.of(2018, Month.SEPTEMBER, 27, 17, 35, 27);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+00:00"));
        Assert.assertEquals(offsetDateTime, meter.getTimestamp());
    }

    /**
     * Verifies that the xml can be read and a result with verified
     * true will be returned. Also additional fields will be set
     */
    @Test
    public void verify_xml_no_embedded_key() throws DecodingException {
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        VerificationResult verificationResult = parser.parseAndVerify(TEST_SIG_ONLY_NO_KEY, EncodingType.base64Decode(PUBLIC_KEY_BASE_64));
        Assert.assertTrue(verificationResult.isVerified());
        Assert.assertEquals(1, verificationResult.getMeters().size());
        Meter meter = verificationResult.getMeters().get(0);
        Assert.assertEquals(0.42340000000000005, meter.getValue(), 0);
        LocalDateTime dateTime = LocalDateTime.of(2018, Month.SEPTEMBER, 27, 17, 35, 27);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+00:00"));
        Assert.assertEquals(offsetDateTime, meter.getTimestamp());

    }

    /**
     * Verifies that the xml can be read and a result with verified
     * true will be returned. Also additional fields will be set
     */
    @Test
    public void verify_wrong_pk() throws DecodingException {
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        VerificationResult verificationResult = parser.parseAndVerify(TestUtils.TEST_SIG_ONLY, EncodingType.base64Decode(PUBLIC_KEY_BASE_64));
        //this is true as the embedded key is used
        Assert.assertTrue(verificationResult.isVerified());
        Assert.assertEquals(1, verificationResult.getMeters().size());

    }

    /**
     * Verifies that the xml can be read and a result with verified
     * true will be returned. Also additional fields will be set
     */
    @Test
    public void verify_xml_2() throws DecodingException {
        SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
        VerificationResult verificationResult = parser.parseAndVerify(TestUtils.TEST_SIG_ONLY_2, EncodingType.base64Decode(PUBLIC_KEY_BASE_64));
        Assert.assertTrue(verificationResult.isVerified());
        Assert.assertEquals(1, verificationResult.getMeters().size());
        Meter meter = verificationResult.getMeters().get(0);
        Assert.assertEquals(0.4235, meter.getValue(), 0);
        LocalDateTime dateTime = LocalDateTime.of(2018, Month.SEPTEMBER, 27, 17, 37, 16);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+00:00"));
        Assert.assertEquals(offsetDateTime, meter.getTimestamp());

        Assert.assertEquals(11, verificationResult.getVerifiedData().getAdditionalData().size());
    }
}
