package de.safe_ev.transparenzsoftware.verification;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingDecoder;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.xml.PublicKey;

import java.util.Base64;

public class EncodingDecoderTest {

    @Test
    public void testDecodePublicKey() throws DecodingException {
        PublicKey publicKey = new PublicKey();
        publicKey.setEncoding(EncodingType.PLAIN.getCode());
        publicKey.setValue("TEST");

        String plain = EncodingDecoder.decodePublicKey(publicKey);
        Assert.assertEquals("TEST", plain);
    }

    @Test
    public void testDecodePublicKeyBase64() throws DecodingException {
        PublicKey publicKey = new PublicKey();
        publicKey.setEncoding(EncodingType.BASE64.getCode());
        publicKey.setValue(Base64.getEncoder().encodeToString(new byte[]{0, 1}));

        String plain = EncodingDecoder.decodePublicKey(publicKey);
        Assert.assertEquals("00 01", plain);
    }

    @Test
    public void testDecode64() throws DecodingException {
        byte[] decoded = EncodingType.BASE64.decode("7DHd28rwXlUQjNE9H6VBvv3GVUlePiIFU8i7ZM5gINUja/qpNwkF5seHFiGzc55kAAM=");
        Assert.assertNotNull(decoded);
    }


}
