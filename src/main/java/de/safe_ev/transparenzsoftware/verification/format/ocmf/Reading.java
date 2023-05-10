package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import java.beans.Transient;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.xml.Meter.TimeSyncType;

public abstract class Reading {


    /**
     * Time string with synchronicity as last character of the string
     * ISO8601 date format + 1 character
     */
    protected String TM;
    /**
     * Transaction code can be null
     */
    protected String TX;

    /**
     * Reading value (meter value)
     */
    protected String RV;
    /**
     * Reading Identification (OBIS Code)
     */
    protected String RI;

    /**
     * Reading Unit e.g. kWh
     */
    protected String RU;

    /**
     * Shortcode of the status of the meter
     * <p>
     * Valid values are
     * - N NOT_PRESENT
     * - G OK
     * - T TIMEOUT
     * - D DISCONNECTED
     * - R NOT_FOUND
     * - M MANIPULATED
     * - X EXCHANGED
     * - I INCOMPATIBLE
     * - O OUT OF RANGE
     * - S SUBSTIUTE
     * - E OTHER_ERROR
     * - F READ_ERROR
     */
    private String ST;

    /**
     * Parses the timestamp out of the TM field
     *
     * @return OffsetDateTime object or null if it could not be parsed
     */
    public OffsetDateTime getTimestamp() {
        if (TM == null) {
            return null;
        }
        String[] splitted = TM.split(" ");
        if (splitted.length < 2) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSSZ");
            return OffsetDateTime.parse(splitted[0], formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Calculates the synchronicity code out of the timestamp field
     * - U - unknown
     * - I - informative
     * - S - synchron
     *
     * @return synchronicity code
     */
    public String getTimeSynchronicity() {
        if (TM == null) {
            return null;
        }
        String[] splitted = TM.split(" ");
        if (splitted.length < 2) {
            return null;
        }
        return splitted[1];
    }

    public TimeSyncType getTimeSyncType(){
        String syncr = getTimeSynchronicity();
        TimeSyncType result = TimeSyncType.INFORMATIVE;
        if (syncr != null) {
	        switch(this.getTimeSynchronicity().toUpperCase()) {
	        case "I":
	            result = TimeSyncType.INFORMATIVE;
	            break;
	        case "S":
	            result = TimeSyncType.SYNCHRONIZED;
	            break;
	        case "R":
	            result = TimeSyncType.REALTIME;
	            break;
	        case "U":
	        default:
	        
	       }
	    }
        return result;
    }

    public String getLabelForTimeFlag(){
        if(this.getTimeSynchronicity() == null){
            return null;
        }
        String labelForFlag = "app.verify.ocmf.timesynchronicity.unknown";
        switch(this.getTimeSynchronicity()) {
            case "I":
                labelForFlag = "app.verify.ocmf.timesynchronicity.informative";
                break;
            case "S":
                labelForFlag = "app.verify.ocmf.timesynchronicity.synchronised";
                break;
            case "R":
                labelForFlag = "app.verify.ocmf.timesynchronicity.relative";
                break;
            case "U":
            default:
        }
        return labelForFlag;
    }

    public String getTM() {
        return TM;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }

    public String getTX() {
        return TX;
    }

    public void setTX(String TX) {
        this.TX = TX;
    }

    public Double getRV() {
    	if (RV == null) return null;
        return Double.parseDouble(RV);
    }

    public void setRV(Double RV) {
        this.RV = (RV == null) ? null : Double.toString(RV);
    }

    public String getRI() {
        return RI;
    }

    public void setRI(String RI) {
        this.RI = RI;
    }

    public String getRU() {
        return RU;
    }

    public void setRU(String RU) {
        this.RU = RU;
    }

    public String getST() {
        return ST;
    }

    public void setST(String ST) {
        this.ST = ST;
    }

    public boolean isStartTransaction() {
        if (getTX() != null && getTX().trim().equals("B")) {
            return true;
        }
        return false;
    }

    public boolean isStopTransaction() {
        List<String> stopCodes = Arrays.asList("E", "L", "R", "A", "P");
        if (getTX() != null && stopCodes.contains(getTX().trim())) {
            return true;
        }
        return false;
    }


    public Double getEI(){
        return null;
    }

    public String getEF(){
        return null;
    }

	public int getRVDigits() {
		if (RV == null || RV.length() == 0) return -1;
		int k = RV.indexOf('.');
		int l = RV.length();
		if (k < 0) return 3; // no digits
		l -= k;
		switch (l) {
		case 1: return 3;
		case 2: return 2; // one digit
		case 3: return 1; // two digits
		case 4: return 0; // three digits
		case 5: return -1; // four digits
		case 6: return -2; // five digits
		}
		return -1; // default;
	}
}
