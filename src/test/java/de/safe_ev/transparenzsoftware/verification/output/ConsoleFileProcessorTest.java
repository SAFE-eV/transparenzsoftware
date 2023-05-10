package de.safe_ev.transparenzsoftware.verification.output;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.output.ConsoleFileProcessor;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.PublicKey;
import de.safe_ev.transparenzsoftware.verification.xml.SignedData;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

import java.util.List;

public class ConsoleFileProcessorTest {

    public static final String DATA_MENNEKES = "GxsbGwEBAQF2BVcGAABiAGIAcmMBAXYBBzEyMzQ1NgUAAADFCwkBRU1IAABrsygBAWNGXwB2BVcGAABiAGIAcmMHAXcBCwkBRU1IAABrsygHgYCBYgL/cmIBZQAS2w5zdweBgoFUAf8BcmIDc2VZqU7cUwA8UwA8AQEFh0rQ/gF3B4GAgWEB/wEBAQEBAXcHAQABEQD/ZAEBCHJiA3NlWalOkFMAPFMAPGIeUv9WAAAATQGDBJS6FMQxdaxKcXjGD7KtXlyY+UCJngia5RI+XBO9L5FjDeeBn1E6cG11athoeTS+iQAogwTWEeTvCF4i1jEZBffK8P5Nrb9LbDzahtQrEoIny7H4kdRWfxr6jmEXBZYfdB7JMV8AKAFjb1kAdgVXBgAAYgBiAHJjAgFxAWPK4AAbGxsbGgDYqA==";
    public static final String PUBLIC_KEY_MENNEKES = "06c9dd342a6eb18294c6e90bcec6f0270ee11cbecc8436cc3155f681e40d132411f2b64892b105b0e7636252d651ca59";


    @Test
    public void testParseValueEmptyData() {
        VerificationParserFactory factory = new VerificationParserFactory();
        ConsoleFileProcessor fileProcessor = new ConsoleFileProcessor(factory);

        Values values = new Values();
        Value value = new Value();
        value.setPublicKey(createPublicKey());
        SignedData signedData = new SignedData();
        value.setSignedData(signedData);
        values.getValues().add(value);
        List<VerificationResult> result = fileProcessor.processValues(values);
        Assert.assertEquals(1, result.size());
        Assert.assertFalse(result.get(0).isVerified());
        Assert.assertEquals("Empty value provided", result.get(0).getErrorMessages().get(0).getMessage());
    }

    @Test
    public void testParseValueMennekesData() {
        VerificationParserFactory factory = new VerificationParserFactory();
        ConsoleFileProcessor fileProcessor = new ConsoleFileProcessor(factory);

        Values values = new Values();
        Value value = new Value();
        value.setPublicKey(createPublicKey());
        SignedData signedData = new SignedData();
        signedData.setValue(DATA_MENNEKES);
        value.setSignedData(signedData);
        values.getValues().add(value);
        List<VerificationResult> result = fileProcessor.processValues(values);
        Assert.assertEquals(1, result.size());
        Assert.assertFalse(result.get(0).isVerified());
    }

    private PublicKey createPublicKey() {
        PublicKey publicKey = new PublicKey();
        publicKey.setEncoding(EncodingType.PLAIN.getCode());
        publicKey.setValue(PUBLIC_KEY_MENNEKES);
        return publicKey;
    }
}
