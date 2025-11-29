package freemarker.ext.xml;

import freemarker.ext.xml.Navigator;
import freemarker.template.TemplateModelException;
import java.io.StringWriter;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.tree.DefaultAttribute;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.validation.DataBinder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/_Dom4jNavigator.class */
public class _Dom4jNavigator extends Navigator {
    @Override // freemarker.ext.xml.Navigator
    void getAsString(Object node, StringWriter sw) {
        sw.getBuffer().append(((Node) node).asXML());
    }

    @Override // freemarker.ext.xml.Navigator
    void getChildren(Object node, String localName, String namespaceUri, List result) {
        if (node instanceof Element) {
            Element e = (Element) node;
            if (localName == null) {
                result.addAll(e.elements());
                return;
            } else {
                result.addAll(e.elements(e.getQName().getDocumentFactory().createQName(localName, "", namespaceUri)));
                return;
            }
        }
        if (node instanceof Document) {
            Element root = ((Document) node).getRootElement();
            if (localName == null || (equal(root.getName(), localName) && equal(root.getNamespaceURI(), namespaceUri))) {
                result.add(root);
            }
        }
    }

    @Override // freemarker.ext.xml.Navigator
    void getAttributes(Object node, String localName, String namespaceUri, List result) {
        if (node instanceof Element) {
            Element e = (Element) node;
            if (localName == null) {
                result.addAll(e.attributes());
                return;
            }
            Attribute attr = e.attribute(e.getQName().getDocumentFactory().createQName(localName, "", namespaceUri));
            if (attr != null) {
                result.add(attr);
                return;
            }
            return;
        }
        if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) node;
            if (DataBinder.DEFAULT_OBJECT_NAME.equals(localName)) {
                result.add(new DefaultAttribute(DataBinder.DEFAULT_OBJECT_NAME, pi.getTarget()));
                return;
            } else if ("data".equals(localName)) {
                result.add(new DefaultAttribute("data", pi.getText()));
                return;
            } else {
                result.add(new DefaultAttribute(localName, pi.getValue(localName)));
                return;
            }
        }
        if (node instanceof DocumentType) {
            DocumentType doctype = (DocumentType) node;
            if ("publicId".equals(localName)) {
                result.add(new DefaultAttribute("publicId", doctype.getPublicID()));
            } else if ("systemId".equals(localName)) {
                result.add(new DefaultAttribute("systemId", doctype.getSystemID()));
            } else if ("elementName".equals(localName)) {
                result.add(new DefaultAttribute("elementName", doctype.getElementName()));
            }
        }
    }

    @Override // freemarker.ext.xml.Navigator
    void getDescendants(Object node, List result) {
        if (node instanceof Branch) {
            getDescendants((Branch) node, result);
        }
    }

    private void getDescendants(Branch node, List result) {
        List<Node> content = node.content();
        for (Node subnode : content) {
            if (subnode instanceof Element) {
                result.add(subnode);
                getDescendants(subnode, result);
            }
        }
    }

    @Override // freemarker.ext.xml.Navigator
    Object getParent(Object node) {
        return ((Node) node).getParent();
    }

    @Override // freemarker.ext.xml.Navigator
    Object getDocument(Object node) {
        return ((Node) node).getDocument();
    }

    @Override // freemarker.ext.xml.Navigator
    Object getDocumentType(Object node) {
        if (node instanceof Document) {
            return ((Document) node).getDocType();
        }
        return null;
    }

    @Override // freemarker.ext.xml.Navigator
    void getContent(Object node, List result) {
        if (node instanceof Branch) {
            result.addAll(((Branch) node).content());
        }
    }

    @Override // freemarker.ext.xml.Navigator
    String getText(Object node) {
        return ((Node) node).getText();
    }

    @Override // freemarker.ext.xml.Navigator
    String getLocalName(Object node) {
        return ((Node) node).getName();
    }

    @Override // freemarker.ext.xml.Navigator
    String getNamespacePrefix(Object node) {
        if (node instanceof Element) {
            return ((Element) node).getNamespacePrefix();
        }
        if (node instanceof Attribute) {
            return ((Attribute) node).getNamespacePrefix();
        }
        return null;
    }

    @Override // freemarker.ext.xml.Navigator
    String getNamespaceUri(Object node) {
        if (node instanceof Element) {
            return ((Element) node).getNamespaceURI();
        }
        if (node instanceof Attribute) {
            return ((Attribute) node).getNamespaceURI();
        }
        return null;
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
            case 11:
            case 12:
            default:
                return "unknown";
            case 7:
                return "processingInstruction";
            case 8:
                return "comment";
            case 9:
                return "document";
            case 10:
                return "documentType";
            case 13:
                return "namespace";
        }
    }

    @Override // freemarker.ext.xml.Navigator
    Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
        try {
            return new Dom4jXPathEx(xpathString);
        } catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/_Dom4jNavigator$Dom4jXPathEx.class */
    private static final class Dom4jXPathEx extends Dom4jXPath implements Navigator.XPathEx {
        Dom4jXPathEx(String path) throws Exception {
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
