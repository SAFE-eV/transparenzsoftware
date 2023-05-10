package de.safe_ev.transparenzsoftware.output;

public class ConsoleFileProcessorTest {

//    private VerificationParserFactory factory;
//    private MockVerificationParser mockVerificationParser;
//    private MockVerificationContainedKeyParser mockVerificationContainedKeyParser;
//
//    @Before
//    public void setUp() {
//        mockVerificationParser = new MockVerificationParser();
//        mockVerificationContainedKeyParser = new MockVerificationContainedKeyParser();
//        List<VerificationParser> parserList = new ArrayList<>();
//        parserList.add(mockVerificationParser);
//        parserList.add(mockVerificationContainedKeyParser);
//        factory = new VerificationParserFactory(parserList);
//    }
//
//
//
//    /**
//     * Verifies that if no parser match on an unknown format an verification
//     * error will be returned.
//     *
//     * @throws ValidationException
//     * @throws VerificationTypeNotImplementedException
//     */
//    @Test(expected = VerificationTypeNotImplementedException.class)
//    public void parseWithUnknownFormatNoParserTest() throws ValidationException, VerificationTypeNotImplementedException {
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        VerificationResult result = consoleFileProcessor.parseWithUnknownFormat(getTestValue(), EncodingType.PLAIN, "");
//    }
//
//    /**
//     * Verifies that if a parser match on an unknown format with different
//     * public key will result in an verification error will be returned.
//     *
//     * @throws ValidationException
//     * @throws VerificationTypeNotImplementedException
//     */
//    @Test
//    public void parseWithUnknownFormatParserEmbeddedFailTest() throws ValidationException, VerificationTypeNotImplementedException {
//        this.mockVerificationContainedKeyParser.parsePublicKeyResult = "test";
//        MockVerifiedData verifiedData = createVerifiedData();
//        verifiedData.value = 10;
//        this.mockVerificationContainedKeyParser.result = new VerificationResult(verifiedData);
//        this.mockVerificationContainedKeyParser.canParse = true;
//        this.mockVerificationParser.result = new Error(createVerifiedData(), "test error");
//
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        testValue.getSignedData().setFormat("test");
//        VerificationResult result = consoleFileProcessor.parseWithUnknownFormat(testValue, EncodingType.PLAIN, "test1");
//        Assert.assertFalse(result.isVerified());
//        Assert.assertEquals(new Double(0.0), result.getValue());
//    }
//
//
//    /**
//     * Verifies that if a parser match on an unknown format with same
//     * public key will result in an verification success will be returned.
//     *
//     * @throws ValidationException
//     * @throws VerificationTypeNotImplementedException
//     */
//    @Test
//    public void parseWithUnknownFormatParserEmbeddedTest() throws ValidationException, VerificationTypeNotImplementedException {
//        this.mockVerificationContainedKeyParser.parsePublicKeyResult = "test";
//        MockVerifiedData verifiedData = createVerifiedData();
//        verifiedData.value = 10;
//        this.mockVerificationContainedKeyParser.result = new VerificationResult(verifiedData);
//        this.mockVerificationContainedKeyParser.canParse = true;
//        this.mockVerificationParser.result = new Error(createVerifiedData(), "test error");
//
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        VerificationResult result = consoleFileProcessor.parseWithUnknownFormat(testValue, EncodingType.PLAIN, "test");
//        Assert.assertTrue(result.isVerified());
//        Assert.assertEquals(new Double(10), result.getValue());
//        Assert.assertEquals(VerificationType.EDL_40_P, testValue.getSignedData().getFormatAsVerificationType());
//    }
//
//    /**
//     * Verifies that if a parser match on an unknown format will result in
//     * an verification success will be returned.
//     *
//     * @throws ValidationException
//     * @throws VerificationTypeNotImplementedException
//     */
//    @Test
//    public void parseWithUnknownFormatParserTest() throws ValidationException, VerificationTypeNotImplementedException {
//        this.mockVerificationContainedKeyParser.parsePublicKeyResult = "test";
//        MockVerifiedData verifiedData = createVerifiedData();
//        verifiedData.value = 10;
//        this.mockVerificationParser.result = new VerificationResult(verifiedData);
//        this.mockVerificationParser.canParse = true;
//        this.mockVerificationContainedKeyParser.result = new Error(createVerifiedData(), "test error");
//
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        VerificationResult result = consoleFileProcessor.parseWithUnknownFormat(testValue, EncodingType.PLAIN, "test");
//        Assert.assertTrue(result.isVerified());
//        Assert.assertEquals(new Double(10), result.getValue());
//        Assert.assertEquals(VerificationType.EDL_40_P, testValue.getSignedData().getFormatAsVerificationType());
//    }
//
//    /**
//     * Verifies that an empty public key with a contained public key will
//     * result in an verification result
//     *
//     * @throws ValidationException
//     * @throws InvalidInputException
//     * @throws VerificationTypeNotImplementedException
//     */
//    @Test
//    public void parseWithKnownFormatEmptyPublicKeyButEmbeddedTest() throws ValidationException, InvalidInputException, VerificationTypeNotImplementedException {
//        MockVerifiedData verifiedData = createVerifiedData();
//        this.mockVerificationContainedKeyParser.parsePublicKeyResult = "test";
//        this.mockVerificationContainedKeyParser.result = new VerificationResult(verifiedData);
//        this.mockVerificationContainedKeyParser.canParse = true;
//        this.mockVerificationContainedKeyParser.type = VerificationType.EDL_40_SIG;
//
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        testValue.getSignedData().setFormat(VerificationType.EDL_40_SIG.name());
//        VerificationResult res = consoleFileProcessor.parseWithKnownFormat(EncodingType.PLAIN, null, testValue);
//        Assert.assertTrue(res.isVerified());
//        Assert.assertEquals(new Double(15), res.getValue());
//    }
//
//
//    /**
//     * Verifies that an empty public key with a contained public key will
//     * result in an verification result
//     *
//     * @throws ValidationException
//     * @throws InvalidInputException
//     * @throws VerificationTypeNotImplementedException
//     */
//    @Test
//    public void parseWithKnownFormatEmptyPublicKeyTest() throws ValidationException, InvalidInputException, VerificationTypeNotImplementedException {
//        MockVerifiedData verifiedData = createVerifiedData();
//        this.mockVerificationContainedKeyParser.parsePublicKeyResult = null;
//        this.mockVerificationContainedKeyParser.result = new VerificationResult(verifiedData);
//        this.mockVerificationContainedKeyParser.canParse = true;
//        this.mockVerificationContainedKeyParser.type = VerificationType.EDL_40_SIG;
//
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        testValue.getSignedData().setFormat(VerificationType.EDL_40_SIG.name());
//        VerificationResult res = consoleFileProcessor.parseWithKnownFormat(EncodingType.PLAIN, null, testValue);
//        Assert.assertFalse(res.isVerified());
//        Assert.assertEquals(new Double(0), res.getValue());
//    }
//
//    /**
//     * Verifies that an encoding type if set in an signed data object will be used
//     */
//    @Test
//    public void getEncodingTypeFromValueSignedDataEncodingTest() {
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        testValue.getSignedData().setEncoding(EncodingType.BASE64.getCode());
//        EncodingType encodingTypeFromValue = consoleFileProcessor.getEncodingTypeFromValue(testValue);
//
//
//        Assert.assertEquals(EncodingType.BASE64, encodingTypeFromValue);
//    }
//
//    /**
//     * Verifies that a value without public key will result in a null
//     * response on decoding
//     *
//     * @throws DecodingException
//     */
//    @Test
//    public void getDecodedPublicKeyNoKeyTest() throws DecodingException {
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        Assert.assertNull(consoleFileProcessor.getDecodedPublicKey(testValue));
//    }
//
//    /**
//     * Verifies that a value without public key will result in a null
//     * response on decoding
//     *
//     * @throws DecodingException
//     */
//    @Test
//    public void getDecodedPublicKeyTest() throws DecodingException {
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        PublicKey publicKey = new PublicKey();
//        publicKey.setEncoding(EncodingType.BASE64.getCode());
//        publicKey.setValue("00");
//        testValue.setPublicKey(publicKey);
//        Assert.assertEquals("D3", consoleFileProcessor.getDecodedPublicKey(testValue));
//    }
//
//    @Test
//    public void processValueTest(){
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        testValue.getSignedData().setValue(null);
//        VerificationResult verificationResult = consoleFileProcessor.processValue(testValue);
//        Assert.assertFalse(verificationResult.isVerified());
//    }
//
//    @Test
//    public void processValueUnknownFormatTest(){
//        MockVerifiedData verifiedData = createVerifiedData();
//        this.mockVerificationContainedKeyParser.parsePublicKeyResult = "test";
//        this.mockVerificationContainedKeyParser.result = new VerificationResult(verifiedData);
//        this.mockVerificationContainedKeyParser.canParse = true;
//        this.mockVerificationContainedKeyParser.type = VerificationType.EDL_40_SIG;
//
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        testValue.getSignedData().setFormat(VerificationType.EDL_40_SIG.name());
//        VerificationResult res = consoleFileProcessor.processValue(testValue);
//        Assert.assertTrue(res.isVerified());
//        Assert.assertEquals(new Double(15), res.getValue());
//    }
//
//    /**
//     * Verifies that an encoding type if set in an signed data object will be used
//     */
//    @Test
//    public void getEncodingTypeFromGuessingTest() {
//        ConsoleFileProcessor consoleFileProcessor = new ConsoleFileProcessor(factory);
//        Value testValue = getTestValue();
//        testValue.getSignedData().setEncoding(null);
//        BaseEncoding base32 = BaseEncoding.base32();
//        testValue.getSignedData().setValue(base32.encode("test".getBytes()));
//        EncodingType encodingTypeFromValue = consoleFileProcessor.getEncodingTypeFromValue(testValue);
//        Assert.assertEquals(EncodingType.BASE32, encodingTypeFromValue);
//    }
//
//    private MockVerifiedData createVerifiedData() {
//        MockVerifiedData verifiedData = new MockVerifiedData();
//        verifiedData.value = 15;
//        verifiedData.timestamp = LocalDateTime.now();
//        verifiedData.additionalData = new HashMap<>();
//        return verifiedData;
//    }
//
//    private Value getTestValue() {
//        Value value = new Value();
//        SignedData signedData = new SignedData();
//        signedData.setValue("test");
//        value.setSignedData(signedData);
//
//        return value;
//    }
}
