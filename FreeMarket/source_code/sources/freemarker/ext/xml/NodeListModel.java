package freemarker.ext.xml;

import freemarker.log.Logger;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.ClassUtil;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/NodeListModel.class */
public class NodeListModel implements TemplateHashModel, TemplateMethodModel, TemplateScalarModel, TemplateSequenceModel, TemplateNodeModel {
    private static final Logger LOG = Logger.getLogger("freemarker.xml");
    private static final Class DOM_NODE_CLASS = getClass("org.w3c.dom.Node");
    private static final Class DOM4J_NODE_CLASS = getClass("org.dom4j.Node");
    private static final Navigator DOM_NAVIGATOR = getNavigator("Dom");
    private static final Navigator DOM4J_NAVIGATOR = getNavigator("Dom4j");
    private static final Navigator JDOM_NAVIGATOR = getNavigator("Jdom");
    private static volatile boolean useJaxenNamespaces = true;
    private final Navigator navigator;
    private final List nodes;
    private Namespaces namespaces;

    public NodeListModel(Object nodes) {
        Object node = nodes;
        if (nodes instanceof Collection) {
            this.nodes = new ArrayList((Collection) nodes);
            node = this.nodes.isEmpty() ? null : this.nodes.get(0);
        } else if (nodes != null) {
            this.nodes = Collections.singletonList(nodes);
        } else {
            throw new IllegalArgumentException("nodes == null");
        }
        if (DOM_NODE_CLASS != null && DOM_NODE_CLASS.isInstance(node)) {
            this.navigator = DOM_NAVIGATOR;
        } else if (DOM4J_NODE_CLASS != null && DOM4J_NODE_CLASS.isInstance(node)) {
            this.navigator = DOM4J_NAVIGATOR;
        } else {
            this.navigator = JDOM_NAVIGATOR;
        }
        this.namespaces = createNamespaces();
    }

    private Namespaces createNamespaces() {
        if (useJaxenNamespaces) {
            try {
                return (Namespaces) Class.forName("freemarker.ext.xml._JaxenNamespaces").newInstance();
            } catch (Throwable th) {
                useJaxenNamespaces = false;
            }
        }
        return new Namespaces();
    }

    private NodeListModel(Navigator navigator, List nodes, Namespaces namespaces) {
        this.navigator = navigator;
        this.nodes = nodes;
        this.namespaces = namespaces;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NodeListModel deriveModel(List derivedNodes) {
        this.namespaces.markShared();
        return new NodeListModel(this.navigator, derivedNodes, this.namespaces);
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() {
        return this.nodes.size();
    }

    @Override // freemarker.template.TemplateMethodModel
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1) {
            throw new TemplateModelException("Expecting exactly one argument - an XPath expression");
        }
        return deriveModel(this.navigator.applyXPath(this.nodes, (String) arguments.get(0), this.namespaces));
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() throws TemplateModelException {
        StringWriter sw = new StringWriter(size() * 128);
        for (Object o : this.nodes) {
            if (o instanceof String) {
                sw.write((String) o);
            } else {
                this.navigator.getAsString(o, sw);
            }
        }
        return sw.toString();
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) {
        return deriveModel(Collections.singletonList(this.nodes.get(index)));
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        NodeOperator op = this.navigator.getOperator(key);
        String localName = null;
        String namespaceUri = "";
        if (op == null && key.length() > 0 && key.charAt(0) == '_') {
            if (key.equals("_unique")) {
                return deriveModel(removeDuplicates(this.nodes));
            }
            if (key.equals("_filterType") || key.equals("_ftype")) {
                return new FilterByType();
            }
            if (key.equals("_registerNamespace")) {
                if (this.namespaces.isShared()) {
                    this.namespaces = (Namespaces) this.namespaces.clone();
                }
                return this.namespaces;
            }
        }
        if (op == null) {
            int colon = key.indexOf(58);
            if (colon == -1) {
                localName = key;
            } else {
                localName = key.substring(colon + 1);
                String prefix = key.substring(0, colon);
                namespaceUri = this.namespaces.translateNamespacePrefixToUri(prefix);
                if (namespaceUri == null) {
                    throw new TemplateModelException("Namespace prefix " + prefix + " is not registered.");
                }
            }
            if (localName.charAt(0) == '@') {
                op = this.navigator.getAttributeOperator();
                localName = localName.substring(1);
            } else {
                op = this.navigator.getChildrenOperator();
            }
        }
        List result = new ArrayList();
        Iterator iter = this.nodes.iterator();
        while (iter.hasNext()) {
            try {
                op.process(iter.next(), localName, namespaceUri, result);
            } catch (RuntimeException e) {
                throw new TemplateModelException((Exception) e);
            }
        }
        return deriveModel(result);
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    public void registerNamespace(String prefix, String uri) {
        if (this.namespaces.isShared()) {
            this.namespaces = (Namespaces) this.namespaces.clone();
        }
        this.namespaces.registerNamespace(prefix, uri);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/xml/NodeListModel$FilterByType.class */
    private class FilterByType implements TemplateMethodModel {
        private FilterByType() {
        }

        @Override // freemarker.template.TemplateMethodModel
        public Object exec(List arguments) {
            List filteredNodes = new ArrayList();
            for (Object node : arguments) {
                if (arguments.contains(NodeListModel.this.navigator.getType(node))) {
                    filteredNodes.add(node);
                }
            }
            return NodeListModel.this.deriveModel(filteredNodes);
        }
    }

    private static final List removeDuplicates(List list) {
        int s = list.size();
        ArrayList ulist = new ArrayList(s);
        Set set = new HashSet((s * 4) / 3, 0.75f);
        for (Object o : list) {
            if (set.add(o)) {
                ulist.add(o);
            }
        }
        return ulist;
    }

    private static Class getClass(String className) {
        try {
            return ClassUtil.forName(className);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Couldn't load class " + className, e);
                return null;
            }
            return null;
        }
    }

    private static Navigator getNavigator(String navType) {
        try {
            return (Navigator) ClassUtil.forName("freemarker.ext.xml._" + navType + "Navigator").newInstance();
        } catch (Throwable t) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not load navigator for " + navType, t);
                return null;
            }
            return null;
        }
    }

    @Override // freemarker.template.TemplateNodeModel
    public TemplateSequenceModel getChildNodes() throws TemplateModelException {
        return (TemplateSequenceModel) get("_content");
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeName() throws TemplateModelException {
        return getUniqueText((NodeListModel) get("_name"), "name");
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeNamespace() throws TemplateModelException {
        return getUniqueText((NodeListModel) get("_nsuri"), "namespace");
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeType() throws TemplateModelException {
        return getUniqueText((NodeListModel) get("_type"), "type");
    }

    @Override // freemarker.template.TemplateNodeModel
    public TemplateNodeModel getParentNode() throws TemplateModelException {
        return (TemplateNodeModel) get("_parent");
    }

    private String getUniqueText(NodeListModel model, String property) throws TemplateModelException {
        String s1 = null;
        Set s = null;
        for (String s2 : model.nodes) {
            if (s2 != null) {
                if (s1 == null) {
                    s1 = s2;
                } else if (!s1.equals(s2)) {
                    if (s == null) {
                        s = new HashSet();
                        s.add(s1);
                    }
                    s.add(s2);
                }
            }
        }
        if (s == null) {
            return s1;
        }
        throw new TemplateModelException("Value for node " + property + " is ambiguos: " + s);
    }
}
