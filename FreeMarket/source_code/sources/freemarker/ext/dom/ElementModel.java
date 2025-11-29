package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.util.Collections;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/ElementModel.class */
class ElementModel extends NodeModel implements TemplateScalarModel {
    public ElementModel(Element element) {
        super(element);
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return false;
    }

    @Override // freemarker.ext.dom.NodeModel, freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException, DOMException {
        Node nextSibling;
        Node previousSibling;
        if (key.equals("*")) {
            NodeListModel ns = new NodeListModel(this);
            TemplateSequenceModel children = getChildNodes();
            int size = children.size();
            for (int i = 0; i < size; i++) {
                NodeModel child = (NodeModel) children.get(i);
                if (child.node.getNodeType() == 1) {
                    ns.add(child);
                }
            }
            return ns;
        }
        if (key.equals(SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS)) {
            return new NodeListModel(((Element) this.node).getElementsByTagName("*"), this);
        }
        if (key.startsWith("@")) {
            if (key.startsWith("@@")) {
                if (key.equals(AtAtKey.ATTRIBUTES.getKey())) {
                    return new NodeListModel(this.node.getAttributes(), this);
                }
                if (key.equals(AtAtKey.START_TAG.getKey())) {
                    NodeOutputter nodeOutputter = new NodeOutputter(this.node);
                    return new SimpleScalar(nodeOutputter.getOpeningTag((Element) this.node));
                }
                if (key.equals(AtAtKey.END_TAG.getKey())) {
                    NodeOutputter nodeOutputter2 = new NodeOutputter(this.node);
                    return new SimpleScalar(nodeOutputter2.getClosingTag((Element) this.node));
                }
                if (key.equals(AtAtKey.ATTRIBUTES_MARKUP.getKey())) {
                    StringBuilder buf = new StringBuilder();
                    NodeOutputter nu = new NodeOutputter(this.node);
                    nu.outputContent(this.node.getAttributes(), buf);
                    return new SimpleScalar(buf.toString().trim());
                }
                if (key.equals(AtAtKey.PREVIOUS_SIBLING_ELEMENT.getKey())) {
                    Node previousSibling2 = this.node.getPreviousSibling();
                    while (true) {
                        previousSibling = previousSibling2;
                        if (previousSibling == null || isSignificantNode(previousSibling)) {
                            break;
                        }
                        previousSibling2 = previousSibling.getPreviousSibling();
                    }
                    return (previousSibling == null || previousSibling.getNodeType() != 1) ? new NodeListModel(Collections.emptyList(), (NodeModel) null) : wrap(previousSibling);
                }
                if (key.equals(AtAtKey.NEXT_SIBLING_ELEMENT.getKey())) {
                    Node nextSibling2 = this.node.getNextSibling();
                    while (true) {
                        nextSibling = nextSibling2;
                        if (nextSibling == null || isSignificantNode(nextSibling)) {
                            break;
                        }
                        nextSibling2 = nextSibling.getNextSibling();
                    }
                    return (nextSibling == null || nextSibling.getNodeType() != 1) ? new NodeListModel(Collections.emptyList(), (NodeModel) null) : wrap(nextSibling);
                }
                return super.get(key);
            }
            if (DomStringUtil.isXMLNameLike(key, 1)) {
                Attr att = getAttribute(key.substring(1));
                if (att == null) {
                    return new NodeListModel(this);
                }
                return wrap(att);
            }
            if (key.equals("@*")) {
                return new NodeListModel(this.node.getAttributes(), this);
            }
            return super.get(key);
        }
        if (DomStringUtil.isXMLNameLike(key)) {
            NodeListModel result = ((NodeListModel) getChildNodes()).filterByName(key);
            return result.size() != 1 ? result : result.get(0);
        }
        return super.get(key);
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() throws TemplateModelException {
        NodeList nl = this.node.getChildNodes();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);
            int nodeType = child.getNodeType();
            if (nodeType == 1) {
                String msg = "Only elements with no child elements can be processed as text.\nThis element with name \"" + this.node.getNodeName() + "\" has a child element named: " + child.getNodeName();
                throw new TemplateModelException(msg);
            }
            if (nodeType == 3 || nodeType == 4) {
                result.append(child.getNodeValue());
            }
        }
        return result.toString();
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeName() {
        String result = this.node.getLocalName();
        if (result == null || result.equals("")) {
            result = this.node.getNodeName();
        }
        return result;
    }

    @Override // freemarker.ext.dom.NodeModel
    String getQualifiedName() {
        String prefix;
        String nodeName = getNodeName();
        String nsURI = getNodeNamespace();
        if (nsURI == null || nsURI.length() == 0) {
            return nodeName;
        }
        Environment env = Environment.getCurrentEnvironment();
        String defaultNS = env.getDefaultNS();
        if (defaultNS != null && defaultNS.equals(nsURI)) {
            prefix = "";
        } else {
            prefix = env.getPrefixForNamespace(nsURI);
        }
        if (prefix == null) {
            return null;
        }
        if (prefix.length() > 0) {
            prefix = prefix + ":";
        }
        return prefix + nodeName;
    }

    private Attr getAttribute(String qname) throws DOMException {
        String uri;
        Element element = (Element) this.node;
        Attr result = element.getAttributeNode(qname);
        if (result != null) {
            return result;
        }
        int colonIndex = qname.indexOf(58);
        if (colonIndex > 0) {
            String prefix = qname.substring(0, colonIndex);
            if (prefix.equals(Template.DEFAULT_NAMESPACE_PREFIX)) {
                uri = Environment.getCurrentEnvironment().getDefaultNS();
            } else {
                uri = Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
            }
            String localName = qname.substring(1 + colonIndex);
            if (uri != null) {
                result = element.getAttributeNodeNS(uri, localName);
            }
        }
        return result;
    }

    private boolean isSignificantNode(Node node) throws TemplateModelException {
        return (node.getNodeType() == 3 || node.getNodeType() == 4) ? !isBlankXMLText(node.getTextContent()) : (node.getNodeType() == 7 || node.getNodeType() == 8) ? false : true;
    }

    private boolean isBlankXMLText(String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!isXMLWhiteSpace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isXMLWhiteSpace(char c) {
        if (c != ' ' && c != '\t') {
            if (!((c == '\n') | (c == '\r'))) {
                return false;
            }
        }
        return true;
    }

    boolean matchesName(String name, Environment env) {
        return DomStringUtil.matchesName(name, getNodeName(), getNodeNamespace(), env);
    }
}
