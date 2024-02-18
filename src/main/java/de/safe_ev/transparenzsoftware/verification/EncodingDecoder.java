package de.safe_ev.transparenzsoftware.verification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.xml.PublicKey;

public class EncodingDecoder {

    private static final Logger LOGGER = LogManager.getLogger(EncodingDecoder.class);

    public static String decodePublicKey(PublicKey publicKey) throws DecodingException {
        return decodeAsHex(publicKey.getValue(), publicKey.getEncodingType());
    }

    /**
     * Decodes a string based on the given encoding type
     * @param data data to be decoded
     * @param type encoding type
     * @return data string decoded in a hex string
     * @throws DecodingException if data in an invalid format is given or if type is not known
     */
    public static String decodeAsHex(String data, EncodingType type) throws DecodingException {
        String dataToReturn;
        switch (type) {
            case PLAIN:
                dataToReturn = data;
                break;
            case BASE64:
                try {
                    byte[] decode = EncodingType.BASE64.decode(data);
                    dataToReturn = Utils.toFormattedHex(decode);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Invalid base 64 data provided: " + data);
                    throw new DecodingException("Invalid base 64 data provided", "error.encoding.base64.invalid");
                }
                break;
            default:
                LOGGER.error("Unknown encoding type provided" + type);
                throw new DecodingException("Unknown encoding type provided" + type, "error.encoding.unknowntype");
        }
        return dataToReturn;
    }

}
