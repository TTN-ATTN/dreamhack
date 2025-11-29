package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ExpressionWithFixedResult.class */
class ExpressionWithFixedResult extends Expression {
    private final TemplateModel fixedResult;
    private final Expression sourceExpression;

    ExpressionWithFixedResult(TemplateModel fixedResult, Expression sourceExpression) {
        this.fixedResult = fixedResult;
        this.sourceExpression = sourceExpression;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return this.fixedResult;
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return this.sourceExpression.isLiteral();
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ExpressionWithFixedResult(this.fixedResult, this.sourceExpression.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.sourceExpression.getCanonicalForm();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return this.sourceExpression.getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return this.sourceExpression.getParameterCount();
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        return this.sourceExpression.getParameterValue(idx);
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        return this.sourceExpression.getParameterRole(idx);
    }
}
