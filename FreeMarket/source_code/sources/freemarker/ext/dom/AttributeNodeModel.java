package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateScalarModel;
import org.w3c.dom.Attr;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/AttributeNodeModel.class */
class AttributeNodeModel extends NodeModel implements TemplateScalarModel {
    public AttributeNodeModel(Attr att) {
        super(att);
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() {
        return ((Attr) this.node).getValue();
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeName() {
        String result = this.node.getLocalName();
        if (result == null || result.equals("")) {
            result = this.node.getNodeName();
        }
        return result;
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return true;
    }

    @Override // freemarker.ext.dom.NodeModel
    String getQualifiedName() {
        String prefix;
        String nsURI = this.node.getNamespaceURI();
        if (nsURI == null || nsURI.equals("")) {
            return this.node.getNodeName();
        }
        Environment env = Environment.getCurrentEnvironment();
        String defaultNS = env.getDefaultNS();
        if (nsURI.equals(defaultNS)) {
            prefix = Template.DEFAULT_NAMESPACE_PREFIX;
        } else {
            prefix = env.getPrefixForNamespace(nsURI);
        }
        if (prefix == null) {
            return null;
        }
        return prefix + ":" + this.node.getLocalName();
    }
}
