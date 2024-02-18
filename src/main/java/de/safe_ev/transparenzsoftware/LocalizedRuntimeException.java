package de.safe_ev.transparenzsoftware;

import de.safe_ev.transparenzsoftware.i18n.Translator;

public abstract class LocalizedRuntimeException extends RuntimeException {

    protected String localizedMessageKey;

    public LocalizedRuntimeException(String s, String localizedMessageKey) {
        super(s);
        this.localizedMessageKey = localizedMessageKey;
    }

    public LocalizedRuntimeException(String s, String localizedMessageKey, Throwable throwable) {
        super(s, throwable);
        this.localizedMessageKey = localizedMessageKey;
    }

    @Override
    public String getLocalizedMessage() {
        return Translator.get(localizedMessageKey);
    }

    public String getLocalizedMessageKey(){
        return localizedMessageKey;
    }
}
