package freemarker.core;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BooleanExpression.class */
abstract class BooleanExpression extends Expression {
    BooleanExpression() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return evalToBoolean(env) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }
}
