package de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.format.sml.SMLSignature;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMLVerifiedData extends de.safe_ev.transparenzsoftware.verification.format.sml.SMLVerifiedData {

    public SMLVerifiedData(SMLSignature smlSignature, VerificationType edl40Sig, EncodingType plain, String toFormattedHex) {
        super(smlSignature, edl40Sig, plain, toFormattedHex);
    }

    @Override
    public String getCustomerId() {
        return hexRepresentation(super.getCustomerId());
    }

    @Override
    public String getServerId() {
        return hexRepresentation(super.getServerId());
    }

    private String hexRepresentation(String format){
        if(format != null && Utils.hexToAscii(format).matches("[A-Za-z0-9!#/ ]*")){
            return String.format("%s (%s)", format, Utils.hexToAscii(format));
        } else {
            return format;
        }

    }
}
