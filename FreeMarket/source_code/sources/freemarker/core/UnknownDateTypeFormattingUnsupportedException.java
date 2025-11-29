package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnknownDateTypeFormattingUnsupportedException.class */
public final class UnknownDateTypeFormattingUnsupportedException extends UnformattableValueException {
    public UnknownDateTypeFormattingUnsupportedException() {
        super("Can't convert the date-like value to string because it isn't known if it's a date (no time part), time or date-time value.");
    }
}
