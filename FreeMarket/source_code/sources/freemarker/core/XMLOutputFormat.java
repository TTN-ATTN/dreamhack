package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/XMLOutputFormat.class */
public class XMLOutputFormat extends CommonMarkupOutputFormat<TemplateXMLOutputModel> {
    public static final XMLOutputFormat INSTANCE = new XMLOutputFormat();

    protected XMLOutputFormat() {
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return "XML";
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return "application/xml";
    }

    @Override // freemarker.core.CommonMarkupOutputFormat, freemarker.core.MarkupOutputFormat
    public void output(String textToEsc, Writer out) throws TemplateModelException, IOException {
        StringUtil.XMLEnc(textToEsc, out);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public String escapePlainText(String plainTextContent) {
        return StringUtil.XMLEnc(plainTextContent);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("xml");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.CommonMarkupOutputFormat
    public TemplateXMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateXMLOutputModel(plainTextContent, markupContent);
    }
}
