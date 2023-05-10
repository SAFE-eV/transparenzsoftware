package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;

public class EDLMennekesVerificationParserTest {

	private static final String PUBLIC_KEY = "6DACB9C5466A25B3EB9F6466B53457C84A27448B01A64A278C0A28DAC95F2B45DF39B79918A9A4D2E3551F3FE925D09D";
	private static final String PUBLIC_KEY2 = "ddb6d736e4664afbf2748436dfbfbee1975803561fa75ba2b770ce93d10a5b3fd61e0017ffad7917d0146c5abe38c1a5";

	@Test
	public void verify_values() throws ValidationException, DecodingException {
		// URL url =
		// this.getClass().getResource("/xml/test_mennekes_charging_processes.xml");
		final URL url = this.getClass().getResource("/Mennekes/de.xml");
		final File testfile = new File(url.getFile());
		final String testData = TestUtils.readFile(testfile);

		final EDLMennekesVerificationParser parser = new EDLMennekesVerificationParser();
		final VerificationResult verificationResult = parser.parseAndVerify(testData,
				EncodingType.hexDecode(PUBLIC_KEY2), IntrinsicVerified.NOT_VERIFIED);
		Assert.assertTrue(verificationResult.isVerified());
		Assert.assertEquals(2, verificationResult.getMeters().size());
		Assert.assertEquals(Meter.Type.START, verificationResult.getMeters().get(0).getType());
		Assert.assertEquals(Meter.Type.STOP, verificationResult.getMeters().get(1).getType());
	}

	@Test
	public void verify_fail() throws ValidationException, DecodingException {
		// URL url =
		// this.getClass().getResource("/xml/test_mennekes_charging_processes.xml");
		final URL url = this.getClass().getResource("/Mennekes/de.xml");
		final File testfile = new File(url.getFile());
		String testData = TestUtils.readFile(testfile);
		// Modify a value to fail:
		testData = testData.replaceAll("<Value>314940</Value>", "<Value>314941</Value>");
		final EDLMennekesVerificationParser parser = new EDLMennekesVerificationParser();
		final VerificationResult verificationResult = parser.parseAndVerify(testData,
				EncodingType.hexDecode(PUBLIC_KEY2), IntrinsicVerified.NOT_VERIFIED);
		Assert.assertFalse(verificationResult.isVerified());
		Assert.assertEquals(2, verificationResult.getMeters().size());
		Assert.assertEquals(Meter.Type.START, verificationResult.getMeters().get(0).getType());
		Assert.assertEquals(Meter.Type.STOP, verificationResult.getMeters().get(1).getType());
	}

	@Test
	public void verify_publicKey() throws ValidationException, DecodingException {
		final URL url = this.getClass().getResource("/xml/test_mennekes_charging_processes.xml");
		final File testfile = new File(url.getFile());
		final String testData = TestUtils.readFile(testfile);

		final EDLMennekesVerificationParser parser = new EDLMennekesVerificationParser();
		Assert.assertEquals(PUBLIC_KEY, parser.parsePublicKey(testData));
	}

	@Test
	public void verify_publicKey_splitted() throws ValidationException, DecodingException {
		final URL url = this.getClass().getResource("/xml/test_mennekes_charging_processes.xml");
		final File testfile = new File(url.getFile());
		final String testData = TestUtils.readFile(testfile);

		final EDLMennekesVerificationParser parser = new EDLMennekesVerificationParser();
		Assert.assertEquals(Utils.splitStringToGroups(PUBLIC_KEY, 4), parser.createFormattedKey(testData));
	}
}
