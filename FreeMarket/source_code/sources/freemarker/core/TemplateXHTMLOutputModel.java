package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateXHTMLOutputModel.class */
public class TemplateXHTMLOutputModel extends TemplateXMLOutputModel {
    protected TemplateXHTMLOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override // freemarker.core.TemplateXMLOutputModel, freemarker.core.CommonTemplateMarkupOutputModel, freemarker.core.TemplateMarkupOutputModel
    public XHTMLOutputFormat getOutputFormat() {
        return XHTMLOutputFormat.INSTANCE;
    }
}
