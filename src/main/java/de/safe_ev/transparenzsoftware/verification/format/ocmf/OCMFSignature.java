package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;

public class OCMFSignature {

    /**
     * Signature Encoding
     * - values hex (default)
     * - base64
     */
    private String SE;
    /**
     * Signature mime type
     * - default application/x-der
     */
    private String SM;
    /**
     * Signature algorithm used
     */
    private String SA;

    /**
     * Actual signature data
     */
    private String SD;

    public String getSE() {
        return SE;
    }

    public EncodingType getEncoding() {
        return SE != null ? EncodingType.fromCode(SE) : EncodingType.HEX;
    }

    public void setSE(String SE) {
        this.SE = SE;
    }

    public String getSM() {
        return SM;
    }

    public String getSignatureMimeType(){
        return getSM() == null ? "application/x-der" : getSM();
    }

    public void setSM(String SM) {
        this.SM = SM;
    }

    public String getSA() {
        return SA;
    }

    public String getCurve() throws ValidationException {
        return getSplittedSA()[1];
    }

    public String getHashing() throws ValidationException {
        return getSplittedSA()[2];
    }

    private String[] getSplittedSA() throws OCMFValidationException {
        String type = SA == null ? "ECDSA-secp256r1-SHA256" : SA;
        String[] splitted = type.split("-");
        if(splitted.length != 3){
            throw new OCMFValidationException("Invalid signature algorithm for OCMF", "error.ocmf.invalid.signaturealgorithm");
        }
        splitted[1] = splitted[1].toLowerCase();
        if (splitted[1].indexOf("brainpoolp") < 0) {
        	splitted[1] = splitted[1].replaceAll("brainpool", "brainpoolp");
        }
        return splitted;
    }

    public void setSA(String SA) {
        this.SA = SA;
    }

    public String getSD() {
        return SD;
    }

    public void setSD(String SD) {
        this.SD = SD;
    }

    public String getSignatureMethod() throws OCMFValidationException {
        return getSplittedSA()[0];
    }
}
