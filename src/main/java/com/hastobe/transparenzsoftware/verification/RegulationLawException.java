package com.hastobe.transparenzsoftware.verification;

import com.hastobe.transparenzsoftware.LocalizedException;

public class RegulationLawException extends LocalizedException {
    public RegulationLawException(String s, String localizedMessageKey) {
        super(s, localizedMessageKey);
    }

    public RegulationLawException(String s, String localizedMessageKey, Throwable throwable) {
        super(s, localizedMessageKey, throwable);
    }
}
