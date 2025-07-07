package de.safe_ev.transparenzsoftware.verification;

import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.metabit.custom.safe.safeseal.SAFESealRevealer;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.result.IntrinsicVerified;
import de.safe_ev.transparenzsoftware.verification.xml.Value;

public class EncryptedDataDecoder {

    private static final Logger LOGGER = LogManager.getLogger(EncryptedDataDecoder.class);

    public static final String ALGORITHM_SPEC_3 = "RSA/None/NoPadding"; // textbook RSA, correct spec
    public static final String ALGORITHM_SPEC_2 = "RSA/ECB/NoPadding"; // textbook RSA, bug compatibility version.
    public static final String ALGORITHM_SPEC_1 = "RSA/ECB/PKCS1Padding"; // PKCS1.5 padding, including random bytes

    private final byte[] publicKeyBytes;
    private static Provider securityProvider = new BouncyCastleProvider();

    static {
	Security.addProvider(securityProvider);
    }

    public EncryptedDataDecoder(byte[] publicKey) {
	publicKeyBytes = publicKey;
    }

    public EncryptedDataDecoder(String publicKeyContent) {
	publicKeyBytes = Utils.hexStringToByteArray(publicKeyContent);
    }

    /**
     *
     * @param data
     * @param intrinsicVerified
     * @return
     */
    public String decode(Value data, AtomicReference<IntrinsicVerified> intrinsicVerified) {
	try {
	    final AsymmetricKeyParameter asymKey = PublicKeyFactory.createKey(publicKeyBytes);
	    final SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymKey);
	    final PublicKey pubKey = BouncyCastleProvider.getPublicKey(keyInfo);
	    final byte[] encodedData = Utils.hexStringToByteArray(data.getEncodedData().getValue());
	    final SAFESealRevealer revealer = new SAFESealRevealer(2);
	    final byte[] decodedData = revealer.reveal(pubKey, null, encodedData);
	    final String flatData = new String(decodedData);
	    intrinsicVerified.set(IntrinsicVerified.VERIFIED);
	    return flatData;
	} catch (final Exception e) {
	    LOGGER.debug("Could not decode {}", data, e);
	}
	return null;
    }

}
