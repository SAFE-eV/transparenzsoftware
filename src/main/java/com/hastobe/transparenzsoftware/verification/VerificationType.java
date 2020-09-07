package com.hastobe.transparenzsoftware.verification;

public enum VerificationType {

    ISA_EDL_40_P(false),
    EDL_40_P(false),
    EDL_40_MENNEKES(true),
    EDL_40_SIG(true),
    OCMF(false),
    ALFEN(true),;


    private final boolean publicKeyAware;

    VerificationType(boolean publicKeyAware) {
        this.publicKeyAware = publicKeyAware;
    }

    public boolean isPublicKeyAware() {
        return publicKeyAware;
    }
}
