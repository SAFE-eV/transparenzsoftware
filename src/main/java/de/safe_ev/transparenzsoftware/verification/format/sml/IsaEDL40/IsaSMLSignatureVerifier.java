package de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40;

import static de.safe_ev.transparenzsoftware.Constants.BOUNCY_CASTLE_PROVIDER_CODE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.safe_ev.transparenzsoftware.Constants;
import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationLogger;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignatureVerifier;

public class IsaSMLSignatureVerifier extends SMLSignatureVerifier {

    private final static Logger LOGGER = LogManager.getLogger(IsaSMLSignatureVerifier.class);

    /**
     * Initializes the verifier and also the bouncy castle library as security
     * provider.
     */
    public IsaSMLSignatureVerifier() {
	// enable bc as provider
	Security.addProvider(new BouncyCastleProvider());
	PUBLIC_KEY_BYTES_LENGTH = 64;
	KEY_POINT_DATA_LENGTH = 32;
	ELLIPTIC_CURVE_ALGORITHM = "secp256r1";
	CROPPED_DATA_LENGTH = 32;
    }

    @Override
    public boolean verify(byte[] publicKey, byte[] signature, byte[] hashData) throws ValidationException {
	boolean verify;
	final byte[] r = Arrays.copyOfRange(signature, 0, signature.length / 2);
	final byte[] s = Arrays.copyOfRange(signature, signature.length / 2, signature.length);

	final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	final ASN1OutputStream derOutputStream = ASN1OutputStream.create(byteArrayOutputStream, ASN1Encoding.DER);
	// t he key is in asn1 format we calculate two points
	// using the DERSequence
	final ASN1EncodableVector v = new ASN1EncodableVector();
	v.add(new ASN1Integer(new BigInteger(PLUS_SIGN, r)));
	v.add(new ASN1Integer(new BigInteger(PLUS_SIGN, s)));
	try {
	    derOutputStream.writeObject(new DERSequence(v));
	} catch (final IOException e) {
	    throw new RuntimeException("Could not create DER sequence");
	}
	final byte[] derSignature = byteArrayOutputStream.toByteArray();
	try {
	    final Signature signatureVerifier = initSignature(publicKey, hashData);
	    verify = signatureVerifier.verify(derSignature);
	    VerificationLogger.log("IsaSML", ELLIPTIC_CURVE_ALGORITHM, publicKey, hashData, derSignature, verify);
	} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
	    throw new ValidationException("Failure on initialising the crypto algorithms", e);
	} catch (final SignatureException e) {
	    throw new ValidationException("Invalid signature supplied", e);
	} catch (final InvalidKeyException e) {
	    throw new ValidationException("Invalid public key supplied", e);
	}

	return verify;
    }

    @Override
    public boolean verify(byte[] publicKey, SMLSignature SMLSignature) throws ValidationException {
	boolean verify;

	final byte[] dataForHash = SMLSignature.buildExtendedSignatureData();
	final byte[] hashData = Utils.hashSHA256(dataForHash);
	// String hexHashData = Utils.bytesToHex(dataForHash);

	final byte[] signature = ((IsaEDL40Signature) SMLSignature).getDataSignature();
	verify = verify(publicKey, signature, hashData);

	return verify;
    }

    private Signature initSignature(byte[] publicKey, byte[] croppedPayloadData) throws NoSuchProviderException,
	    NoSuchAlgorithmException, ValidationException, SignatureException, InvalidKeyException {
	assert croppedPayloadData.length == CROPPED_DATA_LENGTH;
	final Signature signatureVerifier = Signature.getInstance(SIGNATURE_ALGORITHM, BOUNCY_CASTLE_PROVIDER_CODE);
	signatureVerifier.initVerify(getPublicKeyFromBytes(publicKey));
	signatureVerifier.update(croppedPayloadData);
	return signatureVerifier;
    }

    private BigInteger getPointXKeyCurve(byte[] pubKey) {
	assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

	final byte[] x = new byte[KEY_POINT_DATA_LENGTH];
	System.arraycopy(pubKey, 0, x, 0, KEY_POINT_DATA_LENGTH);
	// our points are always positive
	return new BigInteger(PLUS_SIGN, x);
    }

    private BigInteger getPointYKeyCurve(byte[] pubKey) {
	assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

	final byte[] y = new byte[KEY_POINT_DATA_LENGTH];
	System.arraycopy(pubKey, KEY_POINT_DATA_LENGTH, y, 0, KEY_POINT_DATA_LENGTH);
	// our points are always positive
	return new BigInteger(PLUS_SIGN, y);
    }

    private ECPublicKeySpec initPublicKeyCryptoSpecs(byte[] pubKey)
	    throws NoSuchProviderException, NoSuchAlgorithmException, InvalidParameterSpecException {
	assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;
	// sets up the Elliptic curve algorithm with bouncy castle
	final AlgorithmParameters parameters = AlgorithmParameters.getInstance(KEY_ALGORITHM,
		Constants.BOUNCY_CASTLE_PROVIDER_CODE);
	// sets the curve type
	parameters.init(new ECGenParameterSpec(ELLIPTIC_CURVE_ALGORITHM));
	// specs will be created out of the algorithm parameters to have right curve
	// to calculate the points on
	final ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);

	// Separate x and y of coordinates into separate variables
	// our public key consists of two 24 bytes long values
	// those are the coordinates on the curve and give us a point
	final ECPoint point = new ECPoint(getPointXKeyCurve(pubKey), getPointYKeyCurve(pubKey));
	return new ECPublicKeySpec(point, ecParameterSpec);
    }
}