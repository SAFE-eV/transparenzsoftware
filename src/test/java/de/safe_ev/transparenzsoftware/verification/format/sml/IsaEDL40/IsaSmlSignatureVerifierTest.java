package de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.Verifier;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenVerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40.IsaEDL40Signature;
import de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40.IsaSMLSignatureVerifier;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

import java.util.Collections;

public class IsaSmlSignatureVerifierTest {
    public static final String PUBLIC_KEY_TEST = "995A6E883250F8A1980ADF7367DF8B40F54BE3C6D4ED4F687F4E393DAF076BDDD17D52B9378BEE022F5C0A1C887C3C1E01231A78AB3683E60E4EEB3F4BDE66F2";
    public static final String SIGNATURE_TEST = "2F56EA2C20A7CE7F35676556F6CAAAFFA18B7F2380FACF231B6F9D218B23888C4BAF07A3C6B91F26466F0DCF5B38A83B75368A92236075ED5FBB560742C03782";
    public static final String HASH_DATA_TEST = "6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B";
    public static final String WRONG_SIGNATURE_TEST = "3F56EA2C20A7CE7F35676556F6CAAAFFA18B7F2380FACF231B6F9D218B23888C4BAF07A3C6B91F26466F0DCF5B38A83B75368A92236075ED5FBB560742C03782";
    public static final String WRONG_HASH_DATA_TEST = "6786B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B";

    public static final String ISA_PUBLIC_KEY = "995A6E883250F8A1980ADF7367DF8B40F54BE3C6D4ED4F687F4E393DAF076BDDD17D52B9378BEE022F5C0A1C887C3C1E01231A78AB3683E60E4EEB3F4BDE66F2";
    public static final String ISA_END_CONTEXT_SIGNATURE = "995A6E883250F8A1980ADF7367DF8B40F54BE3C6D4ED4F687F4E393DAF076BDDD17D52B9378BEE022F5C0A1C887C3C1E01231A78AB3683E60E4EEB3F4BDE66F2";

    public static final String PUBLIC_KEY_TEST_1 = "319098b3378bd30891f207679be8b0cd78a824d025d68606872e8d13bf4e08e0beeb8d41248eae7a613054e81aa767829b70ff7176f8b0ac81f6559bfd0fcf42";
    public static final String SIGNATURE_TEST_1 = "d31625d84fe3a7a2ede87886c75ff67030efc373efe72b97358ffab748d4541f09b466b42dc3f72169b95aa17cfd039fc37891ce8ef1c1c5f8254221546d5c68";
    public static final String HASH_DATA_TEST_1 = "28d70b6e829b0d171e2280763d5e8b865204614c01d7813005eefdd788d26ac9";


    @Test
    public void testDataSignatureVerify() throws ValidationException {

        byte[] publicKey = Utils.hexStringToByteArray(PUBLIC_KEY_TEST_1);
        byte[] hashData = Utils.hexStringToByteArray(HASH_DATA_TEST_1);
        byte[] signature = Utils.hexStringToByteArray(SIGNATURE_TEST_1);

        IsaSMLSignatureVerifier verifier = new IsaSMLSignatureVerifier();
        boolean result = verifier.verify(publicKey, signature, hashData);
        Assert.assertTrue(result);
    }
    @Test
    public void testDataSignatureFail() throws ValidationException {

        byte[] publicKey = Utils.hexStringToByteArray(PUBLIC_KEY_TEST_1);
        byte[] hashData = Utils.hexStringToByteArray(WRONG_HASH_DATA_TEST);
        byte[] signature = Utils.hexStringToByteArray(SIGNATURE_TEST_1);

        IsaSMLSignatureVerifier verifier = new IsaSMLSignatureVerifier();
        boolean result = verifier.verify(publicKey, signature, hashData);
        Assert.assertFalse(result);
    }

    //HBO : NPE: @Test
    public void endContextSignatureVerify() throws ValidationException {
        byte[] publicKey = Utils.hexStringToByteArray(ISA_PUBLIC_KEY);

        IsaEDL40Signature edlSgnature = GetIsaEDL40SignatureEnd();
        byte[] signature = edlSgnature.getActualEcSignature();
        byte[] hashData = Utils.hexStringToByteArray(HASH_DATA_TEST);



        IsaSMLSignatureVerifier verifier = new IsaSMLSignatureVerifier();
        boolean result = verifier.verify(publicKey, signature, hashData);
        Assert.assertTrue(result);
    }




    private IsaEDL40Signature GetIsaEDL40SignatureEnd() throws SMLValidationException
    {
        IsaEDL40Signature parsedSml = null;
        //ServerId: 0901495341000112C384
        parsedSml.setServerId(Utils.hexStringToByteArray("0901495341000112C384"));
        //8180816200FF
        parsedSml.setListNameOfRes(Utils.hexStringToByteArray("8180816200FF"));
        //
        byte[] actualEcSignature = Utils.hexStringToByteArray("21D72F271DEDE109E838BD3FE7E45917A13C97DCE31CF9A56AF7E0F082B9949B944856BBC726E1A3DF7D3923DCF80B95AB507D21895B7076F790064DA250CA160048");
        //take the last two bytes as those are logbook bytes FNN spec page 42
        //0048
        parsedSml.setBytesLog(actualEcSignature[actualEcSignature.length - 2], actualEcSignature[actualEcSignature.length - 1]);

        return parsedSml;

        /* Sample of SMLFile Contex: End
1B1B1B1B01010101
76 0500000045 6201 6200 72 6500000101 76 01 0A504373746174696F6E 0531324138 0B09014953410000000001 72 6201 6500931BD4 6201 63FA42 00
76
	0500000046
	6201
	6200
	72
		6500000701
	77
		0A504373746174696F6E
		0B09014953410000000001
		078180816202FF
		72
			6203
			73
				655F27E685
				53003C
				53003C
		75
			77
				078182815401FF
				01
				72
					6203
					73
						655F27E62A
						53003C
						53003C
				01
				01
				88023F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F3F
				01
			77
				07010001080080
				690000040000000018
				72
					6202
					655F28024A
				621E
				52FF
				590000000003403F53
				01
			77
				078180C7F040FF
				01
				01
				01
				01
				6500000008
				01
			77
				078180816101FF
				01
				01
				01
				01
				8106808182838485868788898A8B8C8D8E8F90919293
				01
			77
				070100010800FF
				69000006C000000018
				72
					6202
					655F2802A4
				621E
				52FF
				5900000000034101C1
				840421D72F271DEDE109E838BD3FE7E45917A13C97DCE31CF9A56AF7E0F082B9949B944856BBC726E1A3DF7D3923DCF80B95AB507D21895B7076F790064DA250CA160048
		84048DA4C811E805CB757999C91F6F5120EC829909C2DF6FA658614AB30221AC6AAD060726151DE542B91FBA6DA7C7A5B23B6134DFE685C31E54B83D14D58B60E1B30048
		01
	633B50 00
76 0500000047 6201 6200 72 6500000201 71 01 63F610 00 000000 1B1B1B1B 1A03 315A

        * */
    }



    private IsaEDL40Signature GetIsaEDL40SignatureBegin() throws SMLValidationException
    {
        IsaEDL40Signature parsedSml = null;

        //ServerId: 0901495341000112C384
        parsedSml.setServerId(Utils.hexStringToByteArray("0901495341000112C384"));
        //8180816200FF
        parsedSml.setListNameOfRes(Utils.hexStringToByteArray("8180816200FF"));
        //
        byte[] originalSignature = Utils.hexStringToByteArray("769C5080D59789B4B95AC8A082D2533EBCBB8AE74BAC7CE86A5A74FE8F293038C9048B58F9BB62CE3E379E6990BFA5BA409C1BCE848D8D79696AB1128A6ADE2F0042");
        //take the last two bytes as those are logbook bytes FNN spec page 42
        //0042
        parsedSml.setBytesLog(originalSignature[originalSignature.length - 2], originalSignature[originalSignature.length - 1]);


        return parsedSml;
        /* Sample of SMLFile  Context: Begin
1B1B1B1B01010101
7605000000006201620072650000010176010A504373746174696F6E05313241380B0901495341000112C38472620165000241FD620163720800
76
	0500000001
	6201
	6200
	72
		6500000701
		77
			0A504373746174696F6E
			0B0901495341000112C384
			078180816200FF
			72
				6203
				73
					655F213D39
					53003C
					53003C
			75
				77
					078182815401FF
					01
					72
						6203
						73
							655F213798
							53003C
							53003C
					01
					01
					8802AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
					01
				77
					07010001080080
					690000040000000018
					72
						6202
						655F2153B8
					621E
					52FF
					59000000000033FD20
					01
				77
					078180C7F040FF
					01
					01
					01
					01
					6500000002
					01
				77
					078180816101FF
					01
					01
					01
					01
					81060000000000000000000000000000000000000000
					01
				77
					070100010800FF
					690000060000000018
					72
						6202
						6500000001
					621E
					52FF
					59000000000033FD20
					8404277360B607834D0015899E49C0ECCCB74823F0C7BC51233C549E2E05730D24251045F74C9A65DA8FA9674FABC1D335C648CCCDD57825EDB42FF2540B4ADE99930042
			8404769C5080D59789B4B95AC8A082D2533EBCBB8AE74BAC7CE86A5A74FE8F293038C9048B58F9BB62CE3E379E6990BFA5BA409C1BCE848D8D79696AB1128A6ADE2F0042
			01
	635C32 00
76050000000262016200726500000201710163BE60 00 000000 1B1B1B1B 1A03 2201

        * */
    }



}
