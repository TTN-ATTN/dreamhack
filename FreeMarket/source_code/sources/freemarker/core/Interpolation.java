package freemarker.core;

import freemarker.template.TemplateException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Interpolation.class */
abstract class Interpolation extends TemplateElement {
    protected abstract String dump(boolean z, boolean z2);

    protected abstract Object calculateInterpolatedStringOrMarkup(Environment environment) throws TemplateException;

    Interpolation() {
    }

    @Override // freemarker.core.TemplateElement
    protected final String dump(boolean canonical) {
        return dump(canonical, false);
    }

    final String getCanonicalFormInStringLiteral() {
        return dump(true, true);
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }
}
