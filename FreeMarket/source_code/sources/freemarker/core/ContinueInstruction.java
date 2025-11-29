package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ContinueInstruction.class */
final class ContinueInstruction extends TemplateElement {
    ContinueInstruction() {
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) {
        throw BreakOrContinueException.CONTINUE_INSTANCE;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        return canonical ? "<" + getNodeTypeSymbol() + "/>" : getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#continue";
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
}
