package freemarker.ext.dom;

import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateSequenceModel;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/NodeModel.class */
public abstract class NodeModel implements TemplateNodeModelEx, TemplateHashModel, TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, _UnexpectedTypeErrorExplainerTemplateModel {
    private static DocumentBuilderFactory docBuilderFactory;
    private static XPathSupport jaxenXPathSupport;
    private static ErrorHandler errorHandler;
    static Class xpathSupportClass;
    final Node node;
    private TemplateSequenceModel children;
    private NodeModel parent;
    private static final Logger LOG = Logger.getLogger("freemarker.dom");
    private static final Object STATIC_LOCK = new Object();
    private static final Map xpathSupportMap = Collections.synchronizedMap(new WeakHashMap());

    static {
        try {
            useDefaultXPathSupport();
        } catch (Exception e) {
        }
        if (xpathSupportClass == null && LOG.isWarnEnabled()) {
            LOG.warn("No XPath support is available. If you need it, add Apache Xalan or Jaxen as dependency.");
        }
    }

    @Deprecated
    public static void setDocumentBuilderFactory(DocumentBuilderFactory docBuilderFactory2) {
        synchronized (STATIC_LOCK) {
            docBuilderFactory = docBuilderFactory2;
        }
    }

    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        DocumentBuilderFactory documentBuilderFactory;
        synchronized (STATIC_LOCK) {
            if (docBuilderFactory == null) {
                DocumentBuilderFactory newFactory = DocumentBuilderFactory.newInstance();
                newFactory.setNamespaceAware(true);
                newFactory.setIgnoringElementContentWhitespace(true);
                docBuilderFactory = newFactory;
            }
            documentBuilderFactory = docBuilderFactory;
        }
        return documentBuilderFactory;
    }

    @Deprecated
    public static void setErrorHandler(ErrorHandler errorHandler2) {
        synchronized (STATIC_LOCK) {
            errorHandler = errorHandler2;
        }
    }

    public static ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler2;
        synchronized (STATIC_LOCK) {
            errorHandler2 = errorHandler;
        }
        return errorHandler2;
    }

    public static NodeModel parse(InputSource is, boolean removeComments, boolean removePIs) throws ParserConfigurationException, SAXException, DOMException, IOException {
        DocumentBuilder builder = getDocumentBuilderFactory().newDocumentBuilder();
        ErrorHandler errorHandler2 = getErrorHandler();
        if (errorHandler2 != null) {
            builder.setErrorHandler(errorHandler2);
        }
        try {
            Document doc = builder.parse(is);
            if (removeComments && removePIs) {
                simplify(doc);
            } else {
                if (removeComments) {
                    removeComments(doc);
                }
                if (removePIs) {
                    removePIs(doc);
                }
                mergeAdjacentText(doc);
            }
            return wrap(doc);
        } catch (MalformedURLException e) {
            if (is.getSystemId() == null && is.getCharacterStream() == null && is.getByteStream() == null) {
                throw new MalformedURLException("The SAX InputSource has systemId == null && characterStream == null && byteStream == null. This is often because it was created with a null InputStream or Reader, which is often because the XML file it should point to was not found. (The original exception was: " + e + ")");
            }
            throw e;
        }
    }

    public static NodeModel parse(InputSource is) throws ParserConfigurationException, SAXException, IOException {
        return parse(is, true, true);
    }

    public static NodeModel parse(File f, boolean removeComments, boolean removePIs) throws ParserConfigurationException, SAXException, DOMException, IOException {
        DocumentBuilder builder = getDocumentBuilderFactory().newDocumentBuilder();
        ErrorHandler errorHandler2 = getErrorHandler();
        if (errorHandler2 != null) {
            builder.setErrorHandler(errorHandler2);
        }
        Document doc = builder.parse(f);
        if (removeComments && removePIs) {
            simplify(doc);
        } else {
            if (removeComments) {
                removeComments(doc);
            }
            if (removePIs) {
                removePIs(doc);
            }
            mergeAdjacentText(doc);
        }
        return wrap(doc);
    }

    public static NodeModel parse(File f) throws ParserConfigurationException, SAXException, IOException {
        return parse(f, true, true);
    }

    protected NodeModel(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    public TemplateModel get(String key) throws TemplateModelException {
        if (key.startsWith("@@")) {
            if (key.equals(AtAtKey.TEXT.getKey())) {
                return new SimpleScalar(getText(this.node));
            }
            if (key.equals(AtAtKey.NAMESPACE.getKey())) {
                String nsURI = this.node.getNamespaceURI();
                if (nsURI == null) {
                    return null;
                }
                return new SimpleScalar(nsURI);
            }
            if (key.equals(AtAtKey.LOCAL_NAME.getKey())) {
                String localName = this.node.getLocalName();
                if (localName == null) {
                    localName = getNodeName();
                }
                return new SimpleScalar(localName);
            }
            if (key.equals(AtAtKey.MARKUP.getKey())) {
                StringBuilder buf = new StringBuilder();
                NodeOutputter nu = new NodeOutputter(this.node);
                nu.outputContent(this.node, buf);
                return new SimpleScalar(buf.toString());
            }
            if (key.equals(AtAtKey.NESTED_MARKUP.getKey())) {
                StringBuilder buf2 = new StringBuilder();
                NodeOutputter nu2 = new NodeOutputter(this.node);
                nu2.outputContent(this.node.getChildNodes(), buf2);
                return new SimpleScalar(buf2.toString());
            }
            if (key.equals(AtAtKey.QNAME.getKey())) {
                String qname = getQualifiedName();
                if (qname != null) {
                    return new SimpleScalar(qname);
                }
                return null;
            }
            if (AtAtKey.containsKey(key)) {
                throw new TemplateModelException("\"" + key + "\" is not supported for an XML node of type \"" + getNodeType() + "\".");
            }
            throw new TemplateModelException("Unsupported @@ key: " + key);
        }
        XPathSupport xps = getXPathSupport();
        if (xps == null) {
            throw new TemplateModelException("No XPath support is available (add Apache Xalan or Jaxen as dependency). This is either malformed, or an XPath expression: " + key);
        }
        return xps.executeQuery(this.node, key);
    }

    @Override // freemarker.template.TemplateNodeModel
    public TemplateNodeModel getParentNode() {
        if (this.parent == null) {
            Node parentNode = this.node.getParentNode();
            if (parentNode == null && (this.node instanceof Attr)) {
                parentNode = ((Attr) this.node).getOwnerElement();
            }
            this.parent = wrap(parentNode);
        }
        return this.parent;
    }

    @Override // freemarker.template.TemplateNodeModelEx
    public TemplateNodeModelEx getPreviousSibling() throws TemplateModelException {
        return wrap(this.node.getPreviousSibling());
    }

    @Override // freemarker.template.TemplateNodeModelEx
    public TemplateNodeModelEx getNextSibling() throws TemplateModelException {
        return wrap(this.node.getNextSibling());
    }

    @Override // freemarker.template.TemplateNodeModel
    public TemplateSequenceModel getChildNodes() {
        if (this.children == null) {
            this.children = new NodeListModel(this.node.getChildNodes(), this);
        }
        return this.children;
    }

    @Override // freemarker.template.TemplateNodeModel
    public final String getNodeType() throws TemplateModelException {
        short nodeType = this.node.getNodeType();
        switch (nodeType) {
            case 1:
                return "element";
            case 2:
                return BeanDefinitionParserDelegate.QUALIFIER_ATTRIBUTE_ELEMENT;
            case 3:
                return "text";
            case 4:
                return "text";
            case 5:
                return "entity_reference";
            case 6:
                return "entity";
            case 7:
                return "pi";
            case 8:
                return "comment";
            case 9:
                return "document";
            case 10:
                return "document_type";
            case 11:
                return "document_fragment";
            case 12:
                return "notation";
            default:
                throw new TemplateModelException("Unknown node type: " + ((int) nodeType) + ". This should be impossible!");
        }
    }

    public TemplateModel exec(List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("Expecting exactly one arguments");
        }
        String query = (String) args.get(0);
        XPathSupport xps = getXPathSupport();
        if (xps == null) {
            throw new TemplateModelException("No XPath support available");
        }
        return xps.executeQuery(this.node, query);
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public final int size() {
        return 1;
    }

    @Override // freemarker.template.TemplateSequenceModel
    public final TemplateModel get(int i) {
        if (i == 0) {
            return this;
        }
        return null;
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeNamespace() {
        int nodeType = this.node.getNodeType();
        if (nodeType != 2 && nodeType != 1) {
            return null;
        }
        String result = this.node.getNamespaceURI();
        if (result == null && nodeType == 1) {
            result = "";
        } else if ("".equals(result) && nodeType == 2) {
            result = null;
        }
        return result;
    }

    public final int hashCode() {
        return this.node.hashCode();
    }

    public boolean equals(Object other) {
        return other != null && other.getClass() == getClass() && ((NodeModel) other).node.equals(this.node);
    }

    public static NodeModel wrap(Node node) {
        if (node == null) {
            return null;
        }
        NodeModel result = null;
        switch (node.getNodeType()) {
            case 1:
                result = new ElementModel((Element) node);
                break;
            case 2:
                result = new AttributeNodeModel((Attr) node);
                break;
            case 3:
            case 4:
            case 8:
                result = new CharacterDataNodeModel((CharacterData) node);
                break;
            case 7:
                result = new PINodeModel((ProcessingInstruction) node);
                break;
            case 9:
                result = new DocumentModel((Document) node);
                break;
            case 10:
                result = new DocumentTypeModel((DocumentType) node);
                break;
        }
        return result;
    }

    public static void removeComments(Node parent) throws DOMException {
        Node firstChild = parent.getFirstChild();
        while (true) {
            Node child = firstChild;
            if (child != null) {
                Node nextSibling = child.getNextSibling();
                if (child.getNodeType() == 8) {
                    parent.removeChild(child);
                } else if (child.hasChildNodes()) {
                    removeComments(child);
                }
                firstChild = nextSibling;
            } else {
                return;
            }
        }
    }

    public static void removePIs(Node parent) throws DOMException {
        Node firstChild = parent.getFirstChild();
        while (true) {
            Node child = firstChild;
            if (child != null) {
                Node nextSibling = child.getNextSibling();
                if (child.getNodeType() == 7) {
                    parent.removeChild(child);
                } else if (child.hasChildNodes()) {
                    removePIs(child);
                }
                firstChild = nextSibling;
            } else {
                return;
            }
        }
    }

    public static void mergeAdjacentText(Node parent) throws DOMException {
        mergeAdjacentText(parent, new StringBuilder(0));
    }

    private static void mergeAdjacentText(Node parent, StringBuilder collectorBuf) throws DOMException {
        Node firstChild = parent.getFirstChild();
        while (true) {
            Node child = firstChild;
            if (child != null) {
                Node next = child.getNextSibling();
                if (child instanceof Text) {
                    boolean atFirstText = true;
                    while (next instanceof Text) {
                        if (atFirstText) {
                            collectorBuf.setLength(0);
                            collectorBuf.ensureCapacity(child.getNodeValue().length() + next.getNodeValue().length());
                            collectorBuf.append(child.getNodeValue());
                            atFirstText = false;
                        }
                        collectorBuf.append(next.getNodeValue());
                        parent.removeChild(next);
                        next = child.getNextSibling();
                    }
                    if (!atFirstText && collectorBuf.length() != 0) {
                        ((CharacterData) child).setData(collectorBuf.toString());
                    }
                } else {
                    mergeAdjacentText(child, collectorBuf);
                }
                firstChild = next;
            } else {
                return;
            }
        }
    }

    public static void simplify(Node parent) throws DOMException {
        simplify(parent, new StringBuilder(0));
    }

    private static void simplify(Node parent, StringBuilder collectorTextChildBuff) throws DOMException {
        Node collectorTextChild = null;
        Node firstChild = parent.getFirstChild();
        while (true) {
            Node child = firstChild;
            if (child == null) {
                break;
            }
            Node next = child.getNextSibling();
            if (child.hasChildNodes()) {
                if (collectorTextChild != null) {
                    if (collectorTextChildBuff.length() != 0) {
                        ((CharacterData) collectorTextChild).setData(collectorTextChildBuff.toString());
                        collectorTextChildBuff.setLength(0);
                    }
                    collectorTextChild = null;
                }
                simplify(child, collectorTextChildBuff);
            } else {
                int type = child.getNodeType();
                if (type == 3 || type == 4) {
                    if (collectorTextChild != null) {
                        if (collectorTextChildBuff.length() == 0) {
                            collectorTextChildBuff.ensureCapacity(collectorTextChild.getNodeValue().length() + child.getNodeValue().length());
                            collectorTextChildBuff.append(collectorTextChild.getNodeValue());
                        }
                        collectorTextChildBuff.append(child.getNodeValue());
                        parent.removeChild(child);
                    } else {
                        collectorTextChild = child;
                        collectorTextChildBuff.setLength(0);
                    }
                } else if (type == 8) {
                    parent.removeChild(child);
                } else if (type == 7) {
                    parent.removeChild(child);
                } else if (collectorTextChild != null) {
                    if (collectorTextChildBuff.length() != 0) {
                        ((CharacterData) collectorTextChild).setData(collectorTextChildBuff.toString());
                        collectorTextChildBuff.setLength(0);
                    }
                    collectorTextChild = null;
                }
            }
            firstChild = next;
        }
        if (collectorTextChild != null && collectorTextChildBuff.length() != 0) {
            ((CharacterData) collectorTextChild).setData(collectorTextChildBuff.toString());
            collectorTextChildBuff.setLength(0);
        }
    }

    NodeModel getDocumentNodeModel() {
        if (this.node instanceof Document) {
            return this;
        }
        return wrap(this.node.getOwnerDocument());
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x006c  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0058 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void useDefaultXPathSupport() {
        /*
            java.lang.Object r0 = freemarker.ext.dom.NodeModel.STATIC_LOCK
            r1 = r0
            r4 = r1
            monitor-enter(r0)
            r0 = 0
            freemarker.ext.dom.NodeModel.xpathSupportClass = r0     // Catch: java.lang.Throwable -> L71
            r0 = 0
            freemarker.ext.dom.NodeModel.jaxenXPathSupport = r0     // Catch: java.lang.Throwable -> L71
            useXalanXPathSupport()     // Catch: java.lang.ClassNotFoundException -> L14 java.lang.Exception -> L18 java.lang.IllegalAccessError -> L25 java.lang.Throwable -> L71
            goto L2f
        L14:
            r5 = move-exception
            goto L2f
        L18:
            r5 = move-exception
            freemarker.log.Logger r0 = freemarker.ext.dom.NodeModel.LOG     // Catch: java.lang.Throwable -> L71
            java.lang.String r1 = "Failed to use Xalan XPath support."
            r2 = r5
            r0.debug(r1, r2)     // Catch: java.lang.Throwable -> L71
            goto L2f
        L25:
            r5 = move-exception
            freemarker.log.Logger r0 = freemarker.ext.dom.NodeModel.LOG     // Catch: java.lang.Throwable -> L71
            java.lang.String r1 = "Failed to use Xalan internal XPath support."
            r2 = r5
            r0.debug(r1, r2)     // Catch: java.lang.Throwable -> L71
        L2f:
            java.lang.Class r0 = freemarker.ext.dom.NodeModel.xpathSupportClass     // Catch: java.lang.Throwable -> L71
            if (r0 != 0) goto L52
            useSunInternalXPathSupport()     // Catch: java.lang.Exception -> L3b java.lang.IllegalAccessError -> L48 java.lang.Throwable -> L71
            goto L52
        L3b:
            r5 = move-exception
            freemarker.log.Logger r0 = freemarker.ext.dom.NodeModel.LOG     // Catch: java.lang.Throwable -> L71
            java.lang.String r1 = "Failed to use Sun internal XPath support."
            r2 = r5
            r0.debug(r1, r2)     // Catch: java.lang.Throwable -> L71
            goto L52
        L48:
            r5 = move-exception
            freemarker.log.Logger r0 = freemarker.ext.dom.NodeModel.LOG     // Catch: java.lang.Throwable -> L71
            java.lang.String r1 = "Failed to use Sun internal XPath support. Tip: On Java 9+, you may need Xalan or Jaxen+Saxpath."
            r2 = r5
            r0.debug(r1, r2)     // Catch: java.lang.Throwable -> L71
        L52:
            java.lang.Class r0 = freemarker.ext.dom.NodeModel.xpathSupportClass     // Catch: java.lang.Throwable -> L71
            if (r0 != 0) goto L6c
            useJaxenXPathSupport()     // Catch: java.lang.ClassNotFoundException -> L5e java.lang.Throwable -> L62 java.lang.Throwable -> L71
            goto L6c
        L5e:
            r5 = move-exception
            goto L6c
        L62:
            r5 = move-exception
            freemarker.log.Logger r0 = freemarker.ext.dom.NodeModel.LOG     // Catch: java.lang.Throwable -> L71
            java.lang.String r1 = "Failed to use Jaxen XPath support."
            r2 = r5
            r0.debug(r1, r2)     // Catch: java.lang.Throwable -> L71
        L6c:
            r0 = r4
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L71
            goto L76
        L71:
            r6 = move-exception
            r0 = r4
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L71
            r0 = r6
            throw r0
        L76:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.ext.dom.NodeModel.useDefaultXPathSupport():void");
    }

    public static void useJaxenXPathSupport() throws Exception {
        Class.forName("org.jaxen.dom.DOMXPath");
        Class c = Class.forName("freemarker.ext.dom.JaxenXPathSupport");
        jaxenXPathSupport = (XPathSupport) c.newInstance();
        synchronized (STATIC_LOCK) {
            xpathSupportClass = c;
        }
        LOG.debug("Using Jaxen classes for XPath support");
    }

    public static void useXalanXPathSupport() throws Exception {
        Class.forName("org.apache.xpath.XPath");
        Class c = Class.forName("freemarker.ext.dom.XalanXPathSupport");
        synchronized (STATIC_LOCK) {
            xpathSupportClass = c;
        }
        LOG.debug("Using Xalan classes for XPath support");
    }

    public static void useSunInternalXPathSupport() throws Exception {
        Class.forName("com.sun.org.apache.xpath.internal.XPath");
        Class c = Class.forName("freemarker.ext.dom.SunInternalXalanXPathSupport");
        synchronized (STATIC_LOCK) {
            xpathSupportClass = c;
        }
        LOG.debug("Using Sun's internal Xalan classes for XPath support");
    }

    public static void setXPathSupportClass(Class cl) {
        if (cl != null && !XPathSupport.class.isAssignableFrom(cl)) {
            throw new RuntimeException("Class " + cl.getName() + " does not implement freemarker.ext.dom.XPathSupport");
        }
        synchronized (STATIC_LOCK) {
            xpathSupportClass = cl;
        }
    }

    public static Class getXPathSupportClass() {
        Class cls;
        synchronized (STATIC_LOCK) {
            cls = xpathSupportClass;
        }
        return cls;
    }

    private static String getText(Node node) throws DOMException {
        String result = "";
        if ((node instanceof Text) || (node instanceof CDATASection)) {
            result = ((CharacterData) node).getData();
        } else if (node instanceof Element) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                result = result + getText(children.item(i));
            }
        } else if (node instanceof Document) {
            result = getText(((Document) node).getDocumentElement());
        }
        return result;
    }

    XPathSupport getXPathSupport() {
        if (jaxenXPathSupport != null) {
            return jaxenXPathSupport;
        }
        XPathSupport xps = null;
        Document doc = this.node.getOwnerDocument();
        if (doc == null) {
            doc = (Document) this.node;
        }
        synchronized (doc) {
            WeakReference ref = (WeakReference) xpathSupportMap.get(doc);
            if (ref != null) {
                xps = (XPathSupport) ref.get();
            }
            if (xps == null && xpathSupportClass != null) {
                try {
                    xps = (XPathSupport) xpathSupportClass.newInstance();
                    xpathSupportMap.put(doc, new WeakReference(xps));
                } catch (Exception e) {
                    LOG.error("Error instantiating xpathSupport class", e);
                }
            }
        }
        return xps;
    }

    String getQualifiedName() throws TemplateModelException {
        return getNodeName();
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return this.node;
    }

    @Override // freemarker.ext.util.WrapperTemplateModel
    public Object getWrappedObject() {
        return this.node;
    }

    @Override // freemarker.core._UnexpectedTypeErrorExplainerTemplateModel
    public Object[] explainTypeError(Class[] expectedClasses) {
        for (Class expectedClass : expectedClasses) {
            if (TemplateDateModel.class.isAssignableFrom(expectedClass) || TemplateNumberModel.class.isAssignableFrom(expectedClass) || TemplateBooleanModel.class.isAssignableFrom(expectedClass)) {
                return new Object[]{"XML node values are always strings (text), that is, they can't be used as number, date/time/datetime or boolean without explicit conversion (such as someNode?number, someNode?datetime.xs, someNode?date.xs, someNode?time.xs, someNode?boolean)."};
            }
        }
        return null;
    }
}
