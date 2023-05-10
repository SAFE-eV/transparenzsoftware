package de.safe_ev.transparenzsoftware.verification;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.output.MockVerificationContainedKeyParser;
import de.safe_ev.transparenzsoftware.output.MockVerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenVerificationParser;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;
import de.safe_ev.transparenzsoftware.verification.xml.PublicKey;
import de.safe_ev.transparenzsoftware.verification.xml.SignedData;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.VerifiedData;

public class VerifierTest {

	private final static String ALFEN_TEST_STRING_INIT = "AP;0;3;AMVBBEIORR2RGJLJ6YRZUGACQAXSDFCL66EIP3N7;BJKGK43UIRSXMAAROYYDCMZNUYFACRC2I4ADGAABIAAAAAAAQ6ACMAD5CH4FWAIAAEEAB7Y6ACJD2AAAAAAAAABRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASAAAAAEIAAAAA====;IGRCBV3TL45XIGPJU7QGD3H4V6ICQ75GLPWEFNEKZX3RTTKJI2FBXHPCWUIWL5OENEHE3SQRVACHG===;";
	public static final String PUBL_KEY_DECODED_NOT_EQUAL = "AMVBBEIORR2RGJLJ6YRZUGACQAXSDFCL66EIP3N8";
	public static final String PUBL_KEY_DECODED = "AMVBBEIORR2RGJLJ6YRZUGACQAXSDFCL66EIP3N7";

	private VerificationParserFactory factory;
	private MockVerificationParser mockVerificationParser;
	private MockVerificationContainedKeyParser mockVerificationContainedKeyParser;
	private Value alfenTestValue;
	private Value emptyValue;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockVerificationParser = new MockVerificationParser();
		mockVerificationContainedKeyParser = new MockVerificationContainedKeyParser();
		final List<VerificationParser> parserList = new ArrayList<>();
		parserList.add(mockVerificationParser);
		parserList.add(mockVerificationContainedKeyParser);
		factory = new VerificationParserFactory(parserList);
		alfenTestValue = new Value();
		alfenTestValue.setSignedData(new SignedData());
		alfenTestValue.getSignedData().setValue(ALFEN_TEST_STRING_INIT);
		emptyValue = new Value();
		emptyValue.setSignedData(new SignedData());
		emptyValue.getSignedData().setValue("");
	}

	@Test
	public void testParseValueEmptyData() {
		final VerificationParserFactory factory = new VerificationParserFactory();
		final Verifier verifier = new Verifier(factory);

		final Value value = new Value();
		value.setPublicKey(createPublicKey());
		final SignedData signedData = new SignedData();
		value.setSignedData(signedData);
		final VerificationResult result = verifier.verify(value);
		Assert.assertFalse(result.isVerified());
		Assert.assertEquals("Empty value provided", result.getErrorMessages().get(0).getMessage());
	}

	@Test
	public void testParseValueMennekesData() {
		final VerificationParserFactory factory = new VerificationParserFactory();
		final Verifier verifier = new Verifier(factory);

		final Value value = new Value();
		value.setPublicKey(createPublicKey());
		final SignedData signedData = new SignedData();
		signedData.setValue(TestUtils.SML_FULL);
		value.setSignedData(signedData);
		final VerificationResult result = verifier.verify(value);
		Assert.assertTrue(result.isVerified());
		Assert.assertEquals(new Double(0.2918), result.getMeters().get(0).getValue(), 0);

		final LocalDateTime localDateTime = LocalDateTime.of(2018, Month.AUGUST, 28, 18, 57, 37);
		final OffsetDateTime offsetTime = OffsetDateTime.of(localDateTime, ZoneOffset.of("+02:00"));
		Assert.assertEquals(offsetTime, result.getMeters().get(0).getTimestamp());
	}

	/**
	 * Verifies that if the parser instance is not a containedPublicKeyParser the
	 * same key will be returned as entered.
	 *
	 * @throws InvalidInputException
	 */
	@Test
	public void checkForEmbeddedPublicKeyNotEmbeddedTest() throws InvalidInputException {
		final Verifier verifier = new Verifier(factory);
		Assert.assertEquals("test", verifier.checkForEmbeddedPublicKey(mockVerificationParser, "test", emptyValue));
	}

	/**
	 * Verifies that if the embedded public key is the same as the provided the
	 * input key will be returned
	 *
	 * @throws InvalidInputException
	 */
	@Test
	public void checkForEmbeddedPublicKeyEmbeddedSameTest() throws InvalidInputException {
		final Verifier verifier = new Verifier(factory);
		mockVerificationContainedKeyParser.parsePublicKeyResult = "test";
		Assert.assertEquals("test",
				verifier.checkForEmbeddedPublicKey(mockVerificationContainedKeyParser, "test", emptyValue));
	}

	/**
	 * Verifies that if the provided public key is null the embedded will be
	 * returned
	 *
	 * @throws InvalidInputException
	 */
	@Test
	public void checkForEmbeddedPublicKeyProvidedNullTest() throws InvalidInputException {
		final Verifier verifier = new Verifier(factory);
		mockVerificationContainedKeyParser.parsePublicKeyResult = "test1";
		Assert.assertEquals("test1",
				verifier.checkForEmbeddedPublicKey(mockVerificationContainedKeyParser, null, emptyValue));
	}

	/**
	 * Verifies that if the provided public key is not the same as the embedded, an
	 * exception will be thrown.
	 *
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void checkForEmbeddedPublicKeyProvidedNotEqualTest() throws InvalidInputException {
		final Verifier verifier = new Verifier(factory);
		mockVerificationContainedKeyParser.parsePublicKeyResult = "test";
		verifier.checkForEmbeddedPublicKey(mockVerificationContainedKeyParser, "test1", emptyValue);
	}

	/**
	 * Verifies that an empty parser list will always result in error telling no
	 * parser could be found.
	 */
	@Test
	public void verifyEmptyParserList() {
		final Verifier verifier = new Verifier(factory);
		final VerificationResult result = verifier.verify(new ArrayList<>(), emptyValue, "");
		Assert.assertFalse(result.isVerified());
		Assert.assertEquals(1, result.getErrorMessages().size());
		Assert.assertEquals("error.parse.payload", result.getErrorMessages().get(0).getLocalizedMessageCode());
	}

	/**
	 * Verifies that if only one parser is present and it checkForEmbeddedPUblicKey
	 * will result in an InvalidInputException will try again and with an warning
	 */
	@Test
	public void verifyOneParserPublNotMatchData() {
		final Verifier verifier = new Verifier(factory);

		final AlfenVerificationParser parser = new AlfenVerificationParser();
		final VerificationResult result = verifier.verify(Collections.singletonList(parser), alfenTestValue,
				PUBL_KEY_DECODED_NOT_EQUAL);

		Assert.assertFalse(result.isVerified());
		Assert.assertEquals(1, result.getErrorMessages().size());
	}

	/**
	 * Verifies that if only one parser is present and it checkForEmbeddedPUblicKey
	 * will result in an InvalidInputException will try again and with an warning
	 */
	@Test
	public void verifyOneParserPublMatchData() {
		final Verifier verifier = new Verifier(factory);
		final AlfenVerificationParser parser = new AlfenVerificationParser();
		final VerificationResult result = verifier.verify(Collections.singletonList(parser), alfenTestValue,
				PUBL_KEY_DECODED);
		Assert.assertTrue(result.isVerified());
		Assert.assertEquals(0, result.getErrorMessages().size());
	}

	/**
	 * Verifies that multiple parses will still result in a valid result with alfen
	 * test string
	 */
	@Test
	public void verifyMultParserPublMatchData() throws VerificationTypeNotImplementedException {
		final Verifier verifier = new Verifier(new VerificationParserFactory());
		final VerificationResult result = verifier.verify(alfenTestValue, PUBL_KEY_DECODED);
		Assert.assertTrue(result.isVerified());
		Assert.assertEquals(0, result.getErrorMessages().size());
	}

	/**
	 * Verifies that if only one parser is present and it checkForEmbeddedPUblicKey
	 * will result in an InvalidInputException will try again and with an warning
	 */
	@Test
	public void verifyMultParserPublNotMatchData() throws VerificationTypeNotImplementedException {
		final Verifier verifier = new Verifier(new VerificationParserFactory());
		final VerificationResult result = verifier.verify(alfenTestValue, PUBL_KEY_DECODED_NOT_EQUAL);
		Assert.assertFalse(result.isVerified());
		Assert.assertEquals(1, result.getErrorMessages().size());
	}

	/**
	 * Verifies that a value object with no pk will also be parsed if possible
	 */
	@Test
	public void verifyValueParserPkNull() {
		final Verifier verifier = new Verifier(new VerificationParserFactory());
		final VerificationResult result = verifier.verify(alfenTestValue);
		Assert.assertTrue(result.isVerified());
		Assert.assertEquals(0, result.getErrorMessages().size());
	}

	/**
	 * Verifies that a value object with no pk will also be parsed if possible but
	 * result in error if not embedded
	 */
	@Test
	public void verifyValueParserPkNullNotInc() {
		final Value value = new Value();
		final SignedData signedData = new SignedData();
		signedData.setValue(TestUtils.SML_FULL);
		value.setSignedData(signedData);
		final Verifier verifier = new Verifier(new VerificationParserFactory());
		final VerificationResult result = verifier.verify(value);
		Assert.assertFalse(result.isVerified());
		Assert.assertEquals(1, result.getErrorMessages().size());
	}

	/**
	 * Verifies that value object with different pk will also be parsed
	 */
	@Test
	public void verifyValueParserPkDiff() {
		final Value value = new Value();
		final SignedData signedData = new SignedData();
		signedData.setValue(ALFEN_TEST_STRING_INIT);
		value.setSignedData(signedData);
		value.setPublicKey(createPublicKey());
		final Verifier verifier = new Verifier(new VerificationParserFactory());
		final VerificationResult result = verifier.verify(value);
		Assert.assertFalse(result.isVerified());
		Assert.assertEquals(1, result.getErrorMessages().size());
	}

	/**
	 * Verifies that value object with same pk will also be parsed
	 */
	@Test
	public void verifyValueParserPkEq() {
		final Value value = new Value();
		final SignedData signedData = new SignedData();
		signedData.setValue(ALFEN_TEST_STRING_INIT);
		value.setSignedData(signedData);
		final PublicKey publicKey = createPublicKey();
		publicKey.setValue(PUBL_KEY_DECODED);
		value.setPublicKey(publicKey);
		final Verifier verifier = new Verifier(new VerificationParserFactory());
		final VerificationResult result = verifier.verify(value);
		Assert.assertTrue(result.isVerified());
		Assert.assertEquals(0, result.getErrorMessages().size());
	}

	@Test
	public void verifyTransactionTestNoStartValues() throws TransactionValidationException {
		expectedException.expectMessage("No start value");
		final Value stopValue = new Value();
		stopValue.setTransactionId(BigInteger.valueOf(1));
		stopValue.setContext(Value.CONTEXT_END);
		final Verifier verifier = new Verifier(factory);
		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser, Arrays.asList(stopValue),
				"test");
	}

	@Test
	public void verifyTransactionTestNoStopValues() throws TransactionValidationException {
		expectedException.expectMessage("No stop value");
		final Value startValue = new Value();
		startValue.setTransactionId(BigInteger.valueOf(1));
		startValue.setContext(Value.CONTEXT_BEGIN);
		final Verifier verifier = new Verifier(factory);
		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser, Arrays.asList(startValue),
				"test");
	}

	@Test
	public void verifyTransactionTooManyStart() throws TransactionValidationException {
		expectedException.expectMessage("Too many start");
		final Value startValue1 = new Value();
		startValue1.setTransactionId(BigInteger.valueOf(1));
		startValue1.setContext(Value.CONTEXT_BEGIN);
		final Value startValue2 = new Value();
		startValue2.setTransactionId(BigInteger.valueOf(1));
		startValue2.setContext(Value.CONTEXT_BEGIN);
		final Value stopValue = new Value();
		stopValue.setTransactionId(BigInteger.valueOf(1));
		stopValue.setContext(Value.CONTEXT_END);

		final Verifier verifier = new Verifier(factory);
		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser,
				Arrays.asList(startValue1, startValue2, stopValue), "test");

	}

	@Test
	public void verifyTransactionTooManyStop() throws TransactionValidationException {
		expectedException.expectMessage("Too many stop");
		final Value startValue1 = new Value();
		startValue1.setTransactionId(BigInteger.valueOf(1));
		startValue1.setContext(Value.CONTEXT_BEGIN);
		final Value stopValue1 = new Value();
		stopValue1.setTransactionId(BigInteger.valueOf(1));
		stopValue1.setContext(Value.CONTEXT_END);
		final Value stopValue2 = new Value();
		stopValue2.setTransactionId(BigInteger.valueOf(1));
		stopValue2.setContext(Value.CONTEXT_END);

		final Verifier verifier = new Verifier(factory);
		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser,
				Arrays.asList(startValue1, stopValue1, stopValue2), "test");

	}

	@Test
	public void verifyTransactionVerifyResultNull() throws TransactionValidationException {
		expectedException.expectMessage("Unknown error on verification results");
		final Value startValue1 = new Value();
		startValue1.setTransactionId(BigInteger.valueOf(1));
		startValue1.setSignedData(new SignedData());
		startValue1.setContext(Value.CONTEXT_BEGIN);
		final Value stopValue1 = new Value();
		stopValue1.setTransactionId(BigInteger.valueOf(1));
		stopValue1.setContext(Value.CONTEXT_END);
		stopValue1.setSignedData(new SignedData());

		final Verifier verifier = new VerifySingleMock(factory);
		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser,
				Arrays.asList(startValue1, stopValue1), "test");

	}

	@Test
	public void verifyTransactionVerifyResult() throws TransactionValidationException {
		final Value startValue1 = new Value();
		startValue1.setTransactionId(BigInteger.valueOf(1));
		startValue1.setSignedData(new SignedData());
		startValue1.setContext(Value.CONTEXT_BEGIN);
		final Value stopValue1 = new Value();
		stopValue1.setTransactionId(BigInteger.valueOf(1));
		stopValue1.setContext(Value.CONTEXT_END);
		stopValue1.setSignedData(new SignedData());

		final VerifySingleMock verifier = new VerifySingleMock(factory);
		final Meter meterStart = new Meter(100, OffsetDateTime.now(), 0);
		final Meter meterStop = new Meter(101, OffsetDateTime.now().plusMinutes(1), 0);
		final VerifiedDataStubs startData = new VerifiedDataStubs(Collections.singletonList(meterStart), false);
		final VerifiedDataStubs stopData = new VerifiedDataStubs(Collections.singletonList(meterStop), false);
		final VerificationResult resultStart = new VerificationResult(startData, IntrinsicVerified.NOT_VERIFIED);
		final VerificationResult resultStop = new VerificationResult(stopData, IntrinsicVerified.NOT_VERIFIED);
		verifier.results.push(resultStop);
		verifier.results.push(resultStart);

		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser,
				Arrays.asList(startValue1, stopValue1), "test");
		Assert.assertTrue(result.isVerified());
		Assert.assertEquals(2, result.getMeters().size());
		Assert.assertEquals(0, result.getErrorMessages().size());
		Assert.assertEquals(0, result.getErrorMessages().size());
		Assert.assertTrue(result.isTransactionResult());
	}

	@Test
	public void verifyTransactionVerifyMergeFail() throws TransactionValidationException {
		final Value startValue1 = new Value();
		startValue1.setTransactionId(BigInteger.valueOf(1));
		startValue1.setSignedData(new SignedData());
		startValue1.setContext(Value.CONTEXT_BEGIN);
		final Value stopValue1 = new Value();
		stopValue1.setTransactionId(BigInteger.valueOf(1));
		stopValue1.setContext(Value.CONTEXT_END);
		stopValue1.setSignedData(new SignedData());

		final VerifySingleMock verifier = new VerifySingleMock(factory);
		final Meter meterStart = new Meter(100, OffsetDateTime.now(), 0);
		final Meter meterStop = new Meter(101, OffsetDateTime.now().plusMinutes(1), 0);
		final VerifiedDataStubs startData = new VerifiedDataStubs(Collections.singletonList(meterStart), true);
		final VerifiedDataStubs stopData = new VerifiedDataStubs(Collections.singletonList(meterStop), false);
		final VerificationResult resultStart = new VerificationResult(startData, IntrinsicVerified.NOT_VERIFIED);
		final VerificationResult resultStop = new VerificationResult(stopData, IntrinsicVerified.NOT_VERIFIED);
		verifier.results.push(resultStop);
		verifier.results.push(resultStart);

		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser,
				Arrays.asList(startValue1, stopValue1), "test");
		Assert.assertEquals(1, result.getErrorMessages().size());
	}

	@Test
	public void verifyTransactionCheckMetersFail() throws TransactionValidationException {
		expectedException.expectMessage("Stop value is less than start value");
		final Value startValue1 = new Value();
		startValue1.setTransactionId(BigInteger.valueOf(1));
		startValue1.setSignedData(new SignedData());
		startValue1.setContext(Value.CONTEXT_BEGIN);
		final Value stopValue1 = new Value();
		stopValue1.setTransactionId(BigInteger.valueOf(1));
		stopValue1.setContext(Value.CONTEXT_END);
		stopValue1.setSignedData(new SignedData());

		final VerifySingleMock verifier = new VerifySingleMock(factory);
		final Meter meterStart = new Meter(105, OffsetDateTime.now(), 0);
		final Meter meterStop = new Meter(101, OffsetDateTime.now().plusMinutes(1), 0);
		final VerifiedDataStubs startData = new VerifiedDataStubs(Collections.singletonList(meterStart), false);
		final VerifiedDataStubs stopData = new VerifiedDataStubs(Collections.singletonList(meterStop), false);
		final VerificationResult resultStart = new VerificationResult(startData, IntrinsicVerified.NOT_VERIFIED);
		final VerificationResult resultStop = new VerificationResult(stopData, IntrinsicVerified.NOT_VERIFIED);
		verifier.results.push(resultStop);
		verifier.results.push(resultStart);

		final VerificationResult result = verifier.verifyTransaction(mockVerificationParser,
				Arrays.asList(startValue1, stopValue1), "test");
	}

	@Test
	public void testTryParserNoPublicKey1() throws VerificationException {
		expectedException.expect(VerificationException.class);
		expectedException.expectMessage("Could not find a parser for this public key.");
		final VerifySingleMock verifier = new VerifySingleMock(factory);
		verifier.tryParser(mockVerificationParser, null, null);
	}

	@Test
	public void testTryParserNoPublicKey2() throws VerificationException {
		expectedException.expect(VerificationException.class);
		expectedException.expectMessage("Could not find a parser for this public key.");
		final VerifySingleMock verifier = new VerifySingleMock(factory);
		verifier.tryParser(mockVerificationParser, "", null);
	}

	@Test
	public void testTryParserNoGuessFound() throws VerificationException {
		expectedException.expect(VerificationException.class);
		expectedException.expectMessage("no encoding found for key");
		final VerifySingleMock verifier = new VerifySingleMock(factory);
		verifier.tryParser(mockVerificationParser, "XXXXX____YYYYY", null);
	}

	/**
	 * Verifies that we always return an verification error if there is one
	 *
	 * @throws VerificationException
	 */
	@Test
	public void testVerificationErrorBeforeValidationError() throws VerificationException {
		final VerifySingleMock verifier = new VerifySingleMock(factory);
		final Meter meterStart = new Meter(100, OffsetDateTime.now(), 0);
		final Meter meterStop = new Meter(0, OffsetDateTime.now(), 0);
		final VerifiedDataStubs wantedResult = new VerifiedDataStubs(Collections.singletonList(meterStart), false);

		final VerifiedDataStubs notWanted = new VerifiedDataStubs(Collections.singletonList(meterStart), false);

		mockVerificationParser.results.push(new VerificationResult(notWanted, Error.withDecodingPublicKeyFailed()));
		mockVerificationParser.results.push(new VerificationResult(wantedResult, Error.withVerificationFailed()));
		mockVerificationParser.results.push(new VerificationResult(notWanted, Error.withDecodingSignatureFailed()));
		final VerificationResult result = verifier.tryParser(mockVerificationParser, "AA32", getSampleValue());
		Assert.assertTrue(result.containsErrorOfType(Error.Type.VERIFICATION));
	}

	/**
	 * Verifies that we always return the success case
	 *
	 * @throws VerificationException
	 */
	@Test
	public void testVerificationSuccessBeforeError() throws VerificationException {
		final VerifySingleMock verifier = new VerifySingleMock(factory);
		final Meter meterStart = new Meter(100, OffsetDateTime.now(), 0);
		final Meter meterStop = new Meter(0, OffsetDateTime.now(), 0);
		final VerifiedDataStubs wantedResult = new VerifiedDataStubs(Collections.singletonList(meterStart), false);

		final VerifiedDataStubs notWanted = new VerifiedDataStubs(Collections.singletonList(meterStart), false);

		mockVerificationParser.results.push(new VerificationResult(notWanted, Error.withDecodingPublicKeyFailed()));
		mockVerificationParser.results.push(new VerificationResult(wantedResult, IntrinsicVerified.NOT_VERIFIED));
		mockVerificationParser.results.push(new VerificationResult(notWanted, Error.withDecodingSignatureFailed()));
		final VerificationResult result = verifier.tryParser(mockVerificationParser, "AA32", getSampleValue());
		Assert.assertTrue(result.isVerified());
	}

	private Value getSampleValue() {
		final Value val = new Value();
		final SignedData data = new SignedData();
		val.setSignedData(data);
		data.setValue("AAAA");
		return val;
	}

	private PublicKey createPublicKey() {
		final PublicKey publicKey = new PublicKey();
		publicKey.setEncoding(EncodingType.PLAIN.getCode());
		publicKey.setValue(TestUtils.SML_FULL_PUBLICKEY);
		return publicKey;
	}

	public class VerifySingleMock extends Verifier {

		public Stack<VerificationResult> results;

		/**
		 * Initiates the console file processor
		 *
		 * @param factory factory of parser which will be used to create the results of
		 *                the parsing
		 */
		public VerifySingleMock(VerificationParserFactory factory) {
			super(factory);
			results = new Stack<>();
		}

		@Override
		public VerificationResult verify(VerificationParser parser, Value data, String publicKey) {
			return results.isEmpty() ? null : results.pop();
		}
	}

	public class VerifiedDataStubs extends VerifiedData {

		private final boolean throwRegulationException;
		public List<Meter> meters;

		public VerifiedDataStubs(List<Meter> asList, boolean throwRegulationException) {
			meters = asList;
			this.throwRegulationException = throwRegulationException;

		}

		@Override
		public List<Meter> getMeters() {
			return meters;
		}

		@Override
		public String getFormat() {
			return "test";
		}

		@Override
		public String getPublicKey() {
			return "test";
		}

		@Override
		public String getEncoding() {
			return "test";
		}

		@Override
		public DetailsList getAdditionalData() {
			return new DetailsList();
		}

		@Override
		public boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException {
			if (throwRegulationException) {
				throw new RegulationLawException("Test exception", "unit.test");
			}
			return true;
		}
	}
}
