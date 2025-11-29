package freemarker.ext.dom;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import org.w3c.dom.DocumentType;
import org.w3c.dom.ProcessingInstruction;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/DocumentTypeModel.class */
class DocumentTypeModel extends NodeModel {
    public DocumentTypeModel(DocumentType docType) {
        super(docType);
    }

    public String getAsString() {
        return ((ProcessingInstruction) this.node).getData();
    }

    public TemplateSequenceModel getChildren() throws TemplateModelException {
        throw new TemplateModelException("entering the child nodes of a DTD node is not currently supported");
    }

    @Override // freemarker.ext.dom.NodeModel, freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        throw new TemplateModelException("accessing properties of a DTD is not currently supported");
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeName() {
        return "@document_type$" + this.node.getNodeName();
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return true;
    }
}
