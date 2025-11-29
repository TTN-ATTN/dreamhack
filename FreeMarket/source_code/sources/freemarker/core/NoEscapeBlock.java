package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NoEscapeBlock.class */
class NoEscapeBlock extends TemplateElement {
    NoEscapeBlock(TemplateElements children) {
        setChildren(children);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return getChildBuffer();
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            return "<" + getNodeTypeSymbol() + '>' + getChildrenCanonicalForm() + "</" + getNodeTypeSymbol() + '>';
        }
        return getNodeTypeSymbol();
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

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#noescape";
    }

    @Override // freemarker.core.TemplateElement
    boolean isOutputCacheable() {
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
