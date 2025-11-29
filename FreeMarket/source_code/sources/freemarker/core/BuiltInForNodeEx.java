package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModelEx;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForNodeEx.class */
public abstract class BuiltInForNodeEx extends BuiltIn {
    abstract TemplateModel calculateResult(TemplateNodeModelEx templateNodeModelEx, Environment environment) throws TemplateModelException;

    @Override // freemarker.core.BuiltIn, freemarker.core.TemplateObject
    public /* bridge */ /* synthetic */ String getCanonicalForm() {
        return super.getCanonicalForm();
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateNodeModelEx) {
            return calculateResult((TemplateNodeModelEx) model, env);
        }
        throw new NonExtendedNodeException(this.target, model, env);
    }
}
