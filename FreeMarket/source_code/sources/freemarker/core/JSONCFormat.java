package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JSONCFormat.class */
public final class JSONCFormat extends AbstractJSONLikeFormat {
    public static final String NAME = "JSON";
    public static final JSONCFormat INSTANCE = new JSONCFormat();

    private JSONCFormat() {
    }

    @Override // freemarker.core.CFormat
    String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.jsStringEnc(s, StringUtil.JsStringEncCompatibility.JSON, StringUtil.JsStringEncQuotation.QUOTATION_MARK);
    }

    @Override // freemarker.core.CFormat
    public String getName() {
        return "JSON";
    }
}
