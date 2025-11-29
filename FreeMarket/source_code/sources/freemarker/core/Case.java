package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Case.class */
final class Case extends TemplateElement {
    static final int TYPE_CASE = 0;
    static final int TYPE_DEFAULT = 1;
    Expression condition;

    Case(Expression matchingValue, TemplateElements children) {
        this.condition = matchingValue;
        setChildren(children);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) {
        return getChildBuffer();
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        if (this.condition != null) {
            sb.append(' ');
            sb.append(this.condition.getCanonicalForm());
        }
        if (canonical) {
            sb.append('>');
            sb.append(getChildrenCanonicalForm());
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return this.condition != null ? "#case" : "#default";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.condition;
            case 1:
                return Integer.valueOf(this.condition != null ? 0 : 1);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.CONDITION;
            case 1:
                return ParameterRole.AST_NODE_SUBTYPE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
