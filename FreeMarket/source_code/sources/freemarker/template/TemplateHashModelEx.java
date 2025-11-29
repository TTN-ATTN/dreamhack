package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateHashModelEx.class */
public interface TemplateHashModelEx extends TemplateHashModel {
    int size() throws TemplateModelException;

    TemplateCollectionModel keys() throws TemplateModelException;

    TemplateCollectionModel values() throws TemplateModelException;
}
