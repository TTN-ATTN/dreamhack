package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsWithLazyConditionals.class */
final class BuiltInsWithLazyConditionals {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsWithLazyConditionals$then_BI.class */
    static class then_BI extends BuiltInWithParseTimeParameters {
        private Expression whenTrueExp;
        private Expression whenFalseExp;

        then_BI() {
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            boolean lho = this.target.evalToBoolean(env);
            return (lho ? this.whenTrueExp : this.whenFalseExp).evalToNonMissing(env);
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
            if (parameters.size() != 2) {
                throw newArgumentCountException("requires exactly 2", openParen, closeParen);
            }
            this.whenTrueExp = parameters.get(0);
            this.whenFalseExp = parameters.get(1);
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected Expression getArgumentParameterValue(int argIdx) {
            switch (argIdx) {
                case 0:
                    return this.whenTrueExp;
                case 1:
                    return this.whenFalseExp;
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected int getArgumentsCount() {
            return 2;
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected List<Expression> getArgumentsAsList() {
            ArrayList<Expression> args = new ArrayList<>(2);
            args.add(this.whenTrueExp);
            args.add(this.whenFalseExp);
            return args;
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected void cloneArguments(Expression cloneExp, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
            then_BI clone = (then_BI) cloneExp;
            clone.whenTrueExp = this.whenTrueExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
            clone.whenFalseExp = this.whenFalseExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
        }
    }

    private BuiltInsWithLazyConditionals() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsWithLazyConditionals$switch_BI.class */
    static class switch_BI extends BuiltInWithParseTimeParameters {
        private List<Expression> parameters;

        switch_BI() {
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
            if (parameters.size() < 2) {
                throw newArgumentCountException("must have at least 2", openParen, closeParen);
            }
            this.parameters = parameters;
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected List<Expression> getArgumentsAsList() {
            return this.parameters;
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected int getArgumentsCount() {
            return this.parameters.size();
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected Expression getArgumentParameterValue(int argIdx) {
            return this.parameters.get(argIdx);
        }

        @Override // freemarker.core.BuiltInWithParseTimeParameters
        protected void cloneArguments(Expression clone, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
            List<Expression> parametersClone = new ArrayList<>(this.parameters.size());
            for (Expression parameter : this.parameters) {
                parametersClone.add(parameter.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
            }
            ((switch_BI) clone).parameters = parametersClone;
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel targetValue = this.target.evalToNonMissing(env);
            List<Expression> parameters = this.parameters;
            int paramCnt = parameters.size();
            for (int i = 0; i + 1 < paramCnt; i += 2) {
                Expression caseExp = parameters.get(i);
                TemplateModel caseValue = caseExp.evalToNonMissing(env);
                if (EvalUtil.compare(targetValue, this.target, 1, "==", caseValue, caseExp, this, true, false, false, false, env)) {
                    return parameters.get(i + 1).evalToNonMissing(env);
                }
            }
            if (paramCnt % 2 == 0) {
                throw new _MiscTemplateException(this.target, "The value before ?", this.key, "(case1, value1, case2, value2, ...) didn't match any of the case parameters, and there was no default value parameter (an additional last parameter) eithter. ");
            }
            return parameters.get(paramCnt - 1).evalToNonMissing(env);
        }
    }
}
