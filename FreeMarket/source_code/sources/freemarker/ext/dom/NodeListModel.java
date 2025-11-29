package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/NodeListModel.class */
class NodeListModel extends SimpleSequence implements TemplateHashModel, _UnexpectedTypeErrorExplainerTemplateModel {
    NodeModel contextNode;
    XPathSupport xpathSupport;
    private static final ObjectWrapper NODE_WRAPPER = new ObjectWrapper() { // from class: freemarker.ext.dom.NodeListModel.1
        @Override // freemarker.template.ObjectWrapper
        public TemplateModel wrap(Object obj) {
            if (obj instanceof NodeModel) {
                return (NodeModel) obj;
            }
            return NodeModel.wrap((Node) obj);
        }
    };

    NodeListModel(Node contextNode) {
        this(NodeModel.wrap(contextNode));
    }

    NodeListModel(NodeModel contextNode) {
        super(NODE_WRAPPER);
        this.contextNode = contextNode;
    }

    NodeListModel(NodeList nodeList, NodeModel contextNode) {
        super(NODE_WRAPPER);
        for (int i = 0; i < nodeList.getLength(); i++) {
            this.list.add(nodeList.item(i));
        }
        this.contextNode = contextNode;
    }

    NodeListModel(NamedNodeMap nodeList, NodeModel contextNode) {
        super(NODE_WRAPPER);
        for (int i = 0; i < nodeList.getLength(); i++) {
            this.list.add(nodeList.item(i));
        }
        this.contextNode = contextNode;
    }

    NodeListModel(List list, NodeModel contextNode) {
        super(list, NODE_WRAPPER);
        this.contextNode = contextNode;
    }

    NodeListModel filterByName(String name) throws TemplateModelException {
        NodeListModel result = new NodeListModel(this.contextNode);
        int size = size();
        if (size == 0) {
            return result;
        }
        Environment env = Environment.getCurrentEnvironment();
        for (int i = 0; i < size; i++) {
            NodeModel nm = (NodeModel) get(i);
            if ((nm instanceof ElementModel) && ((ElementModel) nm).matchesName(name, env)) {
                result.add(nm);
            }
        }
        return result;
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        TemplateSequenceModel tsm;
        int size = size();
        if (size == 1) {
            return ((NodeModel) get(0)).get(key);
        }
        if (key.startsWith("@@")) {
            if (key.equals(AtAtKey.MARKUP.getKey()) || key.equals(AtAtKey.NESTED_MARKUP.getKey()) || key.equals(AtAtKey.TEXT.getKey())) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    TemplateScalarModel textModel = (TemplateScalarModel) ((NodeModel) get(i)).get(key);
                    result.append(textModel.getAsString());
                }
                return new SimpleScalar(result.toString());
            }
            if (key.length() != 2) {
                if (AtAtKey.containsKey(key)) {
                    throw new TemplateModelException("\"" + key + "\" is only applicable to a single XML node, but it was applied on " + (size != 0 ? size + " XML nodes (multiple matches)." : "an empty list of XML nodes (no matches)."));
                }
                throw new TemplateModelException("Unsupported @@ key: " + key);
            }
        }
        if (DomStringUtil.isXMLNameLike(key) || ((key.startsWith("@") && (DomStringUtil.isXMLNameLike(key, 1) || key.equals("@@") || key.equals("@*"))) || key.equals("*") || key.equals(SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS))) {
            NodeListModel result2 = new NodeListModel(this.contextNode);
            for (int i2 = 0; i2 < size; i2++) {
                NodeModel nm = (NodeModel) get(i2);
                if ((nm instanceof ElementModel) && (tsm = (TemplateSequenceModel) nm.get(key)) != null) {
                    int tsmSize = tsm.size();
                    for (int j = 0; j < tsmSize; j++) {
                        result2.add(tsm.get(j));
                    }
                }
            }
            if (result2.size() == 1) {
                return result2.get(0);
            }
            return result2;
        }
        XPathSupport xps = getXPathSupport();
        if (xps == null) {
            throw new TemplateModelException("No XPath support is available (add Apache Xalan or Jaxen as dependency). This is either malformed, or an XPath expression: " + key);
        }
        Object context = size == 0 ? null : rawNodeList();
        return xps.executeQuery(context, key);
    }

    private List rawNodeList() throws TemplateModelException {
        int size = size();
        ArrayList al = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            al.add(((NodeModel) get(i)).node);
        }
        return al;
    }

    XPathSupport getXPathSupport() throws TemplateModelException {
        if (this.xpathSupport == null) {
            if (this.contextNode != null) {
                this.xpathSupport = this.contextNode.getXPathSupport();
            } else if (size() > 0) {
                this.xpathSupport = ((NodeModel) get(0)).getXPathSupport();
            }
        }
        return this.xpathSupport;
    }

    @Override // freemarker.core._UnexpectedTypeErrorExplainerTemplateModel
    public Object[] explainTypeError(Class[] expectedClasses) {
        for (Class expectedClass : expectedClasses) {
            if (TemplateScalarModel.class.isAssignableFrom(expectedClass) || TemplateDateModel.class.isAssignableFrom(expectedClass) || TemplateNumberModel.class.isAssignableFrom(expectedClass) || TemplateBooleanModel.class.isAssignableFrom(expectedClass)) {
                return newTypeErrorExplanation("string");
            }
            if (TemplateNodeModel.class.isAssignableFrom(expectedClass)) {
                return newTypeErrorExplanation("node");
            }
        }
        return null;
    }

    private Object[] newTypeErrorExplanation(String type) {
        int size = size();
        Object[] objArr = new Object[6];
        objArr[0] = "This XML query result can't be used as ";
        objArr[1] = type;
        objArr[2] = " because for that it had to contain exactly 1 XML node, but it contains ";
        objArr[3] = Integer.valueOf(size);
        objArr[4] = " nodes. That is, the constructing XML query has found ";
        objArr[5] = size == 0 ? "no matches." : "multiple matches.";
        return objArr;
    }
}
