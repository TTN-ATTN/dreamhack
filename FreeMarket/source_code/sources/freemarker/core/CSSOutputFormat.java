package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CSSOutputFormat.class */
public class CSSOutputFormat extends OutputFormat {
    public static final CSSOutputFormat INSTANCE = new CSSOutputFormat();

    private CSSOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return "CSS";
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return "text/css";
    }

    @Override // freemarker.core.OutputFormat
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }
}
