package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForLegacyEscaping.class */
abstract class BuiltInForLegacyEscaping extends BuiltInBannedWhenAutoEscaping {
    abstract TemplateModel calculateResult(String str, Environment environment) throws TemplateException;

    BuiltInForLegacyEscaping() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel tm = this.target.eval(env);
        Object moOrStr = EvalUtil.coerceModelToStringOrMarkup(tm, this.target, null, env);
        if (moOrStr instanceof String) {
            return calculateResult((String) moOrStr, env);
        }
        TemplateMarkupOutputModel<?> mo = (TemplateMarkupOutputModel) moOrStr;
        if (mo.getOutputFormat().isLegacyBuiltInBypassed(this.key)) {
            return mo;
        }
        throw new NonStringException(this.target, tm, env);
    }
}
