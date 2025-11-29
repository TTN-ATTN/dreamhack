package freemarker.template;

import freemarker.ext.beans.BeansWrapper;

@SuppressFBWarnings(value = {"IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION"}, justification = "BC")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/ObjectWrapper.class */
public interface ObjectWrapper {

    @Deprecated
    public static final ObjectWrapper BEANS_WRAPPER = BeansWrapper.getDefaultInstance();

    @Deprecated
    public static final ObjectWrapper DEFAULT_WRAPPER = DefaultObjectWrapper.instance;

    @Deprecated
    public static final ObjectWrapper SIMPLE_WRAPPER = SimpleObjectWrapper.instance;

    TemplateModel wrap(Object obj) throws TemplateModelException;
}
