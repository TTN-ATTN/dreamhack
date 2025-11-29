package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AssignmentInstruction.class */
final class AssignmentInstruction extends TemplateElement {
    private int scope;
    private Expression namespaceExp;

    AssignmentInstruction(int scope) {
        this.scope = scope;
        setChildBufferCapacity(1);
    }

    void addAssignment(Assignment assignment) {
        addChild(assignment);
    }

    void setNamespaceExp(Expression namespaceExp) {
        this.namespaceExp = namespaceExp;
        int ln = getChildCount();
        for (int i = 0; i < ln; i++) {
            ((Assignment) getChild(i)).setNamespaceExp(namespaceExp);
        }
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return getChildBuffer();
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(Assignment.getDirectiveName(this.scope));
        if (canonical) {
            buf.append(' ');
            int ln = getChildCount();
            for (int i = 0; i < ln; i++) {
                if (i != 0) {
                    buf.append(", ");
                }
                Assignment assignment = (Assignment) getChild(i);
                buf.append(assignment.getCanonicalForm());
            }
        } else {
            buf.append("-container");
        }
        if (this.namespaceExp != null) {
            buf.append(" in ");
            buf.append(this.namespaceExp.getCanonicalForm());
        }
        if (canonical) {
            buf.append(">");
        }
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return Integer.valueOf(this.scope);
            case 1:
                return this.namespaceExp;
            default:
                return null;
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.VARIABLE_SCOPE;
            case 1:
                return ParameterRole.NAMESPACE;
            default:
                return null;
        }
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return Assignment.getDirectiveName(this.scope);
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
