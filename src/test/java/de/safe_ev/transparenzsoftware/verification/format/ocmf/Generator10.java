package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ASN1Utils;
import de.safe_ev.transparenzsoftware.verification.xml.OffsetDateTimeAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Hacky helper class to generate test data sets
 */
public class Generator10 {


    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        long ts_beginning = System.currentTimeMillis();


        create("brainpoolp256r1", "ECDSA-brainpool256r1-SHA256", ts_beginning);
        create("brainpoolp384r1", "ECDSA-brainpool384r1-SHA256", ts_beginning);
        create("secp192k1", "ECDSA-secp192k1-SHA256", ts_beginning);
        create("secp256k1", "ECDSA-secp256k1-SHA256", ts_beginning);
        create("secp192r1", "ECDSA-secp192r1-SHA256", ts_beginning);
        create("secp256r1", "ECDSA-secp256r1-SHA256", ts_beginning);
        create("secp384r1", "ECDSA-secp384r1-SHA256", ts_beginning);
        create("secp256k1", "ECDSA-secp256k1-SHA256", ts_beginning);
        System.out.println("Finished creating");

    }

    private static void create(String curveName, String curveNameOCMF, long ts_beginning) throws Exception {
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(curveName);
        X9ECParameters curve = curveName.contains("brain") ? TeleTrusTNamedCurves.getByName(curveName) : SECNamedCurves.getByName(curveName);
        ECDomainParameters domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());

        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
        g.initialize(ecSpec, new SecureRandom());
        KeyPair keypair = g.generateKeyPair();
        BCECPrivateKey privateKey = (BCECPrivateKey) keypair.getPrivate();
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKey.getD(), domain);

        PrintWriter pkDataOut = new PrintWriter(String.format("test_ignored_files/%d_key_data.txt", ts_beginning));

        System.out.println("Write key file now");
        pkDataOut.println("Private key: " + keypair.getPrivate().toString());
        pkDataOut.println("Private key encoded: " + Utils.toFormattedHex(keypair.getPrivate().getEncoded()));

        pkDataOut.println("Public key: " + keypair.getPublic().toString());
        String publicKeyFormatted = Utils.toFormattedHex(keypair.getPublic().getEncoded());
        pkDataOut.println("Public key encoded: " + publicKeyFormatted);


        System.out.println("Generate data sets now");


//        System.out.println("OCMF|");
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        offsetDateTime = offsetDateTime.minusDays(15);


        int max = 3;
        for (int i = 0; i < max; i++) {
            String fileName = String.format("test_ignored_files/%d_data_%s_%06d.xml", ts_beginning, curveName, i + 1);
            System.out.println(String.format("Create data set %d from %d (File %s) with curve %s", i + 1, max, fileName, curveName));

            double valueStart = (Math.random() * 10000000) / 1000000;
            double valueStop = valueStart + (Math.random() * 10000000) / 1000000;
            int transactionsId = (int) (Math.random() * 1000);

            offsetDateTime = offsetDateTime.plusSeconds((long) (Math.random() * 10 * 60)).plusMinutes((long) (Math.random() * 10));
            String rdDataStart;
            String rdDataStop;
            if((i+1)%3 == 1){
                rdDataStart = prepareSdData(offsetDateTime, 'S', valueStart, 'B', 1523, 'G', null);
                rdDataStop = prepareSdData(offsetDateTime, 'S', valueStop, 'E', 1523, 'G', null);
            } else if((i+1)%3 == 2){
                rdDataStart = prepareSdData(offsetDateTime, 'S', valueStart, 'B', 1523, 'G', null);
                rdDataStop = prepareSdData(offsetDateTime, 'S', valueStop, 'X', 1523, 'G', null);
            } else {
                rdDataStart = prepareSdData(offsetDateTime, 'S', valueStart, 'X', 1523, 'G', null);
                rdDataStop = prepareSdData(offsetDateTime, 'S', valueStop, 'E', 1523, 'G', null);
            }


            byte[] idTagData = new byte[7];
            new SecureRandom().nextBytes(idTagData);
            String idTag = Utils.bytesToHex(idTagData);

            String rdData = String.format("%s,%s", rdDataStart, rdDataStop);
            String sdData = String.format("{\"FV\":\"1.0\",\"GI\":\"HTB\",\"GS\":\"HTBSerial\",\"GV\":\"0.0.1\",\"PG\":\"T12345\",\"MV\":\"HTB\",\"MM\":\"HTB\",\"MS\":\"HTBGenerated1\",\"MF\":\"1.0\",\"IS\":true,\"IL\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],\"IT\":\"ISO14443\",\"ID\":\"%s\",\"CI\":\"HTB\",\"CT\":\"CBIDC\",\"RD\":[%s]}", idTag, rdData);
            byte[] signature = sign(privateKeyParameters, ecSpec, sdData);
            String ocmfData = String.format("OCMF|%s|{\"SA\":\"%s\",\"SD\":\"%s\"}", sdData, curveNameOCMF, Utils.bytesToHex(signature));
            String xmlData = encapsulateInValueString(ocmfData, publicKeyFormatted, transactionsId);


            PrintWriter data_set_writer = new PrintWriter(fileName);
            data_set_writer.println(encapsulateInXML(xmlData));
            data_set_writer.close();
        }
        pkDataOut.close();
    }

    private static String encapsulateInXML(String data) {
        return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<values>\n%s\n</values>", data);
    }


    private static String encapsulateInValueString(String data, String publicKey, int transactionsId) {
        return String.format("<value>\n" +
                "        <signedData format=\"OCMF\" encoding=\"plain\" transactionId=\"%d\">%s</signedData>\n" +
                "        <publicKey encoding=\"hex\">%s</publicKey>\n" +
                "    </value>", transactionsId, data, publicKey);
    }

    private static String prepareSdData(OffsetDateTime dateTime, char synchronicity, double value, char tx, int eventIndex, char st, String ef) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSSZ");
        String rdDataTemplate = String.format("{\"TM\":\"%s %s\",\"TX\":\"%s\",\"RV\":%.5f,\"RI\":\"1-b:1.8.e\",\"RU\":\"kWh\",\"EI\":%s,\"ST\":\"%s\"",
                OffsetDateTimeAdapter.formattedDateTime(dateTime, formatter),
                synchronicity,
                tx,
                value,
                eventIndex,
                st
        );
        rdDataTemplate = String.format("%s%s}", rdDataTemplate, ef != null ? ",\"EF\":\"E\"" : "");
        return rdDataTemplate;
    }

    private static byte[] sign(CipherParameters publicKeyParsed, ECNamedCurveParameterSpec ecSpec, String sdData) {
        byte[] hashSHA256 = Utils.hashSHA256(sdData.getBytes(StandardCharsets.UTF_8));
        return sign(publicKeyParsed, hashSHA256, ecSpec);
    }

    private static byte[] sign(CipherParameters publicKeyParsed, byte[] payloadData, ECNamedCurveParameterSpec ecSpec) {
        ECDSASigner signer = new ECDSASigner(); //new HMacDSAKCalculator(new SHA256Digest()));
        signer.init(true, publicKeyParsed);

        BigInteger[] signature = signer.generateSignature(payloadData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            DERSequenceGenerator seq = new DERSequenceGenerator(baos);
            seq.addObject(new ASN1Integer(signature[0]));
            seq.addObject(new ASN1Integer(toCanonicalS(signature[1], ecSpec)));
            seq.close();
            return baos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private static BigInteger toCanonicalS(BigInteger s, ECNamedCurveParameterSpec ecSpec) {
        if (s.compareTo(ecSpec.getN().shiftRight(1)) <= 0) {
            return s;
        } else {
            return ecSpec.getN().subtract(s);
        }
    }
}
