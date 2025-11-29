package freemarker.ext.xml;

import freemarker.ext.xml.Navigator;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.StringWriter;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.dom.DOMXPath;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.validation.DataBinder;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/_DomNavigator.class */
public class _DomNavigator extends Navigator {
    @Override // freemarker.ext.xml.Navigator
    void getAsString(Object node, StringWriter sw) {
        outputContent((Node) node, sw);
    }

    private void outputContent(Node n, StringWriter buf) {
        switch (n.getNodeType()) {
            case 1:
                buf.append('<').append((CharSequence) getQualifiedName(n));
                outputContent(n.getAttributes(), buf);
                buf.append('>');
                outputContent(n.getChildNodes(), buf);
                buf.append("</").append((CharSequence) getQualifiedName(n)).append('>');
                break;
            case 2:
                buf.append(' ').append((CharSequence) getQualifiedName(n)).append("=\"").append((CharSequence) StringUtil.XMLEncNA(n.getNodeValue())).append('\"');
                break;
            case 3:
                buf.append((CharSequence) StringUtil.XMLEncNQG(n.getNodeValue()));
                break;
            case 4:
                buf.append("<![CDATA[").append((CharSequence) n.getNodeValue()).append("]]>");
                break;
            case 5:
                buf.append('&').append((CharSequence) n.getNodeName()).append(';');
                break;
            case 6:
                outputContent(n.getChildNodes(), buf);
                break;
            case 7:
                buf.append("<?").append((CharSequence) n.getNodeName()).append(' ').append((CharSequence) n.getNodeValue()).append("?>");
                break;
            case 8:
                buf.append("<!--").append((CharSequence) n.getNodeValue()).append("-->");
                break;
            case 9:
                outputContent(n.getChildNodes(), buf);
                break;
            case 10:
                buf.append("<!DOCTYPE ").append((CharSequence) n.getNodeName());
                DocumentType dt = (DocumentType) n;
                if (dt.getPublicId() != null) {
                    buf.append(" PUBLIC \"").append((CharSequence) dt.getPublicId()).append('\"');
                }
                if (dt.getSystemId() != null) {
                    buf.append('\"').append((CharSequence) dt.getSystemId()).append('\"');
                }
                if (dt.getInternalSubset() != null) {
                    buf.append(" [").append((CharSequence) dt.getInternalSubset()).append(']');
                }
                buf.append('>');
                break;
        }
    }

    private void outputContent(NodeList nodes, StringWriter buf) {
        for (int i = 0; i < nodes.getLength(); i++) {
            outputContent(nodes.item(i), buf);
        }
    }

    private void outputContent(NamedNodeMap nodes, StringWriter buf) {
        for (int i = 0; i < nodes.getLength(); i++) {
            outputContent(nodes.item(i), buf);
        }
    }

    @Override // freemarker.ext.xml.Navigator
    void getChildren(Object node, String localName, String namespaceUri, List result) {
        if ("".equals(namespaceUri)) {
            namespaceUri = null;
        }
        NodeList children = ((Node) node).getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node subnode = children.item(i);
            if ((subnode.getNodeType() == 1 || subnode.getNodeType() == 3) && (localName == null || (equal(subnode.getNodeName(), localName) && equal(subnode.getNamespaceURI(), namespaceUri)))) {
                result.add(subnode);
            }
        }
    }

    @Override // freemarker.ext.xml.Navigator
    void getAttributes(Object node, String localName, String namespaceUri, List result) throws DOMException {
        if (node instanceof Element) {
            Element e = (Element) node;
            if (localName == null) {
                NamedNodeMap atts = e.getAttributes();
                for (int i = 0; i < atts.getLength(); i++) {
                    result.add(atts.item(i));
                }
                return;
            }
            if ("".equals(namespaceUri)) {
                namespaceUri = null;
            }
            Attr attr = e.getAttributeNodeNS(namespaceUri, localName);
            if (attr != null) {
                result.add(attr);
                return;
            }
            return;
        }
        if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) node;
            if (DataBinder.DEFAULT_OBJECT_NAME.equals(localName)) {
                result.add(createAttribute(pi, DataBinder.DEFAULT_OBJECT_NAME, pi.getTarget()));
                return;
            } else {
                if ("data".equals(localName)) {
                    result.add(createAttribute(pi, "data", pi.getData()));
                    return;
                }
                return;
            }
        }
        if (node instanceof DocumentType) {
            DocumentType doctype = (DocumentType) node;
            if ("publicId".equals(localName)) {
                result.add(createAttribute(doctype, "publicId", doctype.getPublicId()));
            } else if ("systemId".equals(localName)) {
                result.add(createAttribute(doctype, "systemId", doctype.getSystemId()));
            } else if ("elementName".equals(localName)) {
                result.add(createAttribute(doctype, "elementName", doctype.getNodeName()));
            }
        }
    }

    private Attr createAttribute(Node node, String name, String value) throws DOMException {
        Attr attr = node.getOwnerDocument().createAttribute(name);
        attr.setNodeValue(value);
        return attr;
    }

    @Override // freemarker.ext.xml.Navigator
    void getDescendants(Object node, List result) {
        NodeList children = ((Node) node).getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node subnode = children.item(i);
            if (subnode.getNodeType() == 1) {
                result.add(subnode);
                getDescendants(subnode, result);
            }
        }
    }

    @Override // freemarker.ext.xml.Navigator
    Object getParent(Object node) {
        return ((Node) node).getParentNode();
    }

    @Override // freemarker.ext.xml.Navigator
    Object getDocument(Object node) {
        return ((Node) node).getOwnerDocument();
    }

    @Override // freemarker.ext.xml.Navigator
    Object getDocumentType(Object node) {
        if (node instanceof Document) {
            return ((Document) node).getDoctype();
        }
        return null;
    }

    @Override // freemarker.ext.xml.Navigator
    void getContent(Object node, List result) {
        NodeList children = ((Node) node).getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            result.add(children.item(i));
        }
    }

    @Override // freemarker.ext.xml.Navigator
    String getText(Object node) {
        StringBuilder buf = new StringBuilder();
        if (node instanceof Element) {
            NodeList children = ((Node) node).getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Text) {
                    buf.append(child.getNodeValue());
                }
            }
            return buf.toString();
        }
        return ((Node) node).getNodeValue();
    }

    @Override // freemarker.ext.xml.Navigator
    String getLocalName(Object node) {
        return ((Node) node).getNodeName();
    }

    @Override // freemarker.ext.xml.Navigator
    String getNamespacePrefix(Object node) {
        return ((Node) node).getPrefix();
    }

    @Override // freemarker.ext.xml.Navigator
    String getNamespaceUri(Object node) {
        return ((Node) node).getNamespaceURI();
    }

    @Override // freemarker.ext.xml.Navigator
    String getType(Object node) {
        switch (((Node) node).getNodeType()) {
            case 1:
                return "element";
            case 2:
                return BeanDefinitionParserDelegate.QUALIFIER_ATTRIBUTE_ELEMENT;
            case 3:
                return "text";
            case 4:
                return "cdata";
            case 5:
                return "entityReference";
            case 6:
                return "entity";
            case 7:
                return "processingInstruction";
            case 8:
                return "comment";
            case 9:
                return "document";
            case 10:
                return "documentType";
            default:
                return "unknown";
        }
    }

    @Override // freemarker.ext.xml.Navigator
    Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
        try {
            return new DomXPathEx(xpathString);
        } catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/_DomNavigator$DomXPathEx.class */
    private static final class DomXPathEx extends DOMXPath implements Navigator.XPathEx {
        DomXPathEx(String path) throws Exception {
            super(path);
        }

        @Override // freemarker.ext.xml.Navigator.XPathEx
        public List selectNodes(Object object, NamespaceContext namespaces) throws TemplateModelException {
            Context context = getContext(object);
            context.getContextSupport().setNamespaceContext(namespaces);
            try {
                return selectNodesForContext(context);
            } catch (Exception e) {
                throw new TemplateModelException(e);
            }
        }
    }
}
