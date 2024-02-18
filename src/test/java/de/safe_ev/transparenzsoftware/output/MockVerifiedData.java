package de.safe_ev.transparenzsoftware.output;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;
import de.safe_ev.transparenzsoftware.verification.xml.VerifiedData;

public class MockVerifiedData extends VerifiedData {

    double value = 0;
    OffsetDateTime timestamp;
    DetailsList additionalData;
    List<Meter> meters;

    @Override
    public List<Meter> getMeters() {
        return meters;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public String getPublicKey() {
        return null;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public DetailsList getAdditionalData() {
        return additionalData;
    }

    @Override
    public boolean lawConform(VerifiedData stopValue) throws RegulationLawException {
        return true;
    }


}
