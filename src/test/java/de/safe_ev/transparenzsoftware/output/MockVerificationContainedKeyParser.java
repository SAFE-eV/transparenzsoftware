package de.safe_ev.transparenzsoftware.output;

import de.safe_ev.transparenzsoftware.verification.ContainedPublicKeyParser;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;

public class MockVerificationContainedKeyParser extends MockVerificationParser implements VerificationParser, ContainedPublicKeyParser {

    public String parsePublicKeyResult;

    @Override
    public String parsePublicKey(String data) {
        return parsePublicKeyResult;
    }

    @Override
    public String createFormattedKey(String data) {
        return data;
    }
}
