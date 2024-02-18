package de.safe_ev.transparenzsoftware.verification.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import de.safe_ev.transparenzsoftware.Utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adapter class for marshalling and unmarshalling a string to a localdatetime object
 * Also provides a static method to format a local datetime object.
 *
 * @see DateTimeFormatter ISO_DATE_TIME is used
 */
public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public OffsetDateTime unmarshal(String s) throws Exception {
        return OffsetDateTime.parse(Utils.clearString(s), ISO_DATE_TIME);
    }

    @Override
    public String marshal(OffsetDateTime localDateTime) throws Exception {
        return formattedDateTime(localDateTime);
    }

    /**
     * formats a localdate time to the iso date time string
     *
     * @param localDateTime date time to format can be null
     * @return formatted string empty if null
     */
    public static String formattedDateTime(OffsetDateTime localDateTime) {
        return formattedDateTime(localDateTime, ISO_DATE_TIME);
    }

    /**
     * formats a localdate time to the iso date time string
     *
     * @param localDateTime date time to format can be null
     * @return formatted string empty if null
     */
    public static String formattedDateTime(OffsetDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime != null ? localDateTime.format(dateTimeFormatter) : "";
    }
}
