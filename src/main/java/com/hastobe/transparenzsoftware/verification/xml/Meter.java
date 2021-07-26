package com.hastobe.transparenzsoftware.verification.xml;


import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.ValidationException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Meter {

    private String descriptiveMessageText;

    private double value;

    //necessary as jaxb will complain about the timestamp
    @XmlTransient
    private OffsetDateTime timestamp;

    @XmlTransient
    private Type type;

    @XmlTransient
    private TimeSyncType timeSyncType;


    private Meter() {
        value = 0;
        timestamp = null;
        type = null;
    }

    public Meter(double value, OffsetDateTime timestamp) {
        this(value, timestamp, null, TimeSyncType.INFORMATIVE);
    }

    public Meter(double value, OffsetDateTime timestamp, Type type, TimeSyncType timeSyncType) {
        this.value = value;
        this.timestamp = timestamp;
        this.type = type;
        this.timeSyncType = timeSyncType;
    }

    public void setDescriptiveMessageText(String text){
        descriptiveMessageText = text;
    }

    public String getDescriptiveMessageText(){
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
        List<String> additionalData = new ArrayList<>();
        if(timeSyncType != null && !timeSyncType.equals(TimeSyncType.SYNCHRONIZED)) {
            additionalData.add(Translator.get(timeSyncType.message));
        }
        return String.join(", ", additionalData);
    }

    /**
     * Builds the difference between the lowest and highest
     * value
     *
     * @return
     */
    public static double getDifference(List<Meter> values) {
        if (values == null || values.size() < 2) {
            return 0;
        }
        double[] minmax = getMinMax(values);
        return minmax[1] - minmax[0];
    }

    private static double[] getMinMax(List<Meter> values) {
        double minimum = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        boolean startMarkerFound = false;
        boolean stopMarkerFound = false;
        for (Meter meter : values) {
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
        return new double[]{minimum, max};
    }

    /**
     * Builds the time difference between the lowest and highest
     * value
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
        for (Meter meter : values) {
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
     * Gets the time synchronization type, qualifying in order:
     * 1) If any meter value is of type INFORMATIVE, return type is INFORMATIVE
     * 2) If all meter values are of type SYNCHRONIZED, return type is SYNCHRONIZED
     * 3) If no meter value is of type INFORMATIVE and any value is of type REALTIME,
     *    no matter if there are any SYNCHRONIZED values, the return type is REALTIME
     */
    public static TimeSyncType getTimeSyncType(List<Meter> meters) {
        TimeSyncType timeSyncType = null;
        for (Meter meter : meters) {
            if (meter.timeSyncType == TimeSyncType.INFORMATIVE) {
                return TimeSyncType.INFORMATIVE;
            }

            if (timeSyncType == null) {
                timeSyncType = meter.timeSyncType;
            } else if (timeSyncType == TimeSyncType.SYNCHRONIZED && meter.getTimeSyncType() == TimeSyncType.REALTIME) {
                timeSyncType = TimeSyncType.REALTIME;
            }
        }

        if (timeSyncType == null) {
            return TimeSyncType.INFORMATIVE;
        }

        return timeSyncType;
    }

    public static void validateListStartStop(List<Meter> startList, List<Meter> stopList) throws ValidationException {
        if (startList == null || startList.isEmpty()) {
            throw new ValidationException("No start values", "error.values.no.start.meter.values");
        }
        if (stopList == null || stopList.isEmpty()) {
            throw new ValidationException("No stop values", "app.view.no.stop.meter.values");
        }
        double[] minmax1 = getMinMax(startList);
        double[] minmax2 = getMinMax(stopList);
        if (minmax1[1] > minmax2[0]) {
            throw new ValidationException("Stop value is less than start value", "app.view.stop.less.than.start");
        }
    }

    @XmlTransient
    public TimeSyncType getTimeSyncType() {
        return timeSyncType;
    }

    @XmlTransient
    public Type getType(){
        return type;
    }

    public enum Type {
        START("app.verify.start"),
        STOP("app.verify.end"),
        UPDATE("app.verify.update");

        public final String message;

        Type(String message) {
            this.message = message;
        }
    }

    public enum TimeSyncType {
        INFORMATIVE("app.informative"), // default, can not be used for billing in any way, might be random or simply wrong, only for information
        REALTIME("app.informative"), // clock is not synchronized, but RTC is being utilized: timestamps can not be used for billing, but duration between timestamps may (with no relation to when they occurred)
        SYNCHRONIZED("app.synchronized"), // qualified time, clock is synchronized regularly against something like NTP: timestamps and/or duration may be used for billing
        ;

        private final String message;

        TimeSyncType(String s) {
            this.message = s;
        }


    }
}
