package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BooleanLiteral.class */
final class BooleanLiteral extends Expression {
    private final boolean val;

    public BooleanLiteral(boolean val) {
        this.val = val;
    }

    static TemplateBooleanModel getTemplateModel(boolean b) {
        return b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }

    @Override // freemarker.core.Expression
    boolean evalToBoolean(Environment env) {
        return this.val;
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.val ? "true" : "false";
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return getCanonicalForm();
    }

    @Override // freemarker.core.TemplateObject
    public String toString() {
        return this.val ? "true" : "false";
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) {
        return this.val ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return true;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new BooleanLiteral(this.val);
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 0;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }
}
