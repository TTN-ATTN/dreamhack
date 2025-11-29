package freemarker.ext.xml;

import freemarker.template.TemplateModelException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jaxen.NamespaceContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator.class */
abstract class Navigator {
    private final Map xpathCache = new WeakHashMap();
    private final Map operators = createOperatorMap();
    private final NodeOperator attributeOperator = getOperator("_attributes");
    private final NodeOperator childrenOperator = getOperator("_children");

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$XPathEx.class */
    interface XPathEx {
        List selectNodes(Object obj, NamespaceContext namespaceContext) throws TemplateModelException;
    }

    abstract void getAsString(Object obj, StringWriter stringWriter) throws TemplateModelException;

    abstract XPathEx createXPathEx(String str) throws TemplateModelException;

    abstract void getChildren(Object obj, String str, String str2, List list);

    abstract void getAttributes(Object obj, String str, String str2, List list);

    abstract void getDescendants(Object obj, List list);

    abstract Object getParent(Object obj);

    abstract Object getDocument(Object obj);

    abstract Object getDocumentType(Object obj);

    abstract void getContent(Object obj, List list);

    abstract String getText(Object obj);

    abstract String getLocalName(Object obj);

    abstract String getNamespacePrefix(Object obj);

    abstract String getType(Object obj);

    abstract String getNamespaceUri(Object obj);

    Navigator() {
    }

    NodeOperator getOperator(String key) {
        return (NodeOperator) this.operators.get(key);
    }

    NodeOperator getAttributeOperator() {
        return this.attributeOperator;
    }

    NodeOperator getChildrenOperator() {
        return this.childrenOperator;
    }

    List applyXPath(List nodes, String xpathString, Object namespaces) throws TemplateModelException {
        XPathEx xpath;
        try {
            synchronized (this.xpathCache) {
                xpath = (XPathEx) this.xpathCache.get(xpathString);
                if (xpath == null) {
                    xpath = createXPathEx(xpathString);
                    this.xpathCache.put(xpathString, xpath);
                }
            }
            return xpath.selectNodes(nodes, (NamespaceContext) namespaces);
        } catch (Exception e) {
            throw new TemplateModelException("Could not evaulate XPath expression " + xpathString, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getAncestors(Object node, List result) {
        while (true) {
            Object parent = getParent(node);
            if (parent != null) {
                result.add(parent);
                node = parent;
            } else {
                return;
            }
        }
    }

    String getQualifiedName(Object node) {
        String lname = getLocalName(node);
        if (lname == null) {
            return null;
        }
        String nsprefix = getNamespacePrefix(node);
        if (nsprefix == null || nsprefix.length() == 0) {
            return lname;
        }
        return nsprefix + ":" + lname;
    }

    boolean equal(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    private Map createOperatorMap() {
        Map map = new HashMap();
        map.put("_attributes", new AttributesOp());
        map.put("@*", map.get("_attributes"));
        map.put("_children", new ChildrenOp());
        map.put("*", map.get("_children"));
        map.put("_descendantOrSelf", new DescendantOrSelfOp());
        map.put("_descendant", new DescendantOp());
        map.put("_document", new DocumentOp());
        map.put("_doctype", new DocumentTypeOp());
        map.put("_ancestor", new AncestorOp());
        map.put("_ancestorOrSelf", new AncestorOrSelfOp());
        map.put("_content", new ContentOp());
        map.put("_name", new LocalNameOp());
        map.put("_nsprefix", new NamespacePrefixOp());
        map.put("_nsuri", new NamespaceUriOp());
        map.put("_parent", new ParentOp());
        map.put("_qname", new QualifiedNameOp());
        map.put("_text", new TextOp());
        map.put("_type", new TypeOp());
        return map;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$ChildrenOp.class */
    private class ChildrenOp implements NodeOperator {
        private ChildrenOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getChildren(node, localName, namespaceUri, result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$AttributesOp.class */
    private class AttributesOp implements NodeOperator {
        private AttributesOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getAttributes(node, localName, namespaceUri, result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$DescendantOrSelfOp.class */
    private class DescendantOrSelfOp implements NodeOperator {
        private DescendantOrSelfOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            result.add(node);
            Navigator.this.getDescendants(node, result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$DescendantOp.class */
    private class DescendantOp implements NodeOperator {
        private DescendantOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getDescendants(node, result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$AncestorOrSelfOp.class */
    private class AncestorOrSelfOp implements NodeOperator {
        private AncestorOrSelfOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            result.add(node);
            Navigator.this.getAncestors(node, result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$AncestorOp.class */
    private class AncestorOp implements NodeOperator {
        private AncestorOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getAncestors(node, result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$ParentOp.class */
    private class ParentOp implements NodeOperator {
        private ParentOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Object parent = Navigator.this.getParent(node);
            if (parent != null) {
                result.add(parent);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$DocumentOp.class */
    private class DocumentOp implements NodeOperator {
        private DocumentOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Object document = Navigator.this.getDocument(node);
            if (document != null) {
                result.add(document);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$DocumentTypeOp.class */
    private class DocumentTypeOp implements NodeOperator {
        private DocumentTypeOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Object documentType = Navigator.this.getDocumentType(node);
            if (documentType != null) {
                result.add(documentType);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$ContentOp.class */
    private class ContentOp implements NodeOperator {
        private ContentOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getContent(node, result);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$TextOp.class */
    private class TextOp implements NodeOperator {
        private TextOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getText(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$LocalNameOp.class */
    private class LocalNameOp implements NodeOperator {
        private LocalNameOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getLocalName(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$QualifiedNameOp.class */
    private class QualifiedNameOp implements NodeOperator {
        private QualifiedNameOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            String qname = Navigator.this.getQualifiedName(node);
            if (qname != null) {
                result.add(qname);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$NamespacePrefixOp.class */
    private class NamespacePrefixOp implements NodeOperator {
        private NamespacePrefixOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getNamespacePrefix(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$NamespaceUriOp.class */
    private class NamespaceUriOp implements NodeOperator {
        private NamespaceUriOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getNamespaceUri(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/Navigator$TypeOp.class */
    private class TypeOp implements NodeOperator {
        private TypeOp() {
        }

        @Override // freemarker.ext.xml.NodeOperator
        public void process(Object node, String localName, String namespaceUri, List result) {
            result.add(Navigator.this.getType(node));
        }
    }
}
