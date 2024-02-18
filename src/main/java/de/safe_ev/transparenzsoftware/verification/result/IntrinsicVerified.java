package de.safe_ev.transparenzsoftware.verification.result;

public enum IntrinsicVerified {

	VERIFIED, NOT_VERIFIED,;

	public boolean ok() {
		return this == VERIFIED;
	}

}
