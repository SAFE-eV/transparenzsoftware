package de.safe_ev.transparenzsoftware;

import com.google.common.io.BaseEncoding;

import de.safe_ev.transparenzsoftware.Utils;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Verifies the utility functions
 */
public class UtilsTest {

    /**
     * Verifies that an array with 1 entry will not be changed
     */
    @Test
    public void testSingleByte() {
        byte[] toTest = {0x01};
        byte[] check = Utils.reverseByteOrder(toTest);
        Assert.assertEquals(0x01, check[0]);
    }

    @Test
    public void testEmptyArray() {
        byte[] toTest = new byte[0];
        byte[] check = Utils.reverseByteOrder(toTest);
        Assert.assertEquals(0, check.length);
    }

    /**
     * Verifies that an array will be switched
     */
    @Test
    public void testMultipleBytes() {
        byte[] toTest = {0x01, 0x02, 0x03, 0x04, 0x05};
        byte[] check = Utils.reverseByteOrder(toTest);
        Assert.assertEquals(0x05, check[0]);
        Assert.assertEquals(0x04, check[1]);
        Assert.assertEquals(0x03, check[2]);
        Assert.assertEquals(0x02, check[3]);
        Assert.assertEquals(0x01, check[4]);
    }

    /**
     * Verifies the conversion of a simple byte array to hex
     */
    @Test
    public void testHexConversion() {
        byte[] toTest = {0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xAF, (byte) 0xFF};
        Assert.assertEquals("01 02 03 04 05 AF FF", Utils.toFormattedHex(toTest));
    }

    /**
     * Verifies that a hash on a byte array with sha256
     * returns in a 32 byte array
     */
    @Test
    public void testSHA256Hash() {
        byte[] toTest = {0x01, 0x02, 0x03, 0x04, 0x05};
        byte[] hashed = Utils.hashSHA256(toTest);
        byte[] toCompare = {116, -8, 31, -31, 103, -39, -101, 76, -76, 29, 109, 12, -51, -88, 34, 120, -54, -18, -97, 62, 47, 37, -43, -27, -93, -109, 111, -13, -36, -20, 96, -48};
        Assert.assertEquals(32, hashed.length);
        for (int i = 0; i < toCompare.length; i++) {
            Assert.assertEquals(toCompare[i], hashed[i]);
        }
    }

    /**
     * Verifies that an array of bytes with padding of 0 com the end will
     * be trimmed
     */
    @Test
    public void testTrimArray() {
        byte[] toTrim = new byte[]{0, 1, 2, 3, 4, 0, 0, 0, 0};
        byte[] trimmed = new byte[]{0, 1, 2, 3, 4};

        Assert.assertArrayEquals(trimmed, Utils.trimPaddingAtEnd(toTrim));
    }

    /**
     * Verifies that an array of bytes with no padding will not be trimmed
     */
    @Test
    public void testTrimArrayNothingToTrim() {
        byte[] toTrim = new byte[]{0, 1, 2, 3, 4};
        byte[] trimmed = new byte[]{0, 1, 2, 3, 4};

        Assert.assertArrayEquals(trimmed, Utils.trimPaddingAtEnd(toTrim));
    }

    /**
     * Verifies that an array of bytes with 0 in between and padding
     * com the end will be trimmed
     */
    @Test
    public void testTrimArrayZerosInBetween() {
        byte[] toTrim = new byte[]{0, 1, 2, 3, 4, 0, 0, 5, 0, 0, 0, 0, 0, 0};
        byte[] trimmed = new byte[]{0, 1, 2, 3, 4, 0, 0, 5};

        Assert.assertArrayEquals(trimmed, Utils.trimPaddingAtEnd(toTrim));
    }

    @Test
    public void compareDifferentEncodingsBase64Base32(){
        String test1234 = "test1234";
        byte[] testBytes = test1234.getBytes(Charset.forName("UTF-8"));
        String a = Base64.getEncoder().encodeToString(testBytes);
        String b = BaseEncoding.base32().encode(testBytes);
        Assert.assertTrue(Utils.compareEncodedStrings(a, b));

    }

    @Test
    public void compareDifferentEncodingsBase64Hex(){
        String a = "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z";
        String b = "5C BC 98 00 26 7C FE 46 83 40 1F 96 CD A4 E8 F3 18 5F 97 BC 66 A3 8E AF 36 19 36 6C FC 40 F2 FE FD 31 26 8C 57 7C F5 9A 69 C1 08 46 1A AA 6D B3";
        Assert.assertTrue(Utils.compareEncodedStrings(a, b));
    }

    @Test
    public void compareDifferentEncodingsBase32Hex(){
        String a = "LS6JQABGPT7ENA2AD6LM3JHI6MMF7F54M2RY5LZWDE3GZ7CA6L7P2MJGRRLXZ5M2NHAQQRQ2VJW3G===";
        String b = "5C BC 98 00 26 7C FE 46 83 40 1F 96 CD A4 E8 F3 18 5F 97 BC 66 A3 8E AF 36 19 36 6C FC 40 F2 FE FD 31 26 8C 57 7C F5 9A 69 C1 08 46 1A AA 6D B3";
        Assert.assertTrue(Utils.compareEncodedStrings(a, b));
    }
}
