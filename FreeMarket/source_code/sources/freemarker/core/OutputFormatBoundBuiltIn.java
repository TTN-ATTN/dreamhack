package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/OutputFormatBoundBuiltIn.class */
abstract class OutputFormatBoundBuiltIn extends SpecialBuiltIn {
    protected OutputFormat outputFormat;
    protected int autoEscapingPolicy;

    protected abstract TemplateModel calculateResult(Environment environment) throws TemplateException;

    OutputFormatBoundBuiltIn() {
    }

    void bindToOutputFormat(OutputFormat outputFormat, int autoEscapingPolicy) {
        NullArgumentException.check(outputFormat);
        this.outputFormat = outputFormat;
        this.autoEscapingPolicy = autoEscapingPolicy;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        if (this.outputFormat == null) {
            throw new NullPointerException("outputFormat was null");
        }
        return calculateResult(env);
    }
}
