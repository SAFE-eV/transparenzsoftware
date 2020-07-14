package com.hastobe.transparenzsoftware.verification.format.alfen;

import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AlfenVerificationParserTest {

    private final static String ALFEN_TEST_STRING = "AP;0;3;AMVBBEIORR2RGJLJ6YRZUGACQAXSDFCL66EIP3N7;BJKGK43UIRSXMAAROYYDCMZNUYFACRC2I4ADGAABIAAAAAAAQ6ACMAD5CH4FWAIAAEEAB7Y6ACJD2AAAAAAAAABRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASAAAAAEIAAAAA====;IGRCBV3TL45XIGPJU7QGD3H4V6ICQ75GLPWEFNEKZX3RTTKJI2FBXHPCWUIWL5OENEHE3SQRVACHG===;";
    private final static String ALFEN_TEST_STRING_PUB = "AMVBBEIORR2RGJLJ6YRZUGACQAXSDFCL66EIP3N7";

    private AlfenVerificationParser parser;

    @Before
    public void setUp(){
        parser = new AlfenVerificationParser();
    }

    @Test
    public void testType(){
        Assert.assertEquals(VerificationType.ALFEN, parser.getVerificationType());
    }

    @Test
    public void testCanNotParseData(){
        Assert.assertFalse(parser.canParseData("asdfd"));
    }

    @Test
    public void testCanParseData(){
        Assert.assertTrue(parser.canParseData(ALFEN_TEST_STRING));
    }


    @Test
    public void testParsePublicKey(){
        Assert.assertEquals(ALFEN_TEST_STRING_PUB, parser.parsePublicKey(ALFEN_TEST_STRING));
    }

    @Test
    public void testParsePublicKeyFail(){
        Assert.assertNull(parser.parsePublicKey("AAP;00;asdf"));
    }

    @Test
    public void testVerifiedDataClass(){
        Assert.assertEquals(AlfenVerifiedData.class, parser.getVerfiedDataClass());
    }

    @Test
    public void testParseAndVerify() throws DecodingException {
        VerificationResult result = parser.parseAndVerify(ALFEN_TEST_STRING, EncodingType.base32Decode(ALFEN_TEST_STRING_PUB));
        Assert.assertTrue(result.isVerified());
        Assert.assertTrue(result.getErrorMessages().isEmpty());
        Assert.assertEquals(VerificationType.ALFEN.name(), result.getVerifiedData().getFormat());
        Assert.assertEquals(EncodingType.PLAIN.getCode(), result.getVerifiedData().getEncoding());
        Assert.assertEquals(ALFEN_TEST_STRING_PUB, result.getVerifiedData().getPublicKey());
        Assert.assertEquals(15.762, result.getVerifiedData().getMeters().get(0).getValue(), 0);
        LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
        Assert.assertEquals(offsetDateTime, result.getMeters().get(0).getTimestamp());
    }
}
