package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForNode.class */
abstract class BuiltInForNode extends BuiltIn {
    abstract TemplateModel calculateResult(TemplateNodeModel templateNodeModel, Environment environment) throws TemplateModelException;

    BuiltInForNode() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateNodeModel) {
            return calculateResult((TemplateNodeModel) model, env);
        }
        throw new NonNodeException(this.target, model, env);
    }
}
