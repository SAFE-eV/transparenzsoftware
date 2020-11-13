package com.hastobe.transparenzsoftware.verification.format.sml.IsaEDL40;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.RegulationLawException;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.format.sml.SMLSignature;
import com.hastobe.transparenzsoftware.verification.xml.LocalDateTimeAdapter;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import com.hastobe.transparenzsoftware.verification.xml.VerifiedData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.*;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsaSMLVerifiedData extends VerifiedData {

    private VerificationType verificationType;
    private EncodingType encodingType;
    private String publicKey;

    private int pagination;
    private String serverId;
    private String context;
    private String contractId;

    private String startEcObisId;
    private String startEcValue;
    private OffsetDateTime startEcTime;
    private String startEcStatus;
    private String startEcUnit;
    private String startEcScaler;

    private OffsetDateTime actualEcTime;
    private String actualEcObisId;
    private String actualEcStatus;
    private String actualEcUnit;
    private String actualEcScaler;

    private String startEValueHexString;
    private double startEValue;
    private String actualEValueHexString;
    private double actualEValue;
    private List<Meter> meters;

    private String detailsHeader;

    public IsaSMLVerifiedData(IsaEDL40Signature smlSignature, VerificationType verificationType, EncodingType encodingType, String publicKey)
    {
        this.verificationType = verificationType;
        this.encodingType = encodingType;
        this.publicKey = publicKey;
        if (smlSignature != null) {
            pagination = new BigInteger(Utils.reverseByteOrder(smlSignature.getPagination())).intValue();
            String cntxDesc = " (Start der Ladetransaktion)";
            detailsHeader = "Ablesezeitpunkt (Beginn der Ladetransaktion):";
            String getListName =Utils.toFormattedHex(smlSignature.getListNameOfRes());
            if(getListName.equals("81 80 81 62 01 FF")) {
                cntxDesc = " (Update während der Ladetransaktion)";
                detailsHeader = "Ablesezeitpunkt (Update während der Ladetransaktion):";
            }
            else if(getListName.equals("81 80 81 62 02 FF")) {
                cntxDesc = " (Ende der Ladetransaktion)";
                detailsHeader = "Ablesezeitpunkt (Ende der Ladetransaktion):";
            }

            context = Utils.toFormattedHex(smlSignature.getListNameOfRes())+cntxDesc;
            serverId = Utils.toFormattedHex(smlSignature.getServerId());
            contractId = Utils.toFormattedHex(Utils.trimPaddingAtEnd(smlSignature.getContractId()));

            startEcObisId = Utils.toFormattedHex(smlSignature.getStartEcObisId());
            startEcStatus = Utils.toFormattedHex(smlSignature.getStartEcStatus());
            long sEcValue= Utils.bytesToLong(smlSignature.getStartEcValue());
            //long sEcValue=smlSignature.getStartEcValueAsLong();
            startEcValue = Long.toString(sEcValue);
            startEcTime = smlSignature.getTimestampContractIdAsDate();
            startEcUnit = Utils.toFormattedHex(smlSignature.getStartEcUnit());
            startEcScaler = Utils.toFormattedHex(smlSignature.getStartEcScaler());

            actualEcTime = smlSignature.getTimestampAsDate();
            actualEcObisId=Utils.toFormattedHex(smlSignature.getActualEcObisId());
            actualEcStatus=  Utils.toFormattedHex(smlSignature.getActualEcStatus());
            actualEcUnit = Utils.toFormattedHex(smlSignature.getActualEcUnit());
            actualEcScaler = Utils.toFormattedHex(smlSignature.getActualEcScaler());

            meters = new ArrayList<>();
            startEValue = Utils.bytesToLong(smlSignature.getStartEcValue());
            actualEValue= Utils.bytesToLong(smlSignature.getActualEcValue());
            //calculate kWh

            startEValueHexString = Utils.toFormattedHex(smlSignature.getStartEcValue());
            startEValue = startEValue != 0 ? (startEValue * Math.pow(10, smlSignature.getStartEcScaler()) / 1000.0)  : 0;
            actualEValueHexString = Utils.toFormattedHex(smlSignature.getActualEcValue());
            actualEValue = actualEValue != 0 ? (actualEValue * Math.pow(10, smlSignature.getActualEcScaler()) / 1000.0)  : 0;
            if(getListName.equals("81 80 81 62 00 FF") || getListName.equals("81 80 81 62 02 FF"))
            {
                meters.add(new Meter(startEValue, startEcTime, Meter.Type.START, Meter.TimeSyncType.INFORMATIVE));
                meters.add(new Meter(actualEValue, actualEcTime, Meter.Type.STOP, Meter.TimeSyncType.INFORMATIVE));
            }
            else
            {
                meters.add(new Meter(actualEValue, actualEcTime, Meter.Type.UPDATE, Meter.TimeSyncType.INFORMATIVE));
            }
        }
    }

    @Override
    public List<Meter> getMeters() {
        return meters;
    }

    @Override
    public String getFormat() {
        return verificationType.name();
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public String getEncoding() {
        return encodingType.getCode();
    }

    @Override
    public LinkedHashMap<String, Object> getAdditionalData() {
        LinkedHashMap<String, Object> additionalData = new LinkedHashMap<String, Object>();
        additionalData.put("Datensatznummer:", pagination);
        additionalData.put("OBIS-ID Datensatzkontext", context);
        additionalData.put("ServerId:", getServerId());
        additionalData.put("Kontrakt ID/Kundenmerkmal:", getContractId());

        additionalData.put("Beginn der Ladetransaktion", " ");
        additionalData.put("OBIS-ID:", startEcObisId);
        additionalData.put("Ablesezeitpunkt:", LocalDateTimeAdapter.formattedDateTime(startEcTime.toLocalDateTime()));
        additionalData.put("Value:", String.format("%.4f kWh", startEValue));
        additionalData.put("Ablesewert (HEX):", startEValueHexString);
        additionalData.put("Status:", startEcStatus);

        additionalData.put(detailsHeader, "  ");
        additionalData.put("OBIS-ID: ", actualEcObisId);
        additionalData.put("Ablesezeitpunkt: ", LocalDateTimeAdapter.formattedDateTime(actualEcTime.toLocalDateTime()));
        additionalData.put("Actual Energy value:", String.format("%.4f kWh", actualEValue));
        additionalData.put("Ablesewert (HEX): ", actualEValueHexString);
        additionalData.put("Status: ", actualEcStatus);

        return additionalData;
    }

    @Override
    public boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException {

        return true;
    }



    public String getServerId() { return serverId; }
    public String getContractId() { return contractId; }
}
