package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/FallbackInstruction.class */
final class FallbackInstruction extends TemplateElement {
    FallbackInstruction() {
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        env.fallback();
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        return canonical ? "<" + getNodeTypeSymbol() + "/>" : getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#fallback";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 0;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }
}
