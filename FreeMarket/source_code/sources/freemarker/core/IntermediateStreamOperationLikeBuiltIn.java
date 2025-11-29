package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.util.Collections;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/IntermediateStreamOperationLikeBuiltIn.class */
abstract class IntermediateStreamOperationLikeBuiltIn extends BuiltInWithParseTimeParameters {
    private Expression elementTransformerExp;
    private ElementTransformer precreatedElementTransformer;
    private boolean lazilyGeneratedResultEnabled;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/IntermediateStreamOperationLikeBuiltIn$ElementTransformer.class */
    interface ElementTransformer {
        TemplateModel transformElement(TemplateModel templateModel, Environment environment) throws TemplateException;
    }

    protected abstract TemplateModel calculateResult(TemplateModelIterator templateModelIterator, TemplateModel templateModel, boolean z, ElementTransformer elementTransformer, Environment environment) throws TemplateException;

    IntermediateStreamOperationLikeBuiltIn() {
    }

    @Override // freemarker.core.BuiltInWithParseTimeParameters
    void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
        if (parameters.size() != 1) {
            throw newArgumentCountException("requires exactly 1", openParen, closeParen);
        }
        Expression elementTransformerExp = parameters.get(0);
        setElementTransformerExp(elementTransformerExp);
    }

    private void setElementTransformerExp(Expression elementTransformerExp) throws ParseException {
        this.elementTransformerExp = elementTransformerExp;
        if (this.elementTransformerExp instanceof LocalLambdaExpression) {
            LocalLambdaExpression localLambdaExp = (LocalLambdaExpression) this.elementTransformerExp;
            checkLocalLambdaParamCount(localLambdaExp, 1);
            this.precreatedElementTransformer = new LocalLambdaElementTransformer(localLambdaExp);
        }
    }

    @Override // freemarker.core.BuiltInWithParseTimeParameters
    protected final boolean isLocalLambdaParameterSupported() {
        return true;
    }

    @Override // freemarker.core.Expression
    final void enableLazilyGeneratedResult() {
        this.lazilyGeneratedResultEnabled = true;
    }

    protected final boolean isLazilyGeneratedResultEnabled() {
        return this.lazilyGeneratedResultEnabled;
    }

    @Override // freemarker.core.BuiltIn
    protected void setTarget(Expression target) {
        super.setTarget(target);
        target.enableLazilyGeneratedResult();
    }

    @Override // freemarker.core.BuiltInWithParseTimeParameters
    protected List<Expression> getArgumentsAsList() {
        return Collections.singletonList(this.elementTransformerExp);
    }

    @Override // freemarker.core.BuiltInWithParseTimeParameters
    protected int getArgumentsCount() {
        return 1;
    }

    @Override // freemarker.core.BuiltInWithParseTimeParameters
    protected Expression getArgumentParameterValue(int argIdx) {
        if (argIdx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.elementTransformerExp;
    }

    protected Expression getElementTransformerExp() {
        return this.elementTransformerExp;
    }

    @Override // freemarker.core.BuiltInWithParseTimeParameters
    protected void cloneArguments(Expression clone, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        try {
            ((IntermediateStreamOperationLikeBuiltIn) clone).setElementTransformerExp(this.elementTransformerExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        } catch (ParseException e) {
            throw new BugException("Deep-clone elementTransformerExp failed", e);
        }
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModelIterator targetIterator;
        boolean targetIsSequence;
        TemplateModel targetValue = this.target.eval(env);
        if (targetValue instanceof TemplateCollectionModel) {
            targetIterator = isLazilyGeneratedResultEnabled() ? new LazyCollectionTemplateModelIterator((TemplateCollectionModel) targetValue) : ((TemplateCollectionModel) targetValue).iterator();
            targetIsSequence = targetValue instanceof LazilyGeneratedCollectionModel ? ((LazilyGeneratedCollectionModel) targetValue).isSequence() : targetValue instanceof TemplateSequenceModel;
        } else if (targetValue instanceof TemplateSequenceModel) {
            targetIterator = new LazySequenceIterator((TemplateSequenceModel) targetValue);
            targetIsSequence = true;
        } else {
            throw new NonSequenceOrCollectionException(this.target, targetValue, env);
        }
        return calculateResult(targetIterator, targetValue, targetIsSequence, evalElementTransformerExp(env), env);
    }

    private ElementTransformer evalElementTransformerExp(Environment env) throws TemplateException {
        if (this.precreatedElementTransformer != null) {
            return this.precreatedElementTransformer;
        }
        TemplateModel elementTransformerModel = this.elementTransformerExp.eval(env);
        if (elementTransformerModel instanceof TemplateMethodModel) {
            return new MethodElementTransformer((TemplateMethodModel) elementTransformerModel);
        }
        if (elementTransformerModel instanceof Macro) {
            return new FunctionElementTransformer((Macro) elementTransformerModel, this.elementTransformerExp);
        }
        throw new NonMethodException(this.elementTransformerExp, elementTransformerModel, true, true, null, env);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/IntermediateStreamOperationLikeBuiltIn$LocalLambdaElementTransformer.class */
    private static class LocalLambdaElementTransformer implements ElementTransformer {
        private final LocalLambdaExpression elementTransformerExp;

        public LocalLambdaElementTransformer(LocalLambdaExpression elementTransformerExp) {
            this.elementTransformerExp = elementTransformerExp;
        }

        @Override // freemarker.core.IntermediateStreamOperationLikeBuiltIn.ElementTransformer
        public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateException {
            return this.elementTransformerExp.invokeLambdaDefinedFunction(element, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/IntermediateStreamOperationLikeBuiltIn$MethodElementTransformer.class */
    private static class MethodElementTransformer implements ElementTransformer {
        private final TemplateMethodModel elementTransformer;

        public MethodElementTransformer(TemplateMethodModel elementTransformer) {
            this.elementTransformer = elementTransformer;
        }

        @Override // freemarker.core.IntermediateStreamOperationLikeBuiltIn.ElementTransformer
        public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateModelException {
            Object result = this.elementTransformer.exec(Collections.singletonList(element));
            return result instanceof TemplateModel ? (TemplateModel) result : env.getObjectWrapper().wrap(result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/IntermediateStreamOperationLikeBuiltIn$FunctionElementTransformer.class */
    private static class FunctionElementTransformer implements ElementTransformer {
        private final Macro templateTransformer;
        private final Expression elementTransformerExp;

        public FunctionElementTransformer(Macro templateTransformer, Expression elementTransformerExp) {
            this.templateTransformer = templateTransformer;
            this.elementTransformerExp = elementTransformerExp;
        }

        @Override // freemarker.core.IntermediateStreamOperationLikeBuiltIn.ElementTransformer
        public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateException {
            ExpressionWithFixedResult functionArgExp = new ExpressionWithFixedResult(element, this.elementTransformerExp);
            return env.invokeFunction(env, this.templateTransformer, Collections.singletonList(functionArgExp), this.elementTransformerExp);
        }
    }
}
