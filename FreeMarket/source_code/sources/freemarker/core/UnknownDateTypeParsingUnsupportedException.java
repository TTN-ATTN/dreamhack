package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnknownDateTypeParsingUnsupportedException.class */
public final class UnknownDateTypeParsingUnsupportedException extends UnformattableValueException {
    public UnknownDateTypeParsingUnsupportedException() {
        super("Can't parse the string to date-like value because it isn't known if it's desired result should be a date (no time part), a time, or a date-time value.");
    }
}
