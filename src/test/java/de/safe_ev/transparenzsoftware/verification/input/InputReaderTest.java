package de.safe_ev.transparenzsoftware.verification.input;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.input.InputReader;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

import java.io.File;
import java.net.URL;

public class InputReaderTest {

    @Test(expected = InvalidInputException.class)
    public void readFromStringInvalidXml() throws InvalidInputException {
        InputReader inputReader = new InputReader();
        inputReader.readString("<xml");
    }

    @Test(expected = InvalidInputException.class)
    public void readFromStringXml() throws InvalidInputException {
        InputReader inputReader = new InputReader();
        Values values = inputReader.readString("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                "<values>  \n" +
                "  <value>  \n" +
                "    <signedData format=\"SML_EDL40_P\" encoding=\"base64\">TEST1</signedData>  \n" +
                "    <publicKey encoding=\"base64\">TEST2</publicKey>  \n" +
                "  </value> \n" +
                "</values");
        Assert.assertEquals(1, values.getValues().size());
        Value value = values.getValues().get(0);
        Assert.assertEquals("SML_EDL40_P", value.getSignedData().getFormat());
        Assert.assertEquals("base64", value.getSignedData().getEncoding());
        Assert.assertEquals("TEST1", value.getSignedData().getValue());
        Assert.assertEquals("TEST2", value.getPublicKey().getValue());
        Assert.assertEquals("base64", value.getPublicKey().getEncoding());
        value.validate(true); //should not throw an error
    }


    @Test(expected = InvalidInputException.class)
    public void readFromStringXmlTwoValues() throws InvalidInputException {
        InputReader inputReader = new InputReader();
        Values values = inputReader.readString("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                "<values>  \n" +
                "  <value>  \n" +
                "    <signedData format=\"SML_EDL40_P\" encoding=\"base64\">TEST1</signedData>  \n" +
                "    <publicKey encoding=\"plain\">TEST2</publicKey>  \n" +
                "  </value> \n" +
                "  <value>  \n" +
                "    <signedData format=\"SML_EDL40_P\" encoding=\"base64\">TEST3</signedData>  \n" +
                "    <publicKey encoding=\"base64\">TEST4</publicKey>  \n" +
                "  </value> \n" +
                "</values");
        Assert.assertEquals(2, values.getValues().size());
        Value value1 = values.getValues().get(0);
        Assert.assertEquals("SML_EDL40_P", value1.getSignedData().getFormat());
        Assert.assertEquals("base64", value1.getSignedData().getEncoding());
        Assert.assertEquals(EncodingType.BASE64, value1.getSignedData().getEncodingType());
        Assert.assertEquals("TEST1", value1.getSignedData().getValue());
        Assert.assertEquals("TEST2", value1.getPublicKey().getValue());
        Assert.assertEquals("plain", value1.getPublicKey().getEncoding());
        Assert.assertEquals(EncodingType.PLAIN, value1.getPublicKey().getEncodingType());

        Value value2 = values.getValues().get(0);
        Assert.assertEquals("SML_EDL40_P", value2.getSignedData().getFormat());
        Assert.assertEquals("base64", value2.getSignedData().getEncoding());
        Assert.assertEquals("TEST3", value2.getSignedData().getValue());
        Assert.assertEquals("TEST4", value2.getPublicKey().getValue());
        Assert.assertEquals("base64", value2.getPublicKey().getEncoding());
        Assert.assertEquals(VerificationType.EDL_40_P, value2.getSignedData().getFormatAsVerificationType());
        Assert.assertEquals(EncodingType.BASE64, value2.getSignedData().getEncodingType());
    }

    @Test(expected = InvalidInputException.class)
    public void testFileInvalidStr() throws InvalidInputException {
        URL url = this.getClass().getResource("/xml/test_input_invalid_xml.xml");
        File testfile = new File(url.getFile());
        InputReader inputReader = new InputReader();
        inputReader.readFile(testfile);

    }
}
