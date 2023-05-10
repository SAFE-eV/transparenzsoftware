package de.safe_ev.transparenzsoftware.verification;

/**
 * Interface for types which contain the public key in the payload data
 */
public interface ContainedPublicKeyParser {

    /**
     * Tries to verify a public key out of the data if it cannot be parsed
     * an empty string should be returned
     *
     * @return parsed public key or empty string if the key could not be parsed
     */
    String parsePublicKey(String data);

    /**
     * Should return a string in the format the parser wants it to be shown.
     * This is necessary as multiple formats want to show the public key in
     * different formats
     *
     * @param data data to check and format
     * @return returns a formatted string of the contained key
     */
    String createFormattedKey(String data);
}
