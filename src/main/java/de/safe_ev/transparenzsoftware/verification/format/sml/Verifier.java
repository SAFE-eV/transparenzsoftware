package de.safe_ev.transparenzsoftware.verification.format.sml;

import de.safe_ev.transparenzsoftware.verification.ValidationException;

public interface Verifier {

    /**
     * Verifies a given data set
     *
     * @param publicKey   - public key for verifying
     * @param signature   - signature for verifying
     * @param payloadData - data which should be isVerified and where comparison
     *                    data will be build upon
     * @return boolean indication if verify was successfully
     * @throws ValidationException
     */
    boolean verify(byte[] publicKey, byte[] signature, byte[] payloadData) throws ValidationException;

}
