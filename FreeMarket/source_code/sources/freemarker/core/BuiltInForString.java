package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForString.class */
abstract class BuiltInForString extends BuiltIn {
    abstract TemplateModel calculateResult(String str, Environment environment) throws TemplateException;

    BuiltInForString() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return calculateResult(getTargetString(this.target, env), env);
    }

    static String getTargetString(Expression target, Environment env) throws TemplateException {
        return target.evalAndCoerceToStringOrUnsupportedMarkup(env);
    }
}
