package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import org.slf4j.Marker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnaryPlusMinusExpression.class */
final class UnaryPlusMinusExpression extends Expression {
    private static final int TYPE_MINUS = 0;
    private static final int TYPE_PLUS = 1;
    private final Expression target;
    private final boolean isMinus;
    private static final Integer MINUS_ONE = -1;

    UnaryPlusMinusExpression(Expression target, boolean isMinus) {
        this.target = target;
        this.isMinus = isMinus;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel tm = this.target.eval(env);
        try {
            TemplateNumberModel targetModel = (TemplateNumberModel) tm;
            if (!this.isMinus) {
                return targetModel;
            }
            this.target.assertNonNull(targetModel, env);
            Number n = targetModel.getAsNumber();
            return new SimpleNumber(ArithmeticEngine.CONSERVATIVE_ENGINE.multiply(MINUS_ONE, n));
        } catch (ClassCastException e) {
            throw new NonNumericalException(this.target, tm, env);
        }
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        String op = this.isMinus ? "-" : Marker.ANY_NON_NULL_MARKER;
        return op + this.target.getCanonicalForm();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return this.isMinus ? "-..." : "+...";
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return this.target.isLiteral();
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new UnaryPlusMinusExpression(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.isMinus);
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.target;
            case 1:
                return Integer.valueOf(this.isMinus ? 0 : 1);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.RIGHT_HAND_OPERAND;
            case 1:
                return ParameterRole.AST_NODE_SUBTYPE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
