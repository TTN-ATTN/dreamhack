package freemarker.core;

import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/MarkupOutputFormat.class */
public abstract class MarkupOutputFormat<MO extends TemplateMarkupOutputModel> extends OutputFormat {
    public abstract MO fromPlainTextByEscaping(String str) throws TemplateModelException;

    public abstract MO fromMarkup(String str) throws TemplateModelException;

    public abstract void output(MO mo, Writer writer) throws TemplateModelException, IOException;

    public abstract void output(String str, Writer writer) throws TemplateModelException, IOException;

    public abstract String getSourcePlainText(MO mo) throws TemplateModelException;

    public abstract String getMarkupString(MO mo) throws TemplateModelException;

    public abstract MO concat(MO mo, MO mo2) throws TemplateModelException;

    public abstract String escapePlainText(String str) throws TemplateModelException;

    public abstract boolean isEmpty(MO mo) throws TemplateModelException;

    public abstract boolean isLegacyBuiltInBypassed(String str) throws TemplateModelException;

    public abstract boolean isAutoEscapedByDefault();

    protected MarkupOutputFormat() {
    }

    public <MO2 extends TemplateMarkupOutputModel<MO2>> void outputForeign(MO2 mo, Writer out) throws TemplateModelException, IOException {
        mo.getOutputFormat().output((MarkupOutputFormat) mo, out);
    }
}
