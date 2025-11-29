package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;
import org.springframework.http.MediaType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/XHTMLOutputFormat.class */
public class XHTMLOutputFormat extends XMLOutputFormat {
    public static final XHTMLOutputFormat INSTANCE = new XHTMLOutputFormat();

    protected XHTMLOutputFormat() {
    }

    @Override // freemarker.core.XMLOutputFormat, freemarker.core.OutputFormat
    public String getName() {
        return "XHTML";
    }

    @Override // freemarker.core.XMLOutputFormat, freemarker.core.OutputFormat
    public String getMimeType() {
        return MediaType.APPLICATION_XHTML_XML_VALUE;
    }

    @Override // freemarker.core.XMLOutputFormat, freemarker.core.CommonMarkupOutputFormat, freemarker.core.MarkupOutputFormat
    public void output(String textToEsc, Writer out) throws TemplateModelException, IOException {
        StringUtil.XHTMLEnc(textToEsc, out);
    }

    @Override // freemarker.core.XMLOutputFormat, freemarker.core.MarkupOutputFormat
    public String escapePlainText(String plainTextContent) {
        return StringUtil.XHTMLEnc(plainTextContent);
    }

    @Override // freemarker.core.XMLOutputFormat, freemarker.core.MarkupOutputFormat
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("html") || builtInName.equals("xml") || builtInName.equals("xhtml");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.XMLOutputFormat, freemarker.core.CommonMarkupOutputFormat
    public TemplateXHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateXHTMLOutputModel(plainTextContent, markupContent);
    }
}
