package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import java.beans.Transient;
import java.util.List;

public abstract class OCMFPayloadData {

    /**
     * Format version
     */
    protected String FV;
    /**
     * Vendor / Gateway Identification
     */
    protected String GI;
    /**
     * Vendor /Gateway Version
     */
    protected String GV;

    /**
     * Gateway serial
     */
    protected String GS;

    /**
     * Paginating of the whole dataset Format is `Code.Number` Valid codes are: F
     * (Fiscal), T (Transaction)
     */
    protected String PG;

    /**
     * Meter Vendor, optional field
     */
    protected String MV;

    /**
     * Meter Model, optional field
     */
    protected String MM;

    /**
     * Meter serial number, optional field
     */
    protected String MS;

    /**
     * Meter firmware version, optional field
     */
    protected String MF;

    /**
     * Charge controller firmware version, optional field
     */
    protected String CF;

    /**
     * Identification flags list of strings, valid codes see OCMF documentation They
     * are describing values which are used to identify the authentication media.
     */
    protected List<String> IF;

    /**
     * Identification-Type, defines what kind of id was read
     */
    protected String IT;

    /**
     * Identification id used (e.g. a uuid)
     */
    protected String ID;

    /**
     * Tarif Text field (0..250), optional.
     */
    private String TT;

    public String getFV() {
	return FV;
    }

    public void setFV(String FV) {
	this.FV = FV;
    }

    public String getGI() {
	return GI;
    }

    public void setGI(String GI) {
	this.GI = GI;
    }

    public String getGV() {
	return GV;
    }

    public void setGV(String GV) {
	this.GV = GV;
    }

    public String getPG() {
	return PG;
    }

    public void setPG(String PG) {
	this.PG = PG;
    }

    public String getMV() {
	return MV;
    }

    public void setMV(String MV) {
	this.MV = MV;
    }

    public String getMM() {
	return MM;
    }

    public void setMM(String MM) {
	this.MM = MM;
    }

    public String getMS() {
	return MS;
    }

    public void setMS(String MS) {
	this.MS = MS;
    }

    public String getMF() {
	return MF;
    }

    public void setMF(String MF) {
	this.MF = MF;
    }

    public String getCF() {
	return CF;
    }

    public void setCF(String CF) {
	this.CF = CF;
    }

    public String getIT() {
	return IT;
    }

    public void setIT(String IT) {
	this.IT = IT;
    }

    public List<String> getIF() {
	return IF;
    }

    public void setIF(List<String> IF) {
	this.IF = IF;
    }

    public String getID() {
	return ID;
    }

    public void setID(String ID) {
	this.ID = ID;
    }

    public String getTT() {
	return TT;
    }

    public void setTT(String tarifText) {
	TT = tarifText;
    }

    public abstract List<? extends Reading> getRD();

    public String getGS() {
	return GS;
    }

    public void setGS(String GS) {
	this.GS = GS;
    }

    /**
     * Checks whether the data contains a whole transaction (start and stop data)
     *
     * @return true if data contains complete transaction
     */
    public boolean containsCompleteTransaction() {
	if (!getPaginationContext().equals("T")) {
	    return false;
	}
	boolean startFound = false;
	boolean endFound = false;
	for (final Reading reading : getRD()) {
	    if (reading.getTX() == null) {
		continue;
	    }
	    if (reading.isStartTransaction()) {
		startFound = true;
	    }
	    if (reading.isStopTransaction()) {
		endFound = true;
	    }
	}
	return startFound && endFound;
    }

    /**
     * Returns either F(Fiscal) or T (Transaction)
     *
     * @return pagination code
     */
    public String getPaginationContext() {
	if (getPG() == null || getPG().length() <= 1) {
	    return null;
	}
	return getPG().substring(0, 1);
    }

    @Transient
    public abstract String getIdLevel();

    @Transient
    public abstract String getIdStatus();

    @Transient
    public LossCompensation getLC() {
	return null;
    };

}
