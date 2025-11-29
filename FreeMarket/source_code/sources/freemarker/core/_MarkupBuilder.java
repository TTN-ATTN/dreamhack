package freemarker.core;

import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_MarkupBuilder.class */
public class _MarkupBuilder<MO extends TemplateMarkupOutputModel> {
    private final String markupSource;
    private final MarkupOutputFormat<MO> markupOutputFormat;

    public _MarkupBuilder(MarkupOutputFormat<MO> markupOutputFormat, String markupSource) {
        this.markupOutputFormat = markupOutputFormat;
        this.markupSource = markupSource;
    }

    public MO build() throws TemplateModelException {
        return (MO) this.markupOutputFormat.fromMarkup(this.markupSource);
    }
}
