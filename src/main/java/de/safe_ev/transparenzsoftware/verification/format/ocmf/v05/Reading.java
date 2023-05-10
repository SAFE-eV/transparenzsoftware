package de.safe_ev.transparenzsoftware.verification.format.ocmf.v05;

public class Reading extends de.safe_ev.transparenzsoftware.verification.format.ocmf.Reading {

    /**
     * Error flag (either E - energy, t - time)
     */
    private String EF;
    /**
     * Reading Current type (AC, DC)
     */
    private String RT;


    public String getEF() {
        return EF;
    }

    public void setEF(String EF) {
        this.EF = EF;
    }

    public String getRT() {
        return RT;
    }

    public void setRT(String RT) {
        this.RT = RT;
    }
}
