package de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes;

import de.safe_ev.transparenzsoftware.TestUtils;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.XMLReader;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.embedded.*;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class XMLReaderTest {

    @Test
    public void readXML() throws ValidationException {
        URL url = this.getClass().getResource("/xml/test_mennekes_full.xml");
        File testfile = new File(url.getFile());
        String testData = TestUtils.readFile(testfile);
        XMLReader xmlReader = new XMLReader();
        Billing billing = xmlReader.readFromString(testData);

        Assert.assertNotNull(billing);
        Customer customer = billing.getCustomer();

        Assert.assertNotNull(customer);
        Assert.assertEquals(11, customer.getCustomerId());
        Assert.assertEquals("ptb ptb", customer.getName1());
        Assert.assertEquals("Bundesallee 100", customer.getStreet());
        Assert.assertEquals("Braunschweig", customer.getTown());


        Supplier supplier = billing.getSupplier();
        Assert.assertNotNull(supplier);
        Assert.assertEquals("57399", supplier.getZipCode());
        Assert.assertEquals("MENNEKES Elektrotechnik GmbH & Co. KG", supplier.getName1());
        Assert.assertEquals("Aloys-Mennekes-Straße 1", supplier.getStreet());
        Assert.assertEquals("+49 27 23 / 41 - 1", supplier.getPhone());
        Assert.assertEquals("+49 27 23 / 41 - 214", supplier.getFax());
        Assert.assertEquals("Kirchhundem", supplier.getTown());
        Assert.assertEquals("info@MENNEKES.de", supplier.getMailAddress());
        Assert.assertEquals("test", supplier.getMailSubject());
        Assert.assertEquals("test", supplier.getMailBody());
        Assert.assertEquals("https://www.chargeupyourday.com/", supplier.getWebAddress());
        Assert.assertEquals("https://www.chargeupyourday.com/", supplier.getWebAddressLinkText());


        Assert.assertNotNull(billing.getBillingPeriods());
        Assert.assertEquals(1, billing.getBillingPeriods().size());
        BillingPeriod period = billing.getBillingPeriods().get(0);

        Assert.assertEquals("RE2017-01", period.getBillingNo());
        Assert.assertEquals("1.8.0", period.getObisCode());

        Assert.assertEquals("Lieferung Strom", period.getTitle());

        LocalDateTime dateTimePeriodStart = LocalDateTime.of(2018, Month.SEPTEMBER, 4, 12, 22, 14);
        OffsetDateTime periodStart = OffsetDateTime.of(dateTimePeriodStart, ZoneOffset.of("+02:00"));
        Assert.assertEquals(periodStart, period.getBegin());

        LocalDateTime dateTimePeriodEnd = LocalDateTime.of(2018, Month.SEPTEMBER, 4, 12, 26, 45);
        OffsetDateTime periodEnd = OffsetDateTime.of(dateTimePeriodEnd, ZoneOffset.of("+02:00"));
        Assert.assertEquals(periodEnd, period.getEnd());

        Assert.assertEquals(1, period.getChargingProcesses().size());
        ChargingProcess chargingProcess = period.getChargingProcesses().get(0);
        Assert.assertEquals("0901454D4800005BAE2F", chargingProcess.getServerId());
        Assert.assertEquals("6DACB9C5466A25B3EB9F6466B53457C84A27448B01A64A278C0A28DAC95F2B45DF39B79918A9A4D2E3551F3FE925D09D", chargingProcess.getPublicKey());
        Assert.assertEquals("DE*PWC*E00003*005", chargingProcess.getMeteringPoint());

        SiteAddress siteAddress = chargingProcess.getSiteAddress();
        Assert.assertNotNull(siteAddress);
        Assert.assertEquals("38116", siteAddress.getZipCode());
        Assert.assertEquals("Bundesallee 100", siteAddress.getStreet());
        Assert.assertEquals("Braunschweig", siteAddress.getTown());

        Assert.assertEquals("874AD0FE", chargingProcess.getCustomerIdent());

        LocalDateTime dateTimeCustomerIdent = LocalDateTime.of(2018, Month.SEPTEMBER, 4, 12, 22, 10);
        OffsetDateTime customerIdentTime = OffsetDateTime.of(dateTimeCustomerIdent, ZoneOffset.of("+02:00"));
        Assert.assertEquals(customerIdentTime, chargingProcess.getTimestampCustomerIdent());

        Measurement measurementStart = chargingProcess.getMeasurementStart();
        Assert.assertNotNull(measurementStart);

        LocalDateTime measurementStartDT = LocalDateTime.of(2018, Month.SEPTEMBER, 4, 12, 22, 10);
        OffsetDateTime measurementStartTime = OffsetDateTime.of(measurementStartDT, ZoneOffset.of("+02:00"));
        Assert.assertEquals(measurementStartTime, measurementStart.getTimestampCustomerIdent());

        LocalDateTime measurementStartDTTs = LocalDateTime.of(2018, Month.SEPTEMBER, 4, 12, 22, 14);
        OffsetDateTime measurementStartTimeTs = OffsetDateTime.of(measurementStartDTTs, ZoneOffset.of("+02:00"));
        Assert.assertEquals(measurementStartTimeTs, measurementStart.getTimestamp());

        Assert.assertEquals("55CB60BB5E8AC580516F6A3DA5D6BB0365FD9DC04D50BE53B79FD95A2A92D749C36839B0A507E63A48E0A956A6FEFDB5", measurementStart.getSignature());
        Assert.assertEquals(new Long(8), measurementStart.getEventCounter());
        Assert.assertEquals(new Integer(65800), measurementStart.getMeterStatus());
        Assert.assertEquals(new Long(519116), measurementStart.getValue());
        Assert.assertEquals(-1, measurementStart.getScaler());
        Assert.assertEquals(25, measurementStart.getPagination());
        Assert.assertEquals(74650, measurementStart.getSecondIndex());


        Measurement measurementEnd = chargingProcess.getMeasurementEnd();
        Assert.assertNotNull(measurementEnd);

        Assert.assertNull(measurementEnd.getTimestampCustomerIdent());

        LocalDateTime measurementEndDTTs = LocalDateTime.of(2018, Month.SEPTEMBER, 4, 12, 26, 45);
        OffsetDateTime measurementEndTimeTs = OffsetDateTime.of(measurementEndDTTs, ZoneOffset.of("+02:00"));
        Assert.assertEquals(measurementEndTimeTs, measurementEnd.getTimestamp());

        Assert.assertEquals("6574B62680B76639C2EF03CDEA34CCAC5633F3C7E08E5C251463F0DFED38B35A9675126DDA8F4B0CAB017B1A42C5EBDC", measurementEnd.getSignature());
        Assert.assertEquals(new Long(8), measurementEnd.getEventCounter());
        Assert.assertEquals(new Integer(65800), measurementEnd.getMeterStatus());
        Assert.assertEquals(new Long(520535), measurementEnd.getValue());
        Assert.assertEquals(-1, measurementEnd.getScaler());
        Assert.assertEquals(26, measurementEnd.getPagination());
        Assert.assertEquals(74921, measurementEnd.getSecondIndex());


    }
}
