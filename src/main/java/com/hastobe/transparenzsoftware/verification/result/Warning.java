package com.hastobe.transparenzsoftware.verification.result;

import com.hastobe.transparenzsoftware.LocalizedException;
import com.hastobe.transparenzsoftware.verification.RegulationLawException;

public class Warning {

    private String message;
    private String localizedKey;

    public Warning(String message, String localizedKey) {
        this.message = message;
        this.localizedKey = localizedKey;
    }

    public Warning(RegulationLawException e) {
        this(e.getMessage(), e.getLocalizedMessageKey());
    }

    public String getMessage() {
        return message;
    }

    public String getLocalizedKey() {
        return localizedKey;
    }
}
