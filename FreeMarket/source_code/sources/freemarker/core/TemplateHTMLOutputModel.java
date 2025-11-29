package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateHTMLOutputModel.class */
public class TemplateHTMLOutputModel extends CommonTemplateMarkupOutputModel<TemplateHTMLOutputModel> {
    protected TemplateHTMLOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override // freemarker.core.CommonTemplateMarkupOutputModel, freemarker.core.TemplateMarkupOutputModel
    public HTMLOutputFormat getOutputFormat() {
        return HTMLOutputFormat.INSTANCE;
    }
}
