package de.safe_ev.transparenzsoftware.verification.format.alfen;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenReader;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenSignature;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AlfenReaderTest {

    private final static String ALFEN_TEST_STRING = "AP;1;3;AICIVT423BX3TJGK6QCCVRHQ63LJQUEVZWWTYQUZ;BJKGK43UIRSXMAAROYYDCMZNUYFACRC2I4ADGAABIAAAAAIAQ6ACMAD5CH4FWAIAAEEAB7Y6ACKD2AAAAAAAAABRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASAAAAAEQAAAAA====;C3J3MLA5XLF7QYYHA4RAJV7QLBWU5OB3M3DUKCUTREEQ5QORE45DMUQALYBEI2YOLNX7DYFRWGLYU===;";
    public static final String PUBL_KEY_DECODED = "AICIVT423BX3TJGK6QCCVRHQ63LJQUEVZWWTYQUZ";
    public static final String ADAPTER_ID_HEX = "0A 54 65 73 74 44 65 76 00 11";
    public static final String METER_ID_HEX = "0A 01 44 5A 47 00 33 00 01 40";
    public static final String STATUS_HEX = "00 00 01 00";
    public static final String SECONDINDEX_HEX = "87 80 26 00";
    public static final long SECONDINDEX_DEC = 2523271;
    public static final String TIMESTAMP_HEX = "7D 11 F8 5B";
    public static final long TIMESTAMP_DEC = 1542984061;
    public static final String OBIS_ID_HEX = "01 00 01 08 00 FF";
    public static final String UNIT_HEX = "1E";
    public static final String SCALAR_HEX = "00";
    public static final String VALUE_HEX = "94 3D 00 00 00 00 00 00";
    public static final long VALUE_DEC = 15764;
    public static final String UID_HEX = "31 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";
    public static final String SESSION_ID_HEX = "12 00 00 00";
    public static final long SESSION_ID = 18;
    public static final String PAGING_HEX = "12 00 00 00";
    public static final long PAGING_DEC = 18;
    public static final String SIGNATURE_HEX = "16 D3 B6 2C 1D BA CB F8 63 07 07 22 04 D7 F0 58 6D 4E B8 3B 66 C7 45 0A 93 89 09 0E C1 D1 27 3A 36 52 00 5E 02 44 6B 0E 5B 6F F1 E0 B1 B1 97 8A";

    @Test
    public void testParseDocuString() throws ValidationException {
        AlfenReader alfenReader = new AlfenReader();
        AlfenSignature data = alfenReader.parseString(ALFEN_TEST_STRING);
        Assert.assertEquals("AP", data.getIdentifier());
        Assert.assertEquals("3", data.getBlobVersion());
        Assert.assertEquals("1", data.getType());

        Assert.assertEquals(PUBL_KEY_DECODED, data.getPublicKey());

        Assert.assertEquals(ADAPTER_ID_HEX, Utils.toFormattedHex(data.getAdapterId()));
        Assert.assertEquals(METER_ID_HEX, Utils.toFormattedHex(data.getMeterId()));
        Assert.assertEquals(STATUS_HEX, Utils.toFormattedHex(data.getStatus()));
        Assert.assertEquals(SECONDINDEX_HEX, Utils.toFormattedHex(data.getSecondIndex()));
        Assert.assertEquals(TIMESTAMP_HEX, Utils.toFormattedHex(data.getTimestamp()));
        Assert.assertEquals(OBIS_ID_HEX, Utils.toFormattedHex(data.getObisId()));
        Assert.assertEquals(UNIT_HEX, Utils.toFormattedHex(data.getUnit()));
        Assert.assertEquals(SCALAR_HEX, Utils.toFormattedHex(data.getScalar()));
        Assert.assertEquals(VALUE_HEX, Utils.toFormattedHex(data.getValue()));
        Assert.assertEquals(UID_HEX, Utils.toFormattedHex(data.getUid()));
        Assert.assertEquals(SESSION_ID_HEX, Utils.toFormattedHex(data.getSessionId()));
        Assert.assertEquals(PAGING_HEX, Utils.toFormattedHex(data.getPaging()));

        Assert.assertEquals(SIGNATURE_HEX, Utils.toFormattedHex(data.getSignature()));

        Assert.assertEquals(SECONDINDEX_DEC, data.getSecondIndexAsLong());
        Assert.assertEquals(TIMESTAMP_DEC, data.getTimestampAsLong());
        Assert.assertEquals(SESSION_ID, data.getSessionIdAsLong());
        Assert.assertEquals(PAGING_DEC, data.getPagingAsLong());
        Assert.assertEquals(VALUE_DEC, data.getValueAsLong());

        LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
        Assert.assertEquals(offsetDateTime, data.getTimestampAsLocalDate());
    }
}
