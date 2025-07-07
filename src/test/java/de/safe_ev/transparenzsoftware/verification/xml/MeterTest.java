package de.safe_ev.transparenzsoftware.verification.xml;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.ValidationException;

public class MeterTest {

    @Test
    public void testMeterValues1() {
	final LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
	final OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
	final Meter meter1 = new Meter(50.0, offsetDateTime, 0);
	final Meter meter2 = new Meter(51.0, offsetDateTime, 0);
	final List<Meter> meterList = new ArrayList<>();
	meterList.add(meter1);
	meterList.add(meter2);
	Assert.assertEquals(1.0, Meter.getDifference(meterList), 0);
    }

    @Test
    public void testMeterValues2() {
	final LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
	final OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
	final Meter meter1 = new Meter(50.0, offsetDateTime, 0);
	final Meter meter2 = new Meter(51.0, offsetDateTime, 0);
	final Meter meter3 = new Meter(53.8, offsetDateTime, 0);
	final List<Meter> meterList = new ArrayList<>();
	meterList.add(meter1);
	meterList.add(meter2);
	meterList.add(meter3);
	Assert.assertEquals(3.8, Meter.getDifference(meterList), 0.2);
    }

    @Test(expected = ValidationException.class)
    public void testMeterValuesValidate1() throws ValidationException {
	final LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
	final OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
	final Meter meter1 = new Meter(50.0, offsetDateTime, 0);
	final Meter meter2 = new Meter(51.0, offsetDateTime, 0);
	final Meter meter3 = new Meter(53.8, offsetDateTime, 0);
	final List<Meter> meterListStart = new ArrayList<>();
	meterListStart.add(meter1);
	meterListStart.add(meter2);
	meterListStart.add(meter3);

	final Meter meter4 = new Meter(41.0, offsetDateTime, 0);
	final Meter meter5 = new Meter(51.0, offsetDateTime, 0);
	final Meter meter6 = new Meter(53.8, offsetDateTime, 0);
	final List<Meter> meterStopList = new ArrayList<>();
	meterStopList.add(meter4);
	meterStopList.add(meter5);
	meterStopList.add(meter6);
	Meter.validateListStartStop(meterListStart, meterStopList);
    }

    @Test
    public void testTimeDuration1() throws ValidationException {
	final LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
	final OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
	final LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
	final OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
	final Meter meter1 = new Meter(50.0, offsetDateTime1, 0);
	final Meter meter2 = new Meter(51.0, offsetDateTime2, 0);
	final Meter meter3 = new Meter(53.8, offsetDateTime1, 0);
	final List<Meter> meterListStart = new ArrayList<>();
	meterListStart.add(meter1);
	meterListStart.add(meter2);
	meterListStart.add(meter3);

	final Duration diff = Meter.getTimeDiff(meterListStart);
	Assert.assertEquals(60, diff.getSeconds());
    }

    @Test
    public void testTimeDuration2() throws ValidationException {
	final LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
	final OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
	final LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
	final OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
	final LocalDateTime timestamp3 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 43, 1);
	final OffsetDateTime offsetDateTime3 = OffsetDateTime.of(timestamp3, ZoneOffset.UTC);
	final Meter meter1 = new Meter(50.0, offsetDateTime1, Meter.Type.START, Meter.TimeSyncType.INFORMATIVE, MS,
		false);
	final Meter meter2 = new Meter(51.0, offsetDateTime2, Meter.Type.STOP, Meter.TimeSyncType.INFORMATIVE, MS,
		false);
	final Meter meter3 = new Meter(53.8, offsetDateTime3, 0);
	final List<Meter> meterListStart = new ArrayList<>();
	meterListStart.add(meter1);
	meterListStart.add(meter2);
	meterListStart.add(meter3);

	final Duration diff = Meter.getTimeDiff(meterListStart);
	Assert.assertEquals(60, diff.getSeconds());
    }

    @Test
    public void testMeterDiff1() throws ValidationException {
	final LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
	final OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
	final LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
	final OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
	final Meter meter1 = new Meter(50.0, offsetDateTime1, 0);
	final Meter meter2 = new Meter(51.0, offsetDateTime2, 0);
	final Meter meter3 = new Meter(53.8, offsetDateTime1, 0);
	final List<Meter> meterListStart = new ArrayList<>();
	meterListStart.add(meter1);
	meterListStart.add(meter2);
	meterListStart.add(meter3);

	final double diff = Meter.getDifference(meterListStart);
	Assert.assertEquals(3.8, diff, 0.1);
    }

    public static final int MS = -1; // meter scaling

    @Test
    public void testMeterDiff2() throws ValidationException {
	final LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
	final OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
	final LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
	final OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
	final LocalDateTime timestamp3 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 43, 1);
	final OffsetDateTime offsetDateTime3 = OffsetDateTime.of(timestamp3, ZoneOffset.UTC);
	final Meter meter1 = new Meter(50.0, offsetDateTime1, Meter.Type.START, Meter.TimeSyncType.INFORMATIVE, MS,
		false);
	final Meter meter2 = new Meter(51.0, offsetDateTime2, Meter.Type.STOP, Meter.TimeSyncType.INFORMATIVE, MS,
		false);
	final Meter meter3 = new Meter(53.8, offsetDateTime3, 0);
	final List<Meter> meterListStart = new ArrayList<>();
	meterListStart.add(meter1);
	meterListStart.add(meter2);
	meterListStart.add(meter3);

	final double diff = Meter.getDifference(meterListStart);
	Assert.assertEquals(1, diff, 0);
    }

    @Test
    public void testAdditionalTextStart() {
	final LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.DECEMBER, 25, 7, 30, 0);
	final OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);

	final Meter meter1 = new Meter(50.0, offsetDateTime1, Meter.Type.START, Meter.TimeSyncType.INFORMATIVE, MS,
		false);
	Assert.assertEquals(Translator.get("app.informative"), meter1.getAdditonalText());

    }
}
