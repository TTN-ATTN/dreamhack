package freemarker.ext.dom;

import freemarker.template.TemplateScalarModel;
import org.w3c.dom.ProcessingInstruction;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/PINodeModel.class */
class PINodeModel extends NodeModel implements TemplateScalarModel {
    public PINodeModel(ProcessingInstruction pi) {
        super(pi);
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() {
        return ((ProcessingInstruction) this.node).getData();
    }

    @Override // freemarker.template.TemplateNodeModel
    public String getNodeName() {
        return "@pi$" + ((ProcessingInstruction) this.node).getTarget();
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return true;
    }
}
