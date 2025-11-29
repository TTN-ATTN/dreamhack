package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/RTFOutputFormat.class */
public class RTFOutputFormat extends CommonMarkupOutputFormat<TemplateRTFOutputModel> {
    public static final RTFOutputFormat INSTANCE = new RTFOutputFormat();

    protected RTFOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return "RTF";
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return "application/rtf";
    }

    @Override // freemarker.core.CommonMarkupOutputFormat, freemarker.core.MarkupOutputFormat
    public void output(String textToEsc, Writer out) throws TemplateModelException, IOException {
        StringUtil.RTFEnc(textToEsc, out);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public String escapePlainText(String plainTextContent) {
        return StringUtil.RTFEnc(plainTextContent);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("rtf");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.CommonMarkupOutputFormat
    public TemplateRTFOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateRTFOutputModel(plainTextContent, markupContent);
    }
}
