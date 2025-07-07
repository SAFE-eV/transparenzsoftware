package de.safe_ev.transparenzsoftware.verification.xml;

import jakarta.xml.bind.annotation.*;

import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;

import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class VerifiedData {

    @XmlTransient
    public abstract List<Meter> getMeters();

    @XmlAttribute
    public abstract String getFormat();

    public abstract String getPublicKey();

    @XmlAttribute
    public abstract String getEncoding();

    @XmlTransient
    public abstract DetailsList getAdditionalData();

    /**
     * Indicates if a Eichrechts necessary data change
     * has happen and the transaction is not valid
     * in perspective of the Eichrecht (this should
     * be called on the start value)
     *
     * @param stopValue other verified data it is assumed that this method is called on the start value
     * @return true if
     * @throws RegulationLawException if something is not ok with this value
     * @throws ValidationException if data cannot be compared or similar problems
     */
    public abstract boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException;
}
