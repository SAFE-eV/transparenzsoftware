package de.safe_ev.transparenzsoftware.verification;

import de.safe_ev.transparenzsoftware.i18n.Translator;

public class ValidationException extends Exception {

    protected String localizedMessageKey;

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(String message, String localizedKey) {
        super(message);
        this.localizedMessageKey = localizedKey;
    }

    public ValidationException(String message, String localizedKey, Throwable throwable) {
        super(message, throwable);
        this.localizedMessageKey = localizedKey;
    }

    public ValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    @Override
    public String getLocalizedMessage() {
        return Translator.get(getLocalizedMessageKey());
    }

    public String getLocalizedMessageKey() {
        return localizedMessageKey == null ? "error.validation.error" : localizedMessageKey;
    }
}
