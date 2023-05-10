package de.safe_ev.transparenzsoftware.verification.format.sml;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDL40.EDL40Signature;
import de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.SMLSignatureOnly;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Class to verify signature data logic
 */
public class SMLSignatureTest {

    /**
     * Verifies that an empty array set on set server id
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testServerIdTooShort1() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setServerId(new byte[0]);
    }

    /**
     * Verifies that an array with 9 elements set on set server id
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testServerIdTooShort2() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setServerId(new byte[9]);
    }

    /**
     * Verifies that an array with 11 elements set on set server id
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testServerIdTooLong() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setServerId(new byte[11]);
    }

    /**
     * Verifies that a server id set with 10 elements will be stored
     * in the data structure.
     *
     * @throws ValidationException
     */
    @Test
    public void testServerId() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        byte[] serverId = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        SMLSignatureOnlyMock.setServerId(serverId);
        Assert.assertArrayEquals(serverId, SMLSignatureOnlyMock.getServerId());
    }

    /**
     * Verifies that an empty array set on set timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTimestampTooShort1() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setTimestamp(new byte[0]);
    }


    /**
     * Verifies that an array with 2 elements set on set timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testTimestampTooShort2() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setTimestamp(new byte[3]);
    }

    /**
     * Verifies that an array with 4 elements set on set timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test
    public void testTimestampTooLong() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setTimestamp(new byte[4]);
    }

    /**
     * Verifies that an array with 4 elements set on set timestamp
     * will be stored in the data structure in the changed order
     *
     * @throws ValidationException
     */
    @Test
    public void testTimestampWithByteArray() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setTimestamp(new byte[]{0, 1, 2, 3});
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3}, SMLSignatureOnlyMock.getTimestamp());
    }

    /**
     * Verifies that an array with 4 elements set on set timestamp
     * will be stored in the data structure in the changed order
     *
     * @throws ValidationException
     */
    @Test
    public void testTimestampWithInt() throws ValidationException {
        EDL40Signature SMLSignatureOnlyMock = new EDL40Signature();
        OffsetDateTime timestamp = OffsetDateTime.of(LocalDateTime.ofEpochSecond(1527868080, 0, ZoneOffset.UTC), ZoneOffset.UTC);
        SMLSignatureOnlyMock.setTimestamp(timestamp);
        Assert.assertArrayEquals(new byte[]{(byte) 176, (byte) 106, (byte) 17, (byte) 91}, SMLSignatureOnlyMock.getTimestamp());
    }

    /**
     * Verifies that a status is stored correctly
     */
    @Test
    public void testStatus() {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setStatus(-6);
        Assert.assertEquals(-6, SMLSignatureOnlyMock.getStatus());
    }

    /**
     * Verifies that an array with no set on set obis nr
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testObisNrTooShort1() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setObisNr(new byte[0]);
    }

    /**
     * Verifies that an array with 5 elements set on set timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testObisNrTooShort2() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setObisNr(new byte[5]);
    }

    /**
     * Verifies that an array with 7 elements set on set timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testObisNrTooLong() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setObisNr(new byte[7]);
    }

    /**
     * Verifies that the obis nr is stored correctly
     * in the data structure.
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testObisNr() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setObisNr(new byte[]{0, 1, 2, 3, 4, 5, 6});
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3, 4, 5, 6}, SMLSignatureOnlyMock.getObisNr());
    }

    /**
     * Verifies that setting the byte unit is stored in the data signature
     */
    @Test
    public void testUnitSetByte() {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setUnit((byte) 1);
        Assert.assertEquals(1, SMLSignatureOnlyMock.getUnit());
    }

    /**
     * Verifies that setting the int unit is stored in the data signature
     * with LSB in mind
     */
    @Test
    public void testUnitSetInt1() {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setUnit(18);
        Assert.assertEquals(18, SMLSignatureOnlyMock.getUnit());
    }

    /**
     * Verifies that setting the int unit is stored in the data signature
     * with LSB in mind
     */
    @Test
    public void testUnitSetInt2() {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setUnit(257);
        Assert.assertEquals(1, SMLSignatureOnlyMock.getUnit());
    }


    /**
     * Verifies that setting the byte scaler is stored in the data signature
     */
    @Test
    public void testScalerSetByte() {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setScaler((byte) 1);
        Assert.assertEquals(1, SMLSignatureOnlyMock.getScaler());
    }

    /**
     * Verifies that an array with 0 elements set on set meter position
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testMeterPositionTooShort1() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setMeterPosition(new byte[0]);
    }

    /**
     * Verifies that an array with 7 elements set on set meter position
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testMeterPositionTooShort2() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setMeterPosition(new byte[7]);
    }

    /**
     * Verifies that an array with 9 elements set on set meter position
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testMeterPositionTooLong() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setMeterPosition(new byte[7]);
    }

    /**
     * Verifies that an array with 8 elements set on set meter position
     * will be stored correctly (reverted byte order)
     *
     * @throws ValidationException
     */
    @Test
    public void testMeterPositionBytes() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setMeterPosition(new byte[]{0, 1, 2, 3, 4, 5, 6, 7});
        Assert.assertArrayEquals(new byte[]{7, 6, 5, 4, 3, 2, 1, 0}, SMLSignatureOnlyMock.getMeterPosition());
    }


    /**
     * Verifies that an array with 0 elements set on set log book bytes
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testLogbookTooShort1() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setBytesLog(new byte[0]);
    }

    /**
     * Verifies that an array with 1 elements set on set log book bytes
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testLogbookTooShort2() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setBytesLog(new byte[1]);
    }

    /**
     * Verifies that an array with 3 elements set on set log book bytes
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testLogbookTooLong() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setBytesLog(new byte[3]);
    }

    /**
     * Verifies that an array with 3 elements set on set log book bytes
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test
    public void testLogbook() throws ValidationException {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setBytesLog(new byte[]{4, 5});
        Assert.assertArrayEquals(new byte[]{4, 5}, SMLSignatureOnlyMock.getBytesLog());
    }

    /**
     * Verifies that the setter of log book bytes which takes two
     * bytes stores them correctly
     */
    @Test
    public void testLogbookWithTwoBytes() {
        SMLSignatureOnlyMock SMLSignatureOnlyMock = new SMLSignatureOnlyMock();
        SMLSignatureOnlyMock.setBytesLog((byte) 8, (byte) 9);
        Assert.assertArrayEquals(new byte[]{8, 9}, SMLSignatureOnlyMock.getBytesLog());
    }


    /**
     * Verifies that an array with no elements set on set customer id
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testCustomerIdTooShort1() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setContractId(new byte[0]);
    }
    /**
     * Verifies that an array with no elements set on set customer id
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test
    public void testCustomerIdFillUpEmpty() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setContractId(new byte[0], true);
        Assert.assertEquals(0, data.getContractId()[0]);
    }

    /**
     * Verifies that an array with 127 elements set on set customer id
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test
    public void testCustomerFillUpFirst3() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        byte[] fillUp = new byte[]{1, 45, 3};
        data.setContractId(fillUp, true);
        Assert.assertEquals(1, data.getContractId()[0]);
        Assert.assertEquals(45, data.getContractId()[1]);
        Assert.assertEquals(3, data.getContractId()[2]);
    }

    /**
     * Verifies that an array with 129 elements set on set customer id
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testCustomerIdTooLong() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setContractId(new byte[129]);
    }

    /**
     * Verifies that set customer id stores a given byte array correctly
     *
     * @throws ValidationException
     */
    @Test
    public void testCustomerId1() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        byte[] testdata = TestUtils.createTestArray(128, 1);
        data.setContractId(testdata);
        byte[] toCompare = new byte[128];
        for (int i = 0; i < 128; i++) {
            toCompare[i] = (byte) (i+1);
        }
        Assert.assertArrayEquals(toCompare, data.getContractId());
    }

    /**
     * Verifies that set customer id stores a given byte array correctly
     *
     * @throws ValidationException
     */
    @Test
    public void testCustomerId2() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        byte[] testdata = new byte[128];
        testdata[0] = 10;
        testdata[1] = 11;
        data.setContractId(testdata);

        byte[] toCompare = new byte[128];
        toCompare[0] = 10;
        toCompare[1] = 11;
        Assert.assertArrayEquals(toCompare, data.getContractId());
    }

    /**
     * Verifies that set customer id stores a given byte array correctly
     * where the bytes will be filled up with 0 com the end
     *
     * @throws ValidationException
     */
    @Test
    public void testCustomerIdFillUp() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        byte[] testdata = new byte[2];
        testdata[0] = 10;
        testdata[1] = 11;
        data.setContractId(testdata, true);

        byte[] toCompare = new byte[128];
        toCompare[0] = 10;
        toCompare[1] = 11;
        Assert.assertArrayEquals(toCompare, data.getContractId());
    }

    /**
     * Verifies that an array with no elements set on set customer id timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testCustomerIdTimestampTooShort1() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setTimestampContractId(new byte[0]);
    }

    /**
     * Verifies that an array with 3 elements set on set customer id timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testCustomerIdTimestampTooShort2() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setTimestampContractId(new byte[3]);
    }

    /**
     * Verifies that an array with 5 elements set on set customer id timestamp
     * will throw an validation exception
     *
     * @throws ValidationException
     */
    @Test(expected = ValidationException.class)
    public void testCustomerIdTimestampTooLong() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setTimestampContractId(new byte[5]);
    }

    /**
     * Verifies if customer id timestamp will be set correctly
     *
     * @throws ValidationException
     */
    @Test
    public void testCustomerIdTimestamp() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setTimestampContractId(new byte[]{1, 2, 3, 4});
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, data.getTimestampContractId());
    }

    /**
     * Verifies that an empty array will not be allowed as seconds index
     * @throws SMLValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSecondsIndexTooShort1() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setSecondsIndex(new byte[0]);

    }

    /**
     * Verifies that a too short array will not be allowed as seconds index
     * @throws SMLValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSecondsIndexTooShort2() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setSecondsIndex(new byte[]{1, 2, 3});
    }

    /**
     * Verifies that a too long array will not be allowed as seconds index
     * @throws SMLValidationException
     */
    @Test(expected = ValidationException.class)
    public void testSecondsIndexTooLong() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setSecondsIndex(new byte[]{1, 2, 3, 4, 5});
    }

    /**
     * Verifies that a too long array will not be allowed as seconds index
     * @throws SMLValidationException
     */
    @Test
    public void testSecondsIndex() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setSecondsIndex(new byte[]{1, 2, 3, 4});
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, data.getSecondsIndex());
    }

    /**
     * Verifies that an empty array will not be allowed as pagination
     * @throws SMLValidationException
     */
    @Test(expected = ValidationException.class)
    public void testPaginationTooShort1() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setPagination(new byte[0]);

    }

    /**
     * Verifies that a too short array will not be allowed as pagination
     * @throws SMLValidationException
     */
    @Test(expected = ValidationException.class)
    public void testPaginationTooShort2() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setPagination(new byte[]{1, 2, 3});
    }

    /**
     * Verifies that a too long array will not be allowed as seconds index
     * @throws SMLValidationException
     */
    @Test(expected = ValidationException.class)
    public void testPaginationTooLong() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setPagination(new byte[]{1, 2, 3, 4, 5});
    }

    /**
     * Verifies that a too long array will not be allowed as seconds index
     * @throws SMLValidationException
     */
    @Test
    public void testPagination() throws SMLValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        data.setPagination(new byte[]{1, 2, 3, 4});
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, data.getPagination());
    }

    /**
     * Verifies that the build basic signature results in the right
     * byte order counting up 1 to 33 to check order
     *
     * @throws ValidationException
     */
    @Test(expected = SMLValidationException.class)
    public void testExtendedSignatureData() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        byte[] serverId = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        data.setServerId(serverId);
        data.setTimestamp(new byte[]{11, 12, 13, 14});
        data.setStatus(15);
        data.setSecondsIndex(new byte[]{16, 17, 18, 19});
        data.setPagination(new byte[]{20, 21, 22, 23});
        data.setObisNr(new byte[]{24, 25, 26, 27, 28, 29});
        data.setUnit((byte) 30);
        data.setScaler((byte) 31);
        //is stored in reverse order
        data.setMeterPosition(new byte[]{39, 38, 37, 36, 35, 34, 33, 32});
        data.setBytesLog(new byte[]{40, 41});
        data.setContractId(TestUtils.createTestArray(128, 42));
        data.setTimestampContractId(new byte[]{(byte) 170, (byte) 171, (byte) 172, (byte) 173});

        byte[] toCompare = new byte[320];

        for (int i = 0; i < 173; i++) {
            toCompare[i] = (byte) (i + 1);
        }
        byte[] extSigData = data.buildExtendedSignatureData();
        Assert.assertArrayEquals(toCompare, extSigData);
        for (int i = 0; i < 104; i++) {
            Assert.assertEquals("error on index "+ i, 0, extSigData[216 + i]);
        }
        Assert.assertFalse(data.isDataComplete());
    }


    /**
     * Verifies that the build basic signature results in the right
     * byte order counting up 1 to 33 to check order
     *
     * @throws ValidationException
     */
    @Test
    public void testExtendedSignatureDataValidation() throws ValidationException {
        SMLSignatureOnlyMock data = new SMLSignatureOnlyMock();
        byte[] serverId = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        data.setServerId(serverId);
        data.setTimestamp(new byte[]{11, 12, 13, 14});
        data.setStatus(15);
        data.setSecondsIndex(new byte[]{16, 17, 18, 19});
        data.setPagination(new byte[]{20, 21, 22, 23});
        data.setObisNr(new byte[]{24, 25, 26, 27, 28, 29});
        data.setUnit((byte) 30);
        data.setScaler((byte) 31);
        //is stored in reverse order
        data.setMeterPosition(new byte[]{39, 38, 37, 36, 35, 34, 33, 32});
        data.setBytesLog(new byte[]{40, 41});
        data.setContractId(TestUtils.createTestArray(128, 42));
        data.setTimestampContractId(new byte[]{(byte) 170, (byte) 171, (byte) 172, (byte) 173});

        byte[] toCompare = new byte[320];

        for (int i = 0; i < 173; i++) {
            toCompare[i] = (byte) (i + 1);
        }
        byte[] extSigData = data.buildExtendedSignatureData();
        Assert.assertArrayEquals(toCompare, extSigData);
        for (int i = 0; i < 104; i++) {
            Assert.assertEquals("error on index "+ i, 0, extSigData[216 + i]);
        }
        data.setProvidedSignature(new byte[]{1});
        Assert.assertTrue(data.isDataComplete());
    }


    private class SMLSignatureOnlyMock extends SMLSignatureOnly {
        public SMLSignatureOnlyMock(){
            super();
        }
    }
}
