package de.safe_ev.transparenzsoftware.output;

import java.util.Stack;

import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

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
	public VerificationResult parseAndVerify(String data, byte[] publicKey, IntrinsicVerified intrinsicVerified) {
		return results.pop();
	}

	@Override
	public Class getVerfiedDataClass() {
		return null;
	}
}
