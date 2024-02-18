package de.safe_ev.transparenzsoftware.verification.format.sml;

import org.junit.Assert;
import org.junit.Test;
import org.openmuc.jsml.structures.Integer16;
import org.openmuc.jsml.structures.SmlTimestamp;
import org.openmuc.jsml.structures.SmlTimestampLocal;
import org.openmuc.jsml.structures.Unsigned32;

import de.safe_ev.transparenzsoftware.verification.format.sml.SMLUtils;

import java.time.OffsetDateTime;

public class SMLUtilsTest {

    private final static int BASE_TIME_OFFSET = 1500000000;
    private final static int HOUR_IN_SECONDS = 3600;

    @Test
    public void testLocalTimeWithOffset() {
        SmlTimestampLocal smlTimestampLocal = createSmlLocalTimestamp(60, 60);

        OffsetDateTime actual = SMLUtils.parseSmlTimestamp(smlTimestampLocal);
        Assert.assertEquals(BASE_TIME_OFFSET, actual.toEpochSecond());
        Assert.assertEquals(BASE_TIME_OFFSET + HOUR_IN_SECONDS + HOUR_IN_SECONDS, actual.toEpochSecond()+actual.getOffset().getTotalSeconds());
    }


    @Test
    public void testLocalTimeWithNoOffset() {
        SmlTimestampLocal smlTimestampLocal = createSmlLocalTimestamp(0, 0);
        Assert.assertEquals(BASE_TIME_OFFSET, SMLUtils.parseSmlTimestamp(smlTimestampLocal).toEpochSecond());
    }

    @Test
    public void testLocalTimeWithDaylightSavingOffset() {
        SmlTimestampLocal smlTimestampLocal = createSmlLocalTimestamp(60, 0);
        OffsetDateTime actual = SMLUtils.parseSmlTimestamp(smlTimestampLocal);
        Assert.assertEquals(BASE_TIME_OFFSET, actual.toEpochSecond());
        Assert.assertEquals(BASE_TIME_OFFSET + HOUR_IN_SECONDS, actual.toEpochSecond()+actual.getOffset().getTotalSeconds());
    }

    @Test
    public void testLocalTimeWithTimezoneOffset() {
        SmlTimestampLocal smlTimestampLocal = createSmlLocalTimestamp(0, 60);
        OffsetDateTime offsetDateTime = SMLUtils.parseSmlTimestamp(smlTimestampLocal);
        Assert.assertEquals(BASE_TIME_OFFSET, offsetDateTime.toEpochSecond());
        Assert.assertEquals(BASE_TIME_OFFSET + HOUR_IN_SECONDS, offsetDateTime.toEpochSecond()+offsetDateTime.getOffset().getTotalSeconds());
    }


    private SmlTimestampLocal createSmlLocalTimestamp(int daylightSaving, int timezone) {
        SmlTimestamp smlTimestamp = new SmlTimestamp(new Unsigned32(1500000000));
        Integer16 localOffset = new Integer16((short) timezone);
        Integer16 seasonTimeOffset = new Integer16((short) daylightSaving);
        return new SmlTimestampLocal(smlTimestamp, localOffset, seasonTimeOffset);
    }
}
