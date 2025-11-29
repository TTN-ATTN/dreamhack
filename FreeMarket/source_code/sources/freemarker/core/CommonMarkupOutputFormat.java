package freemarker.core;

import freemarker.core.CommonTemplateMarkupOutputModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CommonMarkupOutputFormat.class */
public abstract class CommonMarkupOutputFormat<MO extends CommonTemplateMarkupOutputModel> extends MarkupOutputFormat<MO> {
    @Override // freemarker.core.MarkupOutputFormat
    public abstract void output(String str, Writer writer) throws TemplateModelException, IOException;

    protected abstract MO newTemplateMarkupOutputModel(String str, String str2) throws TemplateModelException;

    protected CommonMarkupOutputFormat() {
    }

    @Override // freemarker.core.MarkupOutputFormat
    public final MO fromPlainTextByEscaping(String str) throws TemplateModelException {
        return (MO) newTemplateMarkupOutputModel(str, null);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public final MO fromMarkup(String str) throws TemplateModelException {
        return (MO) newTemplateMarkupOutputModel(null, str);
    }

    @Override // freemarker.core.MarkupOutputFormat
    public final void output(MO mo, Writer out) throws TemplateModelException, IOException {
        String mc = mo.getMarkupContent();
        if (mc != null) {
            out.write(mc);
        } else {
            output(mo.getPlainTextContent(), out);
        }
    }

    @Override // freemarker.core.MarkupOutputFormat
    public final String getSourcePlainText(MO mo) throws TemplateModelException {
        return mo.getPlainTextContent();
    }

    @Override // freemarker.core.MarkupOutputFormat
    public final String getMarkupString(MO mo) throws TemplateModelException {
        String mc = mo.getMarkupContent();
        if (mc != null) {
            return mc;
        }
        String mc2 = escapePlainText(mo.getPlainTextContent());
        mo.setMarkupContent(mc2);
        return mc2;
    }

    @Override // freemarker.core.MarkupOutputFormat
    public final MO concat(MO mo, MO mo2) throws TemplateModelException {
        String plainTextContent = mo.getPlainTextContent();
        String markupContent = mo.getMarkupContent();
        String plainTextContent2 = mo2.getPlainTextContent();
        String markupContent2 = mo2.getMarkupContent();
        String str = (plainTextContent == null || plainTextContent2 == null) ? null : plainTextContent + plainTextContent2;
        String str2 = (markupContent == null || markupContent2 == null) ? null : markupContent + markupContent2;
        if (str != null || str2 != null) {
            return (MO) newTemplateMarkupOutputModel(str, str2);
        }
        if (plainTextContent != null) {
            return (MO) newTemplateMarkupOutputModel(null, getMarkupString((CommonMarkupOutputFormat<MO>) mo) + markupContent2);
        }
        return (MO) newTemplateMarkupOutputModel(null, markupContent + getMarkupString((CommonMarkupOutputFormat<MO>) mo2));
    }

    @Override // freemarker.core.MarkupOutputFormat
    public boolean isEmpty(MO mo) throws TemplateModelException {
        String s = mo.getPlainTextContent();
        return s != null ? s.length() == 0 : mo.getMarkupContent().length() == 0;
    }

    @Override // freemarker.core.OutputFormat
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }

    @Override // freemarker.core.MarkupOutputFormat
    public boolean isAutoEscapedByDefault() {
        return true;
    }
}
