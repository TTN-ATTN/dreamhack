package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateHashModelEx2.class */
public interface TemplateHashModelEx2 extends TemplateHashModelEx {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateHashModelEx2$KeyValuePair.class */
    public interface KeyValuePair {
        TemplateModel getKey() throws TemplateModelException;

        TemplateModel getValue() throws TemplateModelException;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateHashModelEx2$KeyValuePairIterator.class */
    public interface KeyValuePairIterator {
        boolean hasNext() throws TemplateModelException;

        KeyValuePair next() throws TemplateModelException;
    }

    KeyValuePairIterator keyValuePairIterator() throws TemplateModelException;
}
