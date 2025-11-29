package freemarker.template.utility;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/UnrecognizedTimeZoneException.class */
public class UnrecognizedTimeZoneException extends Exception {
    private final String timeZoneName;

    public UnrecognizedTimeZoneException(String timeZoneName) {
        super("Unrecognized time zone: " + StringUtil.jQuote(timeZoneName));
        this.timeZoneName = timeZoneName;
    }

    public String getTimeZoneName() {
        return this.timeZoneName;
    }
}
