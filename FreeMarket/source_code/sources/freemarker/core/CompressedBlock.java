package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StandardCompress;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CompressedBlock.class */
final class CompressedBlock extends TemplateElement {
    CompressedBlock(TemplateElements children) {
        setChildren(children);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        TemplateElement[] childBuffer = getChildBuffer();
        if (childBuffer != null) {
            env.visitAndTransform(childBuffer, StandardCompress.INSTANCE, null);
            return null;
        }
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            return "<" + getNodeTypeSymbol() + ">" + getChildrenCanonicalForm() + "</" + getNodeTypeSymbol() + ">";
        }
        return getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#compress";
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
        return getChildCount() == 0 && getParameterCount() == 0;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
