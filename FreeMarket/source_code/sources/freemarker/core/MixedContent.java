package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/MixedContent.class */
final class MixedContent extends TemplateElement {
    MixedContent() {
    }

    @Deprecated
    void addElement(TemplateElement element) {
        addChild(element);
    }

    @Deprecated
    void addElement(int index, TemplateElement element) {
        addChild(index, element);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
        super.postParseCleanup(stripWhitespace);
        return getChildCount() == 1 ? getChild(0) : this;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return getChildBuffer();
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            return getChildrenCanonicalForm();
        }
        if (getParentElement() == null) {
            return "root";
        }
        return getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateElement
    protected boolean isOutputCacheable() {
        int ln = getChildCount();
        for (int i = 0; i < ln; i++) {
            if (!getChild(i).isOutputCacheable()) {
                return false;
            }
        }
        return true;
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#mixed_content";
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
    boolean isIgnorable(boolean stripWhitespace) {
        return getChildCount() == 0;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
