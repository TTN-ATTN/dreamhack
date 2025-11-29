package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ExistsExpression.class */
class ExistsExpression extends Expression {
    protected final Expression exp;

    ExistsExpression(Expression exp) {
        this.exp = exp;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel tm;
        if (this.exp instanceof ParentheticalExpression) {
            boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
            try {
                tm = this.exp.eval(env);
                env.setFastInvalidReferenceExceptions(lastFIRE);
            } catch (InvalidReferenceException e) {
                tm = null;
                env.setFastInvalidReferenceExceptions(lastFIRE);
            } catch (Throwable th) {
                env.setFastInvalidReferenceExceptions(lastFIRE);
                throw th;
            }
        } else {
            tm = this.exp.eval(env);
        }
        return tm == null ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return false;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ExistsExpression(this.exp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.exp.getCanonicalForm() + getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "??";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        return this.exp;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.LEFT_HAND_OPERAND;
    }
}
