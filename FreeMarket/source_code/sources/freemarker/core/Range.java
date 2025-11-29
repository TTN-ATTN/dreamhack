package freemarker.core;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Range.class */
final class Range extends Expression {
    static final int END_INCLUSIVE = 0;
    static final int END_EXCLUSIVE = 1;
    static final int END_UNBOUND = 2;
    static final int END_SIZE_LIMITED = 3;
    final Expression lho;
    final Expression rho;
    final int endType;

    Range(Expression lho, Expression rho, int endType) {
        this.lho = lho;
        this.rho = rho;
        this.endType = endType;
    }

    int getEndType() {
        return this.endType;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        int begin = this.lho.evalToNumber(env).intValue();
        if (this.endType == 2) {
            return _TemplateAPI.getTemplateLanguageVersionAsInt(this) >= _VersionInts.V_2_3_21 ? new ListableRightUnboundedRangeModel(begin) : new NonListableRightUnboundedRangeModel(begin);
        }
        int lhoValue = this.rho.evalToNumber(env).intValue();
        return new BoundedRangeModel(begin, this.endType != 3 ? lhoValue : begin + lhoValue, this.endType == 0, this.endType == 3);
    }

    @Override // freemarker.core.Expression
    boolean evalToBoolean(Environment env) throws TemplateException {
        throw new NonBooleanException(this, new BoundedRangeModel(0, 0, false, false), env);
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        String rhs = this.rho != null ? this.rho.getCanonicalForm() : "";
        return this.lho.getCanonicalForm() + getNodeTypeSymbol() + rhs;
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        switch (this.endType) {
            case 0:
                return CallerDataConverter.DEFAULT_RANGE_DELIMITER;
            case 1:
                return "..<";
            case 2:
                return CallerDataConverter.DEFAULT_RANGE_DELIMITER;
            case 3:
                return "..*";
            default:
                throw new BugException(this.endType);
        }
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        boolean rightIsLiteral = this.rho == null || this.rho.isLiteral();
        return this.constantValue != null || (this.lho.isLiteral() && rightIsLiteral);
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new Range(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.endType);
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.lho;
            case 1:
                return this.rho;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }
}
