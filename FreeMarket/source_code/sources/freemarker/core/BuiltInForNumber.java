package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForNumber.class */
abstract class BuiltInForNumber extends BuiltIn {
    abstract TemplateModel calculateResult(Number number, TemplateModel templateModel) throws TemplateModelException;

    BuiltInForNumber() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        return calculateResult(this.target.modelToNumber(model, env), model);
    }
}
