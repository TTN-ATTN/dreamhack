package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JavaScriptOrJSONCFormat.class */
public final class JavaScriptOrJSONCFormat extends AbstractJSONLikeFormat {
    public static final String NAME = "JavaScript or JSON";
    public static final JavaScriptOrJSONCFormat INSTANCE = new JavaScriptOrJSONCFormat();

    private JavaScriptOrJSONCFormat() {
    }

    @Override // freemarker.core.CFormat
    String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.jsStringEnc(s, StringUtil.JsStringEncCompatibility.JAVA_SCRIPT_OR_JSON, StringUtil.JsStringEncQuotation.QUOTATION_MARK);
    }

    @Override // freemarker.core.CFormat
    public String getName() {
        return NAME;
    }
}
