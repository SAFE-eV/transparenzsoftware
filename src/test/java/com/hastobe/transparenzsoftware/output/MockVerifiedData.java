package com.hastobe.transparenzsoftware.output;

import com.hastobe.transparenzsoftware.gui.views.helper.DetailsList;
import com.hastobe.transparenzsoftware.verification.RegulationLawException;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.VerifiedData;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

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
