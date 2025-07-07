package de.safe_ev.transparenzsoftware.verification.xml;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Meter {

    private String descriptiveMessageText;

    private final double value;

    // necessary as jaxb will complain about the timestamp
    @XmlTransient
    private final OffsetDateTime timestamp;

    @XmlTransient
    private final Type type;

    @XmlTransient
    private TimeSyncType timeSyncType;

    @XmlTransient
    private int scaling = -1;

    @XmlTransient
    private boolean lawRelevant = true;

    @XmlTransient
    private boolean compensated;

    private Meter() {
	value = 0;
	timestamp = null;
	type = null;
    }

    /**
     *
     * @param value
     * @param timestamp
     * @param scale     0= 1/1000 1= 10/1000 2= 100/1000 3 = 1000/1000
     */
    public Meter(double value, OffsetDateTime timestamp, int scale, boolean compensated) {
	this(value, timestamp, null, TimeSyncType.INFORMATIVE, scale, compensated);
    }

    public Meter(double value, OffsetDateTime timestamp, int scale) {
	this(value, timestamp, null, TimeSyncType.INFORMATIVE, scale, false);
    }

    public Meter(double value, OffsetDateTime timestamp, Type type, TimeSyncType timeSyncType, int scaling,
	    boolean compensated) {
	this.value = value;
	this.timestamp = timestamp;
	this.type = type;
	this.timeSyncType = timeSyncType;
	if (scaling > 3) {
	    scaling = 3;
	}
	if (scaling < -2) {
	    scaling = -2;
	}
	this.scaling = scaling;
	this.compensated = compensated;
    }

    public void setDescriptiveMessageText(String text) {
	descriptiveMessageText = text;
    }

    public String getDescriptiveMessageText() {
	return descriptiveMessageText;
    }

    public double getValue() {
	return value;
    }

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    public OffsetDateTime getTimestamp() {
	return timestamp;
    }

    public String getAdditonalText() {
	final List<String> additionalData = new ArrayList<>();
	if (timeSyncType != null && !timeSyncType.equals(TimeSyncType.SYNCHRONIZED)) {
	    additionalData.add(Translator.get(timeSyncType.message));
	}
	return String.join(", ", additionalData);
    }

    /**
     * Builds the difference between the lowest and highest value
     *
     * @return
     */
    public static double getDifference(List<Meter> values) {
	if (values == null || values.size() < 2) {
	    return 0;
	}
	final double[] minmax = getMinMax(values);
	return minmax[1] - minmax[0];
    }

    private static double[] getMinMax(List<Meter> values) {
	double minimum = Double.MAX_VALUE;
	double max = Double.NEGATIVE_INFINITY;
	boolean startMarkerFound = false;
	boolean stopMarkerFound = false;
	for (final Meter meter : values) {
	    if (meter.type != null) {
		if (meter.type.equals(Type.START)) {
		    startMarkerFound = true;
		    minimum = meter.getValue();
		}
		if (meter.type.equals(Type.STOP)) {
		    stopMarkerFound = true;
		    max = meter.getValue();
		}
	    }
	    if (!startMarkerFound) {
		minimum = Math.min(minimum, meter.getValue());
	    }
	    if (!stopMarkerFound) {
		max = Math.max(max, meter.getValue());
	    }

	}
	return new double[] { minimum, max };
    }

    /**
     * Builds the time difference between the lowest and highest value
     *
     * @return
     */
    public static Duration getTimeDiff(List<Meter> values) {
	if (values == null || values.size() < 2) {
	    return Duration.ofMillis(0);
	}

	boolean startMarkerFound = false;
	boolean stopMarkerFound = false;
	OffsetDateTime minimumTime = null;
	OffsetDateTime maximumTime = null;
	for (final Meter meter : values) {
	    if (meter.getTimestamp() == null) {
		continue;
	    }
	    if (meter.type != null && meter.type.equals(Type.START)) {
		startMarkerFound = true;
		minimumTime = meter.getTimestamp();
	    }
	    if (meter.type != null && meter.type.equals(Type.STOP)) {
		stopMarkerFound = true;
		maximumTime = meter.getTimestamp();
	    }
	    if (!startMarkerFound && (minimumTime == null || meter.getTimestamp().isBefore(minimumTime))) {
		minimumTime = meter.getTimestamp();
	    }
	    if (!stopMarkerFound && (maximumTime == null || meter.getTimestamp().isAfter(maximumTime))) {
		maximumTime = meter.getTimestamp();
	    }
	}
	if (minimumTime == null || maximumTime == null) {
	    return Duration.ofMillis(0);
	}
	return Duration.between(minimumTime, maximumTime);
    }

    /*
     * Gets the time synchronization type, qualifying in order: 1) If any meter
     * value is of type INFORMATIVE, return type is INFORMATIVE 2) If all meter
     * values are of type SYNCHRONIZED, return type is SYNCHRONIZED 3) If no meter
     * value is of type INFORMATIVE and any value is of type REALTIME, no matter if
     * there are any SYNCHRONIZED values, the return type is REALTIME 4) If first
     * value is of type INFORMATIVE or REALTIME, and the last is REALTIME the return
     * is REALTIME
     */
    public static TimeSyncType getTimeSyncType(List<Meter> meters) {
	TimeSyncType timeSyncType = null;
	for (final Meter meter : meters) {
	    if (!meter.isLawRelevant()) {
		continue;
	    }
	    final TimeSyncType mt = meter.getTimeSyncType();
	    if (timeSyncType == null || mt == TimeSyncType.INFORMATIVE) {
		// If nothing else is set or if it is INFO:
		timeSyncType = mt;
	    } else if (timeSyncType == TimeSyncType.INFORMATIVE
		    && (mt == TimeSyncType.REALTIME || mt == TimeSyncType.SYNCHRONIZED)) {
		// If previous value is INFO or SYNCHRONIZED and actual value is REAL -> REAL
		timeSyncType = TimeSyncType.REALTIME;
	    } else if (timeSyncType == TimeSyncType.SYNCHRONIZED && mt == TimeSyncType.REALTIME) {
		timeSyncType = TimeSyncType.REALTIME;
	    }
	}

	if (timeSyncType == null) {
	    return TimeSyncType.INFORMATIVE;
	}

	return timeSyncType;
    }

    public static List<Meter> filterLawRelevant(List<Meter> meters) {
	final ArrayList<Meter> result = new ArrayList<>();
	for (final Meter m : meters) {
	    if (m.isLawRelevant()) {
		result.add(m);
	    }
	}
	return result;
    }

    public static void validateListStartStop(List<Meter> startList, List<Meter> stopList) throws ValidationException {
	if (startList == null || startList.isEmpty()) {
	    throw new ValidationException("No start values", "error.values.no.start.meter.values");
	}
	if (stopList == null || stopList.isEmpty()) {
	    throw new ValidationException("No stop values", "app.view.no.stop.meter.values");
	}
	final double[] minmax1 = getMinMax(startList);
	final double[] minmax2 = getMinMax(stopList);
	if (minmax1[1] > minmax2[0]) {
	    throw new ValidationException("Stop value is less than start value", "app.view.stop.less.than.start");
	}
    }

    @XmlTransient
    public TimeSyncType getTimeSyncType() {
	return timeSyncType;
    }

    @XmlTransient
    public Type getType() {
	return type;
    }

    public enum Type {
	START("app.verify.start"), STOP("app.verify.end"), UPDATE("app.verify.update");

	public final String message;

	Type(String message) {
	    this.message = message;
	}
    }

    public enum TimeSyncType {
	INFORMATIVE("app.informative"), // default, can not be used for billing in any way, might be random or simply
					// wrong, only for information
	REALTIME("app.informative"), // clock is not synchronized, but RTC is being utilized: timestamps can not be
				     // used for billing, but duration between timestamps may (with no relation to
				     // when they occurred)
	SYNCHRONIZED("app.synchronized"), // qualified time, clock is synchronized regularly against something like NTP:
					  // timestamps and/or duration may be used for billing
	;

	private final String message;

	TimeSyncType(String s) {
	    message = s;
	}

    }

    /**
     * The precision of the meter, format is 3-scaling. -2 = 1/100000 5 digits -1 =
     * 1/10000 4 digits 0 = 1/1000 3 digits 1 = 1/100 2 digits 2 = 1/10 1 digit 3 =
     * 1.0 0 digit
     *
     * @return
     */
    public String getScalingFormat() {
	final int format = 3 - scaling;
	return "%." + format + "f";
    }

    public int getScaling() {
	return scaling;
    }

    /**
     * Default: this meter value is relevant.
     */
    public boolean isLawRelevant() {
	return lawRelevant;
    }

    /**
     * Default: this meter value is relevant. Can be set to false to hide this value
     * in the overview window.
     */
    public void setLawRelevant(boolean lawRelevant) {
	this.lawRelevant = lawRelevant;
    }

    public boolean isCompensated() {
	return compensated;
    }

    public void setCompensated(boolean compensated) {
	this.compensated = compensated;
    }
}
