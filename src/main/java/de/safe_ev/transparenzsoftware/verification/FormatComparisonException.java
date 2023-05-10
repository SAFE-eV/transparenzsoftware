package de.safe_ev.transparenzsoftware.verification;


public class FormatComparisonException extends ValidationException {
    public FormatComparisonException() {
        super("Cannot compare data from different format", "app.verify.law.conform.different.formats");
    }

}
