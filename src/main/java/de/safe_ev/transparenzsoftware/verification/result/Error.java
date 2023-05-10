package de.safe_ev.transparenzsoftware.verification.result;


import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;

public class Error {

    public enum Type {
        INPUT,
        VALIDATION,
        VERIFICATION
    }

    protected String message;
    protected Type type;
    private final String localizedMessageCode;

    public Error(Type errorType, String message, String localizedMessageCode) {
        this.message = message;
        this.type = errorType;
        this.localizedMessageCode = localizedMessageCode;
    }

    public static Error withValidationException(ValidationException e) {
        return new Error(Type.VALIDATION, e.getMessage(), e.getLocalizedMessageKey());
    }

    public static Error withRegulationLawException(RegulationLawException e) {
        return new Error(Type.VERIFICATION, e.getMessage(), e.getLocalizedMessageKey());
    }

    public static Error withVerificationFailed() {
        return new Error(Error.Type.VERIFICATION, "Verification failed", "error.verification.failed");
    }

    public static Error withDecodingPublicKeyFailed() {
        return new Error(Error.Type.INPUT, "no encoding found for key", "error.values.publickey.cannot.encode");
    }

    public static Error withDecodingSignatureFailed() {
        return new Error(Error.Type.INPUT, "no encoding found for signature", "error.values.signature.cannot.encode");
    }

    public String getMessage() {
        return message;
    }

    public String getLocalizedMessageCode() {
        return localizedMessageCode;
    }

    public Type getType() {
        return type;
    }

    public String getLocalizedMessage() {
        return Translator.get(localizedMessageCode);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Error)) {
            return false;
        }
        Error otherError = (Error) other;
        return type.equals(otherError.type) && this.getMessage().equals(((Error) other).getMessage());
    }

    @Override
    public String toString() {
        return String.format("Type %s: %s", type, message);
    }
}
