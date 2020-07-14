package com.hastobe.transparenzsoftware.output;

import com.hastobe.transparenzsoftware.verification.VerificationParser;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;

import java.util.Stack;

public class MockVerificationParser implements VerificationParser {

    public Stack<VerificationResult> results = new Stack<>();
    public boolean canParse = false;
    public VerificationType type = VerificationType.EDL_40_P;

    @Override
    public VerificationType getVerificationType() {
        return type;
    }

    @Override
    public boolean canParseData(String data) {
        return canParse;
    }

    @Override
    public VerificationResult parseAndVerify(String data, byte[] publicKey){
        return results.pop();
    }

    @Override
    public Class getVerfiedDataClass() {
        return null;
    }
}
