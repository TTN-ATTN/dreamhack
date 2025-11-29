package freemarker.template.utility;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DOMNodeModel.class */
public class DOMNodeModel implements TemplateHashModel {
    private static HashMap equivalenceTable = new HashMap();
    private Node node;
    private HashMap cache = new HashMap();

    static {
        equivalenceTable.put("*", "children");
        equivalenceTable.put("@*", "attributes");
    }

    public DOMNodeModel(Node node) {
        this.node = node;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        TemplateModel result = null;
        if (equivalenceTable.containsKey(key)) {
            key = (String) equivalenceTable.get(key);
        }
        if (this.cache.containsKey(key)) {
            result = (TemplateModel) this.cache.get(key);
        }
        if (result == null) {
            if ("attributes".equals(key)) {
                NamedNodeMap attributes = this.node.getAttributes();
                if (attributes != null) {
                    SimpleHash hash = new SimpleHash(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        Attr att = (Attr) attributes.item(i);
                        hash.put(att.getName(), att.getValue());
                    }
                    result = hash;
                }
            } else if (key.charAt(0) == '@') {
                if (this.node instanceof Element) {
                    String attValue = ((Element) this.node).getAttribute(key.substring(1));
                    result = new SimpleScalar(attValue);
                } else {
                    throw new TemplateModelException("Trying to get an attribute value for a non-element node");
                }
            } else if ("is_element".equals(key)) {
                result = this.node instanceof Element ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            } else if ("is_text".equals(key)) {
                result = this.node instanceof Text ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            } else if ("name".equals(key)) {
                result = new SimpleScalar(this.node.getNodeName());
            } else if ("children".equals(key)) {
                result = new NodeListTM(this.node.getChildNodes());
            } else if ("parent".equals(key)) {
                Node parent = this.node.getParentNode();
                result = parent == null ? null : new DOMNodeModel(parent);
            } else if ("ancestorByName".equals(key)) {
                result = new AncestorByName();
            } else if ("nextSibling".equals(key)) {
                Node next = this.node.getNextSibling();
                result = next == null ? null : new DOMNodeModel(next);
            } else if ("previousSibling".equals(key)) {
                Node previous = this.node.getPreviousSibling();
                result = previous == null ? null : new DOMNodeModel(previous);
            } else if ("nextSiblingElement".equals(key)) {
                Node next2 = nextSiblingElement(this.node);
                result = next2 == null ? null : new DOMNodeModel(next2);
            } else if ("previousSiblingElement".equals(key)) {
                Node previous2 = previousSiblingElement(this.node);
                result = previous2 == null ? null : new DOMNodeModel(previous2);
            } else if ("nextElement".equals(key)) {
                Node next3 = nextElement(this.node);
                result = next3 == null ? null : new DOMNodeModel(next3);
            } else if ("previousElement".equals(key)) {
                Node previous3 = previousElement(this.node);
                result = previous3 == null ? null : new DOMNodeModel(previous3);
            } else if ("text".equals(key)) {
                result = new SimpleScalar(getText(this.node));
            }
            this.cache.put(key, result);
        }
        return result;
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return false;
    }

    private static String getText(Node node) {
        String result = "";
        if (node instanceof Text) {
            result = ((Text) node).getData();
        } else if (node instanceof Element) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                result = result + getText(children.item(i));
            }
        }
        return result;
    }

    private static Element nextSiblingElement(Node node) {
        Node next = node;
        while (next != null) {
            next = next.getNextSibling();
            if (next instanceof Element) {
                return (Element) next;
            }
        }
        return null;
    }

    private static Element previousSiblingElement(Node node) {
        Node previous = node;
        while (previous != null) {
            previous = previous.getPreviousSibling();
            if (previous instanceof Element) {
                return (Element) previous;
            }
        }
        return null;
    }

    private static Element nextElement(Node node) {
        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    return (Element) child;
                }
            }
        }
        Element nextSiblingElement = nextSiblingElement(node);
        if (nextSiblingElement != null) {
            return nextSiblingElement;
        }
        Node parentNode = node.getParentNode();
        while (true) {
            Node parent = parentNode;
            if (parent instanceof Element) {
                Element next = nextSiblingElement(parent);
                if (next != null) {
                    return next;
                }
                parentNode = parent.getParentNode();
            } else {
                return null;
            }
        }
    }

    private static Element previousElement(Node node) {
        Element result = previousSiblingElement(node);
        if (result != null) {
            return result;
        }
        Node parent = node.getParentNode();
        if (parent instanceof Element) {
            return (Element) parent;
        }
        return null;
    }

    void setParent(DOMNodeModel parent) {
        if (parent != null) {
            this.cache.put("parent", parent);
        }
    }

    String getNodeName() {
        return this.node.getNodeName();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DOMNodeModel$AncestorByName.class */
    class AncestorByName implements TemplateMethodModel {
        AncestorByName() {
        }

        @Override // freemarker.template.TemplateMethodModel
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 1) {
                throw new TemplateModelException("Expecting exactly one string argument here");
            }
            String nodeName = (String) arguments.get(0);
            TemplateModel templateModel = DOMNodeModel.this.get("parent");
            while (true) {
                DOMNodeModel ancestor = (DOMNodeModel) templateModel;
                if (ancestor != null) {
                    if (nodeName.equals(ancestor.getNodeName())) {
                        return ancestor;
                    }
                    templateModel = ancestor.get("parent");
                } else {
                    return null;
                }
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DOMNodeModel$NodeListTM.class */
    class NodeListTM implements TemplateSequenceModel, TemplateMethodModel {
        private NodeList nodeList;
        private TemplateModel[] nodes;

        NodeListTM(NodeList nodeList) {
            this.nodeList = nodeList;
            this.nodes = new TemplateModel[nodeList.getLength()];
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) {
            DOMNodeModel result = (DOMNodeModel) this.nodes[index];
            if (result == null) {
                result = new DOMNodeModel(this.nodeList.item(index));
                this.nodes[index] = result;
                result.setParent(DOMNodeModel.this);
            }
            return result;
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() {
            return this.nodes.length;
        }

        @Override // freemarker.template.TemplateMethodModel
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() == 1) {
                if (DOMNodeModel.this.node instanceof Element) {
                    Element elem = (Element) DOMNodeModel.this.node;
                    return DOMNodeModel.this.new NodeListTM(elem.getElementsByTagName((String) arguments.get(0)));
                }
                throw new TemplateModelException("Expecting element here.");
            }
            throw new TemplateModelException("Expecting exactly one string argument here");
        }
    }
}
