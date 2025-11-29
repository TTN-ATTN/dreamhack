package freemarker.core;

import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_TimeZoneBuilder.class */
public class _TimeZoneBuilder {
    private final String timeZoneId;

    public _TimeZoneBuilder(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public TimeZone build() {
        TimeZone timeZone = TimeZone.getTimeZone(this.timeZoneId);
        if (timeZone.getID().equals("GMT") && !this.timeZoneId.equals("GMT") && !this.timeZoneId.equals("UTC") && !this.timeZoneId.equals("GMT+00") && !this.timeZoneId.equals("GMT+00:00") && !this.timeZoneId.equals("GMT+0000")) {
            throw new IllegalArgumentException("Unrecognized time zone: " + this.timeZoneId);
        }
        return timeZone;
    }
}
