package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForSequence.class */
abstract class BuiltInForSequence extends BuiltIn {
    abstract TemplateModel calculateResult(TemplateSequenceModel templateSequenceModel) throws TemplateModelException;

    BuiltInForSequence() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (!(model instanceof TemplateSequenceModel)) {
            throw new NonSequenceException(this.target, model, env);
        }
        return calculateResult((TemplateSequenceModel) model);
    }
}
