package com.hastobe.transparenzsoftware.verification.format.sml.IsaEDL40;

import com.hastobe.transparenzsoftware.Constants;
import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.VerificationLogger;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLSignature;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLSignatureVerifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

import static com.hastobe.transparenzsoftware.Constants.BOUNCY_CASTLE_PROVIDER_CODE;

public class IsaSMLSignatureVerifier extends SMLSignatureVerifier {

    private final static Logger LOGGER = LogManager.getLogger(IsaSMLSignatureVerifier.class);

    /**
     * Initializes the verifier and also the bouncy castle library as security
     * provider.
     */
    public IsaSMLSignatureVerifier() {
        //enable bc as provider
        Security.addProvider(new BouncyCastleProvider());
        PUBLIC_KEY_BYTES_LENGTH = 64;
        KEY_POINT_DATA_LENGTH = 32;
        ELLIPTIC_CURVE_ALGORITHM = "secp256r1";
        CROPPED_DATA_LENGTH = 32;
    }

    @Override
    public boolean verify(byte[] publicKey, byte[] signature, byte[] hashData) throws ValidationException {
        boolean verify;
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
        byte[] derSignature = byteArrayOutputStream.toByteArray();
        try {
            Signature signatureVerifier = initSignature(publicKey, hashData);
            verify = signatureVerifier.verify(derSignature);
            VerificationLogger.log("IsaSML",ELLIPTIC_CURVE_ALGORITHM,publicKey,hashData,derSignature,verify);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new ValidationException("Failure on initialising the crypto algorithms", e);
        } catch (SignatureException e) {
            throw new ValidationException("Invalid signature supplied", e);
        } catch (InvalidKeyException e) {
            throw new ValidationException("Invalid public key supplied", e);
        }

        return verify;
    }

    @Override
    public boolean verify(byte[] publicKey, SMLSignature SMLSignature) throws ValidationException {
        boolean verify;

        byte[] dataForHash = SMLSignature.buildExtendedSignatureData();
        byte[] hashData = Utils.hashSHA256(dataForHash);
        //String hexHashData = Utils.bytesToHex(dataForHash);

        byte[] signature = ((IsaEDL40Signature)SMLSignature).getDataSignature();
        verify = verify(publicKey, signature, hashData);

        return verify;
    }

    private Signature initSignature(byte[] publicKey, byte[] croppedPayloadData) throws NoSuchProviderException, NoSuchAlgorithmException, ValidationException, SignatureException, InvalidKeyException {
        assert croppedPayloadData.length == CROPPED_DATA_LENGTH;
        Signature signatureVerifier = Signature.getInstance(SIGNATURE_ALGORITHM, BOUNCY_CASTLE_PROVIDER_CODE);
        signatureVerifier.initVerify(getPublicKeyFromBytes(publicKey));
        signatureVerifier.update(croppedPayloadData);
        return signatureVerifier;
    }

    private BigInteger getPointXKeyCurve(byte[] pubKey) {
        assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

        byte[] x = new byte[KEY_POINT_DATA_LENGTH];
        System.arraycopy(pubKey, 0, x, 0, KEY_POINT_DATA_LENGTH);
        //our points are always positive
        return new BigInteger(PLUS_SIGN, x);
    }

    private BigInteger getPointYKeyCurve(byte[] pubKey) {
        assert pubKey.length == PUBLIC_KEY_BYTES_LENGTH;

        byte[] y = new byte[KEY_POINT_DATA_LENGTH];
        System.arraycopy(pubKey, KEY_POINT_DATA_LENGTH, y, 0, KEY_POINT_DATA_LENGTH);
        //our points are always positive
        return new BigInteger(PLUS_SIGN, y);
    }

    private ECPublicKeySpec initPublicKeyCryptoSpecs(byte[] pubKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidParameterSpecException {
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
}