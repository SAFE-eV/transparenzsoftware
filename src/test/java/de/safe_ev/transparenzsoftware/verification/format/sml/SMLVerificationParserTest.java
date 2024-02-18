package de.safe_ev.transparenzsoftware.verification.format.sml;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDL40.EDL40Signature;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDL40.EDL40VerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.SignatureOnlyVerificationParser;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public class SMLVerificationParserTest {

	private static final String DATA_SIG_ONLY = "CQFFTUgAAHNA8shDEVsIAQABEQD/Hv/eEwAAAAAAAABw38UoCHit8XUSWKGZS41cA5TgDQjvvnTgvmnXIN7Y5JcPK57sM50JrVSw/jgpN3SKAHAwNDc3MjdlYTAxMmI4MAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABdEEVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
	public static final String DATA_SIG_ONLY_SIGNATURE = "kpsT2TfwP3Cwxwq2ikvrqZJqdMo/x2xRIZqUuBquNA6OjQUO52zCMPMKX/nMclnQAGw=";

	private static final String DATA_EBEE_FULL = "GxsbGwEBAQF2BZ3J/WxiAGIAcmMBAXYBBUViZWUFXcdVGgsJAUVNSAAAesZiAQFj7B0AdgWdyf1rYgBiAHJjBwF3AQsJAUVNSAAAesZiB4GAgXED/3JiAWUABTFgdHcHgYKBVAH/AXJiA3NlW4V/AVMAPFMAPAEBDWFiY2RlZmdoLTEyMwF3BwEAAREA/2QBAQhyYgNzZVuFfwFTADxTADxiHlL/VgAAAAtmAXcHgQBgCAABAQEBAXJiAXJiAWUABTE9AXcHgYCBcQH/AQEBAWUAAABBAYMEKfiBzm3mT65YsoQM/354kgWHkia8tSFep6/OQrhl1qXUGrkwOixS3hs1B5b5KYcVAAoBYzQvAHYFncn9bWIAYgByYwIBcQFj8nIAABsbGxsaAZkP";
	private static final String EBEE_PUBLIC_KEY = "3108 fa2a caa4 45ab 6aef 6465 c98d a3e6 c3e7 b8bc b14e 1c5f 6b02 1064 b59c 7f67 2511 f183 ddac 0e8e 405c 3196 3d2c d73e";

	/**
	 * Verifies that a full sml from a station can be parsed.
	 */
	@Test
	public void canParseFullSMLType() {
		final EDL40VerificationParser verificationParser = new EDL40VerificationParser();
		Assert.assertEquals(verificationParser.getVerificationType(), VerificationType.EDL_40_P);
	}

	/**
	 * Verifies that a full sml from a station can be parsed.
	 */
	@Test
	public void canParseFullSML() {
		final EDL40VerificationParser verificationParser = new EDL40VerificationParser();
		verificationParser.canParseData(DATA_EBEE_FULL);
	}

	/**
	 * Verifies that a full sml from a station can be parsed.
	 */
	@Test
	public void parseFullSML() throws DecodingException {
		final EDL40VerificationParser verificationParser = new EDL40VerificationParser();

		final VerificationResult result = verificationParser.parseAndVerify(DATA_EBEE_FULL,
				EncodingType.hexDecode(EBEE_PUBLIC_KEY), IntrinsicVerified.NOT_VERIFIED);
		Assert.assertTrue(result.isVerified());
		Assert.assertEquals(0, result.getErrorMessages().size());
		Assert.assertEquals(0, result.getErrorMessages().size());
		Assert.assertEquals(VerificationType.EDL_40_P.name(), result.getVerifiedData().getFormat());
		Assert.assertEquals(EncodingType.BASE64.getCode(), result.getVerifiedData().getEncoding());
		Assert.assertEquals(Utils.toFormattedHex(Hex.decode(EBEE_PUBLIC_KEY)), result.getVerifiedData().getPublicKey());
		Assert.assertEquals(0.2918, result.getMeters().get(0).getValue(), 0);

		final LocalDateTime dateTime = LocalDateTime.of(2018, Month.AUGUST, 28, 18, 57, 37);
		final OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+02:00"));
		Assert.assertEquals(offsetDateTime, result.getMeters().get(0).getTimestamp());
	}

	/**
	 * Verifies that a sml payload data array from a station can be parsed.
	 */
	@Test
	public void canParseSignatureType() {
		final SignatureOnlyVerificationParser verificationParser = new SignatureOnlyVerificationParser();
		Assert.assertEquals(verificationParser.getVerificationType(), VerificationType.EDL_40_SIG);
	}

	/**
	 * Verifies that a sml payload data array from a station can be parsed.
	 */
	@Test
	public void canParseSignatureData() {
		final SignatureOnlyVerificationParser verificationParser = new SignatureOnlyVerificationParser();
		Assert.assertTrue(verificationParser.canParseData(TestUtils.TEST_SIG_ONLY));
	}

	@Test
	public void buildAdditionalData() throws ValidationException {
		final EDL40Signature signatureData = new EDL40Signature();
		final OffsetDateTime offsetDateTimeCid = OffsetDateTime
				.of(LocalDateTime.ofEpochSecond(1504275196, 0, ZoneOffset.UTC), ZoneOffset.UTC);
		final OffsetDateTime offsetDateTimestamp = OffsetDateTime
				.of(LocalDateTime.ofEpochSecond(1504275196, 0, ZoneOffset.UTC), ZoneOffset.UTC);
		signatureData.setTimestampContractId(offsetDateTimeCid);
		signatureData.setTimestamp(offsetDateTimestamp);
		signatureData.setObisNr(new byte[] { 0, 1, 2, 3, 4, 5 });
		signatureData.setMeterPosition(1234);
		signatureData.setBytesLog((byte) 4, (byte) 5);
		signatureData.setSecondsIndex(10);
		signatureData.setPagination(11);
		final String serverIDHex = "09 01 45 4D 48 00 00 6B B3 28";
		signatureData.setServerId(Hex.decode(serverIDHex));
		final String customerID = "01 04 05";
		signatureData.setContractId(Hex.decode(customerID), true);
		signatureData.setProvidedSignature(Hex.decode("AA 00 FF AA 00 EE"));
		signatureData.setProvidedSignature(new byte[48]);
		final SMLVerifiedData smlVerifiedData = new SMLVerifiedData(signatureData, VerificationType.EDL_40_SIG,
				EncodingType.PLAIN, "test");

		final DetailsList additionalData = smlVerifiedData.getAdditionalData();
		Assert.assertEquals(11, additionalData.size());
		Assert.assertEquals("09 01 45 4D 48 00 00 6B B3 28",
				additionalData.get(Translator.get("app.verify.sml.serverId")));
		Assert.assertEquals("01 04 05", additionalData.get(Translator.get("app.verify.sml.customerId")));
	}

}
