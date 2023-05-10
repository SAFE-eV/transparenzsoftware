package de.safe_ev.transparenzsoftware.verification.format.alfen;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenReader;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenSignature;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenSignatureVerifier;

public class AlfenSignatureVerifierTest {

    private final static String ALFEN_TEST_STRING = "AP;0;3;AMVBBEIORR2RGJLJ6YRZUGACQAXSDFCL66EIP3N7;BJKGK43UIRSXMAAROYYDCMZNUYFACRC2I4ADGAABIAAAAAAAQ6ACMAD5CH4FWAIAAEEAB7Y6ACJD2AAAAAAAAABRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASAAAAAEIAAAAA====;IGRCBV3TL45XIGPJU7QGD3H4V6ICQ75GLPWEFNEKZX3RTTKJI2FBXHPCWUIWL5OENEHE3SQRVACHG===;";
    private final static String ALFEN_TEST_STRING_2 = "AP;1;3;AICIVT423BX3TJGK6QCCVRHQ63LJQUEVZWWTYQUZ;BJKGK43UIRSXMAAROYYDCMZNUYFACRC2I4ADGAABIAAAAAIAQ6ACMAD5CH4FWAIAAEEAB7Y6ACKD2AAAAAAAAABRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASAAAAAEQAAAAA====;C3J3MLA5XLF7QYYHA4RAJV7QLBWU5OB3M3DUKCUTREEQ5QORE45DMUQALYBEI2YOLNX7DYFRWGLYU===;";
    private final static String ALFEN_TEST_STRING_3 = "AP;1;3;AICIVT423BX3TJGK6QCCVRHQ63LJQUEVZWWTYQUZ;BJKGK43UIRSXMAAROYYDCMZNUYFACRC2I4ADGCABIAAAAAEAQ6ACMAD5CH4FWAIAAEEAB7Y6ACKD2AAAAAAAAABRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASAAAAAEQAAAAA====;XVRQMWYJVQHI5GP53EPYUOYLHDAGBUTUIXJVRRZXKD7CIRXO6TYBFG7H43OT2STWBQW6MU7LMOBD2===;";

    @Test
    public void testExample() throws ValidationException, DecodingException {
        AlfenSignatureVerifier verifier = new AlfenSignatureVerifier();
        AlfenReader reader = new AlfenReader();
        AlfenSignature signature = reader.parseString(ALFEN_TEST_STRING);
        String publicKey = signature.getPublicKey();

        boolean result = verifier.verify(EncodingType.base32Decode(publicKey), signature.getSignature(), signature.getDataset());
        Assert.assertTrue(result);
    }

    @Test
    public void testExample2() throws ValidationException, DecodingException {
        AlfenSignatureVerifier verifier = new AlfenSignatureVerifier();
        AlfenReader reader = new AlfenReader();
        AlfenSignature signature = reader.parseString(ALFEN_TEST_STRING_2);
        boolean result = verifier.verify(EncodingType.base32Decode(signature.getPublicKey()), signature.getSignature(), signature.getDataset());
        Assert.assertTrue(result);
    }

    @Test
    public void testExample3() throws ValidationException, DecodingException {
        AlfenSignatureVerifier verifier = new AlfenSignatureVerifier();
        AlfenReader reader = new AlfenReader();
        AlfenSignature signature = reader.parseString(ALFEN_TEST_STRING_3);
        boolean result = verifier.verify(EncodingType.base32Decode(signature.getPublicKey()), signature.getSignature(), signature.getDataset());
        Assert.assertFalse(result);
    }
}
