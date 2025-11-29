package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateHashModel.class */
public interface TemplateHashModel extends TemplateModel {
    TemplateModel get(String str) throws TemplateModelException;

    boolean isEmpty() throws TemplateModelException;
}
