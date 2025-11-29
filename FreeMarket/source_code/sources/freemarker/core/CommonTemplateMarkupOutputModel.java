package freemarker.core;

import freemarker.core.CommonTemplateMarkupOutputModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CommonTemplateMarkupOutputModel.class */
public abstract class CommonTemplateMarkupOutputModel<MO extends CommonTemplateMarkupOutputModel<MO>> implements TemplateMarkupOutputModel<MO> {
    private final String plainTextContent;
    private String markupContent;

    @Override // freemarker.core.TemplateMarkupOutputModel
    public abstract CommonMarkupOutputFormat<MO> getOutputFormat();

    protected CommonTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        this.plainTextContent = plainTextContent;
        this.markupContent = markupContent;
    }

    final String getPlainTextContent() {
        return this.plainTextContent;
    }

    final String getMarkupContent() {
        return this.markupContent;
    }

    final void setMarkupContent(String markupContent) {
        this.markupContent = markupContent;
    }

    public String toString() {
        return "markupOutput(format=" + getOutputFormat().getName() + ", " + (this.plainTextContent != null ? "plainText=" + this.plainTextContent : "markup=" + this.markupContent) + ")";
    }
}
