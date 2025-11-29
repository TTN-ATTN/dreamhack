package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForDate.class */
abstract class BuiltInForDate extends BuiltIn {
    protected abstract TemplateModel calculateResult(Date date, int i, Environment environment) throws TemplateException;

    BuiltInForDate() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateDateModel) {
            TemplateDateModel tdm = (TemplateDateModel) model;
            return calculateResult(EvalUtil.modelToDate(tdm, this.target), tdm.getDateType(), env);
        }
        throw newNonDateException(env, model, this.target);
    }

    static TemplateException newNonDateException(Environment env, TemplateModel model, Expression target) throws InvalidReferenceException {
        TemplateException e;
        if (model == null) {
            e = InvalidReferenceException.getInstance(target, env);
        } else {
            e = new NonDateException(target, model, "date", env);
        }
        return e;
    }
}
