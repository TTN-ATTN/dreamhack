package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UndefinedOutputFormat.class */
public final class UndefinedOutputFormat extends OutputFormat {
    public static final UndefinedOutputFormat INSTANCE = new UndefinedOutputFormat();

    private UndefinedOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public boolean isOutputFormatMixingAllowed() {
        return true;
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return "undefined";
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return null;
    }
}
