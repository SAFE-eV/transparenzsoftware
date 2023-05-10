package de.safe_ev.transparenzsoftware.verification.format.sml;

import org.openmuc.jsml.structures.SmlTimestamp;
import org.openmuc.jsml.structures.SmlTimestampLocal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class SMLUtils {

    private static final int SECONDS_PER_MINUTE = 60;

    /**
     * Transfer a local timestamp entry to a int with seconds for time
     * with added offset of timezone and season
     * (as specified in EDL Lastenheft page 48)
     *
     * @param timestampLocal smlEntry holding the valtime
     * @return
     */
    public static OffsetDateTime parseSmlTimestamp(SmlTimestampLocal timestampLocal) {
        int offsetTotal = timestampLocal.getLocalOffset().getVal() * SECONDS_PER_MINUTE;
        offsetTotal += timestampLocal.getSeasonTimeOffset().getVal() * SECONDS_PER_MINUTE;
        Instant instant = Instant.ofEpochSecond(timestampLocal.getTimestamp().getLongValue()+offsetTotal);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofTotalSeconds(offsetTotal));
        return offsetDateTime;
    }

    public static OffsetDateTime parseSmlTimestamp(SmlTimestamp timestampLocal) {
        int offsetTotal =0;
        Instant instant = Instant.ofEpochSecond(timestampLocal.getLongValue());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofTotalSeconds(offsetTotal));
        return offsetDateTime;
    }

}
