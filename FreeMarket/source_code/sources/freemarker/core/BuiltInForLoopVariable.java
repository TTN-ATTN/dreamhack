package freemarker.core;

import freemarker.core.IteratorBlock;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForLoopVariable.class */
abstract class BuiltInForLoopVariable extends SpecialBuiltIn {
    private String loopVarName;

    abstract TemplateModel calculateResult(IteratorBlock.IterationContext iterationContext, Environment environment) throws TemplateException;

    BuiltInForLoopVariable() {
    }

    void bindToLoopVariable(String loopVarName) {
        this.loopVarName = loopVarName;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        IteratorBlock.IterationContext iterCtx = env.findEnclosingIterationContextWithVisibleVariable(this.loopVarName);
        if (iterCtx == null) {
            throw new _MiscTemplateException(this, env, "There's no iteration in context that uses loop variable ", new _DelayedJQuote(this.loopVarName), ".");
        }
        return calculateResult(iterCtx, env);
    }
}
