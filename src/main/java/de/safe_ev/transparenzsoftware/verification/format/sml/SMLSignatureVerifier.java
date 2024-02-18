package de.safe_ev.transparenzsoftware.verification.format.sml;

import static de.safe_ev.transparenzsoftware.Constants.BOUNCY_CASTLE_PROVIDER_CODE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
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
import org.bouncycastle.util.encoders.Hex;

import de.safe_ev.transparenzsoftware.Constants;
import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationLogger;

public class SMLSignatureVerifier implements Verifier {

    private final static Logger LOGGER = LogManager.getLogger(SMLSignatureVerifier.class);

    /**
     * "Vorzeichen" we want to make sure that all points are seen as positive
     */
    protected int PLUS_SIGN = 1;
    protected String SIGNATURE_ALGORITHM = "NonewithECDSA";
    protected int CROPPED_DATA_LENGTH = 24;
    protected String ELLIPTIC_CURVE_ALGORITHM = "secp192r1";
    protected String KEY_ALGORITHM = "EC";
    protected int KEY_POINT_DATA_LENGTH = 24;
    protected int PUBLIC_KEY_BYTES_LENGTH = 48;
    protected int CROPPED_HASH_LEN = 24;

    /**
     * Initializes the verifier and also the bouncy castle library as security
     * provider.
     */
    public SMLSignatureVerifier() {
	// enable bc as provider
	Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public boolean verify(byte[] publicKey, byte[] signature, byte[] payloadData) throws ValidationException {
	payloadData = Arrays.copyOfRange(payloadData, 0, CROPPED_DATA_LENGTH);
	final byte[] derSignature = signatureToDER(signature);
	try {
	    final PublicKey parsed = getPublicKeyFromBytes(publicKey);
	    final Signature signatureVerifier = initSignature(parsed, payloadData);
	    final boolean result = signatureVerifier.verify(derSignature);
	    VerificationLogger.log("SML", ELLIPTIC_CURVE_ALGORITHM, publicKey, payloadData, signature, result);
	    return result;
	} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
	    throw new ValidationException("Failure on initialising the crypto algorithms", e);
	} catch (final SignatureException e) {
	    VerificationLogger.log("SML", ELLIPTIC_CURVE_ALGORITHM, publicKey, payloadData, signature, false);
	    throw new ValidationException("Invalid signature supplied", e);
	} catch (final InvalidKeyException e) {
	    throw new ValidationException("Invalid public key supplied", e);
	}
    }

    private Signature initSignature(PublicKey publicKey, byte[] croppedPayloadData) throws NoSuchProviderException,
	    NoSuchAlgorithmException, ValidationException, SignatureException, InvalidKeyException {
	assert croppedPayloadData.length == CROPPED_DATA_LENGTH;
	final Signature signatureVerifier = Signature.getInstance(SIGNATURE_ALGORITHM, BOUNCY_CASTLE_PROVIDER_CODE);
	signatureVerifier.initVerify(publicKey);
	signatureVerifier.update(croppedPayloadData);
	return signatureVerifier;
    }

    /**
     * @param publicKeyBytes public key in bytes (two points on the curve)
     * @param signatureSML   SMLSignature parsed
     * @return true if success
     * @throws ValidationException
     */
    public boolean verify(byte[] publicKeyBytes, SMLSignature signatureSML) throws ValidationException {
	final byte[] providedData = signatureSML.buildExtendedSignatureData();
	LOGGER.info("Provided:    " + Hex.toHexString(providedData));
	final byte[] hashedData = Utils.hashSHA256(providedData);
	final byte[] hashedDataCropped = Arrays.copyOfRange(hashedData, 0, CROPPED_HASH_LEN);

	// 48 bytes because the last 2 bytes are from the logbook
	final byte[] signatureData = signatureSML.getProvidedSignature();
	int cutoff = signatureSML.getVersion() == 4 ? 2 : 0;
	if (signatureData.length == 50) {
	    cutoff = 2;
	}
	final byte[] signatureCropped = Arrays.copyOfRange(signatureData, 0, signatureData.length - cutoff);
	return verify(publicKeyBytes, signatureCropped, hashedDataCropped);
    }

    /**
     * Calculates out the two points format (byte array contains two points) a DER
     * format readable for the crypto library.
     *
     * @param signature signature with the two containing points
     * @return
     */
    public byte[] signatureToDER(byte[] signature) {
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
	return byteArrayOutputStream.toByteArray();
    }

    /**
     * Calculates out of a publicKey in byte format which contains two points on the
     * curve in a PublicKey object ready for usage in the crypto algorithms
     *
     * @param pubKey public key in bytes containing two points have to be 48 bytes
     *               long
     * @return calculated public key out of the two points of the byte array
     * @throws ValidationException if public key cannot be created
     */
    public PublicKey getPublicKeyFromBytes(byte[] pubKey) throws ValidationException {

	if (pubKey.length != PUBLIC_KEY_BYTES_LENGTH) {
	    LOGGER.error("Invalid public key length received, expected: " + PUBLIC_KEY_BYTES_LENGTH + " but was "
		    + pubKey.length);
	    throw new ValidationException("Public key is not " + PUBLIC_KEY_BYTES_LENGTH + " bytes long",
		    "error.invalid.public.key");
	}

	LOGGER.info("Trying " + KEY_ALGORITHM + " with " + PUBLIC_KEY_BYTES_LENGTH);
	try {
	    final KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM, Constants.BOUNCY_CASTLE_PROVIDER_CODE);
	    return kf.generatePublic(initPublicKeyCryptoSpecs(pubKey));
	} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
	    LOGGER.error(e.getClass().getSimpleName() + " occurred when trying to get public key from raw bytes", e);
	    throw new RuntimeException("Cannot calculate the public key failure in crypt library");
	} catch (InvalidParameterSpecException | InvalidKeySpecException e) {
	    throw new ValidationException("Could not create a public key", "error.invalid.public.key", e);
	}
    }

    /**
     * Sets up the elliptic key specs to create a public key out of two points on
     * the elliptic curve
     *
     * @param pubKey as byte array (each half of the byte array represents a point)
     * @return Key specs
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidParameterSpecException
     */
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

    /**
     * Get the point x of the curve this is the first half of the bytes of the
     * pubkey
     *
     * @param pubKey byte array of the public key
     * @return big integer represents a point coordinate on the curve
     */
    private BigInteger getPointXKeyCurve(byte[] pubKey) {
	assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

	final byte[] x = new byte[KEY_POINT_DATA_LENGTH];
	System.arraycopy(pubKey, 0, x, 0, KEY_POINT_DATA_LENGTH);
	// our points are always positive
	return new BigInteger(PLUS_SIGN, x);
    }

    /**
     * Get the point y of the curve this is the second half of the bytes of the
     * pubkey
     *
     * @param pubKey byte array of the public key
     * @return big integer represents a point coordinate on the curve
     */
    private BigInteger getPointYKeyCurve(byte[] pubKey) {
	assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

	final byte[] y = new byte[KEY_POINT_DATA_LENGTH];
	System.arraycopy(pubKey, KEY_POINT_DATA_LENGTH, y, 0, KEY_POINT_DATA_LENGTH);
	// our points are always positive
	return new BigInteger(PLUS_SIGN, y);
    }
}