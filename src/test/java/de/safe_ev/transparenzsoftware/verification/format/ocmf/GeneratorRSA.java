package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.metabit.custom.safe.safeseal.SAFESealSealer;

import de.safe_ev.transparenzsoftware.Utils;

/**
 * Hacky helper class to generate RSA encoded test data sets
 */
public class GeneratorRSA {

    public static final String ALGORITHM_SPEC_2 = "RSA/ECB/NoPadding"; // textbook RSA, bug compatibility version.

    private static int transactionsId;
    private static LocalDateTime offsetDateTime = LocalDateTime.now();
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    public static void main(String[] args) throws Exception {
	Security.addProvider(new BouncyCastleProvider());
	Locale.setDefault(Locale.US); // For right JSON format.
	createKeyPairs();
	final String ocmf1 = generateOCMFData(0);
	final String ocmf2 = generateOCMFData(1);
	final String ocmf3 = generateOCMFData(2);
	final String pubKeyString = Utils.bytesToHex(publicKey.getEncoded());
	final String xml = encapsulateInXML(encapsulateInValueString(pubKeyString, encode(ocmf1), transactionsId)
		+ encapsulateInValueString(pubKeyString, encode(ocmf2), transactionsId)
		+ encapsulateInValueString(pubKeyString, encode(ocmf3), transactionsId));
	System.out.println("Finished creating: " + xml);

    }

    private static void createKeyPairs() {
	KeyPairGenerator keyPairGenerator;
	try {
	    keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	    keyPairGenerator.initialize(2048);

	    // Generate the KeyPair
	    final KeyPair keyPair = keyPairGenerator.generateKeyPair();

	    // Get the public and private key
	    publicKey = keyPair.getPublic();

	    privateKey = keyPair.getPrivate();
	} catch (final NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}
    }

    private static String encode(String ocmf) {
	final byte[] testData = ocmf.getBytes(StandardCharsets.UTF_8);
	try {
	    final SAFESealSealer sealer = new SAFESealSealer();
	    final byte[] encoded = sealer.seal(privateKey, null, testData, 0x1234l);
	    return Utils.bytesToHex(encoded);
	} catch (final Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new RuntimeException("Sealing failed", e);
	}
    }

    private static String generateOCMFData(int i) {
	String rdDataStart;
	String rdDataStop;
	final double valueStart = (Math.random() * 10000000) / 1000000;
	final double valueStop = valueStart + (Math.random() * 10000000) / 1000000;
	transactionsId = (int) (Math.random() * 1000);

	offsetDateTime = offsetDateTime.plusSeconds((long) (Math.random() * 10 * 60))
		.plusMinutes((long) (Math.random() * 10));
	if ((i + 1) % 3 == 1) {
	    rdDataStart = prepareSdData(offsetDateTime, 'S', valueStart, 'B', 1523, 'G', null);
	    rdDataStop = prepareSdData(offsetDateTime, 'S', valueStop, 'E', 1523, 'G', null);
	} else if ((i + 1) % 3 == 2) {
	    rdDataStart = prepareSdData(offsetDateTime, 'S', valueStart, 'B', 1523, 'G', null);
	    rdDataStop = prepareSdData(offsetDateTime, 'S', valueStop, 'X', 1523, 'G', null);
	} else {
	    rdDataStart = prepareSdData(offsetDateTime, 'S', valueStart, 'X', 1523, 'G', null);
	    rdDataStop = prepareSdData(offsetDateTime, 'S', valueStop, 'E', 1523, 'G', null);
	}

	final byte[] idTagData = new byte[7];
	new SecureRandom().nextBytes(idTagData);
	final String idTag = Utils.bytesToHex(idTagData);

	final String rdData = String.format("%s,%s", rdDataStart, rdDataStop);
	final String sdData = String.format(
		"{\"FV\":\"1.0\",\"GI\":\"HTB\",\"GS\":\"HTBSerial\",\"GV\":\"0.0.1\",\"TT\":\"Moonshine\",\"PG\":\"T12345\",\"MV\":\"HTB\",\"MM\":\"HTB\",\"MS\":\"HTBGenerated1\",\"MF\":\"1.0\",\"IS\":true,\"IL\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],\"IT\":\"ISO14443\",\"ID\":\"%s\",\"CI\":\"HTB\",\"CT\":\"CBIDC\",\"RD\":[%s]}",
		idTag, rdData);
	final String ocmfData = String.format("OCMF|%s|{}", sdData);
	return ocmfData;
    }

    private static String encapsulateInXML(String data) {
	return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<values>\n%s\n</values>", data);
    }

    private static String encapsulateInValueString(String publicKey, String encodedData, int transactionsId) {
	return String.format("<value>\n"
		+ "        <encodedData format=\"OCMF\" encoding=\"hex\" transactionId=\"%d\">%s</encodedData>\n"
		+ "        <publicKey encoding=\"hex\">%s</publicKey>\n" + "    </value>", transactionsId, encodedData,
		publicKey);
    }

    private static String prepareSdData(LocalDateTime dateTime, char synchronicity, double value, char tx,
	    int eventIndex, char st, String ef) {
	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	String rdDataTemplate = String.format(
		"{\"TM\":\"%s %s\",\"TX\":\"%s\",\"RV\":%.5f,\"RI\":\"1-b:1.8.e\",\"RU\":\"kWh\",\"EI\":%s,\"ST\":\"%s\"",
		dateTime.format(formatter), synchronicity, tx, value, eventIndex, st);
	rdDataTemplate = String.format("%s%s}", rdDataTemplate, ef != null ? ",\"EF\":\"E\"" : "");
	return rdDataTemplate;
    }
}
