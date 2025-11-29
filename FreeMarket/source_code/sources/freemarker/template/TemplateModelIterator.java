package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateModelIterator.class */
public interface TemplateModelIterator {
    TemplateModel next() throws TemplateModelException;

    boolean hasNext() throws TemplateModelException;
}
