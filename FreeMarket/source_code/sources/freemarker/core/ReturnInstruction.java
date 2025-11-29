package freemarker.core;

import freemarker.template.TemplateException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ReturnInstruction.class */
public final class ReturnInstruction extends TemplateElement {
    private Expression exp;

    ReturnInstruction(Expression exp) {
        this.exp = exp;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException {
        if (this.exp != null) {
            env.setLastReturnValue(this.exp.eval(env));
        }
        if (nextSibling() == null && (getParentElement() instanceof Macro)) {
            return null;
        }
        throw Return.INSTANCE;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        if (this.exp != null) {
            sb.append(' ');
            sb.append(this.exp.getCanonicalForm());
        }
        if (canonical) {
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#return";
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ReturnInstruction$Return.class */
    public static class Return extends FlowControlException {
        static final Return INSTANCE = new Return();

        private Return() {
        }
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
        return this.exp;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.VALUE;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
