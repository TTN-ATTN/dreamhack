package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateCombinedMarkupOutputModel.class */
public final class TemplateCombinedMarkupOutputModel extends CommonTemplateMarkupOutputModel<TemplateCombinedMarkupOutputModel> {
    private final CombinedMarkupOutputFormat outputFormat;

    TemplateCombinedMarkupOutputModel(String plainTextContent, String markupContent, CombinedMarkupOutputFormat outputFormat) {
        super(plainTextContent, markupContent);
        this.outputFormat = outputFormat;
    }

    @Override // freemarker.core.CommonTemplateMarkupOutputModel, freemarker.core.TemplateMarkupOutputModel
    public CombinedMarkupOutputFormat getOutputFormat() {
        return this.outputFormat;
    }
}
