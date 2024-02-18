package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import de.safe_ev.transparenzsoftware.verification.ValidationException;

public class OCMFValidationException extends ValidationException {

    public OCMFValidationException(String s) {
        super(s);
    }

    public OCMFValidationException(String message, String localizedKey) {
        super(message, localizedKey);
    }

    public OCMFValidationException(String message, String localizedKey, Throwable throwable) {
        super(message, localizedKey, throwable);
    }

    public OCMFValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    @Override
    public String getLocalizedMessageKey() {
        return localizedMessageKey == null ? "error.ocmf.validation" : localizedMessageKey;
    }
}
