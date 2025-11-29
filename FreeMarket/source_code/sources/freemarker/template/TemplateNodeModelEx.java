package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateNodeModelEx.class */
public interface TemplateNodeModelEx extends TemplateNodeModel {
    TemplateNodeModelEx getPreviousSibling() throws TemplateModelException;

    TemplateNodeModelEx getNextSibling() throws TemplateModelException;
}
