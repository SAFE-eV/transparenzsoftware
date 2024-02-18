package de.safe_ev.transparenzsoftware.verification;

import de.safe_ev.transparenzsoftware.verification.result.Error;

public class VerificationException extends Exception {

    private Error error;

    public VerificationException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
