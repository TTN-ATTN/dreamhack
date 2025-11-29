package freemarker.template;

import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/MethodCallAwareTemplateHashModel.class */
public interface MethodCallAwareTemplateHashModel extends TemplateHashModel {
    TemplateModel getBeforeMethodCall(String str) throws TemplateModelException, ShouldNotBeGetAsMethodException;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/MethodCallAwareTemplateHashModel$ShouldNotBeGetAsMethodException.class */
    public static final class ShouldNotBeGetAsMethodException extends Exception {
        private final TemplateModel actualValue;
        private final String hint;

        public ShouldNotBeGetAsMethodException(TemplateModel actualValue, String hint) {
            this(actualValue, hint, null);
        }

        public ShouldNotBeGetAsMethodException(TemplateModel actualValue, String hint, Throwable cause) {
            super(null, cause, true, false);
            NullArgumentException.check(actualValue);
            this.actualValue = actualValue;
            this.hint = hint;
        }

        public TemplateModel getActualValue() {
            return this.actualValue;
        }

        public String getHint() {
            return this.hint;
        }
    }
}
