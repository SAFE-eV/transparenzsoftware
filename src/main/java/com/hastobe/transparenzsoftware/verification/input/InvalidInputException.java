package com.hastobe.transparenzsoftware.verification.input;

import com.hastobe.transparenzsoftware.LocalizedException;

/**
 * Exception class which is used to indicate a wrong input format (wrong file,
 * invalid xml).
 */
public class InvalidInputException extends LocalizedException {

    public InvalidInputException(String s, String localizedMessageKey, Throwable throwable) {
        super(s, localizedMessageKey, throwable);

    }

    public InvalidInputException(String message, String localizedMessageKey) {
        super(message, localizedMessageKey);
    }
}
