package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import freemarker.core.Expression;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInWithParseTimeParameters.class */
abstract class BuiltInWithParseTimeParameters extends SpecialBuiltIn {
    abstract void bindToParameters(List<Expression> list, Token token, Token token2) throws ParseException;

    protected abstract List<Expression> getArgumentsAsList();

    protected abstract int getArgumentsCount();

    protected abstract Expression getArgumentParameterValue(int i);

    protected abstract void cloneArguments(Expression expression, String str, Expression expression2, Expression.ReplacemenetState replacemenetState);

    BuiltInWithParseTimeParameters() {
    }

    @Override // freemarker.core.BuiltIn, freemarker.core.TemplateObject
    public String getCanonicalForm() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.getCanonicalForm());
        buf.append("(");
        List<Expression> args = getArgumentsAsList();
        int size = args.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                buf.append(", ");
            }
            Expression arg = args.get(i);
            buf.append(arg.getCanonicalForm());
        }
        buf.append(")");
        return buf.toString();
    }

    @Override // freemarker.core.BuiltIn, freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return super.getNodeTypeSymbol() + "(...)";
    }

    @Override // freemarker.core.BuiltIn, freemarker.core.TemplateObject
    int getParameterCount() {
        return super.getParameterCount() + getArgumentsCount();
    }

    @Override // freemarker.core.BuiltIn, freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        int superParamCnt = super.getParameterCount();
        if (idx < superParamCnt) {
            return super.getParameterValue(idx);
        }
        int argIdx = idx - superParamCnt;
        return getArgumentParameterValue(argIdx);
    }

    @Override // freemarker.core.BuiltIn, freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        int superParamCnt = super.getParameterCount();
        if (idx < superParamCnt) {
            return super.getParameterRole(idx);
        }
        if (idx - superParamCnt < getArgumentsCount()) {
            return ParameterRole.ARGUMENT_VALUE;
        }
        throw new IndexOutOfBoundsException();
    }

    protected final ParseException newArgumentCountException(String ordinalityDesc, Token openParen, Token closeParen) {
        return new ParseException(CallerData.NA + this.key + "(...) " + ordinalityDesc + " parameters", getTemplate(), openParen.beginLine, openParen.beginColumn, closeParen.endLine, closeParen.endColumn);
    }

    protected final void checkLocalLambdaParamCount(LocalLambdaExpression localLambdaExp, int expectedParamCount) throws ParseException {
        int actualParamCount = localLambdaExp.getLambdaParameterList().getParameters().size();
        if (actualParamCount != expectedParamCount) {
            throw new ParseException(CallerData.NA + this.key + "(...) parameter lambda expression must declare exactly " + expectedParamCount + " parameter" + (expectedParamCount > 1 ? "s" : "") + ", but it declared " + actualParamCount + ".", localLambdaExp);
        }
    }

    @Override // freemarker.core.BuiltIn, freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        Expression clone = super.deepCloneWithIdentifierReplaced_inner(replacedIdentifier, replacement, replacementState);
        cloneArguments(clone, replacedIdentifier, replacement, replacementState);
        return clone;
    }

    protected boolean isLocalLambdaParameterSupported() {
        return false;
    }
}
