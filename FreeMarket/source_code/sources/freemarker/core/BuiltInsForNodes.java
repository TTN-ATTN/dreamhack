package freemarker.core;

import freemarker.ext.dom._ExtDomApi;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template._ObjectWrappers;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes.class */
class BuiltInsForNodes {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$ancestorsBI.class */
    static class ancestorsBI extends BuiltInForNode {
        ancestorsBI() {
        }

        @Override // freemarker.core.BuiltInForNode
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            AncestorSequence result = new AncestorSequence(env);
            TemplateNodeModel parentNode = nodeModel.getParentNode();
            while (true) {
                TemplateNodeModel parent = parentNode;
                if (parent != null) {
                    result.add(parent);
                    parentNode = parent.getParentNode();
                } else {
                    return result;
                }
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$childrenBI.class */
    static class childrenBI extends BuiltInForNode {
        childrenBI() {
        }

        @Override // freemarker.core.BuiltInForNode
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getChildNodes();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$node_nameBI.class */
    static class node_nameBI extends BuiltInForNode {
        node_nameBI() {
        }

        @Override // freemarker.core.BuiltInForNode
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return new SimpleScalar(nodeModel.getNodeName());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$node_namespaceBI.class */
    static class node_namespaceBI extends BuiltInForNode {
        node_namespaceBI() {
        }

        @Override // freemarker.core.BuiltInForNode
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            String nsURI = nodeModel.getNodeNamespace();
            if (nsURI == null) {
                return null;
            }
            return new SimpleScalar(nsURI);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$node_typeBI.class */
    static class node_typeBI extends BuiltInForNode {
        node_typeBI() {
        }

        @Override // freemarker.core.BuiltInForNode
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return new SimpleScalar(nodeModel.getNodeType());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$parentBI.class */
    static class parentBI extends BuiltInForNode {
        parentBI() {
        }

        @Override // freemarker.core.BuiltInForNode
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getParentNode();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$rootBI.class */
    static class rootBI extends BuiltInForNode {
        rootBI() {
        }

        @Override // freemarker.core.BuiltInForNode
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            TemplateNodeModel result = nodeModel;
            TemplateNodeModel parentNode = nodeModel.getParentNode();
            while (true) {
                TemplateNodeModel parent = parentNode;
                if (parent != null) {
                    result = parent;
                    parentNode = result.getParentNode();
                } else {
                    return result;
                }
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$previousSiblingBI.class */
    static class previousSiblingBI extends BuiltInForNodeEx {
        previousSiblingBI() {
        }

        @Override // freemarker.core.BuiltInForNodeEx
        TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getPreviousSibling();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$nextSiblingBI.class */
    static class nextSiblingBI extends BuiltInForNodeEx {
        nextSiblingBI() {
        }

        @Override // freemarker.core.BuiltInForNodeEx
        TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getNextSibling();
        }
    }

    private BuiltInsForNodes() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNodes$AncestorSequence.class */
    static class AncestorSequence extends SimpleSequence implements TemplateMethodModel {

        @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "Can't make this Serializable, and not extending SimpleSequence would be non-BC.")
        private Environment env;

        AncestorSequence(Environment env) {
            super(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
            this.env = env;
        }

        @Override // freemarker.template.TemplateMethodModel
        public Object exec(List names) throws TemplateModelException {
            if (names == null || names.isEmpty()) {
                return this;
            }
            AncestorSequence result = new AncestorSequence(this.env);
            for (int i = 0; i < size(); i++) {
                TemplateNodeModel tnm = (TemplateNodeModel) get(i);
                String nodeName = tnm.getNodeName();
                String nsURI = tnm.getNodeNamespace();
                if (nsURI == null) {
                    if (names.contains(nodeName)) {
                        result.add(tnm);
                    }
                } else {
                    int j = 0;
                    while (true) {
                        if (j >= names.size()) {
                            break;
                        }
                        if (!_ExtDomApi.matchesName((String) names.get(j), nodeName, nsURI, this.env)) {
                            j++;
                        } else {
                            result.add(tnm);
                            break;
                        }
                    }
                }
            }
            return result;
        }
    }
}
