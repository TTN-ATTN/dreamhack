package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ParsingNotSupportedException.class */
public class ParsingNotSupportedException extends TemplateValueFormatException {
    public ParsingNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingNotSupportedException(String message) {
        this(message, null);
    }
}
