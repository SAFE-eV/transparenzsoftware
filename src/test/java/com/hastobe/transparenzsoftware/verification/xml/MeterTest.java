package com.hastobe.transparenzsoftware.verification.xml;

import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import org.junit.Assert;
import org.junit.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class MeterTest {

    @Test
    public void testMeterValues1(){
        LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
        Meter meter1 = new Meter(50.0, offsetDateTime);
        Meter meter2 = new Meter(51.0, offsetDateTime);
        List<Meter> meterList = new ArrayList<>();
        meterList.add(meter1);
        meterList.add(meter2);
        Assert.assertEquals(1.0, Meter.getDifference(meterList), 0);
    }
    @Test
    public void testMeterValues2(){
        LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
        Meter meter1 = new Meter(50.0, offsetDateTime);
        Meter meter2 = new Meter(51.0, offsetDateTime);
        Meter meter3 = new Meter(53.8, offsetDateTime);
        List<Meter> meterList = new ArrayList<>();
        meterList.add(meter1);
        meterList.add(meter2);
        meterList.add(meter3);
        Assert.assertEquals(3.8, Meter.getDifference(meterList), 0.2);
    }

    @Test(expected = ValidationException.class)
    public void testMeterValuesValidate1() throws ValidationException {
        LocalDateTime timestamp = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp, ZoneOffset.UTC);
        Meter meter1 = new Meter(50.0, offsetDateTime);
        Meter meter2 = new Meter(51.0, offsetDateTime);
        Meter meter3 = new Meter(53.8, offsetDateTime);
        List<Meter> meterListStart = new ArrayList<>();
        meterListStart.add(meter1);
        meterListStart.add(meter2);
        meterListStart.add(meter3);

        Meter meter4 = new Meter(41.0, offsetDateTime);
        Meter meter5 = new Meter(51.0, offsetDateTime);
        Meter meter6 = new Meter(53.8, offsetDateTime);
        List<Meter> meterStopList = new ArrayList<>();
        meterStopList.add(meter4);
        meterStopList.add(meter5);
        meterStopList.add(meter6);
        Meter.validateListStartStop(meterListStart, meterStopList);
    }

    @Test
    public void testTimeDuration1() throws ValidationException {
        LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
        LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
        OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
        Meter meter1 = new Meter(50.0, offsetDateTime1);
        Meter meter2 = new Meter(51.0, offsetDateTime2);
        Meter meter3 = new Meter(53.8, offsetDateTime1);
        List<Meter> meterListStart = new ArrayList<>();
        meterListStart.add(meter1);
        meterListStart.add(meter2);
        meterListStart.add(meter3);

        Duration diff = Meter.getTimeDiff(meterListStart);
        Assert.assertEquals(60, diff.getSeconds());
    }

    @Test
    public void testTimeDuration2() throws ValidationException {
        LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
        LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
        OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
        LocalDateTime timestamp3 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 43, 1);
        OffsetDateTime offsetDateTime3 = OffsetDateTime.of(timestamp3, ZoneOffset.UTC);
        Meter meter1 = new Meter(50.0, offsetDateTime1, Meter.Type.START, Meter.TimeSyncType.INFORMATIVE);
        Meter meter2 = new Meter(51.0, offsetDateTime2, Meter.Type.STOP, Meter.TimeSyncType.INFORMATIVE);
        Meter meter3 = new Meter(53.8, offsetDateTime3);
        List<Meter> meterListStart = new ArrayList<>();
        meterListStart.add(meter1);
        meterListStart.add(meter2);
        meterListStart.add(meter3);

        Duration diff = Meter.getTimeDiff(meterListStart);
        Assert.assertEquals(60, diff.getSeconds());
    }
    @Test
    public void testMeterDiff1() throws ValidationException {
        LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
        LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
        OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
        Meter meter1 = new Meter(50.0, offsetDateTime1);
        Meter meter2 = new Meter(51.0, offsetDateTime2);
        Meter meter3 = new Meter(53.8, offsetDateTime1);
        List<Meter> meterListStart = new ArrayList<>();
        meterListStart.add(meter1);
        meterListStart.add(meter2);
        meterListStart.add(meter3);

        double diff = Meter.getDifference(meterListStart);
        Assert.assertEquals(3.8, diff, 0.1);
    }

    @Test
    public void testMeterDiff2() throws ValidationException {
        LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 41, 1);
        OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);
        LocalDateTime timestamp2 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 42, 1);
        OffsetDateTime offsetDateTime2 = OffsetDateTime.of(timestamp2, ZoneOffset.UTC);
        LocalDateTime timestamp3 = LocalDateTime.of(2018, Month.NOVEMBER, 23, 14, 43, 1);
        OffsetDateTime offsetDateTime3 = OffsetDateTime.of(timestamp3, ZoneOffset.UTC);
        Meter meter1 = new Meter(50.0, offsetDateTime1, Meter.Type.START, Meter.TimeSyncType.INFORMATIVE);
        Meter meter2 = new Meter(51.0, offsetDateTime2, Meter.Type.STOP, Meter.TimeSyncType.INFORMATIVE);
        Meter meter3 = new Meter(53.8, offsetDateTime3);
        List<Meter> meterListStart = new ArrayList<>();
        meterListStart.add(meter1);
        meterListStart.add(meter2);
        meterListStart.add(meter3);

        double diff = Meter.getDifference(meterListStart);
        Assert.assertEquals(1, diff, 0);
    }

    @Test
    public void testAdditionalTextStart(){
        LocalDateTime timestamp1 = LocalDateTime.of(2018, Month.DECEMBER, 25, 7, 30, 0);
        OffsetDateTime offsetDateTime1 = OffsetDateTime.of(timestamp1, ZoneOffset.UTC);

        Meter meter1 = new Meter(50.0, offsetDateTime1, Meter.Type.START, Meter.TimeSyncType.INFORMATIVE);
        Assert.assertEquals(Translator.get("app.informative"), meter1.getAdditonalText());


    }
}
