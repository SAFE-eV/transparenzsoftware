package de.safe_ev.transparenzsoftware.verification;

import org.bouncycastle.asn1.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Utility class to handle various asn1 data and read out the actual values we
 * need. Heavily uses ASN1Exception to show all kind of error states.
 *
 */
public class ASN1Utils {

    /**
     * Reads out a byte array for the public key of a ASN1 data array
     *
     * @param asn1Data - data to read
     * @return byte array containing the actual value of the public key as byte array
     * @throws ASN1Exception if asn1Data does not contain the correct data
     */
    public static byte[] readPublicKey(byte[] asn1Data) throws ASN1Exception {
        ByteArrayInputStream inStream = new ByteArrayInputStream(asn1Data);
        ASN1InputStream asnInputStream = new ASN1InputStream(inStream);
        ASN1Primitive asn1Primitive;
        try {
            asn1Primitive = asnInputStream.readObject();
        } catch (IOException e) {
            throw new ASN1Exception("Could not read ASN.1 public key data", e);
        }
        if (!(asn1Primitive instanceof DLSequence)) {
            throw new ASN1Exception("ASN.1 object not a sequence object");
        }
        DLSequence dlSequence = (DLSequence) asn1Primitive;
        if (dlSequence.size() < 2) {
            throw new ASN1Exception("ASN.1 Sequence does not contain enough values for a public key");
        }
        if (!(dlSequence.getObjectAt(1) instanceof DERBitString)) {
            throw new ASN1Exception("ASN.1 Sequence does not contain a DER Bit string which contains the public key value");
        }
        DERBitString derBitString = (DERBitString) dlSequence.getObjectAt(1);
        return derBitString.getBytes();
    }

    public static SignatureRS readSignatureRS(byte[] asn1Signature) throws ASN1Exception {
        ASN1InputStream asn1 = new ASN1InputStream (asn1Signature);
        ASN1Primitive asn1Primitive;
        try {
            asn1Primitive = asn1.readObject ();
        } catch (IOException e) {
            throw new ASN1Exception("Could not read ASN.1 public key data");
        }
        if(!(asn1Primitive instanceof DLSequence)){
            throw new ASN1Exception("ASN.1 object not a sequence object");
        }
        DLSequence sequence = (DLSequence) asn1Primitive;
        if(sequence.size() < 2){
            throw new ASN1Exception("ASN.1 Sequence does not contain enough values for signature values");
        }
        if(!(sequence.getObjectAt(0) instanceof ASN1Integer && sequence.getObjectAt(1) instanceof ASN1Integer)){
            throw new ASN1Exception("ASN.1 Sequence does not contain integer values");
        }
        // the data is in an asn1 format so lets read that in first and get
        // the two values r and s of that data
        BigInteger signatureR = ((ASN1Integer) sequence.getObjectAt (0)).getPositiveValue();
        BigInteger signatureS = ((ASN1Integer) sequence.getObjectAt (1)).getPositiveValue();
        return new SignatureRS(signatureR, signatureS);
    }

    public static class SignatureRS {

        private BigInteger r;
        private BigInteger s;

        public SignatureRS(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }

        public BigInteger getR() {
            return r;
        }

        public BigInteger getS() {
            return s;
        }
    }
}
