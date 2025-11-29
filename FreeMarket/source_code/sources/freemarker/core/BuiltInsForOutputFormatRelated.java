package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForOutputFormatRelated.class */
class BuiltInsForOutputFormatRelated {
    BuiltInsForOutputFormatRelated() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForOutputFormatRelated$no_escBI.class */
    static class no_escBI extends AbstractConverterBI implements BuiltInBannedWhenForcedAutoEscaping {
        no_escBI() {
        }

        @Override // freemarker.core.BuiltInsForOutputFormatRelated.AbstractConverterBI
        protected TemplateModel calculateResult(String lho, MarkupOutputFormat outputFormat, Environment env) throws TemplateException {
            return outputFormat.fromMarkup(lho);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForOutputFormatRelated$escBI.class */
    static class escBI extends AbstractConverterBI {
        escBI() {
        }

        @Override // freemarker.core.BuiltInsForOutputFormatRelated.AbstractConverterBI
        protected TemplateModel calculateResult(String lho, MarkupOutputFormat outputFormat, Environment env) throws TemplateException {
            return outputFormat.fromPlainTextByEscaping(lho);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForOutputFormatRelated$AbstractConverterBI.class */
    static abstract class AbstractConverterBI extends MarkupOutputFormatBoundBuiltIn {
        protected abstract TemplateModel calculateResult(String str, MarkupOutputFormat markupOutputFormat, Environment environment) throws TemplateException;

        AbstractConverterBI() {
        }

        @Override // freemarker.core.MarkupOutputFormatBoundBuiltIn
        protected TemplateModel calculateResult(Environment env) throws TemplateException {
            TemplateModel lhoTM = this.target.eval(env);
            Object lhoMOOrStr = EvalUtil.coerceModelToStringOrMarkup(lhoTM, this.target, null, env);
            MarkupOutputFormat contextOF = this.outputFormat;
            if (lhoMOOrStr instanceof String) {
                return calculateResult((String) lhoMOOrStr, contextOF, env);
            }
            TemplateMarkupOutputModel lhoMO = (TemplateMarkupOutputModel) lhoMOOrStr;
            MarkupOutputFormat lhoOF = lhoMO.getOutputFormat();
            if (lhoOF == contextOF || contextOF.isOutputFormatMixingAllowed()) {
                return lhoMO;
            }
            String lhoPlainTtext = lhoOF.getSourcePlainText(lhoMO);
            if (lhoPlainTtext == null) {
                throw new _TemplateModelException(this.target, "The left side operand of ?", this.key, " is in ", new _DelayedToString(lhoOF), " format, which differs from the current output format, ", new _DelayedToString(contextOF), ". Conversion wasn't possible.");
            }
            return contextOF.fromPlainTextByEscaping(lhoPlainTtext);
        }
    }
}
