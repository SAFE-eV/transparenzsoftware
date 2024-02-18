package de.safe_ev.transparenzsoftware.verification.format.alfen;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenSignature;

import org.junit.Assert;
import org.junit.Test;

public class AlfenSignatureTest {

    /**
     * Verifies that a identifier with only 1 character is not valid
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTooShortIdentifier() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setIdentifier("A");
    }

    /**
     * Verifies that a identifier with 3 characters is too long
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTooLongIdentifier() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setIdentifier("ABC");
    }

    /**
     * Test identifier the correct chars
     *
     * @throws ValidationException
     */
    @Test
    public void testIdentifier() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setIdentifier("AP");
        Assert.assertEquals("AP", signature.getIdentifier());
    }

    /**
     * Verifies that an empty type is not accepted
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTooShortType() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setType("");
    }

    /**
     * Verifies that a type with 2 characters is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTooLongType() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setType("12");
    }

    /**
     * Verifies that type which is not 0,1,2 is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testWrongType() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setType("9");
    }

    /**
     * Verifies that the types 0,1,2 are accepted
     *
     * @throws ValidationException
     */
    @Test
    public void testType() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setType("1");
        Assert.assertEquals("1", signature.getType());
        signature.setType("2");
        Assert.assertEquals("2", signature.getType());
        signature.setType("0");
        Assert.assertEquals("0", signature.getType());
    }

    /**
     * Verifies that the blobversion is not allowed to be empty
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testBloberversionTooShort() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setBlobVersion("");
    }

    /**
     * Verifies that the blob version with 2 chars is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testBloberversionTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setBlobVersion("21");
    }

    /**
     * Verifies that the blob version with 1 character is allowed
     *
     * @throws ValidationException
     */
    @Test
    public void testBloberversion() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setBlobVersion("2");
        Assert.assertEquals("2", signature.getBlobVersion());
    }

    /**
     * Verifies that a empty array passed as adapter is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testAdapterIdTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterId(new byte[0]);
    }

    /**
     * Verifies that a adapter id with less than 10 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testAdapterIdTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterId(new byte[9]);
    }

    /**
     * Verifies that an adapter with more than 10 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testAdapterIdTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterId(new byte[11]);
    }

    /**
     * Verifies that a adapter id with 10 bytes is accepted and stored
     */
    @Test
    public void testAdapterId() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterId(TestUtils.createTestArray(10, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(10, 0), signature.getAdapterId());
    }

    /**
     * Verifies that a empty array passed as meter id is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testMeterIdTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setMeterId(new byte[0]);
    }

    /**
     * Verifies that a meter id with less than 10 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testMeterIdTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setMeterId(new byte[9]);
    }

    /**
     * Verifies that an meter id with more than 10 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testMeterIdTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setMeterId(new byte[11]);
    }

    /**
     * Verifies that a meter id with 10 bytes is accepted and stored
     */
    @Test
    public void testMeterId() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setMeterId(TestUtils.createTestArray(10, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(10, 0), signature.getMeterId());
    }

    /**
     * Verifies that a empty array passed as Status is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testStatusTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setStatus(new byte[0]);
    }

    /**
     * Verifies that a Status with less than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testStatusTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setStatus(new byte[3]);
    }

    /**
     * Verifies that an Status with more than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testStatusTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setStatus(new byte[5]);
    }

    /**
     * Verifies that a Status with 4 bytes is accepted and stored
     */
    @Test
    public void testStatus() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setStatus(TestUtils.createTestArray(4, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(4, 0), signature.getStatus());
    }

    /**
     * Verifies that a empty array passed as SecondIndex is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSecondIndexTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSecondIndex(new byte[0]);
    }

    /**
     * Verifies that a SecondIndex with less than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSecondIndexTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSecondIndex(new byte[3]);
    }

    /**
     * Verifies that an SecondIndex with more than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSecondIndexTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSecondIndex(new byte[5]);
    }

    /**
     * Verifies that a SecondIndex with 4 bytes is accepted and stored
     */
    @Test
    public void testSecondIndex() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSecondIndex(TestUtils.createTestArray(4, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(4, 0), signature.getSecondIndex());
    }

    /**
     * Verifies that a empty array passed as Timestamp is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTimestampTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setTimestamp(new byte[0]);
    }

    /**
     * Verifies that a Timestamp with less than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTimestampTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setTimestamp(new byte[3]);
    }

    /**
     * Verifies that an Timestamp with more than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTimestampTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setTimestamp(new byte[5]);
    }

    /**
     * Verifies that a Timestamp with 4 bytes is accepted and stored
     */
    @Test
    public void testTimestamp() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setTimestamp(TestUtils.createTestArray(4, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(4, 0), signature.getTimestamp());
    }

    /**
     * Verifies that a empty array passed as ObisId is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testObisIdTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setObisId(new byte[0]);
    }

    /**
     * Verifies that a ObisId with less than 6 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testObisIdTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setObisId(new byte[5]);
    }

    /**
     * Verifies that an ObisId with more than 6 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testObisIdTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setObisId(new byte[7]);
    }

    /**
     * Verifies that a ObisId with 6 bytes is accepted and stored
     */
    @Test
    public void testObisId() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setObisId(TestUtils.createTestArray(6, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(6, 0), signature.getObisId());
    }

    /**
     * Verifies that a empty array passed as Value is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testValueTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setValue(new byte[0]);
    }

    /**
     * Verifies that a Value with less than 8 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testValueTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setValue(new byte[7]);
    }

    /**
     * Verifies that an Value with more than 8 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testValueTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setValue(new byte[9]);
    }

    /**
     * Verifies that a Value with 8 bytes is accepted and stored
     */
    @Test
    public void testValue() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setValue(TestUtils.createTestArray(8, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(8, 0), signature.getValue());
    }

    /**
     * Verifies that a empty array passed as Paging is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testPagingTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setPaging(new byte[0]);
    }

    /**
     * Verifies that a Paging with less than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testPagingTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setPaging(new byte[3]);
    }

    /**
     * Verifies that an Paging with more than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testPagingTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setPaging(new byte[5]);
    }

    /**
     * Verifies that a Paging with 4 bytes is accepted and stored
     */
    @Test
    public void testPaging() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setPaging(TestUtils.createTestArray(4, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(4, 0), signature.getPaging());
    }

    /**
     * Verifies that a empty array passed as SessionId is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSessionIdTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSessionId(new byte[0]);
    }

    /**
     * Verifies that a SessionId with less than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSessionIdTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSessionId(new byte[3]);
    }

    /**
     * Verifies that an SessionId with more than 4 bytes is not allowed
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSessionIdTooLong() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSessionId(new byte[5]);
    }

    /**
     * Verifies that a SessionId with 4 bytes is accepted and stored
     */
    @Test
    public void testSessionId() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setSessionId(TestUtils.createTestArray(4, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(4, 0), signature.getSessionId());
    }


    @Test(expected = ValidationException.class)
    public void testDataSetTooShort() throws ValidationException {
        AlfenSignature alfenSignature = new AlfenSignature("AP", "0", "2", null, new byte[0], null);
    }

    @Test(expected = ValidationException.class)
    public void testAdapterFirmewareversionTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareVersion(new byte[]{});
    }

    @Test(expected = ValidationException.class)
    public void testAdapterFirmewareversionTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareVersion(TestUtils.createTestArray(3, 0));
    }

    @Test(expected = ValidationException.class)
    public void testAdapterFirmewareversionTooLong1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareVersion(TestUtils.createTestArray(5, 0));
    }

    @Test
    public void testAdapterFirmewarechecksum() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareVersion(TestUtils.createTestArray(4, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(4, 0), signature.getAdapterFirmwareVersion());
    }

    @Test(expected = ValidationException.class)
    public void testAdapterFirmewarechecksumTooShort1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareChecksum(new byte[]{});
    }

    @Test(expected = ValidationException.class)
    public void testAdapterFirmewarechecksumTooShort2() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareChecksum(TestUtils.createTestArray(1, 0));
    }

    @Test(expected = ValidationException.class)
    public void testAdapterFirmewarechecksumTooLong1() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareChecksum(TestUtils.createTestArray(3, 0));
    }

    @Test
    public void testAdapterFirmewareChecksum() throws ValidationException {
        AlfenSignature signature = new AlfenSignature();
        signature.setAdapterFirmwareChecksum(TestUtils.createTestArray(2, 0));
        Assert.assertArrayEquals(TestUtils.createTestArray(2, 0), signature.getAdapterFirmwareChecksum());
    }
}
