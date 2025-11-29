package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JSONOutputFormat.class */
public class JSONOutputFormat extends OutputFormat {
    public static final JSONOutputFormat INSTANCE = new JSONOutputFormat();

    private JSONOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return "JSON";
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return "application/json";
    }

    @Override // freemarker.core.OutputFormat
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }
}
