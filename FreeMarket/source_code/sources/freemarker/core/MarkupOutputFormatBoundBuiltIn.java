package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/MarkupOutputFormatBoundBuiltIn.class */
abstract class MarkupOutputFormatBoundBuiltIn extends SpecialBuiltIn {
    protected MarkupOutputFormat outputFormat;

    protected abstract TemplateModel calculateResult(Environment environment) throws TemplateException;

    MarkupOutputFormatBoundBuiltIn() {
    }

    void bindToMarkupOutputFormat(MarkupOutputFormat outputFormat) {
        NullArgumentException.check(outputFormat);
        this.outputFormat = outputFormat;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        if (this.outputFormat == null) {
            throw new NullPointerException("outputFormat was null");
        }
        return calculateResult(env);
    }
}
