package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ParentheticalExpression.class */
final class ParentheticalExpression extends Expression {
    private final Expression nested;

    ParentheticalExpression(Expression nested) {
        this.nested = nested;
    }

    @Override // freemarker.core.Expression
    boolean evalToBoolean(Environment env) throws TemplateException {
        return this.nested.evalToBoolean(env);
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return "(" + this.nested.getCanonicalForm() + ")";
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "(...)";
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return this.nested.eval(env);
    }

    @Override // freemarker.core.Expression
    public boolean isLiteral() {
        return this.nested.isLiteral();
    }

    Expression getNestedExpression() {
        return this.nested;
    }

    @Override // freemarker.core.Expression
    void enableLazilyGeneratedResult() {
        this.nested.enableLazilyGeneratedResult();
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ParentheticalExpression(this.nested.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.nested;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.ENCLOSED_OPERAND;
    }
}
