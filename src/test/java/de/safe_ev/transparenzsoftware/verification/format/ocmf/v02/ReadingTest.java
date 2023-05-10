package de.safe_ev.transparenzsoftware.verification.format.ocmf.v02;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.format.ocmf.v02.Reading;
import de.safe_ev.transparenzsoftware.verification.xml.Meter.TimeSyncType;

import java.time.OffsetDateTime;

public class ReadingTest {

    @Test
    public void testTimestampParsing() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 S");
        OffsetDateTime timestamp = reading.getTimestamp();
        Assert.assertNotNull(timestamp);
        Assert.assertEquals(2018, timestamp.getYear());
        Assert.assertEquals(7, timestamp.getMonthValue());
        Assert.assertEquals(24, timestamp.getDayOfMonth());
        Assert.assertEquals(13, timestamp.getHour());
        Assert.assertEquals(22, timestamp.getMinute());
        Assert.assertEquals(4, timestamp.getSecond());
        Assert.assertEquals(0, timestamp.getNano());
        Assert.assertEquals(2 * 60 * 60, timestamp.getOffset().getTotalSeconds());
    }

    @Test
    public void testTimestampParsingWrongFormat() {
        Reading reading = new Reading();
        reading.setTM("201800-07-24T13:22:04,000+0200 S");
        OffsetDateTime timestamp = reading.getTimestamp();
        Assert.assertNull(timestamp);
    }

    @Test
    public void testTimestampParsingNull() {
        Reading reading = new Reading();
        OffsetDateTime timestamp = reading.getTimestamp();
        Assert.assertNull(timestamp);
    }

    @Test
    public void testTimestampSynchronicity() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 S");
        Assert.assertEquals("S", reading.getTimeSynchronicity());
    }

    @Test
    public void testTimestampWrongFormatNull() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200S");
        Assert.assertNull(reading.getTimeSynchronicity());
    }

    @Test
    public void testTimestampSynchronicityNull() {
        Reading reading = new Reading();
        Assert.assertNull(reading.getTimeSynchronicity());
    }


    @Test
    public void testTimestampSynchronicityNullLabel() {
        Reading reading = new Reading();
        Assert.assertNull(reading.getLabelForTimeFlag());
    }

    @Test
    public void testTimestampSynchronicityULabel() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 U");
        Assert.assertEquals("app.verify.ocmf.timesynchronicity.unknown", reading.getLabelForTimeFlag());
    }

    @Test
    public void testTimestampSynchronicityYLabel() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 Y");
        Assert.assertEquals("app.verify.ocmf.timesynchronicity.unknown", reading.getLabelForTimeFlag());
    }

    @Test
    public void testTimestampSynchronicitySLabel() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 S");
        Assert.assertEquals("app.verify.ocmf.timesynchronicity.synchronised", reading.getLabelForTimeFlag());
    }

    @Test
    public void testTimestampSynchronicityRLabel() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 R");
        Assert.assertEquals("app.verify.ocmf.timesynchronicity.relative", reading.getLabelForTimeFlag());
    }

    @Test
    public void testTimestampSynchronicityILabel() {
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 I");
        Assert.assertEquals("app.verify.ocmf.timesynchronicity.informative", reading.getLabelForTimeFlag());
    }

    @Test
    public void testIsTimeSynchron(){
        Reading reading = new Reading();
        reading.setTM("2018-07-24T13:22:04,000+0200 I");
        Assert.assertEquals(reading.getTimeSyncType(),TimeSyncType.INFORMATIVE);
        reading.setTM("2018-07-24T13:22:04,000+0200 S");
        Assert.assertEquals(reading.getTimeSyncType(),TimeSyncType.SYNCHRONIZED);
        reading.setTM("2018-07-24T13:22:04,000+0200 U");
        Assert.assertEquals(reading.getTimeSyncType(),TimeSyncType.INFORMATIVE);

        reading.setTM("2018-07-24T13:22:04,000+0200 R");
        Assert.assertEquals(reading.getTimeSyncType(),TimeSyncType.REALTIME);
    }

    @Test
    public void testDigitNumbering() {
    	Reading r = new Reading();
    	r.setRVasString("1.2345");
    	Assert.assertEquals("Expect -1", -1, r.getRVDigits());
    	r.setRVasString("1.234");
    	Assert.assertEquals("Expect 0", 0, r.getRVDigits());
    	r.setRVasString("1.23");
    	Assert.assertEquals("Expect 1", 1, r.getRVDigits());
    	r.setRVasString("1.2");
    	Assert.assertEquals("Expect 2", 2, r.getRVDigits());
    	r.setRVasString("1.");
    	Assert.assertEquals("Expect 3", 3, r.getRVDigits());
    	r.setRVasString("10");
    	Assert.assertEquals("Expect 3", 3, r.getRVDigits());
    	r.setRVasString("10.1234");
    	Assert.assertEquals("Expect -1", -1, r.getRVDigits());
    	r.setRVasString("10.1");
    	Assert.assertEquals("Expect 2", 2, r.getRVDigits());
    	
    }

}
