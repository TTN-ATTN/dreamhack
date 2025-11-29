package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForMarkupOutput.class */
abstract class BuiltInForMarkupOutput extends BuiltIn {
    protected abstract TemplateModel calculateResult(TemplateMarkupOutputModel templateMarkupOutputModel) throws TemplateModelException;

    BuiltInForMarkupOutput() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (!(model instanceof TemplateMarkupOutputModel)) {
            throw new NonMarkupOutputException(this.target, model, env);
        }
        return calculateResult((TemplateMarkupOutputModel) model);
    }
}
