package de.safe_ev.transparenzsoftware.verification;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DLSequence;
import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.ASN1Exception;
import de.safe_ev.transparenzsoftware.verification.ASN1Utils;

import java.io.IOException;

public class ASN1UtilsTest {

    @Test
    public void testReadPublicKey() throws IOException, ASN1Exception {
        byte[] pkData = {0x00, (byte) 0xFF, (byte) 0xBB};
        DERBitString derBitString = new DERBitString(pkData);
        ASN1Primitive[] array  = new ASN1Primitive[2];
        array[0] = new ASN1Integer(1);
        array[1] = derBitString;

        DLSequence dlSequence = new DLSequence(array);
        byte[] readData = ASN1Utils.readPublicKey(dlSequence.getEncoded());
        Assert.assertArrayEquals(pkData, readData);
    }

    @Test(expected = ASN1Exception.class)
    public void testReadPublicKeyNoAsn1() throws ASN1Exception {
        byte[] dummyData = {0x00, (byte) 0xFF, (byte) 0xBB};
        ASN1Utils.readPublicKey(dummyData);
    }

    @Test(expected = ASN1Exception.class)
    public void testReadPublicKeyNoDLSequence() throws ASN1Exception, IOException {
        ASN1Integer test = new ASN1Integer(1);
        byte[] readData = ASN1Utils.readPublicKey(test.getEncoded());
    }

    @Test(expected = ASN1Exception.class)
    public void testReadPublicKeyEmptyDLSequence() throws ASN1Exception, IOException {
        DLSequence dlSequence = new DLSequence();
        byte[] readData = ASN1Utils.readPublicKey(dlSequence.getEncoded());
    }

    @Test(expected = ASN1Exception.class)
    public void testReadPublicKeyNoDERBit() throws ASN1Exception, IOException {
        ASN1Primitive[] array  = new ASN1Primitive[2];
        array[0] = new ASN1Integer(1);
        array[1] = new ASN1Integer(2);

        DLSequence dlSequence = new DLSequence(array);
        ASN1Utils.readPublicKey(dlSequence.getEncoded());
    }
}
