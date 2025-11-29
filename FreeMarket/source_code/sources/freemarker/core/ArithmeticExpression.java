package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ArithmeticExpression.class */
final class ArithmeticExpression extends Expression {
    static final int TYPE_SUBSTRACTION = 0;
    static final int TYPE_MULTIPLICATION = 1;
    static final int TYPE_DIVISION = 2;
    static final int TYPE_MODULO = 3;
    private static final char[] OPERATOR_IMAGES = {'-', '*', '/', '%'};
    private final Expression lho;
    private final Expression rho;
    private final int operator;

    ArithmeticExpression(Expression lho, Expression rho, int operator) {
        this.lho = lho;
        this.rho = rho;
        this.operator = operator;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return _eval(env, this, this.lho.evalToNumber(env), this.operator, this.rho.evalToNumber(env));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v6, types: [java.lang.String[]] */
    static TemplateModel _eval(Environment environment, TemplateObject templateObject, Number number, int i, Number number2) throws TemplateException {
        ArithmeticEngine arithmeticEngine = EvalUtil.getArithmeticEngine(environment, templateObject);
        try {
            switch (i) {
                case 0:
                    return new SimpleNumber(arithmeticEngine.subtract(number, number2));
                case 1:
                    return new SimpleNumber(arithmeticEngine.multiply(number, number2));
                case 2:
                    return new SimpleNumber(arithmeticEngine.divide(number, number2));
                case 3:
                    return new SimpleNumber(arithmeticEngine.modulus(number, number2));
                default:
                    if (templateObject instanceof Expression) {
                        throw new _MiscTemplateException((Expression) templateObject, "Unknown operation: ", Integer.valueOf(i));
                    }
                    throw new _MiscTemplateException("Unknown operation: ", Integer.valueOf(i));
            }
        } catch (ArithmeticException e) {
            Object[] objArr = new Object[2];
            objArr[0] = "Arithmetic operation failed";
            objArr[1] = e.getMessage() != null ? new String[]{": ", e.getMessage()} : " (see cause exception)";
            throw new _MiscTemplateException(e, environment, objArr);
        }
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.lho.getCanonicalForm() + ' ' + getOperatorSymbol(this.operator) + ' ' + this.rho.getCanonicalForm();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return String.valueOf(getOperatorSymbol(this.operator));
    }

    static char getOperatorSymbol(int operator) {
        return OPERATOR_IMAGES[operator];
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return this.constantValue != null || (this.lho.isLiteral() && this.rho.isLiteral());
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ArithmeticExpression(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.operator);
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 3;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.lho;
            case 1:
                return this.rho;
            case 2:
                return Integer.valueOf(this.operator);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.LEFT_HAND_OPERAND;
            case 1:
                return ParameterRole.RIGHT_HAND_OPERAND;
            case 2:
                return ParameterRole.AST_NODE_SUBTYPE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
