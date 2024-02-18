package de.safe_ev.transparenzsoftware.verification;

import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public interface VerificationParser {

	/**
	 * Indicates if that parser can do a verify on the data
	 *
	 * @return the verification type of that parser
	 */
	VerificationType getVerificationType();

	/**
	 * Tries to check if the data is a candidate for parsing
	 *
	 * @param data
	 * @return
	 */
	boolean canParseData(String data);

	/**
	 * Verifies given data with a public key and data which contains the information
	 * to create the signature and the verification data.
	 *
	 * @param data               data to verify
	 * @param publicKey          public key used in verification
	 * @param instrinsicVerified if the data has been already verified by encryption
	 * @return result wrapped in a class
	 */
	VerificationResult parseAndVerify(String data, byte[] publicKey, IntrinsicVerified instrinsicVerified);

	/**
	 * Defines the class used for the verified data outputs
	 *
	 * @return Class
	 */
	Class getVerfiedDataClass();
}
