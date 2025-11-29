package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Identifier.class */
final class Identifier extends Expression {
    private final String name;

    Identifier(String name) {
        this.name = name;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        try {
            return env.getVariable(this.name);
        } catch (NullPointerException e) {
            if (env == null) {
                throw new _MiscTemplateException("Variables are not available (certainly you are in a parse-time executed directive). The name of the variable you tried to read: ", this.name);
            }
            throw e;
        }
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return _CoreStringUtils.toFTLTopLevelIdentifierReference(this.name);
    }

    String getName() {
        return this.name;
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return getCanonicalForm();
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return false;
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

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        if (this.name.equals(replacedIdentifier)) {
            if (replacementState.replacementAlreadyInUse) {
                Expression clone = replacement.deepCloneWithIdentifierReplaced(null, null, replacementState);
                clone.copyLocationFrom(replacement);
                return clone;
            }
            replacementState.replacementAlreadyInUse = true;
            return replacement;
        }
        return new Identifier(this.name);
    }
}
