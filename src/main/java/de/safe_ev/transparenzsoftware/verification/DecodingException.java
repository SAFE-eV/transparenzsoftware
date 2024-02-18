package de.safe_ev.transparenzsoftware.verification;

import de.safe_ev.transparenzsoftware.LocalizedException;

public class DecodingException extends LocalizedException {

    public DecodingException(String s, String localizedMessageKey) {
        super(s, localizedMessageKey);
    }

    public DecodingException(String s, String localizedMessageKey, Throwable e) {
        super(s, localizedMessageKey, e);
    }
}
