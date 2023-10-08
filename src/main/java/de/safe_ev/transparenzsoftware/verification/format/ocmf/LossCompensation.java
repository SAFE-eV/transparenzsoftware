package de.safe_ev.transparenzsoftware.verification.format.ocmf;

public class LossCompensation {

    /**
     * Loss compensation Naming: This parameter is optional. A meter can use this
     * value for adding a traceability text for justifying cable loss
     * characteristics.
     */
    protected String LN;

    /**
     * Loss compensation Identification: This parameter is optional. A meter can use
     * this value for adding a traceability ID number for justifying cable loss
     * characteristics from a lookup table specified in meter's documentation.
     */
    protected String LI;
    /**
     * Loss compensation cable Resistance: This parameter is mandatory. A meter
     * shall use this value for specifying the cable resistance value used in cable
     * Loss compensation computation.
     */
    protected String LR;
    /**
     * Loss compensation cable resistance Unit: This parameter is mandatory. A meter
     * shall use this field for specifying the unit of cable resistance value given
     * by LR field used in cable loss compensation computation. The unit of this
     * value can be traced in OCMF format in addition to meter's documentation.
     * Allowed values are milliohm or microohm; LU value for milliohm shall be
     * "mOhm", LU value for microohm shall be "uOhm".
     */
    protected String LU;

}
