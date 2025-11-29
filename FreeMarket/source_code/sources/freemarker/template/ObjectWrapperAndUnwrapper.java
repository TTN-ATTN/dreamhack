package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/ObjectWrapperAndUnwrapper.class */
public interface ObjectWrapperAndUnwrapper extends ObjectWrapper {
    public static final Object CANT_UNWRAP_TO_TARGET_CLASS = new Object();

    Object unwrap(TemplateModel templateModel) throws TemplateModelException;

    Object tryUnwrapTo(TemplateModel templateModel, Class<?> cls) throws TemplateModelException;
}
