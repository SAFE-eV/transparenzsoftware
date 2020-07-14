package com.hastobe.transparenzsoftware;

import org.junit.Test;

public class AppTest {

    @Test
    public void main() throws Exception {
        App.main(new String[]{"-cli"}, true);
//        Security.addProvider(new BouncyCastleProvider());
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
//        ECGenParameterSpec spec = new ECGenParameterSpec("secp192r1");
//        keyPairGenerator.initialize(spec, new SecureRandom());
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//        /*
//         * Create a Signature object and initialize it with the private key
//         */
//
//        Signature dsa = Signature.getInstance("NonewithECDSA");
//
//        dsa.initSign(keyPair.getPrivate());
//
//        String str = "This is string to sign";
//        byte[] strByte = str.getBytes("UTF-8");
//        dsa.update(strByte);
//        byte[] decPk = keyPair.getPublic().getEncoded();
//        System.out.println("Key beginning: "+ Utils.toFormattedHex(keyPair.getPublic().getEncoded()));
//        System.out.println("PK format: "+keyPair.getPublic().getFormat());
//
//        PublicKey pubKey = ECKeyUtil.publicToExplicitParameters(keyPair.getPublic(), "BC");
//        System.out.println("Key publicToExplicitParameters: "+ Utils.toFormattedHex(pubKey.getEncoded()));
//        System.out.println("PK format: "+pubKey.getFormat());
//
//
//        SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(pubKey.getEncoded()));
//        X962Parameters params = X962Parameters.getInstance(info.getAlgorithmId().getParameters());
//        ASN1Primitive primitive = params.toASN1Primitive();
//        byte[] publicKeyPKCS1 = primitive.getEncoded();
//        System.out.println("Key asn1: "+Utils.toFormattedHex(publicKeyPKCS1));
//        /*
//         * Now that all the data to be signed has been read in, generate a
//         * signature for it
//         */
//        byte[] test = Base64.decode("MG8CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEVTBTAgEBBBg9Vg70r3yNoCCZlJEULEgT0fOIs/gMFWChNAMyAAQKe1gbt28HokLRPoFt6+iQQv4HbSVa/s1qLRpo4SfDzPr56kTaz5O3R/8QAC3AyVA=");
//        byte[] test2 = Hex.decode("303402182AF0D4218B5530C0D3C7959ECE8F08DCC6F521CC7E546B840218190693124CC2D18C15\n" +
//                "0ADC5E858031A2FD1FD51A48C5701B");
//        byte[] test3 = Hex.decode("0410e7910706c0d9994e3dd6f367eeff0570f3916c7e2bdbc6d9728ef1ca92c6e96f6d66843f267d1df8e165ecd6e7f59d");
//        byte[] realSig = dsa.sign();
//        System.out.println("Signature: " + Utils.toFormattedHex(realSig));

    }


}
