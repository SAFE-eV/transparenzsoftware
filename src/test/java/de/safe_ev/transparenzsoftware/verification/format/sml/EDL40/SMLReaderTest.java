package de.safe_ev.transparenzsoftware.verification.format.sml.EDL40;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDL40.SMLReader;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

public class SMLReaderTest {


    public static final String SML_DATA_WITHOUT_EXTENDED_SIGNATURE = "GxsbGwEBAQF2BTLKUmtiAGIAcmMBAXYBBUViZWUFnyRTtgsJAUVNSAAAc0DyAQFjL+4AdgUyylJqYgBiAHJjBwF3AQsJAUVNSAAAc0DyB4GAgWIC/3JiAWUAFh6gc3cHgYKBVAH/AXJiA3NlWxFO4lMAPFMAPAEBDzA0NzcyN2VhMDEyYjgwAXcHgYCBYQH/AQEBAQEBdwcBAAERAP9kAAEIcmIDc2VbEU6QUwA8UwA8Yh5S/1YAAAAUKIMEfluZg0PEmR0xorxe0xCmNYrA9XeWs+ZCxjE2+6B28j58Gh7SlQzYlNUQuVGwcJG5AHKDBHSkRVsNJA5zNs3MeDZ75KzxhJN0usA2Tsb9rPf/ab+SF2AuHKCn+oyUzb6AaOXPtAByAWOvYwB2BTLKUmxiAGIAcmMCAXEBYxWYABsbGxsaAM57";
    public static final String SML_DATA_WITH_EXTENDED_SIGNATURE = "GxsbGwEBAQF2BTLKUmtiAGIAcmMBAXYBBUViZWUFnyRTtgsJAUVNSAAAc0DyAQFjL+4AdgUyylJqYgBiAHJjBwF3AQsJAUVNSAAAc0DyB4GAgWIC/3JiAWUAFh6gc3cHgYKBVAH/AXJiA3NlWxFO4lMAPFMAPAEBDzA0NzcyN2VhMDEyYjgwAXcHgYCBYQH/AQEBAQEBdwcBAAERAP9kAAEIcmIDc2VbEU6QUwA8UwA8Yh5S/1YAAAAUKIMEfluZg0PEmR0xorxe0xCmNYrA9XeWs+ZCxjE2+6B28j58Gh7SlQzYlNUQuVGwcJG5AHKDBHSkRVsNJA5zNs3MeDZ75KzxhJN0usA2Tsb9rPf/ab+SF2AuHKCn+oyUzb6AaOXPtAByAWOvYwB2BTLKUmxiAGIAcmMCAXEBYxWYABsbGxsaAM57";
    public static final String DATA_MENNEKES = "GxsbGwEBAQF2BXcVh2hiAGIAcmMBAXYBBUViZWUFEj7tewsJAUVNSAAAesZiAQFjeLAAdgV3FYdnYgBiAHJjBwF3AQsJAUVNSAAAesZiB4GAgXED/3JiAWUACKttdHcHgYKBVAH/AXJiA3Nl/////1MAAFMAAAEBDWFiY2RlZmdoLTEyMwF3BwEAAREA/2QBAQByYgNzZf////9TAABTAABiHlL/VgAAAAtmAXcHgQBgCAABAQEBAXJiAXJiAWUACKtMAXcHgYCBcQH/AQEBAWUAAABFAYMEQj04Ipr+zFmI+sJJS+zF+xTVDWF0eST0DMmci9iWlHh+JLruyGLNR2DGE579qRUVAAoBYyqSAHYFdxWHaWIAYgByYwIBcQFjvvsAABsbGxsaAdsn";


    public static final String SML_DATA_FULL_NEW = "GxsbGwEBAQF2BZ3J/WxiAGIAcmMBAXYBBUViZWUFXcdVGgsJAUVNSAAAesZiAQFj7B0AdgWdyf1rYgBiAHJjBwF3AQsJAUVNSAAAesZiB4GAgXED/3JiAWUABTFgdHcHgYKBVAH/AXJiA3NlW4V/AVMAPFMAPAEBDWFiY2RlZmdoLTEyMwF3BwEAAREA/2QBAQhyYgNzZVuFfwFTADxTADxiHlL/VgAAAAtmAXcHgQBgCAABAQEBAXJiAXJiAWUABTE9AXcHgYCBcQH/AQEBAWUAAABBAYMEKfiBzm3mT65YsoQM/354kgWHkia8tSFep6/OQrhl1qXUGrkwOixS3hs1B5b5KYcVAAoBYzQvAHYFncn9bWIAYgByYwIBcQFj8nIAABsbGxsaAZkP";
    public static final String SML_DATA_TECHNAGON = "GxsbGwEBAQF2BQAAAB5iAGIAcmMBAXYBBzEyMzQ1NgUAAAAdCwkBRU1IAAB/kgMBAWOOEgB2BQAAAB9iAGIAcmMHAXcBCwkBRU1IAAB/kgMHgYCBcQP/cmIBZQABR2l0dweBgoFUAf8BcmIDc2VaFuV9UwAAUwAAAQGBAkNvbnRyYWN0LWlkIGRhdGEBdwcBAAERAP9kAAAIcmIDc2VaFuWDUwAAUwAAYh5S/1YAAAC0eQF3B4EAYAgAAQEBAQFyYgFyYgFlAAFG8AF3B4GAgXEB/wEBAQFlAAAADgGDBO6MKW9QnsrGdW4qBeqUX8k78x+CN3xX4iaYPOUumAljOWpJslOyejrFM5ikbI8imQA7AWPbowB2BQAAACBiAGIAcmMCAXEBY5H6AAAAGxsbGxoCvVM=";
//
//    @Test
//    public void test_read_data_without_extended_signature() throws ValidationException {
//        SMLReader smlReader = new SMLReader();
//        SMLSignature SMLSignature = smlReader.parseSMLBase64String(SML_DATA_WITHOUT_EXTENDED_SIGNATURE);
//        Assert.assertEquals(-1, SMLSignature.getScaler());
//        Assert.assertEquals(30, SMLSignature.getUnit());
//        Assert.assertEquals(8, SMLSignature.getStatus());
//
//        Assert.assertEquals(2, SMLSignature.getBytesLog().length);
//        Assert.assertArrayEquals(new byte[]{0, 114}, SMLSignature.getBytesLog());
//
//        Assert.assertEquals(4, SMLSignature.getTimestamp().length);
//        Assert.assertArrayEquals(new byte[]{-80, 106, 17, 91}, SMLSignature.getTimestamp());
//
//        Assert.assertEquals(8, SMLSignature.getMeterPosition().length);
//        Assert.assertArrayEquals(new byte[]{40, 20, 0, 0, 0, 0, 0, 0}, SMLSignature.getMeterPosition());
//        Assert.assertEquals(5160, SMLSignature.getMeterPositionAsLong());
//
//        Assert.assertEquals(10, SMLSignature.getServerId().length);
//        Assert.assertArrayEquals(new byte[]{9, 1, 69, 77, 72, 0, 0, 115, 64, -14}, SMLSignature.getServerId());
//
//        Assert.assertEquals(6, SMLSignature.getObisNr().length);
//        Assert.assertArrayEquals(new byte[]{1, 0, 1, 17, 0, -1}, SMLSignature.getObisNr());
//
//        byte[] extendedSignature = Hex.decode("7E 5B 99 83 43 C4 99 1D 31 A2 BC 5E D3 10 A6 35 8A C0 F5 77 96 B3 E6 42 C6 31 36 FB A0 76 F2 3E 7C 1A 1E D2 95 0C D8 94 D5 10 B9 51 B0 70 91 B9 00 72");
//        Assert.assertArrayEquals(extendedSignature, SMLSignature.getBaseSignature());
//
//        Assert.assertEquals(128, SMLSignature.getContractId().length);
//
//        byte[] customerIdFirst14Bytes = Hex.decode("30 34 37 37 32 37 65 61 30 31 32 62 38 30");
//        byte[] customerIdFull128Bytes = new byte[128];
//        System.arraycopy(customerIdFirst14Bytes, 0, customerIdFull128Bytes, 0, customerIdFirst14Bytes.length);
//        Assert.assertArrayEquals(customerIdFull128Bytes, SMLSignature.getContractId());
//
//        OffsetDateTime timestampAsDate = SMLSignature.getTimestampAsDate();
//        Assert.assertEquals(1, timestampAsDate.getDayOfMonth());
//        Assert.assertEquals(6, timestampAsDate.getMonthValue());
//        Assert.assertEquals(2018, timestampAsDate.getYear());
//        Assert.assertEquals(15, timestampAsDate.getHour());
//        Assert.assertEquals(48, timestampAsDate.getMinute());
//        Assert.assertEquals(0, timestampAsDate.getSecond());
//
//        OffsetDateTime timestampCustomerIdAsDate = SMLSignature.getTimestampContractIdAsDate();
//        Assert.assertEquals(1, timestampCustomerIdAsDate.getDayOfMonth());
//        Assert.assertEquals(6, timestampCustomerIdAsDate.getMonthValue());
//        Assert.assertEquals(2018, timestampCustomerIdAsDate.getYear());
//        Assert.assertEquals(15, timestampCustomerIdAsDate.getHour());
//        Assert.assertEquals(49, timestampCustomerIdAsDate.getMinute());
//        Assert.assertEquals(22, timestampCustomerIdAsDate.getSecond());
//
//
//        byte[] customerIdTimestamp = Hex.decode("02 6B 11 5B");
//        Assert.assertArrayEquals(customerIdTimestamp, SMLSignature.getTimestampContractId());
//    }


    //    @Test
//    public void test_full_sml_mennekes() throws ValidationException {
//        SMLReader smlReader = new SMLReader();
//        SMLSignature SMLSignature = smlReader.parseSMLBase64String(DATA_MENNEKES);
//        Assert.assertArrayEquals(new byte[]{1, 0, 1, 17, 0, -1}, SMLSignature.getObisNr());
//        Assert.assertEquals(19713, SMLSignature.getMeterPositionAsLong());
//
//        OffsetDateTime timestampAsDate = SMLSignature.getTimestampAsDate();
//        Assert.assertEquals(1, timestampAsDate.getDayOfMonth());
//        Assert.assertEquals(9, timestampAsDate.getMonthValue());
//        Assert.assertEquals(2017, timestampAsDate.getYear());
//        Assert.assertEquals(14, timestampAsDate.getHour());
//        Assert.assertEquals(12, timestampAsDate.getMinute());
//        Assert.assertEquals(0, timestampAsDate.getSecond());
//
//        OffsetDateTime timestampCustomerIdAsDate = SMLSignature.getTimestampContractIdAsDate();
//        Assert.assertEquals(1, timestampCustomerIdAsDate.getDayOfMonth());
//        Assert.assertEquals(9, timestampCustomerIdAsDate.getMonthValue());
//        Assert.assertEquals(2017, timestampCustomerIdAsDate.getYear());
//        Assert.assertEquals(14, timestampCustomerIdAsDate.getHour());
//        //TODO: what is wrong in converting back time?
//        Assert.assertEquals(13, timestampCustomerIdAsDate.getMinute());
//        Assert.assertEquals(16, timestampCustomerIdAsDate.getSecond());
//
//    }
//
    @Test
    public void test_full_data() throws ValidationException, DecodingException {
        SMLReader smlReader = new SMLReader();
        SMLSignature signature = smlReader.parsePayloadData(EncodingType.BASE64.decode(SML_DATA_FULL_NEW));
        Assert.assertNotNull(signature);
        Assert.assertNotNull(signature.getProvidedSignature());
        Assert.assertEquals(2918, signature.getLawRelevantMeterAsLong());
        Assert.assertEquals("09 01 45 4D 48 00 00 7A C6 62", Utils.toFormattedHex(signature.getServerId()));
        Assert.assertEquals(65, new BigInteger(Utils.reverseByteOrder(signature.getPagination())).intValue());
        Assert.assertEquals(340285, new BigInteger(Utils.reverseByteOrder(signature.getSecondsIndex())).intValue());
        Assert.assertEquals(10, new BigInteger(signature.getBytesLog()).intValue());
        Assert.assertEquals(8, new BigInteger(new byte[]{signature.getStatus()}).intValue());

        LocalDateTime dateTime = LocalDateTime.of(2018, Month.AUGUST, 28, 18, 57, 37);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+02:00"));
        Assert.assertEquals(offsetDateTime, signature.getTimestampAsDate());


    }

    @Test
    public void test_full_data_technagon() throws ValidationException, DecodingException {
        SMLReader smlReader = new SMLReader();
        SMLSignature signature = smlReader.parsePayloadData(EncodingType.BASE64.decode(SML_DATA_TECHNAGON));
        Assert.assertNotNull(signature);
        Assert.assertNotNull(signature.getProvidedSignature());
        Assert.assertEquals(46201, signature.getLawRelevantMeterAsLong());
        Assert.assertEquals("09 01 45 4D 48 00 00 7F 92 03", Utils.toFormattedHex(signature.getServerId()));
        Assert.assertEquals(14, new BigInteger(Utils.reverseByteOrder(signature.getPagination())).intValue());
        Assert.assertEquals(83696, new BigInteger(Utils.reverseByteOrder(signature.getSecondsIndex())).intValue());
        Assert.assertEquals(59, new BigInteger(signature.getBytesLog()).intValue());
        Assert.assertEquals(8, new BigInteger(new byte[]{signature.getStatus()}).intValue());

        LocalDateTime dateTime = LocalDateTime.of(2017, Month.NOVEMBER, 23, 15, 13, 07);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.of("+00:00"));
        Assert.assertEquals(offsetDateTime, signature.getTimestampAsDate());


    }
}
