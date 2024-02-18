package de.safe_ev.transparenzsoftware.output;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenSignature;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenVerifiedData;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.Result;
import de.safe_ev.transparenzsoftware.verification.xml.Results;
import de.safe_ev.transparenzsoftware.verification.xml.SignedData;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

public class OutputTest {

	private static String value1 = "94 3D 00 00 00 00 00 00";
	private static String value2 = "94 3E 00 00 00 00 00 00";

	@Test
	public void test_create_results_single_value() throws JAXBException, DecodingException, ValidationException {
		final VerificationParserFactory factory = new VerificationParserFactory();

		final AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[] { 0, 0, 0, 0 },
				new byte[] { 1, 0, 0, 0 }, EncodingType.hexDecode(value1));
		final AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[] { 0, 0, 0, 0 },
				new byte[] { 0, 1, 0, 0 }, EncodingType.hexDecode(value1));
		final AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
		final AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
		final VerificationResult resultStart = new VerificationResult(start, IntrinsicVerified.NOT_VERIFIED);
		final VerificationResult resultStop = new VerificationResult(start, IntrinsicVerified.NOT_VERIFIED);

		final Values values = new Values();
		final Value valueStart = new Value();
		valueStart.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
		values.getValues().add(valueStart);
		final Value valueStop = new Value();
		valueStop.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
		values.getValues().add(valueStop);

		final List<VerificationResult> ts = Arrays.asList(resultStart, resultStop);
		final Output output = new Output(factory.getVerifiedDataClasses(), ts, values);
		final Results results = output.createResults();
		Assert.assertEquals(2, results.getResults().size());
		final Result result1 = results.getResults().get(0);
		Assert.assertEquals("Verified", result1.getStatus());
		Assert.assertNotNull(result1.getVerifiedData());
		Assert.assertEquals(1, result1.getMeters().size());

		final Result result2 = results.getResults().get(0);
		Assert.assertEquals("Verified", result2.getStatus());
		Assert.assertNotNull(result2.getVerifiedData());
		Assert.assertEquals(1, result2.getMeters().size());
	}

	@Test
	public void test_create_results_transaction() throws JAXBException, DecodingException, ValidationException {
		final VerificationParserFactory factory = new VerificationParserFactory();

		final AlfenSignature startData = TestUtils.createAlfenSignatureData(new byte[] { 0, 0, 0, 0 },
				new byte[] { 1, 0, 0, 0 }, EncodingType.hexDecode(value1));
		final AlfenSignature stopData = TestUtils.createAlfenSignatureData(new byte[] { 0, 0, 0, 0 },
				new byte[] { 1, 0, 0, 0 }, EncodingType.hexDecode(value2));
		final AlfenVerifiedData start = new AlfenVerifiedData(startData, EncodingType.PLAIN);
		final AlfenVerifiedData stop = new AlfenVerifiedData(stopData, EncodingType.PLAIN);
		final VerificationResult resultStart = new VerificationResult(start, IntrinsicVerified.NOT_VERIFIED);
		final VerificationResult resultStop = new VerificationResult(stop, IntrinsicVerified.NOT_VERIFIED);

		final Values values = new Values();
		final Value valueStart = new Value();
		valueStart.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
		values.getValues().add(valueStart);
		final Value valueStop = new Value();
		valueStop.setSignedData(new SignedData(VerificationType.ALFEN, EncodingType.BASE32, "ABCDEDFGH"));
		values.getValues().add(valueStop);

		final List<VerificationResult> ts = Arrays.asList(resultStart, resultStop,
				VerificationResult.mergeVerificationData(resultStart, resultStop, BigInteger.ONE));
		final Output output = new Output(factory.getVerifiedDataClasses(), ts, values);
		final Results results = output.createResults();
		Assert.assertEquals(3, results.getResults().size());
		final Result result1 = results.getResults().get(0);
		Assert.assertEquals("Verified", result1.getStatus());
		Assert.assertNotNull(result1.getVerifiedData());
		Assert.assertEquals(1, result1.getMeters().size());

		final Result result2 = results.getResults().get(1);
		Assert.assertEquals("Verified", result2.getStatus());
		Assert.assertNotNull(result2.getVerifiedData());
		Assert.assertEquals(1, result2.getMeters().size());

		final Result result3 = results.getResults().get(2);
		Assert.assertEquals("Failed", result3.getStatus());
		Assert.assertNull(result3.getVerifiedData());
		Assert.assertNotNull(result3.getTimeDiff());
		Assert.assertNotNull(result3.getMeterDiff());
		Assert.assertEquals(2, result3.getMeters().size());
		final String e = result3.getErrorMessage();
		Assert.assertTrue(e.indexOf("Paginierung von Startwert ist gr") >= 0);
		Assert.assertTrue(e.indexOf("oder gleich dem Endwert") >= 0);
	}
}
