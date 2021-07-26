package com.hastobe.transparenzsoftware.verification.format.sml;

import com.hastobe.transparenzsoftware.Constants;
import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

import static com.hastobe.transparenzsoftware.Constants.BOUNCY_CASTLE_PROVIDER_CODE;

public class SMLSignatureVerifier implements Verifier {

    private final static Logger LOGGER = LogManager.getLogger(SMLSignatureVerifier.class);

    /**
     * "Vorzeichen" we want to make sure that all points are seen as positive
     */
    protected  int PLUS_SIGN = 1;
    protected  String SIGNATURE_ALGORITHM = "NonewithECDSA";
    protected  int CROPPED_DATA_LENGTH = 24;
    protected  String ELLIPTIC_CURVE_ALGORITHM = "secp192r1";
    protected  String KEY_ALGORITHM = "EC";
    protected  int KEY_POINT_DATA_LENGTH = 24;
    protected  int PUBLIC_KEY_BYTES_LENGTH = 48;

    /**
     * Initializes the verifier and also the bouncy castle library as security
     * provider.
     */
    public SMLSignatureVerifier() {
        //enable bc as provider
        Security.addProvider(new BouncyCastleProvider());
    }


    @Override
    public boolean verify(byte[] publicKey, byte[] signature, byte[] payloadData) throws ValidationException {
        payloadData = Arrays.copyOfRange(payloadData, 0, CROPPED_DATA_LENGTH);
        byte[] derSignature = signatureToDER(signature);
        try {
            Signature signatureVerifier = initSignature(publicKey, payloadData);
            LOGGER.info("Signature:    "+Hex.toHexString(signature));
            LOGGER.info("Public key:   "+Hex.toHexString(publicKey));
            LOGGER.info("Hashed data:  "+Hex.toHexString(payloadData));
            boolean result = signatureVerifier.verify(derSignature);
            LOGGER.info("Verified:     "+result);
            return result;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new ValidationException("Failure on initialising the crypto algorithms", e);
        } catch (SignatureException e) {
            throw new ValidationException("Invalid signature supplied", e);
        } catch (InvalidKeyException e) {
            throw new ValidationException("Invalid public key supplied", e);
        }
    }

    private Signature initSignature(byte[] publicKey, byte[] croppedPayloadData) throws NoSuchProviderException, NoSuchAlgorithmException, ValidationException, SignatureException, InvalidKeyException {
        assert croppedPayloadData.length == CROPPED_DATA_LENGTH;
        Signature signatureVerifier = Signature.getInstance(SIGNATURE_ALGORITHM, BOUNCY_CASTLE_PROVIDER_CODE);
        signatureVerifier.initVerify(getPublicKeyFromBytes(publicKey));
        signatureVerifier.update(croppedPayloadData);
        return signatureVerifier;
    }

    /**
     * @param publicKeyBytes public key in bytes (two points on the curve)
     * @param SMLSignature   SMLSignature parsed
     * @return true if success
     * @throws ValidationException
     */
    public boolean verify(byte[] publicKeyBytes, SMLSignature SMLSignature) throws ValidationException {
    	byte[] providedData = SMLSignature.buildExtendedSignatureData();
        LOGGER.info("Provided:    "+Hex.toHexString(providedData));
        byte[] hashedData = Utils.hashSHA256(providedData);
        byte[] hashedDataCropped = Arrays.copyOfRange(hashedData, 0, 24);

        //48 bytes because the last 2 bytes are from the logbook
        byte[] signedData = SMLSignature.getProvidedSignature();
        byte[] signatureCropped = Arrays.copyOfRange(signedData, 0, 48);
        return verify(publicKeyBytes, signatureCropped, hashedDataCropped);
    }

    /**
     * Calculates out the two points format (byte array contains two points)
     * a DER format readable for the crypto library.
     *
     * @param signature signature with the two containing points
     * @return
     */
    public  byte[] signatureToDER(byte[] signature) {
        byte[] r = Arrays.copyOfRange(signature, 0, signature.length / 2);
        byte[] s = Arrays.copyOfRange(signature, signature.length / 2, signature.length);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
        //t he key is in asn1 format we calculate two points
        // using the DERSequence
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(new BigInteger(PLUS_SIGN, r)));
        v.add(new ASN1Integer(new BigInteger(PLUS_SIGN, s)));
        try {
            derOutputStream.writeObject(new DERSequence(v));
        } catch (IOException e) {
            throw new RuntimeException("Could not create DER sequence");
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Calculates out of a publicKey in byte format which contains two points
     * on the curve in a PublicKey object ready for usage in the crypto algorithms
     *
     * @param pubKey public key in bytes containing two points have to be 48 bytes long
     * @return calculated public key out of the two points of the byte array
     * @throws ValidationException if public key cannot be created
     */
    public  PublicKey getPublicKeyFromBytes(byte[] pubKey) throws ValidationException {

        if (pubKey.length != PUBLIC_KEY_BYTES_LENGTH) {
            LOGGER.error("Invalid public key length received");
            throw new ValidationException("Public key is not 48 bytes long", "error.invalid.public.key");
        }

        try {
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM, Constants.BOUNCY_CASTLE_PROVIDER_CODE);
            return kf.generatePublic(initPublicKeyCryptoSpecs(pubKey));
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            LOGGER.error(e.getClass().getSimpleName() + " occurred when trying to get public key from raw bytes", e);
            throw new RuntimeException("Cannot calculate the public key failure in crypt library");
        } catch (InvalidParameterSpecException | InvalidKeySpecException e) {
            throw new ValidationException("Could not create a public key", "error.invalid.public.key", e);
        }
    }

    /**
     * Sets up the elliptic key specs to create a public key out of two points
     * on the elliptic curve
     *
     * @param pubKey as byte array (each half of the byte array represents a point)
     * @return Key specs
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidParameterSpecException
     */
    private  ECPublicKeySpec initPublicKeyCryptoSpecs(byte[] pubKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidParameterSpecException {
        assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;
        //sets up the Elliptic curve algorithm with bouncy castle
        AlgorithmParameters parameters = AlgorithmParameters.getInstance(KEY_ALGORITHM, Constants.BOUNCY_CASTLE_PROVIDER_CODE);
        //sets the curve type
        parameters.init(new ECGenParameterSpec(ELLIPTIC_CURVE_ALGORITHM));
        //specs will be created out of the algorithm parameters to have right curve
        //to calculate the points on
        ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);

        // Separate x and y of coordinates into separate variables
        // our public key consists of two 24 bytes long values
        // those are the coordinates on the curve and give us a point
        ECPoint point = new ECPoint(getPointXKeyCurve(pubKey), getPointYKeyCurve(pubKey));
        return new ECPublicKeySpec(point, ecParameterSpec);
    }

    /**
     * Get the point x of the curve this is the first half of the bytes
     * of the pubkey
     *
     * @param pubKey byte array of the public key
     * @return big integer represents a point coordinate on the curve
     */
    private  BigInteger getPointXKeyCurve(byte[] pubKey) {
        assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

        byte[] x = new byte[KEY_POINT_DATA_LENGTH];
        System.arraycopy(pubKey, 0, x, 0, KEY_POINT_DATA_LENGTH);
        //our points are always positive
        return new BigInteger(PLUS_SIGN, x);
    }

    /**
     * Get the point y of the curve this is the second half of the bytes
     * of the pubkey
     *
     * @param pubKey byte array of the public key
     * @return big integer represents a point coordinate on the curve
     */
    private  BigInteger getPointYKeyCurve(byte[] pubKey) {
        assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

        byte[] y = new byte[KEY_POINT_DATA_LENGTH];
        System.arraycopy(pubKey, KEY_POINT_DATA_LENGTH, y, 0, KEY_POINT_DATA_LENGTH);
        //our points are always positive
        return new BigInteger(PLUS_SIGN, y);
    }
}