package freemarker.core;

import freemarker.core.IteratorBlock;
import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Items.class */
class Items extends TemplateElement {
    private final String loopVarName;
    private final String loopVar2Name;

    Items(String loopVarName, String loopVar2Name, TemplateElements children) {
        this.loopVarName = loopVarName;
        this.loopVar2Name = loopVar2Name;
        setChildren(children);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        IteratorBlock.IterationContext iterCtx = env.findClosestEnclosingIterationContext();
        if (iterCtx == null) {
            throw new _MiscTemplateException(env, getNodeTypeSymbol(), " without iteration in context");
        }
        iterCtx.loopForItemsElement(env, getChildBuffer(), this.loopVarName, this.loopVar2Name);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return true;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        sb.append(" as ");
        sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVarName));
        if (this.loopVar2Name != null) {
            sb.append(", ");
            sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar2Name));
        }
        if (canonical) {
            sb.append('>');
            sb.append(getChildrenCanonicalForm());
            sb.append("</");
            sb.append(getNodeTypeSymbol());
            sb.append('>');
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#items";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return this.loopVar2Name != null ? 2 : 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                if (this.loopVarName == null) {
                    throw new IndexOutOfBoundsException();
                }
                return this.loopVarName;
            case 1:
                if (this.loopVar2Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return this.loopVar2Name;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                if (this.loopVarName == null) {
                    throw new IndexOutOfBoundsException();
                }
                return ParameterRole.TARGET_LOOP_VARIABLE;
            case 1:
                if (this.loopVar2Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return ParameterRole.TARGET_LOOP_VARIABLE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
