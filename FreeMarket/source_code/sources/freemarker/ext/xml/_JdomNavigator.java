package freemarker.ext.xml;

import freemarker.ext.xml.Navigator;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.validation.DataBinder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/_JdomNavigator.class */
public class _JdomNavigator extends Navigator {
    private static final XMLOutputter OUTPUT = new XMLOutputter();

    @Override // freemarker.ext.xml.Navigator
    void getAsString(Object node, StringWriter sw) throws TemplateModelException {
        try {
            if (node instanceof Element) {
                OUTPUT.output((Element) node, sw);
            } else if (node instanceof Attribute) {
                Attribute attribute = (Attribute) node;
                sw.write(" ");
                sw.write(attribute.getQualifiedName());
                sw.write("=\"");
                sw.write(OUTPUT.escapeAttributeEntities(attribute.getValue()));
                sw.write("\"");
            } else if (node instanceof Text) {
                OUTPUT.output((Text) node, sw);
            } else if (node instanceof Document) {
                OUTPUT.output((Document) node, sw);
            } else if (node instanceof ProcessingInstruction) {
                OUTPUT.output((ProcessingInstruction) node, sw);
            } else if (node instanceof Comment) {
                OUTPUT.output((Comment) node, sw);
            } else if (node instanceof CDATA) {
                OUTPUT.output((CDATA) node, sw);
            } else if (node instanceof DocType) {
                OUTPUT.output((DocType) node, sw);
            } else if (node instanceof EntityRef) {
                OUTPUT.output((EntityRef) node, sw);
            } else {
                throw new TemplateModelException(node.getClass().getName() + " is not a core JDOM class");
            }
        } catch (IOException e) {
            throw new TemplateModelException((Exception) e);
        }
    }

    @Override // freemarker.ext.xml.Navigator
    void getChildren(Object node, String localName, String namespaceUri, List result) {
        if (node instanceof Element) {
            Element e = (Element) node;
            if (localName == null) {
                result.addAll(e.getChildren());
                return;
            } else {
                result.addAll(e.getChildren(localName, Namespace.getNamespace("", namespaceUri)));
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
                result.addAll(e.getAttributes());
                return;
            }
            Attribute attr = e.getAttribute(localName, Namespace.getNamespace("", namespaceUri));
            if (attr != null) {
                result.add(attr);
                return;
            }
            return;
        }
        if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) node;
            if (DataBinder.DEFAULT_OBJECT_NAME.equals(localName)) {
                result.add(new Attribute(DataBinder.DEFAULT_OBJECT_NAME, pi.getTarget()));
                return;
            } else if ("data".equals(localName)) {
                result.add(new Attribute("data", pi.getData()));
                return;
            } else {
                result.add(new Attribute(localName, pi.getValue(localName)));
                return;
            }
        }
        if (node instanceof DocType) {
            DocType doctype = (DocType) node;
            if ("publicId".equals(localName)) {
                result.add(new Attribute("publicId", doctype.getPublicID()));
            } else if ("systemId".equals(localName)) {
                result.add(new Attribute("systemId", doctype.getSystemID()));
            } else if ("elementName".equals(localName)) {
                result.add(new Attribute("elementName", doctype.getElementName()));
            }
        }
    }

    @Override // freemarker.ext.xml.Navigator
    void getDescendants(Object node, List result) {
        if (node instanceof Document) {
            Element root = ((Document) node).getRootElement();
            result.add(root);
            getDescendants(root, result);
        } else if (node instanceof Element) {
            getDescendants((Element) node, result);
        }
    }

    private void getDescendants(Element node, List result) {
        for (Element subnode : node.getChildren()) {
            result.add(subnode);
            getDescendants(subnode, result);
        }
    }

    @Override // freemarker.ext.xml.Navigator
    Object getParent(Object node) {
        if (node instanceof Element) {
            return ((Element) node).getParent();
        }
        if (node instanceof Attribute) {
            return ((Attribute) node).getParent();
        }
        if (node instanceof Text) {
            return ((Text) node).getParent();
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction) node).getParent();
        }
        if (node instanceof Comment) {
            return ((Comment) node).getParent();
        }
        if (node instanceof EntityRef) {
            return ((EntityRef) node).getParent();
        }
        return null;
    }

    @Override // freemarker.ext.xml.Navigator
    Object getDocument(Object node) {
        if (node instanceof Element) {
            return ((Element) node).getDocument();
        }
        if (node instanceof Attribute) {
            Element parent = ((Attribute) node).getParent();
            if (parent == null) {
                return null;
            }
            return parent.getDocument();
        }
        if (node instanceof Text) {
            Element parent2 = ((Text) node).getParent();
            if (parent2 == null) {
                return null;
            }
            return parent2.getDocument();
        }
        if (node instanceof Document) {
            return node;
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction) node).getDocument();
        }
        if (node instanceof EntityRef) {
            return ((EntityRef) node).getDocument();
        }
        if (node instanceof Comment) {
            return ((Comment) node).getDocument();
        }
        return null;
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
        if (node instanceof Element) {
            result.addAll(((Element) node).getContent());
        } else if (node instanceof Document) {
            result.addAll(((Document) node).getContent());
        }
    }

    @Override // freemarker.ext.xml.Navigator
    String getText(Object node) {
        if (node instanceof Element) {
            return ((Element) node).getTextTrim();
        }
        if (node instanceof Attribute) {
            return ((Attribute) node).getValue();
        }
        if (node instanceof CDATA) {
            return ((CDATA) node).getText();
        }
        if (node instanceof Comment) {
            return ((Comment) node).getText();
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction) node).getData();
        }
        return null;
    }

    @Override // freemarker.ext.xml.Navigator
    String getLocalName(Object node) {
        if (node instanceof Element) {
            return ((Element) node).getName();
        }
        if (node instanceof Attribute) {
            return ((Attribute) node).getName();
        }
        if (node instanceof EntityRef) {
            return ((EntityRef) node).getName();
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction) node).getTarget();
        }
        if (node instanceof DocType) {
            return ((DocType) node).getElementName();
        }
        return null;
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
        if (node instanceof Attribute) {
            return BeanDefinitionParserDelegate.QUALIFIER_ATTRIBUTE_ELEMENT;
        }
        if (node instanceof CDATA) {
            return "cdata";
        }
        if (node instanceof Comment) {
            return "comment";
        }
        if (node instanceof Document) {
            return "document";
        }
        if (node instanceof DocType) {
            return "documentType";
        }
        if (node instanceof Element) {
            return "element";
        }
        if (node instanceof EntityRef) {
            return "entityReference";
        }
        if (node instanceof Namespace) {
            return "namespace";
        }
        if (node instanceof ProcessingInstruction) {
            return "processingInstruction";
        }
        if (node instanceof Text) {
            return "text";
        }
        return "unknown";
    }

    @Override // freemarker.ext.xml.Navigator
    Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
        try {
            return new JDOMXPathEx(xpathString);
        } catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/_JdomNavigator$JDOMXPathEx.class */
    private static final class JDOMXPathEx extends JDOMXPath implements Navigator.XPathEx {
        JDOMXPathEx(String path) throws Exception {
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
