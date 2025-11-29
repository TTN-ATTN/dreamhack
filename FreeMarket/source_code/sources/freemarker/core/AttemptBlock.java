package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AttemptBlock.class */
final class AttemptBlock extends TemplateElement {
    private TemplateElement attemptedSection;
    private RecoveryBlock recoverySection;

    AttemptBlock(TemplateElements attemptedSectionChildren, RecoveryBlock recoverySection) {
        TemplateElement attemptedSection = attemptedSectionChildren.asSingleElement();
        this.attemptedSection = attemptedSection;
        this.recoverySection = recoverySection;
        setChildBufferCapacity(2);
        addChild(attemptedSection);
        addChild(recoverySection);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        env.visitAttemptRecover(this, this.attemptedSection, this.recoverySection);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (!canonical) {
            return getNodeTypeSymbol();
        }
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getNodeTypeSymbol()).append(">");
        buf.append(getChildrenCanonicalForm());
        buf.append("</").append(getNodeTypeSymbol()).append(">");
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.recoverySection;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.ERROR_HANDLER;
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#attempt";
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
