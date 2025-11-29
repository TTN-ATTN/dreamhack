package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Dot.class */
class Dot extends Expression {
    private final Expression target;
    protected final String key;

    Dot(Expression target, String key) {
        this.target = target;
        this.key = key;
    }

    Dot(Dot dot) {
        this(dot.target, dot.key);
        this.constantValue = dot.constantValue;
        copyFieldsFrom(dot);
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel leftModel = this.target.eval(env);
        if (leftModel instanceof TemplateHashModel) {
            return evalOnHash((TemplateHashModel) leftModel);
        }
        if (leftModel == null && env.isClassicCompatible()) {
            return null;
        }
        throw new NonHashException(this.target, leftModel, env);
    }

    protected TemplateModel evalOnHash(TemplateHashModel leftModel) throws TemplateException {
        return leftModel.get(this.key);
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.target.getCanonicalForm() + getNodeTypeSymbol() + _CoreStringUtils.toFTLIdentifierReferenceAfterDot(this.key);
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return ".";
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return this.target.isLiteral();
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new Dot(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.key);
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        return idx == 0 ? this.target : this.key;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }

    String getRHO() {
        return this.key;
    }

    boolean onlyHasIdentifiers() {
        return (this.target instanceof Identifier) || ((this.target instanceof Dot) && ((Dot) this.target).onlyHasIdentifiers());
    }
}
