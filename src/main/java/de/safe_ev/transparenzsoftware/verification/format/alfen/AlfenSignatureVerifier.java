package de.safe_ev.transparenzsoftware.verification.format.alfen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationLogger;
import de.safe_ev.transparenzsoftware.verification.format.sml.Verifier;

import java.math.BigInteger;
import java.security.Security;

public class AlfenSignatureVerifier implements Verifier {

    private final static Logger LOGGER = LogManager.getLogger(AlfenSignatureVerifier.class);

    /**
     * "Vorzeichen" we want to make sure that all points are seen as positive
     */
    private static final int PLUS_SIGN = 1;
    public static final String ELLIPTIC_CURVE_ALGORITHM = "secp192r1";
    public static final int SIGNATURE_LENGTH = 48;

    /**
     * Initializes the verifier and also the bouncy castle library as security
     * provider.
     */
    public AlfenSignatureVerifier() {
        //enable bc as provider
        Security.addProvider(new BouncyCastleProvider());
    }


    @Override
    public boolean verify(byte[] publicKey, byte[] signature, byte[] payloadData) throws ValidationException {
        ECDSASigner signer = new ECDSASigner();
        X9ECParameters ecParameters = SECNamedCurves.getByName(ELLIPTIC_CURVE_ALGORITHM);
        ECCurve curve = ecParameters.getCurve();

        //create the public key paramters for init of the signing lib
        ECDomainParameters ecDomainParameters = new ECDomainParameters(curve, ecParameters.getG(), ecParameters.getN(), ecParameters.getH(), ecParameters.getSeed());
        ECPublicKeyParameters publicKeyParsed;
        try {
            publicKeyParsed = new ECPublicKeyParameters(curve.decodePoint(publicKey), ecDomainParameters);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid point given for public key", e);
            //is thrown if point is not valid e.g. not on curve
            throw new ValidationException("Invalid public key point given", "error.invalid.public.key");
        }

        if (signature.length != SIGNATURE_LENGTH) {
            throw new ValidationException("Invalid length of signature given", "error.invalid.signature.length");
        }
        //verify the signature divided in two coordinates, r and s
        BigInteger signatureR = new BigInteger(PLUS_SIGN, Utils.copyFromWithLength(signature, 0, 24));
        BigInteger signatureS = new BigInteger(PLUS_SIGN, Utils.copyFromWithLength(signature, 24, 24));

        byte[] hashedData = Utils.hashSHA256(payloadData);

        signer.init(false, publicKeyParsed);
        boolean verify = signer.verifySignature(hashedData, signatureR, signatureS);
        VerificationLogger.log("ALFEN", ELLIPTIC_CURVE_ALGORITHM, publicKey, hashedData, signature, verify);
        return verify;

    }
}
