package freemarker.core;

import freemarker.core.IteratorBlock;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables.class */
class BuiltInsForLoopVariables {
    BuiltInsForLoopVariables() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$indexBI.class */
    static class indexBI extends BuiltInForLoopVariable {
        indexBI() {
        }

        @Override // freemarker.core.BuiltInForLoopVariable
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return new SimpleNumber(iterCtx.getIndex());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$counterBI.class */
    static class counterBI extends BuiltInForLoopVariable {
        counterBI() {
        }

        @Override // freemarker.core.BuiltInForLoopVariable
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return new SimpleNumber(iterCtx.getIndex() + 1);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$BooleanBuiltInForLoopVariable.class */
    static abstract class BooleanBuiltInForLoopVariable extends BuiltInForLoopVariable {
        protected abstract boolean calculateBooleanResult(IteratorBlock.IterationContext iterationContext, Environment environment);

        BooleanBuiltInForLoopVariable() {
        }

        @Override // freemarker.core.BuiltInForLoopVariable
        final TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return calculateBooleanResult(iterCtx, env) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$has_nextBI.class */
    static class has_nextBI extends BooleanBuiltInForLoopVariable {
        has_nextBI() {
        }

        @Override // freemarker.core.BuiltInsForLoopVariables.BooleanBuiltInForLoopVariable
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.hasNext();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$is_lastBI.class */
    static class is_lastBI extends BooleanBuiltInForLoopVariable {
        is_lastBI() {
        }

        @Override // freemarker.core.BuiltInsForLoopVariables.BooleanBuiltInForLoopVariable
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return !iterCtx.hasNext();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$is_firstBI.class */
    static class is_firstBI extends BooleanBuiltInForLoopVariable {
        is_firstBI() {
        }

        @Override // freemarker.core.BuiltInsForLoopVariables.BooleanBuiltInForLoopVariable
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.getIndex() == 0;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$is_odd_itemBI.class */
    static class is_odd_itemBI extends BooleanBuiltInForLoopVariable {
        is_odd_itemBI() {
        }

        @Override // freemarker.core.BuiltInsForLoopVariables.BooleanBuiltInForLoopVariable
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.getIndex() % 2 == 0;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$is_even_itemBI.class */
    static class is_even_itemBI extends BooleanBuiltInForLoopVariable {
        is_even_itemBI() {
        }

        @Override // freemarker.core.BuiltInsForLoopVariables.BooleanBuiltInForLoopVariable
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.getIndex() % 2 != 0;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$item_parityBI.class */
    static class item_parityBI extends BuiltInForLoopVariable {
        private static final SimpleScalar ODD = new SimpleScalar("odd");
        private static final SimpleScalar EVEN = new SimpleScalar("even");

        item_parityBI() {
        }

        @Override // freemarker.core.BuiltInForLoopVariable
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return iterCtx.getIndex() % 2 == 0 ? ODD : EVEN;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$item_parity_capBI.class */
    static class item_parity_capBI extends BuiltInForLoopVariable {
        private static final SimpleScalar ODD = new SimpleScalar("Odd");
        private static final SimpleScalar EVEN = new SimpleScalar("Even");

        item_parity_capBI() {
        }

        @Override // freemarker.core.BuiltInForLoopVariable
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return iterCtx.getIndex() % 2 == 0 ? ODD : EVEN;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$item_cycleBI.class */
    static class item_cycleBI extends BuiltInForLoopVariable {
        item_cycleBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForLoopVariables$item_cycleBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private final IteratorBlock.IterationContext iterCtx;

            private BIMethod(IteratorBlock.IterationContext iterCtx) {
                this.iterCtx = iterCtx;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                item_cycleBI.this.checkMethodArgCount(args, 1, Integer.MAX_VALUE);
                return args.get(this.iterCtx.getIndex() % args.size());
            }
        }

        @Override // freemarker.core.BuiltInForLoopVariable
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return new BIMethod(iterCtx);
        }
    }
}
