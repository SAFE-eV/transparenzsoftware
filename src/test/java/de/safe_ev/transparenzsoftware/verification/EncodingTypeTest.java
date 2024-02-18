package de.safe_ev.transparenzsoftware.verification;

import com.google.common.io.BaseEncoding;

import de.safe_ev.transparenzsoftware.verification.EncodingType;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EncodingTypeTest {

    @Test
    public void testGuessBase32() {
        BaseEncoding base32 = BaseEncoding.base32();
        List<EncodingType> guessed = EncodingType.guessType(base32.encode("test".getBytes()));
        Assert.assertTrue(guessed.contains(EncodingType.BASE32));
    }


    @Test
    public void testGuessBase64() {
        BaseEncoding base64 = BaseEncoding.base64();
        List<EncodingType> guessed = EncodingType.guessType(base64.encode("test".getBytes()));
        Assert.assertTrue(guessed.contains(EncodingType.BASE64));
    }

    @Test
    public void testHex(){
        Assert.assertTrue(EncodingType.guessType("AA00FF ").contains(EncodingType.HEX));
    }

    @Test
    public void testPlain(){
        Assert.assertTrue(EncodingType.guessType("AA00FF II", true).contains(EncodingType.PLAIN));
        Assert.assertFalse(EncodingType.guessType("AA00FF II").contains(EncodingType.PLAIN));
    }
}
