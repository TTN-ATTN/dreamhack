package freemarker.core;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.StringWriter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BlockAssignment.class */
final class BlockAssignment extends TemplateElement {
    private final String varName;
    private final Expression namespaceExp;
    private final int scope;
    private final MarkupOutputFormat<?> markupOutputFormat;

    BlockAssignment(TemplateElements children, String varName, int scope, Expression namespaceExp, MarkupOutputFormat<?> markupOutputFormat) {
        setChildren(children);
        this.varName = varName;
        this.namespaceExp = namespaceExp;
        this.scope = scope;
        this.markupOutputFormat = markupOutputFormat;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        TemplateModel value;
        TemplateElement[] children = getChildBuffer();
        if (children != null) {
            StringWriter out = new StringWriter();
            env.visit(children, out);
            value = capturedStringToModel(out.toString());
        } else {
            value = capturedStringToModel("");
        }
        if (this.namespaceExp != null) {
            TemplateModel uncheckedNamespace = this.namespaceExp.eval(env);
            try {
                Environment.Namespace namespace = (Environment.Namespace) uncheckedNamespace;
                if (namespace == null) {
                    throw InvalidReferenceException.getInstance(this.namespaceExp, env);
                }
                namespace.put(this.varName, value);
                return null;
            } catch (ClassCastException e) {
                throw new NonNamespaceException(this.namespaceExp, uncheckedNamespace, env);
            }
        }
        if (this.scope == 1) {
            env.setVariable(this.varName, value);
            return null;
        }
        if (this.scope == 3) {
            env.setGlobalVariable(this.varName, value);
            return null;
        }
        if (this.scope == 2) {
            env.setLocalVariable(this.varName, value);
            return null;
        }
        throw new BugException("Unhandled scope");
    }

    private TemplateModel capturedStringToModel(String s) throws TemplateModelException {
        return this.markupOutputFormat == null ? new SimpleScalar(s) : this.markupOutputFormat.fromMarkup(s);
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append("<");
        }
        sb.append(getNodeTypeSymbol());
        sb.append(' ');
        sb.append(this.varName);
        if (this.namespaceExp != null) {
            sb.append(" in ");
            sb.append(this.namespaceExp.getCanonicalForm());
        }
        if (canonical) {
            sb.append('>');
            sb.append(getChildrenCanonicalForm());
            sb.append("</");
            sb.append(getNodeTypeSymbol());
            sb.append('>');
        } else {
            sb.append(" = .nested_output");
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return Assignment.getDirectiveName(this.scope);
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 3;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.varName;
            case 1:
                return Integer.valueOf(this.scope);
            case 2:
                return this.namespaceExp;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.ASSIGNMENT_TARGET;
            case 1:
                return ParameterRole.VARIABLE_SCOPE;
            case 2:
                return ParameterRole.NAMESPACE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
