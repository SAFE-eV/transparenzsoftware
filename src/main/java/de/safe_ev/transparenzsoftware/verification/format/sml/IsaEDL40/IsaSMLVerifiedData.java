package de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.xml.LocalDateTimeAdapter;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;
import de.safe_ev.transparenzsoftware.verification.xml.VerifiedData;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "verifiedData")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsaSMLVerifiedData extends VerifiedData {

    private final VerificationType verificationType;
    private final EncodingType encodingType;
    private final String publicKey;

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
    private String endMessageDescriptiveText = null;

    public IsaSMLVerifiedData(IsaEDL40Signature smlSignature, VerificationType verificationType,
	    EncodingType encodingType, String publicKey) {
	this.verificationType = verificationType;
	this.encodingType = encodingType;
	this.publicKey = publicKey;
	if (smlSignature != null) {
	    pagination = new BigInteger(Utils.reverseByteOrder(smlSignature.getPagination())).intValue();
	    String cntxDesc = " (Start der Ladetransaktion)";
	    detailsHeader = "Ablesezeitpunkt (Beginn der Ladetransaktion):";
	    endMessageDescriptiveText = "Z&auml;hlerstand zum Ablesezeitpunkt (Beginn der Ladetransaktion)";
	    final String getListName = Utils.toFormattedHex(smlSignature.getListNameOfRes());
	    if (getListName.equals("81 80 81 62 01 FF")) {
		cntxDesc = " (Update w&auml;hrend der Ladetransaktion)";
		detailsHeader = "Ablesezeitpunkt (Update w&auml;hrend der Ladetransaktion):";
		endMessageDescriptiveText = "Z&auml;hlerstand zum Ablesezeitpunkt (W&auml;hrend der Ladetransaktion)";
	    } else if (getListName.equals("81 80 81 62 02 FF")) {
		cntxDesc = " (Ende der Ladetransaktion)";
		detailsHeader = "Ablesezeitpunkt (Ende der Ladetransaktion):";
		endMessageDescriptiveText = "Z&auml;hlerstand zum Ablesezeitpunkt (Ende der Ladetransaktion)";
	    }

	    context = Utils.toFormattedHex(smlSignature.getListNameOfRes()) + cntxDesc;
	    serverId = Utils.toFormattedHex(smlSignature.getServerId());
	    contractId = Utils.toFormattedHex(Utils.trimPaddingAtEnd(smlSignature.getContractId()));

	    startEcObisId = Utils.toFormattedHex(smlSignature.getStartEcObisId());
	    startEcStatus = Utils.toFormattedHex(smlSignature.getStartEcStatus());
	    final long sEcValue = Utils.bytesToLong(smlSignature.getStartEcValue());
	    // long sEcValue=smlSignature.getStartEcValueAsLong();
	    startEcValue = Long.toString(sEcValue);
	    startEcTime = smlSignature.getTimestampContractIdAsDate();
	    startEcUnit = Utils.toFormattedHex(smlSignature.getStartEcUnit());
	    startEcScaler = Utils.toFormattedHex(smlSignature.getStartEcScaler());

	    actualEcTime = smlSignature.getTimestampAsDate();
	    actualEcObisId = Utils.toFormattedHex(smlSignature.getActualEcObisId());
	    actualEcStatus = Utils.toFormattedHex(smlSignature.getActualEcStatus());
	    actualEcUnit = Utils.toFormattedHex(smlSignature.getActualEcUnit());
	    actualEcScaler = Utils.toFormattedHex(smlSignature.getActualEcScaler());

	    meters = new ArrayList<>();
	    startEValue = Utils.bytesToLong(smlSignature.getStartEcValue());
	    actualEValue = Utils.bytesToLong(smlSignature.getActualEcValue());
	    // calculate kWh

	    startEValueHexString = Utils.toFormattedHex(smlSignature.getStartEcValue());
	    startEValue = startEValue != 0 ? (startEValue * Math.pow(10, smlSignature.getStartEcScaler()) / 1000.0) : 0;
	    actualEValueHexString = Utils.toFormattedHex(smlSignature.getActualEcValue());
	    actualEValue = actualEValue != 0 ? (actualEValue * Math.pow(10, smlSignature.getActualEcScaler()) / 1000.0)
		    : 0;
	    final Meter startMeter = new Meter(startEValue, startEcTime, Meter.Type.START,
		    Meter.TimeSyncType.INFORMATIVE, smlSignature.getStartEcScaler(), false);
	    startMeter.setDescriptiveMessageText("Z&auml;hlerstand zu Beginn der Ladetransaktion");
	    meters.add(startMeter);
	    final Meter stopMeter = new Meter(actualEValue, actualEcTime,
		    getListName.equals("81 80 81 62 00 FF") || getListName.equals("81 80 81 62 02 FF") ? Meter.Type.STOP
			    : Meter.Type.UPDATE,
		    Meter.TimeSyncType.INFORMATIVE, smlSignature.getActualEcScaler(), false);
	    stopMeter.setDescriptiveMessageText(endMessageDescriptiveText);
	    meters.add(stopMeter);
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
    public DetailsList getAdditionalData() {
	final DetailsList additionalData = new DetailsList();
	additionalData.put("Datensatznummer:", pagination);
	additionalData.put("OBIS-ID Datensatzkontext", context);
	additionalData.put("ServerId:", getServerId());
	additionalData.put("Kontrakt ID/Kundenmerkmal:", getContractId());

	additionalData.put("Beginn der Ladetransaktion", " ");
	additionalData.put("- OBIS-ID:", startEcObisId);
	additionalData.put("- Ablesezeitpunkt:", LocalDateTimeAdapter.formattedDateTime(startEcTime.toLocalDateTime()));
	additionalData.put("- Z&auml;hlerstand:", String.format("%.4f kWh", startEValue));
	additionalData.put("- Z&auml;hlerstand (HEX):", startEValueHexString);
	additionalData.put("- Status:", startEcStatus);

	additionalData.put(detailsHeader, "  ");
	additionalData.put("- OBIS-ID: ", actualEcObisId);
	additionalData.put("- Ablesezeitpunkt: ",
		LocalDateTimeAdapter.formattedDateTime(actualEcTime.toLocalDateTime()));
	additionalData.put("- Z&auml;hlerstand: ", String.format("%.4f kWh", actualEValue));
	additionalData.put("- Z&auml;hlerstand (HEX): ", actualEValueHexString);
	additionalData.put("- Status: ", actualEcStatus);

	return additionalData;
    }

    @Override
    public boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException {

	return true;
    }

    public String getServerId() {
	return serverId;
    }

    public String getContractId() {
	return contractId;
    }
}
