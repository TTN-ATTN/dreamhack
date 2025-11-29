package freemarker.core;

import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CombinedMarkupOutputFormat.class */
public final class CombinedMarkupOutputFormat extends CommonMarkupOutputFormat<TemplateCombinedMarkupOutputModel> {
    private final String name;
    private final MarkupOutputFormat outer;
    private final MarkupOutputFormat inner;

    public CombinedMarkupOutputFormat(MarkupOutputFormat outer, MarkupOutputFormat inner) {
        this(null, outer, inner);
    }

    public CombinedMarkupOutputFormat(String name, MarkupOutputFormat outer, MarkupOutputFormat inner) {
        this.name = name != null ? null : outer.getName() + "{" + inner.getName() + "}";
        this.outer = outer;
        this.inner = inner;
    }

    @Override // freemarker.core.OutputFormat
    public String getName() {
        return this.name;
    }

    @Override // freemarker.core.OutputFormat
    public String getMimeType() {
        return this.outer.getMimeType();
    }

    @Override // freemarker.core.CommonMarkupOutputFormat, freemarker.core.MarkupOutputFormat
    public void output(String textToEsc, Writer out) throws TemplateModelException, IOException {
        this.outer.output(this.inner.escapePlainText(textToEsc), out);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public <MO2 extends TemplateMarkupOutputModel<MO2>> void outputForeign(MO2 mo, Writer out) throws TemplateModelException, IOException {
        this.outer.outputForeign(mo, out);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public String escapePlainText(String plainTextContent) throws TemplateModelException {
        return this.outer.escapePlainText(this.inner.escapePlainText(plainTextContent));
    }

    @Override // freemarker.core.MarkupOutputFormat
    public boolean isLegacyBuiltInBypassed(String builtInName) throws TemplateModelException {
        return this.outer.isLegacyBuiltInBypassed(builtInName);
    }

    @Override // freemarker.core.CommonMarkupOutputFormat, freemarker.core.MarkupOutputFormat
    public boolean isAutoEscapedByDefault() {
        return this.outer.isAutoEscapedByDefault();
    }

    @Override // freemarker.core.CommonMarkupOutputFormat, freemarker.core.OutputFormat
    public boolean isOutputFormatMixingAllowed() {
        return this.outer.isOutputFormatMixingAllowed();
    }

    public MarkupOutputFormat getOuterOutputFormat() {
        return this.outer;
    }

    public MarkupOutputFormat getInnerOutputFormat() {
        return this.inner;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.CommonMarkupOutputFormat
    public TemplateCombinedMarkupOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateCombinedMarkupOutputModel(plainTextContent, markupContent, this);
    }
}
