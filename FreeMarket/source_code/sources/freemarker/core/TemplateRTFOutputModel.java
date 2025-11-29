package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateRTFOutputModel.class */
public class TemplateRTFOutputModel extends CommonTemplateMarkupOutputModel<TemplateRTFOutputModel> {
    protected TemplateRTFOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override // freemarker.core.CommonTemplateMarkupOutputModel, freemarker.core.TemplateMarkupOutputModel
    public RTFOutputFormat getOutputFormat() {
        return RTFOutputFormat.INSTANCE;
    }
}
