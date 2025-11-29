package freemarker.core;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/RecurseNode.class */
final class RecurseNode extends TemplateElement {
    Expression targetNode;
    Expression namespaces;

    RecurseNode(Expression targetNode, Expression namespaces) {
        this.targetNode = targetNode;
        this.namespaces = namespaces;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        TemplateModel node = this.targetNode == null ? null : this.targetNode.eval(env);
        if (node != null && !(node instanceof TemplateNodeModel)) {
            throw new NonNodeException(this.targetNode, node, "node", env);
        }
        TemplateModel nss = this.namespaces == null ? null : this.namespaces.eval(env);
        if (this.namespaces instanceof StringLiteral) {
            nss = env.importLib(((TemplateScalarModel) nss).getAsString(), (String) null);
        } else if (this.namespaces instanceof ListLiteral) {
            nss = ((ListLiteral) this.namespaces).evaluateStringsToNamespaces(env);
        }
        if (nss != null) {
            if (nss instanceof TemplateHashModel) {
                SimpleSequence ss = new SimpleSequence(1, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
                ss.add(nss);
                nss = ss;
            } else if (!(nss instanceof TemplateSequenceModel)) {
                if (this.namespaces != null) {
                    throw new NonSequenceException(this.namespaces, nss, env);
                }
                throw new _MiscTemplateException(env, "Expecting a sequence of namespaces after \"using\"");
            }
        }
        env.recurse((TemplateNodeModel) node, (TemplateSequenceModel) nss);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        if (this.targetNode != null) {
            sb.append(' ');
            sb.append(this.targetNode.getCanonicalForm());
        }
        if (this.namespaces != null) {
            sb.append(" using ");
            sb.append(this.namespaces.getCanonicalForm());
        }
        if (canonical) {
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#recurse";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.targetNode;
            case 1:
                return this.namespaces;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.NODE;
            case 1:
                return ParameterRole.NAMESPACE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }
}
