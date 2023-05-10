package de.safe_ev.transparenzsoftware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;

import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private final static Logger LOGGER = LogManager.getLogger(Utils.class);

    /**
     * Prints an formatted hex array out of a byte array
     *
     * @param bytes bytes to convert to a string
     * @return byte array as hex with a space between each 2nd value
     */
    public static String toFormattedHex(byte[] bytes) {
        return toFormattedHex(bytes, 2);
    }

    /**
     * Prints an formatted hex array out of a byte array
     *
     * @param bytes bytes to convert to a string
     * @param groupSize size of char groups for formatting
     * @return byte array as hex with a space between each 2nd value
     */
    public static String toFormattedHex(byte[] bytes, int groupSize) {
        String hex = bytesToHex(bytes);
        return splitStringToGroups(hex, groupSize);
    }

    /**
     * Splits a string in groups of each groupSize characters
     *
     * @param toSplit   string to split
     * @param groupSize size of character groups
     * @return splitted string
     */
    public static String splitStringToGroups(String toSplit, int groupSize) {
        return splitStringToGroups(toSplit, groupSize, " ");
    }


    /**
     * Splits a string in groups of each groupSize characters
     *
     * @param toSplit   string to split
     * @param groupSize size of character groups
     * @return splitted string
     */
    public static String splitStringToGroups(String toSplit, int groupSize, String delimiter) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (char a : toSplit.toCharArray()) {
            count++;
            builder.append(a);
            if (count % groupSize == 0) {
                builder.append(delimiter);
            }
        }
        return builder.toString().trim();
    }

    /**
     * Prints an formatted hex array value out of a byte
     *
     * @param bytes byte to convert to a string
     * @return byte as hex
     */
    public static String toFormattedHex(byte bytes) {
        return bytesToHex(new byte[]{bytes});
    }

    /**
     * Build debug string of bytes transferring than to hex
     * with position
     *
     * @param bytes bytes to build
     * @return string containing lines with e.g. "Pos 1 E7\n"
     */
    public static String debugHexString(byte[] bytes) {
        String hex = bytesToHex(bytes);
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (char a : hex.toCharArray()) {
            count++;
            if (count % 2 == 1) {
                builder.append(String.format("Pos %d ", count / 2));
            }
            builder.append(a);
            if (count % 2 == 0) {
                builder.append("\n");
            }
        }
        return builder.toString().trim();
    }

    /**
     * Switches the order of byte array so that
     * LSB becomes MSB
     *
     * @param toReverse array to switch
     * @return reversed array
     */
    public static byte[] reverseByteOrder(byte[] toReverse) {
        byte[] switched = new byte[toReverse.length];
        for (int origPos = 0, newPos = toReverse.length - 1; origPos < toReverse.length; origPos++, newPos--) {
            switched[newPos] = toReverse[origPos];
        }
        return switched;
    }

    /**
     * Builds a SHA 256 has of a byte array
     *
     * @param data to hash
     * @return data as byte array hashed with SHA256
     */
    public static byte[] hashSHA256(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error on creating sha-256 instance", e);
            throw new RuntimeException("Could not load SHA-256 algorithm");
        }
        md.update(data);
        return md.digest();
    }

    /**
     * Calculates long value of byte[] array
     *
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        ((Buffer)buffer).flip();//need flip
        return buffer.getLong();
    }

    /**
     * Copies a range of a array form a start point until the given length
     *
     * @param array  array to copy from
     * @param from   starting point in array
     * @param length length in array
     * @return copied array
     */
    public static byte[] copyFromWithLength(byte[] array, int from, int length) {
        return Arrays.copyOfRange(array, from, from + length);
    }

    /**
     * Creates an unsigned byte
     *
     * @param byteToUnsign
     * @return
     */
    public static byte toUnsignedByte(byte byteToUnsign) {
        return (byte) (byteToUnsign & 0xFF);
    }

    /**
     * Trims an byte array containing values starting at the end
     * where a padding was added with 0 after the value
     * e.g. FF AA 00 CC 00 00 00 00 00 00 results in
     * FF AA 00 CC
     *
     * @param toTrim array which will be trimmed
     * @return trimmed array
     */
    public static byte[] trimPaddingAtEnd(byte[] toTrim) {
        ArrayList<Byte> significantBytes = new ArrayList<>();
        int countZeros = 0;
        for (byte b : toTrim) {
            if (b != 0) {
                if (countZeros > 0) {
                    for (int i = 0; i < countZeros; i++) {
                        significantBytes.add((byte) 0);
                    }
                    //reset count
                    countZeros = 0;
                }
                significantBytes.add(b);
            } else {
                // if nothing than zeros was there it means it was padding
                countZeros++;
            }
        }
        //copy back to an array
        byte[] finalArray = new byte[significantBytes.size()];
        for (int i = 0; i < significantBytes.size(); i++) {
            finalArray[i] = significantBytes.get(i);
        }
        return finalArray;
    }

    /**
     * Converts the stored byte arrays back to a timestamp
     *
     * @param timestamp as byte array
     * @return Date
     */
    public static OffsetDateTime timeBytesToTimestamp(byte[] timestamp) {
        int timeInSeconds = new BigInteger(1, Utils.reverseByteOrder(timestamp)).intValue();
        return OffsetDateTime.of(LocalDateTime.ofEpochSecond(timeInSeconds, 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }

    /**
     * Creates out of a 4 byte long array a long value
     *
     * @param bytes   byte to be converted
     * @param reverse if true it is assumed that the order is little endian
     * @return long value of the bytes
     */
    public static long parseUint32(byte[] bytes, boolean reverse) {
        assert bytes.length == 4;
        ByteOrder order = reverse ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        ByteBuffer bf = ByteBuffer.allocate(8).order(order).put(bytes);
        bf.position(0);
        return bf.getLong();
    }

    /**
     * parses an unsinged uint 8 from a byte
     *
     * @param b
     * @return
     */
    public static int parseUint8(byte b) {
        return b & 0xFF;
    }

    /**
     * Parses a chain of uint8 bytes which represents a number
     *
     * @param bytes
     * @return
     */
    public static long parseUint8Chain(byte[] bytes) {
        StringBuilder concat = new StringBuilder();
        for (byte b : bytes) {
            concat.append(parseUint8(b));
        }
        return Long.getLong(concat.toString());
    }

    /**
     * Converts a byte array to a hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String unescapeXML(final String text) {
        StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            char charAt = text.charAt(i);
            if (charAt != '&') {
                result.append(charAt);
                i++;
            } else {
                if (text.startsWith("&amp;", i)) {
                    result.append('&');
                    i += 5;
                } else if (text.startsWith("&apos;", i)) {
                    result.append('\'');
                    i += 6;
                } else if (text.startsWith("&quot;", i)) {
                    result.append('"');
                    i += 6;
                } else if (text.startsWith("&lt;", i)) {
                    result.append('<');
                    i += 4;
                } else if (text.startsWith("&gt;", i)) {
                    result.append('>');
                    i += 4;
                } else i++;
            }
        }
        return result.toString();
    }

    /**
     * Converts a hex string to a ascii value
     * @param hex - string to convert
     * @return ascii representation of hex code
     */
    public static String hexToAscii(String hex){
        StringBuilder output = new StringBuilder();
        hex = hex.replaceAll(" ", "").toLowerCase();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    /**
     * Compares two different string with possible different encoding
     * and checks if the value itself is the same
     *
     * Sample: We receive two public keys
     * @param a
     * @param b
     * @return
     */
    public static boolean compareEncodedStrings(String a, String b){
        List<EncodingType> encodingTypesA = EncodingType.guessType(a, false);
        List<EncodingType> encodingTypesB = EncodingType.guessType(b, false);

        List<byte[]> decodedA = new ArrayList<>();
        for (EncodingType encodingType : encodingTypesA) {
            try {
                decodedA.add(EncodingType.decode(encodingType, a));
            } catch (DecodingException e) {
                LOGGER.warn("Could not decode on key comparison", e);
            }
        }
        for (EncodingType encodingType : encodingTypesB) {
            try {
                for (byte[] bytesA : decodedA) {
                    boolean result = Arrays.equals(bytesA, encodingType.decode(b));
                    if(result){
                        return true;
                    }
                }
            } catch (DecodingException e) {
                LOGGER.warn("Could not decode on key comparison", e);
            }
        }
        return false;
    }

    /**
     * Clear string from spaces, tabs and newlines
     * @param value
     * @return
     */
    public static String clearString(String value){
        return value.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "");
    }

    /**
     * Formats a duration to hours minutes seconds
     * @param duration
     * @return
     */
    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%dh %02dm %02ds",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    public static String toBitString(final byte[] b) {
        final char[] bits = new char[8 * b.length];
        for(int i = 0; i < b.length; i++) {
            final byte byteval = b[i];
            int bytei = i << 3;
            int mask = 0x1;
            for(int j = 7; j >= 0; j--) {
                final int bitval = byteval & mask;
                if(bitval == 0) {
                    bits[bytei + j] = '0';
                } else {
                    bits[bytei + j] = '1';
                }
                mask <<= 1;
            }
        }
        return String.valueOf(bits);
    }

    public static boolean isBitSet(final byte[] arr, int bit) {
        try {
            int index = bit / 8;  // Get the index of the array for the byte with this bit
            int bitPosition = bit % 8;  // Position of this bit in a byte

            return (arr[index] >> bitPosition & 1) == 1;
        } catch (Exception e){
            LOGGER.debug("Error on reading bit from byte array");
            return false;
        }
    }

    public static boolean areBitSet(final byte[] arr, int[] index) {
        if(index == null || arr == null) {
            return false;
        }
        boolean result = false;
        for (int i : index) {
            result = result || isBitSet(arr, i);
        }
        return result;
    }

    public static byte[] longToByteArray(long value)
    {
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        bb.putLong(value);
        byte[] array = bb.array();

        return array;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private String hexRepresentation(String format){
        if(format != null && Utils.hexToAscii(format).matches("[A-Za-z0-9]*")){
            return String.format("%s (%s)", format, Utils.hexToAscii(format));
        } else {
            return format;
        }

    }

	public static String toASCIIOrFormattedHex(byte[] bytes) {
		// Check for ASCII values:
		for (byte b : bytes) {
			if (b < 32) return toFormattedHex(bytes);
		}
		return new String(bytes);
	}
}
