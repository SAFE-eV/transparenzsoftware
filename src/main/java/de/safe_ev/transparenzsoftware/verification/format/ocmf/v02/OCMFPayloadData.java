package de.safe_ev.transparenzsoftware.verification.format.ocmf.v02;

import java.util.List;

public class OCMFPayloadData extends de.safe_ev.transparenzsoftware.verification.format.ocmf.OCMFPayloadData {


    private String VI;
    private String VV;

    /**
     * Identification status
     * Valid values success codes: NONE, HEARSAY, TRUSTED, VERIFIED, CERTIFIED, SECURE
     * Valid values which are error codes: MISMATCH, INVALID, OUTDATED, UNKNOWN
     */
    protected String IS;

    /**
     * Information about the meter values, which share the basic fields
     * of OCMFPayloadData
     */
    protected List<Reading> RD;

    public String getVI() {
        return VI;
    }

    public void setVI(String VI) {
        this.VI = VI;
    }

    public String getVV() {
        return VV;
    }

    public void setVV(String VV) {
        this.VV = VV;
    }

    public List<Reading> getRD() {
        return RD;
    }

    public String getIS() {
        return IS;
    }

    public void setIS(String IS) {
        this.IS = IS;
    }

    @Override
    public String getIdLevel() {
        return getIS();
    }

    @Override
    public String getIdStatus() {
        return null;
    }

    public void setRD(List<Reading> RD) {
        this.RD = RD;
    }

    @Override
    public String getGI() {
        return getVI();
    }

    @Override
    public String getGV() {
        return getVV();
    }
}
