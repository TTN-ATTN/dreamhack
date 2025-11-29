package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/PlainTextOutputFormat.class */
public final class PlainTextOutputFormat extends OutputFormat {
    public static final PlainTextOutputFormat INSTANCE = new PlainTextOutputFormat();

    private PlainTextOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return "plainText";
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return "text/plain";
    }
}
