package de.safe_ev.transparenzsoftware.verification.format.pcdf;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;

import de.safe_ev.transparenzsoftware.verification.ContainedPublicKeyParser;
import de.safe_ev.transparenzsoftware.verification.VerificationLogger;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.Error.Type;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public class PcdfVerificationParser implements VerificationParser, ContainedPublicKeyParser {

	@Override
	public String parsePublicKey(String data) {
		final int pos = data.indexOf("(PK:");

		if (pos != -1) {
			String pk = data.substring(pos + 4);
			final int pos2 = pk.indexOf(")(SG:");
			if (pos2 != -1) {
				pk = pk.substring(0, pos2);
			} else {
				pk = pk.substring(0, pk.length() - 1);
			}
			return pk;
		} else {
			return null;
		}
	}

	@Override
	public String createFormattedKey(String data) {
		final int pos = data.indexOf("(PK:");
		if (pos != -1) {
			final int ps = data.indexOf(")", pos);
			final String pubKey = data.substring(pos + 4, ps);
			return pubKey;
		}
		return null;
	}

	@Override
	public VerificationType getVerificationType() {
		return VerificationType.PCDF;
	}

	@Override
	public boolean canParseData(String data) {
		final int pos = data.indexOf("128.8.0");
		if ((pos == 0) || (pos == 1) || (pos == 6)) {
			return true;
		} else if (data.indexOf("(RV:") > 0) {
			return true;
		}
		return false;
	}

	private boolean checkSignAndPublicKeyByte(String data, String sign, byte[] pke) {
		try {
			final int lns = sign.length();
			final byte[] se = new byte[lns / 2];
			for (int i = 0; i < lns / 2; i++) {
				final String val = sign.substring(i * 2, i * 2 + 2);
				se[i] = (byte) Integer.parseInt(val, 16);
			}

			final byte[] x = new byte[32];
			final byte[] y = new byte[32];
			System.arraycopy(pke, 1, x, 0, 32);
			System.arraycopy(pke, 33, y, 0, 32);

			try {
				final X9ECParameters params = SECNamedCurves.getByName("secp256r1");
				final ECDomainParameters ecParams = new ECDomainParameters(params.getCurve(), params.getG(),
						params.getN(), params.getH());
				final ECPublicKeyParameters pubKeyParams = new ECPublicKeyParameters(
						ecParams.getCurve().decodePoint(pke), ecParams);

				final ASN1InputStream asn1 = new ASN1InputStream(se);
				final ECDSASigner signer2 = new ECDSASigner();
				// not for signing...
				signer2.init(false, pubKeyParams);
				final DLSequence seq = (DLSequence) asn1.readObject();
				final BigInteger r = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
				final BigInteger s = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();

				final MessageDigest digest = MessageDigest.getInstance("SHA-256");
				final byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
				final boolean result = signer2.verifySignature(hash, r.abs(), s.abs());
				VerificationLogger.log("PCDF", "secp256r1", pke, hash, se, result);
				return result;
			} catch (final NoSuchAlgorithmException e) {
				// getLogger().error(e.getClass().getSimpleName() + " occurred when trying to
				// get public key from raw bytes", e);
				// return null;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private byte[] makePublicKeyByte(String pk) {
		final int ln = pk.length();
		final byte[] pke = new byte[ln / 2];

		int i = 0;
		for (i = 0; i < ln / 2; i++) {
			final String val = pk.substring(i * 2, i * 2 + 2);
			pke[i] = (byte) Integer.parseInt(val, 16);
		}
		return pke;
	}

	private boolean checkTwoBytePublicKeys(byte[] pk1, byte[] pk2) {
		if (pk1.length != pk2.length) {
			return false;
		} else {
			for (int i = 0; i < pk1.length; i++) {
				if (pk1[i] != pk2[i]) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkSignAndPublicKey(String data, String sign, String pk) {
		try {
			final int ln = pk.length();
			final byte[] pke = new byte[ln / 2];

			int i = 0;
			for (i = 0; i < ln / 2; i++) {
				final String val = pk.substring(i * 2, i * 2 + 2);
				pke[i] = (byte) Integer.parseInt(val, 16);
				// System.out.println("PKE [" + Integer.toString(i) + "]=" +
				// Integer.toString(pke[i]));
			}
			// System.out.println("PKE : " + pke.toString());

			final int lns = sign.length();
			final byte[] se = new byte[lns / 2];
			for (i = 0; i < lns / 2; i++) {
				final String val = sign.substring(i * 2, i * 2 + 2);
				se[i] = (byte) Integer.parseInt(val, 16);
				// System.out.println("SE [" + Integer.toString(i) + "]=" +
				// Integer.toString(se[i]));
			}

			// System.out.println("SE : " + pke.toString());

			final byte[] x = new byte[32];
			final byte[] y = new byte[32];
			System.arraycopy(pke, 1, x, 0, 32);
			System.arraycopy(pke, 33, y, 0, 32);
			// System.out.println("X: " + x.toString() + " Y: " + y.toString());

			try {
				final KeyFactory kf = KeyFactory.getInstance("EC");

				final AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
				parameters.init(new ECGenParameterSpec("secp256r1"));
				final ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);

				final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(
						new ECPoint(new BigInteger(x), new BigInteger(y)), ecParameterSpec);
				final ECPublicKey ecPublicKey = (ECPublicKey) kf.generatePublic(ecPublicKeySpec);

				final PublicKey publicKey = kf.generatePublic(ecPublicKeySpec);

				final Signature signature = Signature.getInstance("SHA256withECDSA");
				signature.initVerify(publicKey);

				final byte[] bytes = data.getBytes();
				signature.update(bytes);

				final boolean verified = signature.verify(se);
				if (verified) {
					return true;
				} else {
					return false;
				}
			} catch (NoSuchAlgorithmException | InvalidParameterSpecException | InvalidKeySpecException e) {
				// getLogger().error(e.getClass().getSimpleName() + " occurred when trying to
				// get public key from raw bytes", e);
				// return null;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public VerificationResult parseAndVerify(String data, byte[] publicKey, IntrinsicVerified intrinsicVerified) {
		final String pbKeyStr = parsePublicKey(data);
		final PcdfVerifiedData verData = new PcdfVerifiedData(pbKeyStr, data);
		VerificationResult vr = null;
		final byte[] dtPK = makePublicKeyByte(pbKeyStr);

		// if (publicKey.length == (pbKeyStr.length() / 2))
		if (checkTwoBytePublicKeys(dtPK, publicKey)) {
			final int pos = data.indexOf("(SG:");
			if (pos != -1) {
				final String justData = data.substring(0, pos);

				final String sign = data.substring(pos + 4, data.length() - 1);

				if (!checkSignAndPublicKeyByte(justData, sign, publicKey)) {
					de.safe_ev.transparenzsoftware.verification.result.Error er;
					er = new de.safe_ev.transparenzsoftware.verification.result.Error(Type.VALIDATION,
							"Signature verification failed", "error.pcdf.verification.signature.failed");
					vr = new VerificationResult(verData, false, intrinsicVerified);
					vr.addError(er);
				} else {
					vr = new VerificationResult(verData, true, intrinsicVerified);
				}
			} else {
				de.safe_ev.transparenzsoftware.verification.result.Error er;
				er = new de.safe_ev.transparenzsoftware.verification.result.Error(Type.VALIDATION,
						"No signature present in data tupple", "error.pcdf.missing.signature");
				vr = new VerificationResult(verData, false, intrinsicVerified);
				vr.addError(er);
			}
		} else {
			de.safe_ev.transparenzsoftware.verification.result.Error er;
			er = new de.safe_ev.transparenzsoftware.verification.result.Error(Type.VALIDATION, "Wrong Public Key",
					"error.invalid.public.key.embedded");
			vr = new VerificationResult(verData, false, intrinsicVerified);
			vr.addError(er);
		}

		return vr;
	}

	@Override
	public Class getVerfiedDataClass() {
		return PcdfVerifiedData.class;
	}

}
