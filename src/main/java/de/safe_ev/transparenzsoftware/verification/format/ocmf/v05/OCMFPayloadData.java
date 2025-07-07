package de.safe_ev.transparenzsoftware.verification.format.ocmf.v05;

import java.util.List;

import de.safe_ev.transparenzsoftware.verification.format.ocmf.LossCompensation;

/**
 * Compatible until version 1
 */
public class OCMFPayloadData extends de.safe_ev.transparenzsoftware.verification.format.ocmf.OCMFPayloadData {

    /**
     * Identification status true or false
     */
    protected Boolean IS;
    /**
     * Identification level Valid values success codes: NONE, HEARSAY, TRUSTED,
     * VERIFIED, CERTIFIED, SECURE Valid values which are error codes: MISMATCH,
     * INVALID, OUTDATED, UNKNOWN
     */
    protected String IL;

    /**
     * Chargepoint id type
     */
    protected String CT;
    /**
     * Chargepoint id
     */
    protected String CI;

    /**
     * Information about the meter values, which share the basic fields of
     * OCMFPayloadData
     */
    protected List<Reading> RD;

    public Boolean getIS() {
	return IS;
    }

    public void setIS(Boolean IS) {
	this.IS = IS;
    }

    public String getIL() {
	return IL;
    }

    public void setIL(String IL) {
	this.IL = IL;
    }

    @Override
    public List<Reading> getRD() {
	return RD;
    }

    @Override
    public String getIdLevel() {
	return getIL();
    }

    @Override
    public String getIdStatus() {
	return getIS() != null ? getIS().toString() : null;
    }

    public void setRD(List<Reading> RD) {
	this.RD = RD;
    }

    public String getCT() {
	return CT;
    }

    public void setCT(String CT) {
	this.CT = CT;
    }

    public String getCI() {
	return CI;
    }

    public void setCI(String CI) {
	this.CI = CI;
    }

    private LossCompensation LC;

    @Override
    public LossCompensation getLC() {
	return LC;
    }

    public void setLC(LossCompensation lC) {
	LC = lC;
    }

}
