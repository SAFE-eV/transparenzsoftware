package de.safe_ev.transparenzsoftware.verification.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import de.safe_ev.transparenzsoftware.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adapter class for marshalling and unmarshalling a string to a localdatetime object
 * Also provides a static method to format a local datetime object.
 *
 * @see DateTimeFormatter FORMAT is used
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public LocalDateTime unmarshal(String s) throws Exception {
        return LocalDateTime.parse(Utils.clearString(s), FORMAT);
    }

    @Override
    public String marshal(LocalDateTime localDateTime) throws Exception {
        return formattedDateTime(localDateTime);
    }

    /**
     * formats a localdate time to the iso date time string
     *
     * @param localDateTime date time to format can be null
     * @return formatted string empty if null
     */
    public static String formattedDateTime(LocalDateTime localDateTime) {
        return formattedDateTime(localDateTime, FORMAT);
    }

    /**
     * formats a localdate time to the iso date time string
     *
     * @param localDateTime date time to format can be null
     * @return formatted string empty if null
     */
    public static String formattedDateTime(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime != null ? localDateTime.format(dateTimeFormatter) : "";
    }
}
