package de.safe_ev.transparenzsoftware.verification.format.sml;

public class SMLSignatureVerifier256 extends SMLSignatureVerifier {

    /**
     * Initializes the verifier and also the bouncy castle library as security
     * provider.
     */
    public SMLSignatureVerifier256() {
	super();
	CROPPED_DATA_LENGTH = 32;
	ELLIPTIC_CURVE_ALGORITHM = "secp256r1";
	KEY_POINT_DATA_LENGTH = 32;
	PUBLIC_KEY_BYTES_LENGTH = 64;
	CROPPED_HASH_LEN = 32;
    }

}