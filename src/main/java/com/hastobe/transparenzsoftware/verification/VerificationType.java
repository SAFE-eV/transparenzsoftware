package com.hastobe.transparenzsoftware.verification;

public enum VerificationType {
    OCMF(false),
    PCDF(false),
    ISA_EDL_40_P(false),
    ALFEN(true),
    EDL_40_P(false),
    EDL_40_SIG(true),
    EDL_40_MENNEKES(true),
    ;

    private final boolean publicKeyAware;

    VerificationType(boolean publicKeyAware) {
        this.publicKeyAware = publicKeyAware;
    }

    public boolean isPublicKeyAware() {
        return publicKeyAware;
    }
}
