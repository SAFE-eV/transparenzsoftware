package com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes;

import com.hastobe.transparenzsoftware.verification.RegulationLawException;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EDLMennekesVerifiedDataTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = ValidationException.class)
    public void testLawConform() throws RegulationLawException, ValidationException {
        MennekesTestData td = new MennekesTestData(1, 1, 1, 2, 1, 2);
        td.lawConform(null);
    }

    @Test
    public void testLawIntegrity1() throws RegulationLawException {
        MennekesTestData td = new MennekesTestData(1, 1, 1, 2, 1, 2);
        Assert.assertTrue(td.checkLawIntegrityForTransaction());
    }

    @Test
    public void testLawIntegrity_wrong_event_counter() throws RegulationLawException {
        expectedException.expectMessage("Event counter");
        MennekesTestData td = new MennekesTestData(2, 1, 1, 2, 1, 2);
        td.checkLawIntegrityForTransaction();
    }

    @Test
    public void testLawIntegrity_meter_value() throws RegulationLawException {
        expectedException.expectMessage("Meter value");
        MennekesTestData td = new MennekesTestData(1, 1, 6, 2, 1, 2);
        td.checkLawIntegrityForTransaction();
    }

    @Test
    public void testLawIntegrity_pagination() throws RegulationLawException {
        expectedException.expectMessage("Pagination");
        MennekesTestData td = new MennekesTestData(1, 1, 1, 2, 4, 2);
        td.checkLawIntegrityForTransaction();
    }

    public class MennekesTestData extends EDLMennekesVerifiedData {

        public MennekesTestData(int eventCounterStart, int eventCounterEnd, double meterStart, double meterStop, int paginationStart, int paginationEnd){
            setEventCounterStart(eventCounterStart);
            setEventCounterEnd(eventCounterEnd);
            setMeterStart(meterStart);
            setMeterStop(meterStop);
            setPaginationStart(paginationStart);
            setPaginationEnd(paginationEnd);
        }
    }
}
