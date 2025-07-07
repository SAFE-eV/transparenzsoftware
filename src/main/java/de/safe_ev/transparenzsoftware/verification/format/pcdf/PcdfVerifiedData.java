package de.safe_ev.transparenzsoftware.verification.format.pcdf;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import de.safe_ev.transparenzsoftware.gui.views.helper.DetailsList;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.RegulationLawException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;
import de.safe_ev.transparenzsoftware.verification.xml.Meter.TimeSyncType;
import de.safe_ev.transparenzsoftware.verification.xml.Meter.Type;
import de.safe_ev.transparenzsoftware.verification.xml.VerifiedData;

public class PcdfVerifiedData extends VerifiedData {

    private final String publicKey;

    private final List<Meter> meters;

    private final DetailsList addData;

    public PcdfVerifiedData(String pk, String chData) {
	publicKey = pk;
	meters = new ArrayList<Meter>();

	String cons = extractAttribute("RV", chData);
	cons = cons.replace("*kWh", "");

	addData = new DetailsList();

	final String startTime = extractAttribute("ST", chData);

	addData.put(Translator.get("app.verify.pcdf.charge.start"), timeNormalizer(startTime));

	final String stopTime = extractAttribute("CT", chData);

	addData.put(Translator.get("app.verify.pcdf.charge.current.time"), timeNormalizer(stopTime));

	final double consDou = Double.parseDouble(cons);

	final String tsStr = extractAttribute("CT", chData);
	final OffsetDateTime odt = OffsetDateTime.parse(timeNormalizer(startTime) + "+00:00");

	final Meter first = new Meter(0.0, odt, Type.START, TimeSyncType.REALTIME, 0, false);
	meters.add(first);

	final OffsetDateTime odt2 = OffsetDateTime.parse(timeNormalizer(stopTime) + "+00:00");
	final Meter met = new Meter(consDou, odt2, Type.STOP, TimeSyncType.REALTIME, 0, false);

	meters.add(met);

	final String chDur = extractAttribute("CD", chData);

	addData.put(Translator.get("app.verify.pcdf.charge.duration"), durationNormalizer(chDur));

	final String billValid = extractAttribute("BV", chData);

	if (billValid.equals("1")) {
	    addData.put(Translator.get("app.verify.pcdf.charge.billing.validity"),
		    Translator.get("app.verify.pcdf.charge.yes"));
	} else {
	    addData.put(Translator.get("app.verify.pcdf.charge.billing.validity"),
		    Translator.get("app.verify.pcdf.charge.no"));
	}

	final String stopValid = extractAttribute("SP", chData);

	if (stopValid.equals("1")) {
	    addData.put(Translator.get("app.verify.pcdf.charge.stop.time"),
		    Translator.get("app.verify.pcdf.charge.yes"));
	} else {
	    addData.put(Translator.get("app.verify.pcdf.charge.stop.time"),
		    Translator.get("app.verify.pcdf.charge.no"));
	}

	final String chCount = extractAttribute("CSC", chData);

	addData.put(Translator.get("app.verify.pcdf.charge.counter"), chCount);

	final String cs = extractAttribute("CS", chData);

	addData.put(Translator.get("app.verify.pcdf.charge.software"), cs);

	final String hw = extractAttribute("HW", chData);

	addData.put(Translator.get("app.verify.pcdf.charge.hardware"), hw);

	final String dt = extractAttribute("DT", chData);
	if (dt.equals("0")) {
	    addData.put(Translator.get("app.verify.pcdf.charge.dcmeter.type"), "PES DCMeter EU");
	} else {
	    addData.put(Translator.get("app.verify.pcdf.charge.dcmeter.type"),
		    Translator.get("app.verify.pcdf.unknown.dcmeter.type"));
	}

	final String si = extractAttribute("SI", chData);
	final String[] vll = si.split("\\*");
	addData.put(Translator.get("app.verify.pcdf.charge.userid"), vll[0]);
	addData.put(Translator.get("app.verify.pcdf.charge.userid.type"), getTagType(vll[1]));
	addData.put(Translator.get("app.verify.pcdf.charge.txid"), vll[2]);
    }

    private String getTagType(String intType) {
	final int intInt = Integer.parseInt(intType);
	if (intInt == 1) {
	    return Translator.get("app.verify.pcdf.userid.type.rfid");
	}
	if (intInt == 2) {
	    return Translator.get("app.verify.pcdf.userid.type.emaid");
	}
	if (intInt == 3) {
	    return Translator.get("app.verify.pcdf.userid.type.creditcard");
	}
	if (intInt == 4) {
	    return Translator.get("app.verify.pcdf.userid.type.remote");
	}
	if (intInt == 5) {
	    return Translator.get("app.verify.pcdf.userid.type.nfc");
	}
	return Translator.get("app.verify.pcdf.userid.type.unknown");
    }

    private String timeNormalizer(String timeInfo) {
	timeInfo = "20" + timeInfo;

	timeInfo = timeInfo.substring(0, 4) + "-" + timeInfo.substring(4, 6) + "-" + timeInfo.substring(6, 8) + "T"
		+ timeInfo.substring(8, 10) + ":" + timeInfo.substring(10, 12) + ":" + timeInfo.substring(12);

	return timeInfo;
    }

    private String durationNormalizer(String durInfo) {
	durInfo = durInfo + "sec";
	if ((durInfo.charAt(0) == '0') && (durInfo.charAt(1) == '0')) {
	    durInfo = durInfo.substring(2);

	    if ((durInfo.charAt(0) == '0') && (durInfo.charAt(1) == '0')) {
		durInfo = durInfo.substring(2);
	    } else {
		durInfo = durInfo.substring(0, 2) + "min " + durInfo.substring(2);
	    }
	} else {
	    durInfo = durInfo.substring(0, 2) + "h " + durInfo.substring(2, 4) + "min " + durInfo.substring(4);
	}
	return durInfo;
    }

    private String extractAttribute(String attr, String chData) {
	final int pos = chData.indexOf("(" + attr + ":");

	if (pos != -1) {
	    final int pos1 = chData.indexOf(")", pos);
	    final String attrVal = chData.substring(pos + attr.length() + 2, pos1);
	    return attrVal;
	}
	return null;
    }

    @Override
    public List<Meter> getMeters() {
	// TODO Auto-generated method stub
	return meters;
    }

    @Override
    public String getFormat() {
	return VerificationType.PCDF.name();
    }

    @Override
    public String getPublicKey() {
	return publicKey;
    }

    @Override
    public String getEncoding() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public DetailsList getAdditionalData() {
	// TODO Auto-generated method stub
	return addData;
    }

    @Override
    public boolean lawConform(VerifiedData stopValue) throws RegulationLawException, ValidationException {
	// TODO Auto-generated method stub
	return false;
    }

}
