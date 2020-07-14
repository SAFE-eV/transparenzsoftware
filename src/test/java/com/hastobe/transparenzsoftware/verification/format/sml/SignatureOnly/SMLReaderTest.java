package com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly;

import com.hastobe.transparenzsoftware.TestUtils;
import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly.embedded.SignedMeterValue;
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
