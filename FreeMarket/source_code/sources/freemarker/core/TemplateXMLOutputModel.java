package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateXMLOutputModel.class */
public class TemplateXMLOutputModel extends CommonTemplateMarkupOutputModel<TemplateXMLOutputModel> {
    protected TemplateXMLOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override // freemarker.core.CommonTemplateMarkupOutputModel, freemarker.core.TemplateMarkupOutputModel
    public XMLOutputFormat getOutputFormat() {
        return XMLOutputFormat.INSTANCE;
    }
}
