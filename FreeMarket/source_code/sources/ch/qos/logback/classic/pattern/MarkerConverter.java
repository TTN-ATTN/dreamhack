package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Marker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-classic-1.2.12.jar:ch/qos/logback/classic/pattern/MarkerConverter.class */
public class MarkerConverter extends ClassicConverter {
    private static String EMPTY = "";

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent le) {
        Marker marker = le.getMarker();
        if (marker == null) {
            return EMPTY;
        }
        return marker.toString();
    }
}
