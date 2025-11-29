package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateNodeModel.class */
public interface TemplateNodeModel extends TemplateModel {
    TemplateNodeModel getParentNode() throws TemplateModelException;

    TemplateSequenceModel getChildNodes() throws TemplateModelException;

    String getNodeName() throws TemplateModelException;

    String getNodeType() throws TemplateModelException;

    String getNodeNamespace() throws TemplateModelException;
}
