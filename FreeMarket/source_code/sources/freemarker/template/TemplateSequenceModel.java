package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateSequenceModel.class */
public interface TemplateSequenceModel extends TemplateModel {
    TemplateModel get(int i) throws TemplateModelException;

    int size() throws TemplateModelException;
}
