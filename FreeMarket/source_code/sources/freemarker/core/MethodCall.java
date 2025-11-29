package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/MethodCall.class */
final class MethodCall extends Expression {
    private final Expression target;
    private final ListLiteral arguments;

    MethodCall(Expression target, ArrayList arguments) {
        this(target, new ListLiteral(arguments));
    }

    private MethodCall(Expression target, ListLiteral arguments) {
        this.target = target;
        this.arguments = arguments;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        List valueList;
        TemplateModel targetModel = this.target.eval(env);
        if (targetModel instanceof TemplateMethodModel) {
            TemplateMethodModel targetMethod = (TemplateMethodModel) targetModel;
            if (targetMethod instanceof TemplateMethodModelEx) {
                valueList = this.arguments.getModelList(env);
            } else {
                valueList = this.arguments.getValueList(env);
            }
            List argumentStrings = valueList;
            Object result = targetMethod.exec(argumentStrings);
            return env.getObjectWrapper().wrap(result);
        }
        if (targetModel instanceof Macro) {
            return env.invokeFunction(env, (Macro) targetModel, this.arguments.items, this);
        }
        throw new NonMethodException(this.target, targetModel, true, false, null, env);
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.target.getCanonicalForm());
        buf.append("(");
        String list = this.arguments.getCanonicalForm();
        buf.append((CharSequence) list, 1, list.length() - 1);
        buf.append(")");
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "...(...)";
    }

    TemplateModel getConstantValue() {
        return null;
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return false;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new MethodCall(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), (ListLiteral) this.arguments.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1 + this.arguments.items.size();
    }

    Expression getTarget() {
        return this.target;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx == 0) {
            return this.target;
        }
        if (idx < getParameterCount()) {
            return this.arguments.items.get(idx - 1);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx == 0) {
            return ParameterRole.CALLEE;
        }
        if (idx < getParameterCount()) {
            return ParameterRole.ARGUMENT_VALUE;
        }
        throw new IndexOutOfBoundsException();
    }
}
