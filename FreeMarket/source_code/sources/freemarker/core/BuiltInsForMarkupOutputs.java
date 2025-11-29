package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForMarkupOutputs.class */
class BuiltInsForMarkupOutputs {
    BuiltInsForMarkupOutputs() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForMarkupOutputs$markup_stringBI.class */
    static class markup_stringBI extends BuiltInForMarkupOutput {
        markup_stringBI() {
        }

        @Override // freemarker.core.BuiltInForMarkupOutput
        protected TemplateModel calculateResult(TemplateMarkupOutputModel model) throws TemplateModelException {
            return new SimpleScalar(model.getOutputFormat().getMarkupString(model));
        }
    }
}
