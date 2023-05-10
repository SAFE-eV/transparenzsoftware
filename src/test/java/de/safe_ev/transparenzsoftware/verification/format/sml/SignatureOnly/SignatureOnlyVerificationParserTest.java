package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;

public class SignatureOnlyVerificationParserTest {

	private static String PUBLIC_KEY_BASE_64 = "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z";
	private static String PUBLIC_KEY_FALSE_BASE_64 = "XLyYACZ8/kaDQB+WzaTo7xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z";

	public static String TEST_SIG_ONLY_NO_XML = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
			+ "&lt;signedMeterValue>\n" + "&lt;publicKey encoding=\"base64\">\n"
			+ "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z&lt;/publicKey>\n"
			+ "&lt;meterValueSignature encoding=\"base64\">\n"
			+ "5YP4W8xZDLC1Cr9uX5BiWYKFeBUP+t4i6fnt58dadMxk20zbgnxZgjrYH8Kl0xzbAAI=&lt;/meterValueSignature>\n"
			+ "&lt;signatureMethod>\n" + "ECDSA192SHA256&lt;/signatureMethod>\n" + "&lt;encodingMethod>\n"
			+ "EDL&lt;/encodingMethod>\n" + "&lt;encodedMeterValue encoding=\"base64\">\n"
			+ "CQFFTUgAAH7gNd8UrVsItpUAAAcAAAABAAERAP8e/4oQAAAAAAAAAAIxYTdkNmE0MwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN4UrVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=&lt;/encodedMeterValue>\n";

	public static String TEST_SIG_ONLY_NO_KEY = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
			+ "&lt;signedMeterValue>\n" + "&lt;meterValueSignature encoding=\"base64\">\n"
			+ "5YP4W8xZDLC1Cr9uX5BiWYKFeBUP+t4i6fnt58dadMxk20zbgnxZgjrYH8Kl0xzbAAI=&lt;/meterValueSignature>\n"
			+ "&lt;signatureMethod>\n" + "ECDSA192SHA256&lt;/signatureMethod>\n" + "&lt;encodingMethod>\n"
			+ "EDL&lt;/encodingMethod>\n" + "&lt;encodedMeterValue encoding=\"base64\">\n"
			+ "CQFFTUgAAH7gNd8UrVsItpUAAAcAAAABAAERAP8e/4oQAAAAAAAAAAIxYTdkNmE0MwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN4UrVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=&lt;/encodedMeterValue>\n"
			+ "&lt;/signedMeterValue>";

	public static String TEST_SIG_INV_DATA = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
			+ "&lt;signedMeterValue>\n" + "&lt;publicKey encoding=\"base64\">\n"
			+ "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z&lt;/publicKey>\n"
			+ "&lt;meterValueSignature encoding=\"base64\">\n"
			+ "5YP4W8xZDLC1Cr9uX5BiWYKFeBUP+t4i6fnt58dadMxk20zbgmxZgjrYH8Kl0xzbAAI=&lt;/meterValueSignature>\n"
			+ "&lt;signatureMethod>\n" + "ECDSA192SHA256&lt;/signatureMethod>\n" + "&lt;encodingMethod>\n"
			+ "EDL&lt;/encodingMethod>\n" + "&lt;encodedMeterValue encoding=\"base64\">\n"
			+ "CQFFTUgAAH7gNd8UrVsItpUAAAcAAAABAAERAP8e/4oQAAAAAAAAAAIxYTdkNmE0MwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN4UrVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=&lt;/encodedMeterValue>\n"
			+ "&lt;/signedMeterValue>";

	/**
	 * Verifies that a xml structure expected in sml xml format can be read
	 */
	@Test
	public void test_xml_can_parse() {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		Assert.assertTrue(parser.canParseData(TestUtils.TEST_SIG_ONLY));
	}

	/**
	 * Verifies that an incomplete xml cannot be read by the app
	 */
	@Test
	public void test_xml_can_not_parse() {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		Assert.assertFalse(parser.canParseData(TEST_SIG_ONLY_NO_XML));
	}

	/**
	 * Verifies that an embedded public key cannot be read
	 */
	@Test
	public void test_read_public_key() {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		final String s = parser.parsePublicKey(TestUtils.TEST_SIG_ONLY);
		Assert.assertNotNull(s);
	}

	/**
	 * Verifies that the verification type is set
	 */
	@Test
	public void test_type_set() {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		Assert.assertEquals(VerificationType.EDL_40_SIG, parser.getVerificationType());
	}

	/**
	 * Verifies that the xml can be read and a result with verified true will be
	 * returned. Also additional fields will be set
	 */
	@Test
	public void verify_xml() throws DecodingException {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		final VerificationResult verificationResult = parser.parseAndVerify(TestUtils.TEST_SIG_ONLY,
				EncodingType.base64Decode(PUBLIC_KEY_BASE_64), IntrinsicVerified.NOT_VERIFIED);
		Assert.assertTrue(verificationResult.isVerified());
		Assert.assertEquals(1, verificationResult.getMeters().size());
		final Meter meter = verificationResult.getMeters().get(0);
		Assert.assertEquals(0.42340000000000005, meter.getValue(), 0);
		final LocalDateTime dateTime = LocalDateTime.of(2018, Month.SEPTEMBER, 27, 17, 35, 27);
		final OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+00:00"));
		Assert.assertEquals(offsetDateTime, meter.getTimestamp());
	}

	/**
	 * Verifies that the xml can be read and a result with verified true will be
	 * returned. Also additional fields will be set
	 */
	@Test
	public void verify_xml_no_embedded_key() throws DecodingException {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		final VerificationResult verificationResult = parser.parseAndVerify(TEST_SIG_ONLY_NO_KEY,
				EncodingType.base64Decode(PUBLIC_KEY_BASE_64), IntrinsicVerified.NOT_VERIFIED);
		Assert.assertTrue(verificationResult.isVerified());
		Assert.assertEquals(1, verificationResult.getMeters().size());
		final Meter meter = verificationResult.getMeters().get(0);
		Assert.assertEquals(0.42340000000000005, meter.getValue(), 0);
		final LocalDateTime dateTime = LocalDateTime.of(2018, Month.SEPTEMBER, 27, 17, 35, 27);
		final OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+00:00"));
		Assert.assertEquals(offsetDateTime, meter.getTimestamp());

	}

	/**
	 * Verifies that the xml can be read and a result with verified true will be
	 * returned. Also additional fields will be set
	 */
	@Test
	public void verify_wrong_pk() throws DecodingException {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		final VerificationResult verificationResult = parser.parseAndVerify(TestUtils.TEST_SIG_ONLY,
				EncodingType.base64Decode(PUBLIC_KEY_FALSE_BASE_64), IntrinsicVerified.NOT_VERIFIED);
		// this is true as the embedded key is used
		Assert.assertFalse(verificationResult.isVerified());
		Assert.assertEquals(0, verificationResult.getMeters().size());

	}

	/**
	 * Verifies that the xml can be read and a result with verified true will be
	 * returned. Also additional fields will be set
	 */
	@Test
	public void verify_wrong_data() throws DecodingException {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		final VerificationResult verificationResult = parser.parseAndVerify(TEST_SIG_INV_DATA,
				EncodingType.base64Decode(PUBLIC_KEY_BASE_64), IntrinsicVerified.NOT_VERIFIED);
		// this is true as the embedded key is used
		Assert.assertFalse(verificationResult.isVerified());
		Assert.assertEquals(1, verificationResult.getMeters().size());

	}

	/**
	 * Verifies that the xml can be read and a result with verified true will be
	 * returned. Also additional fields will be set
	 */
	@Test
	public void verify_xml_2() throws DecodingException {
		final SignatureOnlyVerificationParser parser = new SignatureOnlyVerificationParser();
		final VerificationResult verificationResult = parser.parseAndVerify(TestUtils.TEST_SIG_ONLY_2,
				EncodingType.base64Decode(PUBLIC_KEY_BASE_64), IntrinsicVerified.NOT_VERIFIED);
		Assert.assertTrue(verificationResult.isVerified());
		Assert.assertEquals(1, verificationResult.getMeters().size());
		final Meter meter = verificationResult.getMeters().get(0);
		Assert.assertEquals(0.4235, meter.getValue(), 0);
		final LocalDateTime dateTime = LocalDateTime.of(2018, Month.SEPTEMBER, 27, 17, 37, 16);
		final OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+00:00"));
		Assert.assertEquals(offsetDateTime, meter.getTimestamp());

		Assert.assertEquals(11, verificationResult.getVerifiedData().getAdditionalData().size());
	}
}
