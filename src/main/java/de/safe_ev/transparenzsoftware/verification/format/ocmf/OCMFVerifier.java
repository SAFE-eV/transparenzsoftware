package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ASN1Exception;
import de.safe_ev.transparenzsoftware.verification.ASN1Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationLogger;
import de.safe_ev.transparenzsoftware.verification.format.sml.Verifier;

import java.security.Security;

public class OCMFVerifier implements Verifier {
    private final static Logger LOGGER = LogManager.getLogger(OCMFVerifier.class);

    private String curve;

    public OCMFVerifier(String curve) {
        this.curve = curve;
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public boolean verify(byte[] publicKey, byte[] signature, byte[] payloadData) throws ValidationException {


        ECDSASigner signer = new ECDSASigner(); //new HMacDSAKCalculator(new SHA256Digest()));

        ECPublicKeyParameters publicKeyParsed = preparePublicKeyData(publicKey);
        signer.init(false, publicKeyParsed);

        //load the signature divided in two coordinates, r and s
        ASN1Utils.SignatureRS signatureRS;
        try {
            signatureRS = ASN1Utils.readSignatureRS(signature);
        } catch (ASN1Exception e) {
            throw new OCMFValidationException("Invalid length of asn1Signature given", "error.invalid.asn1Signature.length", e);
        }

        //hash message before verifying it
        byte[] hashedMessage = Utils.hashSHA256(payloadData);

        boolean verify = signer.verifySignature(hashedMessage, signatureRS.getR(), signatureRS.getS());
        VerificationLogger.log("OCMF", "ECDSA", publicKey, hashedMessage, signature, verify);
        return verify;
    }

    /**
     * Prepares the public key data to be in the state of
     * a ECPublicKeyParameters object
     *
     * @param rawPublicKey raw byte data
     * @return ECPublicKeyParameters created out of the rawPublicKey
     * @throws ValidationException if public key data was in a wrong format
     */
    private ECPublicKeyParameters preparePublicKeyData(byte[] rawPublicKey) throws ValidationException {
        byte[] publicKeyASN1Parsed;
        if(rawPublicKey == null){
            throw new OCMFValidationException("Cannot read public key (null)", "error.values.publickey.cannot.encode");
        }
        try {
            publicKeyASN1Parsed = ASN1Utils.readPublicKey(rawPublicKey);
        } catch (ASN1Exception e) {
            throw new OCMFValidationException("Cannot read public key", "error.values.publickey.cannot.encode");
        }

        X9ECParameters ecParameters = curve.contains("brain") ? TeleTrusTNamedCurves.getByName(curve) : SECNamedCurves.getByName(curve);
        if (ecParameters == null) {
            throw new OCMFValidationException("Invalid curve provided", "error.invalid.curve.name");
        }
        ECCurve ecCurve = ecParameters.getCurve();

        //create the public key paramters for init of the signing lib
        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecCurve, ecParameters.getG(), ecParameters.getN(), ecParameters.getH(), ecParameters.getSeed());
        ECPublicKeyParameters publicKeyParsed;

        try {
            ECPoint publicKeyPoint = ecCurve.decodePoint(publicKeyASN1Parsed);
            publicKeyParsed = new ECPublicKeyParameters(publicKeyPoint, ecDomainParameters);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid point given for public key", e);
            //is thrown if point is not valid e.g. not on curve
            throw new OCMFValidationException("Invalid public key point given", "error.invalid.public.key", e);
        }
        return publicKeyParsed;
    }
}
