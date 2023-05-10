package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.SMLReader;
import de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.embedded.SignedMeterValue;

import org.junit.Assert;
import org.junit.Test;

public class SMLReaderTest {

    @Test
    public void test_signature_only_data() throws ValidationException, DecodingException {
        SMLReader smlReader = new SMLReader();

        SignedMeterValue meterValue = smlReader.readFromString(Utils.unescapeXML(TestUtils.TEST_SIG_ONLY));
        Assert.assertNotNull(meterValue.getPublicKey());
        Assert.assertNotNull(meterValue.getSignatureMethod());
        Assert.assertNotNull(meterValue.getMeterValueSignature());
        Assert.assertNotNull(meterValue.getEncodedMeterValue());

        Assert.assertNotNull(meterValue.getPublicKey().getValueEncoded());
        Assert.assertNotNull(meterValue.getMeterValueSignature().getValueEncoded());
        Assert.assertNotNull(meterValue.getEncodedMeterValue().getValueEncoded());

    }


}
