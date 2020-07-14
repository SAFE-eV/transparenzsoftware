package com.hastobe.transparenzsoftware.verification;

import com.hastobe.transparenzsoftware.LocalizedException;

public class DecodingException extends LocalizedException {

    public DecodingException(String s, String localizedMessageKey) {
        super(s, localizedMessageKey);
    }

    public DecodingException(String s, String localizedMessageKey, Throwable e) {
        super(s, localizedMessageKey, e);
    }
}
