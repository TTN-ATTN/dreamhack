package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LocalLambdaExpression.class */
final class LocalLambdaExpression extends Expression {
    private final LambdaParameterList lho;
    private final Expression rho;

    LocalLambdaExpression(LambdaParameterList lho, Expression rho) {
        this.lho = lho;
        this.rho = rho;
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.lho.getCanonicalForm() + " -> " + this.rho.getCanonicalForm();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "->";
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        throw new TemplateException("Can't get lambda expression as a value: Lambdas currently can only be used on a few special places.", env);
    }

    TemplateModel invokeLambdaDefinedFunction(TemplateModel argValue, Environment env) throws TemplateException {
        return env.evaluateWithNewLocal(this.rho, this.lho.getParameters().get(0).getName(), argValue != null ? argValue : TemplateNullModel.INSTANCE);
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return false;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        for (Identifier parameter : this.lho.getParameters()) {
            if (parameter.getName().equals(replacedIdentifier)) {
                throw new UncheckedParseException(new ParseException("Escape placeholder (" + replacedIdentifier + ") can't be used in the parameter list of a lambda expressions.", this));
            }
        }
        return new LocalLambdaExpression(this.lho, this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return this.lho.getParameters().size() + 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        int paramCount = getParameterCount();
        if (idx < paramCount - 1) {
            return this.lho.getParameters().get(idx);
        }
        if (idx == paramCount - 1) {
            return this.rho;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        int paramCount = getParameterCount();
        if (idx < paramCount - 1) {
            return ParameterRole.ARGUMENT_NAME;
        }
        if (idx == paramCount - 1) {
            return ParameterRole.VALUE;
        }
        throw new IndexOutOfBoundsException();
    }

    LambdaParameterList getLambdaParameterList() {
        return this.lho;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LocalLambdaExpression$LambdaParameterList.class */
    static class LambdaParameterList {
        private final Token openingParenthesis;
        private final Token closingParenthesis;
        private final List<Identifier> parameters;

        public LambdaParameterList(Token openingParenthesis, List<Identifier> parameters, Token closingParenthesis) {
            this.openingParenthesis = openingParenthesis;
            this.closingParenthesis = closingParenthesis;
            this.parameters = parameters;
        }

        public Token getOpeningParenthesis() {
            return this.openingParenthesis;
        }

        public Token getClosingParenthesis() {
            return this.closingParenthesis;
        }

        public List<Identifier> getParameters() {
            return this.parameters;
        }

        public String getCanonicalForm() {
            if (this.parameters.size() == 1) {
                return this.parameters.get(0).getCanonicalForm();
            }
            StringBuilder sb = new StringBuilder();
            sb.append('(');
            for (int i = 0; i < this.parameters.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                Identifier parameter = this.parameters.get(i);
                sb.append(parameter.getCanonicalForm());
            }
            sb.append(')');
            return sb.toString();
        }
    }
}
