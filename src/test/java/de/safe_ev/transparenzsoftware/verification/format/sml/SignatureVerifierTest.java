package de.safe_ev.transparenzsoftware.verification.format.sml;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignatureVerifier;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDL40.SMLReader;
import de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.embedded.SignedMeterValue;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;

public class SignatureVerifierTest {

    public static final String PUBLIC_KEY_BASE64 = "iH+r9Aesgngu7/8iIML4Vq6wvCI2S7zGtVdhkR7WUdGpIrraiIGMlnGv7nCU1/U2";


    /**
     * Verifies that a predefined set of data will verify
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws SignatureException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws ValidationException
     */
    @Test
    public void testWithGivenBytes() throws NoSuchAlgorithmException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, ValidationException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(PUBLIC_KEY_BASE64);
        byte[] signatureWithLogbookBytes = Base64.getDecoder().decode("kpsT2TfwP3Cwxwq2ikvrqZJqdMo/x2xRIZqUuBquNA6OjQUO52zCMPMKX/nMclnQAGw=");
        //remove the last two bytes as those are the logbook entries
        byte[] signature = Arrays.copyOfRange(signatureWithLogbookBytes, 0, signatureWithLogbookBytes.length - 2);
        byte[] originalData = Hex.decode("9123c1445fbb687ebfa682d5f13b3673c16bdba0e9b74b0f42936b0114627ccc");

        SMLSignatureVerifier verifier = new SMLSignatureVerifier();
        //original data will be the data we will build
        //signature is the data we will read out
        boolean verify = verifier.verify(publicKeyBytes, signature, originalData);
        Assert.assertTrue(verify);
    }

    @Test
    public void test_with_sigdata1() throws ValidationException {
        de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.SMLReader smlReader = new de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.SMLReader();
        SignedMeterValue signedMeterValue = smlReader.readFromString(Utils.unescapeXML(TestUtils.TEST_SIG_ONLY));
        SMLSignature signature = smlReader.parseSMLSigXml(signedMeterValue);
        SMLSignatureVerifier verifier = new SMLSignatureVerifier();

        boolean verify = verifier.verify(signedMeterValue.getPublicKey().getValueEncoded(), signature);
        Assert.assertTrue(verify);
    }

    @Test
    public void test_verify_data() throws ValidationException, DecodingException {
        SMLReader smlReader = new SMLReader();
        SMLSignature signature = smlReader.parsePayloadData(EncodingType.BASE64.decode(TestUtils.SML_FULL));
        SMLSignatureVerifier verifier = new SMLSignatureVerifier();

        byte[] publicKeyBytes = Hex.decode(TestUtils.SML_FULL_PUBLICKEY);
        //original data will be the data we will build
        //signature is the data we will read out
        boolean verify = verifier.verify(publicKeyBytes, signature);
        Assert.assertTrue(verify);
    }

}
