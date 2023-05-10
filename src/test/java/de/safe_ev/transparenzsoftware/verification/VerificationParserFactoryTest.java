package de.safe_ev.transparenzsoftware.verification;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.VerificationTypeNotImplementedException;

public class VerificationParserFactoryTest {

    @Test(expected = VerificationTypeNotImplementedException.class)
    public void testgetParserUnknown() throws VerificationTypeNotImplementedException {
        VerificationParserFactory factory = new VerificationParserFactory();
        factory.getParser(null);
    }

    @Test
    public void testgetParser() throws VerificationTypeNotImplementedException {
        VerificationParserFactory factory = new VerificationParserFactory();
        Assert.assertNotNull(factory.getParser(VerificationType.EDL_40_P));
    }
}
