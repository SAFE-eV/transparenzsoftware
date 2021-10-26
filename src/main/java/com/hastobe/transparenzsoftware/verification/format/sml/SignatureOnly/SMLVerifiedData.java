package com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly;


import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLSignature;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMLVerifiedData extends com.hastobe.transparenzsoftware.verification.format.sml.SMLVerifiedData {

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
