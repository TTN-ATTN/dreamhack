package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/HTMLOutputFormat.class */
public class HTMLOutputFormat extends CommonMarkupOutputFormat<TemplateHTMLOutputModel> {
    public static final HTMLOutputFormat INSTANCE = new HTMLOutputFormat();

    protected HTMLOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return "HTML";
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return "text/html";
    }

    @Override // freemarker.core.CommonMarkupOutputFormat, freemarker.core.MarkupOutputFormat
    public void output(String textToEsc, Writer out) throws TemplateModelException, IOException {
        StringUtil.XHTMLEnc(textToEsc, out);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public String escapePlainText(String plainTextContent) {
        return StringUtil.XHTMLEnc(plainTextContent);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("html") || builtInName.equals("xml") || builtInName.equals("xhtml");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.CommonMarkupOutputFormat
    public TemplateHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateHTMLOutputModel(plainTextContent, markupContent);
    }
}
