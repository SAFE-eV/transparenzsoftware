package de.safe_ev.transparenzsoftware.verification;

import de.safe_ev.transparenzsoftware.i18n.Translator;

public class TransactionValidationException extends Exception {

    protected String localizedMessageKey;

    public TransactionValidationException(String s) {
        super(s);
    }

    public TransactionValidationException(String message, String localizedKey) {
        super(message);
        this.localizedMessageKey = localizedKey;
    }

    public TransactionValidationException(String message, String localizedKey, Throwable throwable) {
        super(message, throwable);
        this.localizedMessageKey = localizedKey;
    }

    public TransactionValidationException(String s, Throwable throwable) {
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
