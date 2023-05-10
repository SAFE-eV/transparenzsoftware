package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import java.io.File;
import java.net.URL;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.ASN1Exception;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.TransactionValidationException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;
import de.safe_ev.transparenzsoftware.verification.Verifier;
import de.safe_ev.transparenzsoftware.verification.input.InputReader;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

public class OCMFVerifierTest {

	private final static String UNIFIED_JSON = "{\"FV\":\"0.1\",\"VI\":\"ABL\",\"VV\":\"1.4p3\",\"PG\":\"T12345\",\"MV\":\"Phoenix Contact\",\"MM\":\"EEM-350-D-MCB\",\"MS\":\"BQ27400330016\",\"MF\":\"1.0\",\"IS\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],\"IT\":\"ISO14443\",\"ID\":\"1F2D3A4F5506C7\",\"RD\":[{\"TM\":\"2018-07-24T13:22:04,000+0200 S\",\"TX\":\"B\",\"RV\":2935.6,\"RI\":\"1-b:1.8.e\",\"RU\":\"kWh\",\"EI\":567,\"ST\":\"G\"}]}";
	private final static String SD_DATA = "3046022100A7F1FD39278A88432E1AB81229C34CE1066885D0EAD8810DB900018A4960888302210089004420623749BF75561F29685CD87D6853EC08E83BD1A15C5DAFF9F03F4115";
	private final static String PUBLIC_KEY = "30 56 30 10 06 07 2A 86  48 CE 3D 02 01 06 05 2B"
			+ "81 04 00 0A 03 42 00 04  4E 49 70 09 8E EF F5 E0" + "E2 86 E3 A3 85 52 67 97  71 B8 93 15 A4 9D DD F6"
			+ "6E BA C6 F1 76 FB 02 DF  98 41 09 10 10 E6 85 05" + "10 54 0D AD 0C F9 67 FD  8D E0 AB 25 19 82 82 B3"
			+ "95 97 DD CE 09 ED F4 59";

	@Test
	public void testVerify() throws ValidationException, ASN1Exception {
		final OCMFVerifier verifier = new OCMFVerifier("secp256k1");

		final byte[] sign = Hex.decode(SD_DATA);
		final byte[] payload = UNIFIED_JSON.getBytes();
		final byte[] publicKey = Hex.decode(PUBLIC_KEY);

		Assert.assertTrue(verifier.verify(publicKey, sign, payload));
	}

	@Test
	public void test_keba_test_file() throws InvalidInputException, DecodingException {
		final URL url = this.getClass().getResource("/xml/keba_test.xml");
		final File testfile = new File(url.getFile());
		final InputReader inputReader = new InputReader();
		final Values values = inputReader.readFile(testfile);
		int i = 0;
		for (final Value value : values.getValues()) {
			final OCMFVerificationParser verificationParser = new OCMFVerificationParser();

			final VerificationResult verificationResult = verificationParser.parseAndVerify(
					value.getSignedData().getValue(), EncodingType.hexDecode(value.getPublicKey().getValue()),
					IntrinsicVerified.NOT_VERIFIED);
			Assert.assertTrue("Failed to verify keba data set on value " + i, verificationResult.isVerified());
			i++;
		}
		Assert.assertEquals(i, 100);
	}

	@Test
	public void test_ocmf_transaction_test_file()
			throws InvalidInputException, DecodingException, TransactionValidationException {
		final URL url = this.getClass().getResource("/xml/test_ocmf_transaction_two_values.xml");
		final File testfile = new File(url.getFile());
		final InputReader inputReader = new InputReader();
		final Values values = inputReader.readFile(testfile);
		int i = 0;
		final OCMFVerificationParser verificationParser = new OCMFVerificationParser();
		for (final Value value : values.getValues()) {

			final VerificationResult verificationResult = verificationParser.parseAndVerify(
					value.getSignedData().getValue(), EncodingType.hexDecode(value.getPublicKey().getValue()),
					IntrinsicVerified.NOT_VERIFIED);
			Assert.assertTrue("Failed to verify ocmf data set on value " + i, verificationResult.isVerified());
			i++;
		}
		final VerificationParserFactory factory = new VerificationParserFactory();
		final Verifier verifier = new Verifier(factory);
		final VerificationResult verificationResult = verifier.verifyTransaction(verificationParser, values.getValues(),
				values.getValues().get(0).getPublicKey().getValue());
		Assert.assertTrue(verificationResult.isVerified());
		Assert.assertEquals(4, verificationResult.getMeters().size());
	}
}
