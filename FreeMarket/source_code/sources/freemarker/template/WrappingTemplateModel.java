package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/WrappingTemplateModel.class */
public abstract class WrappingTemplateModel {

    @Deprecated
    private static ObjectWrapper defaultObjectWrapper = DefaultObjectWrapper.instance;
    private ObjectWrapper objectWrapper;

    @Deprecated
    public static void setDefaultObjectWrapper(ObjectWrapper objectWrapper) {
        defaultObjectWrapper = objectWrapper;
    }

    @Deprecated
    public static ObjectWrapper getDefaultObjectWrapper() {
        return defaultObjectWrapper;
    }

    @Deprecated
    protected WrappingTemplateModel() {
        this(defaultObjectWrapper);
    }

    protected WrappingTemplateModel(ObjectWrapper objectWrapper) {
        this.objectWrapper = objectWrapper != null ? objectWrapper : defaultObjectWrapper;
        if (this.objectWrapper == null) {
            DefaultObjectWrapper defaultObjectWrapper2 = new DefaultObjectWrapper();
            defaultObjectWrapper = defaultObjectWrapper2;
            this.objectWrapper = defaultObjectWrapper2;
        }
    }

    public ObjectWrapper getObjectWrapper() {
        return this.objectWrapper;
    }

    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        this.objectWrapper = objectWrapper;
    }

    protected final TemplateModel wrap(Object obj) throws TemplateModelException {
        return this.objectWrapper.wrap(obj);
    }
}
