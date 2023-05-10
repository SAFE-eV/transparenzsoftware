package de.safe_ev.transparenzsoftware.verification.format.ocmf.v02;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.ocmf.OCMF;
import de.safe_ev.transparenzsoftware.verification.format.ocmf.OCMFValidationException;
import de.safe_ev.transparenzsoftware.verification.format.ocmf.OCMFVerifiedData;
import de.safe_ev.transparenzsoftware.verification.format.ocmf.v02.OCMFPayloadData;
import de.safe_ev.transparenzsoftware.verification.format.ocmf.v02.Reading;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;

import java.util.ArrayList;

public class OCMFVerifiedDataTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = ValidationException.class)
    public void testOCMFLawConform() throws RegulationLawException, ValidationException {
        OCMFVerifiedData start = new OCMFVerifiedData();
        OCMFVerifiedData stop = new OCMFVerifiedData();
        start.lawConform(stop);
    }


    @Test
    public void testOCMFLawConformStopContainsStart() throws RegulationLawException, ValidationException {
        expectedException.expect(OCMFValidationException.class);
        expectedException.expectMessage("OCMF entry marked as stop contains a start value");
        OCMFPayloadData ocmfPayloadDataStart = new OCMFPayloadData();
        ocmfPayloadDataStart.setRD(new ArrayList<>());

        Reading startReading = new Reading();
        startReading.setRV((double) 1);
        startReading.setST("G");
        startReading.setTX("B");
        startReading.setTM("2018-07-24T13:22:04,000+0200 S");
        ocmfPayloadDataStart.getRD().add(startReading);

        OCMFPayloadData ocmfPayloadDataStop = new OCMFPayloadData();
        ocmfPayloadDataStop.setRD(new ArrayList<>());

        Reading stopReading = new Reading();
        stopReading.setRV((double) 2);
        stopReading.setST("G");
        stopReading.setTX("B");
        stopReading.setTM("2018-07-24T13:23:04,000+0200 S");
        ocmfPayloadDataStop.getRD().add(stopReading);
        ocmfPayloadDataStop.setIS("1");

        OCMF ocmfStart = new OCMF(ocmfPayloadDataStart, null, null, null);
        OCMF ocmfStop = new OCMF(ocmfPayloadDataStop, null, null, null);

        OCMFVerifiedData start = new OCMFVerifiedData(ocmfStart, null, null);
        OCMFVerifiedData stop = new OCMFVerifiedData(ocmfStop, null, null);
        start.lawConform(stop);
    }

    @Test
    public void testOCMFLawConformStartContainsStop() throws RegulationLawException, ValidationException {
        expectedException.expect(OCMFValidationException.class);
        expectedException.expectMessage("OCMF entry marked as start contains a stop value");
        OCMFPayloadData ocmfPayloadDataStart = new OCMFPayloadData();
        ocmfPayloadDataStart.setRD(new ArrayList<>());

        Reading startReading = new Reading();
        startReading.setRV((double) 1);
        startReading.setST("G");
        startReading.setTX("L");
        startReading.setTM("2018-07-24T13:22:04,000+0200 S");
        ocmfPayloadDataStart.getRD().add(startReading);

        OCMFPayloadData ocmfPayloadDataStop = new OCMFPayloadData();
        ocmfPayloadDataStop.setRD(new ArrayList<>());

        Reading stopReading = new Reading();
        stopReading.setRV((double) 1);
        stopReading.setST("G");
        stopReading.setTX("E");
        stopReading.setTM("2018-07-24T13:22:04,000+0200 S");
        ocmfPayloadDataStop.getRD().add(stopReading);

        ocmfPayloadDataStop.setIS("1");
        OCMF ocmfStart = new OCMF(ocmfPayloadDataStart, null, null, null);
        OCMF ocmfStop = new OCMF(ocmfPayloadDataStop, null, null, null);

        OCMFVerifiedData start = new OCMFVerifiedData(ocmfStart, null, null);
        OCMFVerifiedData stop = new OCMFVerifiedData(ocmfStop, null, null);
        start.lawConform(stop);
    }

    @Test
    public void testOCMFLawConformStartGtStop() throws RegulationLawException, ValidationException {
        expectedException.expect(RegulationLawException.class);
        expectedException.expectMessage("Meter value of start is higher than meter value of stop");
        OCMFPayloadData ocmfPayloadDataStart = new OCMFPayloadData();
        ocmfPayloadDataStart.setRD(new ArrayList<>());

        Reading startReading = new Reading();
        startReading.setRV((double) 10);
        startReading.setRU("U");
        startReading.setST("G");
        startReading.setTX("B");
        startReading.setTM("2018-07-24T13:22:04,100+0200 S");
        startReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStart.getRD().add(startReading);

        OCMFPayloadData ocmfPayloadDataStop = new OCMFPayloadData();
        ocmfPayloadDataStop.setRD(new ArrayList<>());

        Reading stopReading = new Reading();
        stopReading.setRV((double) 1);
        stopReading.setST("G");
        stopReading.setTX("E");
        stopReading.setTM("2018-07-24T13:22:04,000+0200 S");
        stopReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStop.getRD().add(stopReading);

        ocmfPayloadDataStop.setIS("1");
        OCMF ocmfStart = new OCMF(ocmfPayloadDataStart, null, null, null);
        OCMF ocmfStop = new OCMF(ocmfPayloadDataStop, null, null, null);

        OCMFVerifiedData start = new OCMFVerifiedData(ocmfStart, null, null);
        OCMFVerifiedData stop = new OCMFVerifiedData(ocmfStop, null, null);
        start.lawConform(stop);
    }

    @Test
    public void testOCMFLawConformStartNewerStop() throws RegulationLawException, ValidationException {
        expectedException.expect(RegulationLawException.class);
        expectedException.expectMessage("Meter timestamp of start is after timestamp of stop");
        OCMFPayloadData ocmfPayloadDataStart = new OCMFPayloadData();
        ocmfPayloadDataStart.setRD(new ArrayList<>());

        Reading startReading = new Reading();
        startReading.setRV((double) 0.9);
        startReading.setRU("U");
        startReading.setST("G");
        startReading.setTX("B");
        startReading.setTM("2018-07-24T14:22:04,100+0200 S");
        startReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStart.getRD().add(startReading);

        OCMFPayloadData ocmfPayloadDataStop = new OCMFPayloadData();
        ocmfPayloadDataStop.setRD(new ArrayList<>());

        Reading stopReading = new Reading();
        stopReading.setRV((double) 1);
        stopReading.setST("G");
        stopReading.setTX("E");
        stopReading.setTM("2018-07-24T13:22:04,000+0200 S");
        stopReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStop.getRD().add(stopReading);

        ocmfPayloadDataStop.setIS("1");
        OCMF ocmfStart = new OCMF(ocmfPayloadDataStart, null, null, null);
        OCMF ocmfStop = new OCMF(ocmfPayloadDataStop, null, null, null);

        OCMFVerifiedData start = new OCMFVerifiedData(ocmfStart, null, null);
        OCMFVerifiedData stop = new OCMFVerifiedData(ocmfStop, null, null);
        start.lawConform(stop);
    }


    @Test
    public void testOCMFLawConformIdLevelStart() throws RegulationLawException, ValidationException {
        expectedException.expect(RegulationLawException.class);
        expectedException.expectMessage("Error on reading contract id");
        OCMFPayloadData ocmfPayloadDataStart = new OCMFPayloadData();
        ocmfPayloadDataStart.setRD(new ArrayList<>());
        ocmfPayloadDataStart.setIS("MISMATCH");

        Reading startReading = new Reading();
        startReading.setRV((double) 0.9);
        startReading.setRU("U");
        startReading.setST("G");
        startReading.setTX("B");
        startReading.setTM("2018-07-24T12:22:04,100+0200 S");
        startReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStart.getRD().add(startReading);

        OCMFPayloadData ocmfPayloadDataStop = new OCMFPayloadData();
        ocmfPayloadDataStop.setRD(new ArrayList<>());

        Reading stopReading = new Reading();
        stopReading.setRV((double) 1);
        stopReading.setST("G");
        stopReading.setTX("E");
        stopReading.setTM("2018-07-24T13:22:04,000+0200 S");
        stopReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStop.getRD().add(stopReading);

        ocmfPayloadDataStop.setIS("1");
        OCMF ocmfStart = new OCMF(ocmfPayloadDataStart, null, null, null);
        OCMF ocmfStop = new OCMF(ocmfPayloadDataStop, null, null, null);

        OCMFVerifiedData start = new OCMFVerifiedData(ocmfStart, null, null);
        OCMFVerifiedData stop = new OCMFVerifiedData(ocmfStop, null, null);
        start.lawConform(stop);
    }

    @Test
    public void testOCMFLawConformIdLevelStop() throws RegulationLawException, ValidationException {
        expectedException.expect(RegulationLawException.class);
        expectedException.expectMessage("Error on reading contract id");
        OCMFPayloadData ocmfPayloadDataStart = new OCMFPayloadData();
        ocmfPayloadDataStart.setRD(new ArrayList<>());

        Reading startReading = new Reading();
        startReading.setRV((double) 0.9);
        startReading.setRU("U");
        startReading.setST("G");
        startReading.setTX("B");
        startReading.setTM("2018-07-24T12:22:04,100+0200 S");
        startReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStart.getRD().add(startReading);

        OCMFPayloadData ocmfPayloadDataStop = new OCMFPayloadData();
        ocmfPayloadDataStop.setRD(new ArrayList<>());

        Reading stopReading = new Reading();
        stopReading.setRV((double) 1);
        stopReading.setST("G");
        stopReading.setTX("E");
        stopReading.setTM("2018-07-24T13:22:04,000+0200 S");
        stopReading.setRI("1-0:1.8.0");
        ocmfPayloadDataStop.getRD().add(stopReading);
        ocmfPayloadDataStop.setIS("MISMATCH");

        OCMF ocmfStart = new OCMF(ocmfPayloadDataStart, null, null, null);
        OCMF ocmfStop = new OCMF(ocmfPayloadDataStop, null, null, null);

        OCMFVerifiedData start = new OCMFVerifiedData(ocmfStart, null, null);
        OCMFVerifiedData stop = new OCMFVerifiedData(ocmfStop, null, null);
        start.lawConform(stop);
    }

    @Test
    public void testOCMFVerifiedData() throws RegulationLawException, ValidationException {


        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 1);
        r1.setRU("RU");
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 2);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");
        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }


    @Test
    public void testBuildResult1() throws RegulationLawException, ValidationException {


        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV(1.3457782);
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 I");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 2);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 R");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");
        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertEquals(2, data.getMeters().size());
        Assert.assertEquals(Meter.TimeSyncType.INFORMATIVE, data.getMeters().get(0).getTimeSyncType());
        Assert.assertEquals(Meter.TimeSyncType.REALTIME, data.getMeters().get(1).getTimeSyncType());

        Assert.assertEquals(1.3457782, data.getMeters().get(0).getValue(), 0);
        Assert.assertEquals(2, data.getMeters().get(1).getValue(), 0);
    }

    @Test
    public void testBuildResult2NullValues() throws RegulationLawException, ValidationException {


        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV(null);
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 I");
        r1.setRI("1-b:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 2);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 R");
        r2.setRI("1-b:1.8.0");

        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");
        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertEquals(2, data.getMeters().size());
        Assert.assertEquals(Meter.TimeSyncType.INFORMATIVE, data.getMeters().get(0).getTimeSyncType());
        Assert.assertEquals(Meter.TimeSyncType.REALTIME, data.getMeters().get(1).getTimeSyncType());

        Assert.assertEquals(Meter.TimeSyncType.REALTIME,Meter.getTimeSyncType(data.getMeters()));
        
        Assert.assertEquals(0, data.getMeters().get(0).getValue(), 0);
        Assert.assertEquals(2, data.getMeters().get(1).getValue(), 0);
    }

    @Test
    public void test_law_check_to_much_start() throws RegulationLawException, ValidationException {

        expectedException.expectMessage("start values");
        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 1);
        r1.setRU("U");
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 2);
        r2.setST("G");
        r2.setTX("B");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }

    @Test
    public void test_law_check_no_start() throws RegulationLawException, ValidationException {

        expectedException.expectMessage("no start values");
        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r2 = new Reading();
        r2.setRV((double) 2);
        r2.setRU("U");
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }


    @Test
    public void test_law_check_to_much_stop() throws RegulationLawException, ValidationException {

        expectedException.expectMessage("stop values");
        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 1);
        r1.setRU("U");
        r1.setST("G");
        r1.setTX("E");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 2);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }

    @Test
    public void test_law_check_no_stop() throws RegulationLawException, ValidationException {

        expectedException.expectMessage("no stop values");
        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r2 = new Reading();
        r2.setRV((double) 2);
        r2.setRU("U");
        r2.setST("G");
        r2.setTX("B");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");


        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }


    @Test
    public void test_law_start_meter_more_than_stop() throws RegulationLawException, ValidationException {

        expectedException.expectMessage("higher");
        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 15);
        r1.setRU("U");
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 5);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }

    @Test
    public void test_law_contract_status_error() throws RegulationLawException, ValidationException {

        expectedException.expectMessage("contract id");
        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 1);
        r1.setRU("U");
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 5);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("UNKNOWN");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }

    @Test
    public void test_law_meter_error_code() throws RegulationLawException, ValidationException {

        expectedException.expectMessage("Meter error code present");
        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 1);
        r1.setRU("U");
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 5);
        r2.setST("H");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }

    @Test
    public void test_law_time_synchron() throws RegulationLawException, ValidationException {

        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 1);
        r1.setRU("U");
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 5);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 T");
        r2.setRI("1-0:1.8.0");

        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }

    @Test
    public void test_law_ok() throws RegulationLawException, ValidationException {

        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 1);
        r1.setRU("U");
        r1.setST("G");
        r1.setTX("B");
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 5);
        r2.setST("G");
        r2.setTX("E");
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRI("1-0:1.8.0");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertTrue(data.checkLawIntegrityForTransaction());
    }

    @Test
    public void test_kwh_readings() throws RegulationLawException, ValidationException {

        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 10010);
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRU("kWh");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 56789056);
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRU("kWh");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");


        Reading r3 = new Reading();
        r3.setRV((double) 0);
        r3.setTM("2018-07-24T13:23:04,000+0200 S");
        r3.setRU("kWh");
        ocmfPayloadData.getRD().add(r3);
        ocmfPayloadData.setIS("1");

        Reading r4 = new Reading();
        r4.setRV((double) 1);
        r4.setTM("2018-07-24T13:23:04,000+0200 S");
        r4.setRU("kWh");
        ocmfPayloadData.getRD().add(r4);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertEquals(4, data.getMeters().size());
        Assert.assertEquals(10010, data.getMeters().get(0).getValue(), 0);
        Assert.assertEquals(56789056, data.getMeters().get(1).getValue(), 0);
        Assert.assertEquals(0, data.getMeters().get(2).getValue(), 0);
        Assert.assertEquals(1, data.getMeters().get(3).getValue(), 0);
    }

    @Test
    public void test_wh_readings() throws RegulationLawException, ValidationException {

        OCMFPayloadData ocmfPayloadData = new OCMFPayloadData();
        ocmfPayloadData.setRD(new ArrayList<>());

        Reading r1 = new Reading();
        r1.setRV((double) 10010);
        r1.setTM("2018-07-24T13:22:04,000+0200 S");
        r1.setRU("Wh");
        ocmfPayloadData.getRD().add(r1);

        Reading r2 = new Reading();
        r2.setRV((double) 56789056);
        r2.setTM("2018-07-24T13:23:04,000+0200 S");
        r2.setRU("Wh");
        ocmfPayloadData.getRD().add(r2);
        ocmfPayloadData.setIS("1");


        Reading r3 = new Reading();
        r3.setRV((double) 0);
        r3.setTM("2018-07-24T13:23:04,000+0200 S");
        r3.setRU("Wh");
        ocmfPayloadData.getRD().add(r3);
        ocmfPayloadData.setIS("1");

        Reading r4 = new Reading();
        r4.setRV((double) 1);
        r4.setTM("2018-07-24T13:23:04,000+0200 S");
        r4.setRU("Wh");
        ocmfPayloadData.getRD().add(r4);
        ocmfPayloadData.setIS("1");

        Reading r5 = new Reading();
        r5.setRV((double) 1);
        r5.setTM("2018-07-24T13:23:04,000+0200 S");
        r5.setRU("kWh");
        ocmfPayloadData.getRD().add(r5);
        ocmfPayloadData.setIS("1");

        OCMF ocmf = new OCMF(ocmfPayloadData, "", null, "");

        OCMFVerifiedData data = new OCMFVerifiedData(ocmf, "", "");
        Assert.assertEquals(5, data.getMeters().size());
        Assert.assertEquals(10.010, data.getMeters().get(0).getValue(), 0);
        Assert.assertEquals(56789.056, data.getMeters().get(1).getValue(), 0);
        Assert.assertEquals(0, data.getMeters().get(2).getValue(), 0);
        Assert.assertEquals(0.001, data.getMeters().get(3).getValue(), 0);
        Assert.assertEquals(1, data.getMeters().get(4).getValue(), 0);
    }
}
