package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UncheckedParseException.class */
final class UncheckedParseException extends RuntimeException {
    private final ParseException parseException;

    public UncheckedParseException(ParseException parseException) {
        this.parseException = parseException;
    }

    public ParseException getParseException() {
        return this.parseException;
    }
}
