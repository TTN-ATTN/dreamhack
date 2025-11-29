package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleObjectWrapper.class */
public class SimpleObjectWrapper extends DefaultObjectWrapper {
    static final SimpleObjectWrapper instance = new SimpleObjectWrapper();

    @Deprecated
    public SimpleObjectWrapper() {
    }

    public SimpleObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    @Override // freemarker.template.DefaultObjectWrapper
    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        throw new TemplateModelException(getClass().getName() + " deliberately won't wrap this type: " + obj.getClass().getName());
    }

    @Override // freemarker.ext.beans.BeansWrapper, freemarker.template.utility.ObjectWrapperWithAPISupport
    public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
        throw new TemplateModelException(getClass().getName() + " deliberately doesn't allow ?api.");
    }
}
