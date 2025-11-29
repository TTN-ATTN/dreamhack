package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JavaScriptOutputFormat.class */
public class JavaScriptOutputFormat extends OutputFormat {
    public static final JavaScriptOutputFormat INSTANCE = new JavaScriptOutputFormat();

    private JavaScriptOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return JavaScriptCFormat.NAME;
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return "application/javascript";
    }

    @Override // freemarker.core.OutputFormat
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }
}
