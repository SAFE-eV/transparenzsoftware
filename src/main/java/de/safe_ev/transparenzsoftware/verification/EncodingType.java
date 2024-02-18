package de.safe_ev.transparenzsoftware.verification;

import com.google.common.io.BaseEncoding;

import de.safe_ev.transparenzsoftware.Utils;

import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public enum EncodingType {

    BASE64("base64"),
    BASE32("base32"),
    PLAIN("plain"),
    HEX("hex");

    private final String code;

    EncodingType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Try to fetch a encoding type with the given
     *
     * @param code
     * @return EncodingType if found otherwise null
     */
    public static EncodingType fromCode(String code) {
        for (EncodingType formatType : EncodingType.values()) {
            if (formatType.code.equals(code)) {
                return formatType;
            }
        }
        return null;
    }

    /**
     * Tries to guess the encoding type base on the data
     * if nothing is found plain is choosen as default
     *
     * @param data
     * @return list of possible types
     */
    public static List<EncodingType> guessType(String data){
        return guessType(data, false);
    }

    /**
     * Tries to guess the encoding type base on the data
     * if nothing is found plain is choosen as default
     *
     * @param data
     * @param allowPlain - if plain should be added too
     * @return list of possible types
     */
    public static List<EncodingType> guessType(String data, boolean allowPlain) {
        List<EncodingType> matches = new ArrayList<>();
        if(data == null){
            return matches;
        }
        try {
            base32Decode(data);
            matches.add(BASE32);
        } catch (DecodingException e) {
            //no op
        }
        try {
            base64Decode(data);
            matches.add(BASE64);
        } catch (DecodingException e) {
            //no op
        }
        try {
            hexDecode(data);
            matches.add(HEX);
        } catch (DecodingException e) {
            // no op
        }
        if(allowPlain) {
            matches.add(PLAIN);
        }
        return matches;
    }


    public static byte[] decode(EncodingType type, String data) throws DecodingException {
        switch (type) {
            case HEX:
                return hexDecode(data);
            case BASE32:
                return base32Decode(data);
            case BASE64:
                return base64Decode(data);
            default:
                return data.getBytes();
        }
    }


    public byte[] decode(String data) throws DecodingException {
        return EncodingType.decode(this, data);
    }

    public static byte[] hexDecode(String data) throws DecodingException {
        try {
            return Hex.decode(Utils.clearString(data));
        } catch (DecoderException e) {
            throw new DecodingException("Invalid hex data", "error.encoding.hex.invalid", e);
        }
    }

    public static byte[] base64Decode(String data) throws DecodingException {
        try {
            String base64Data = Utils.clearString(data);
            return Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            throw new DecodingException("Invalid base 64 data", "error.encoding.base64.invalid", e);
        }
    }

    public static byte[] base32Decode(String data) throws DecodingException {
        try {
            // a public key in base 32 might be entered with spaces
            String base32Data = Utils.clearString(data);
            BaseEncoding base32 = BaseEncoding.base32();
            return base32.decode(base32Data);
        } catch (IllegalArgumentException e) {
            throw new DecodingException("Invalid base 32 data", "error.encoding.base32.invalid", e);
        }
    }
}
