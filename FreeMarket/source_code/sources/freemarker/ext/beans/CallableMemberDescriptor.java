package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.InvocationTargetException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/CallableMemberDescriptor.class */
abstract class CallableMemberDescriptor extends MaybeEmptyCallableMemberDescriptor {
    abstract TemplateModel invokeMethod(BeansWrapper beansWrapper, Object obj, Object[] objArr) throws IllegalAccessException, TemplateModelException, InvocationTargetException;

    abstract Object invokeConstructor(BeansWrapper beansWrapper, Object[] objArr) throws IllegalAccessException, TemplateModelException, InstantiationException, IllegalArgumentException, InvocationTargetException;

    abstract String getDeclaration();

    abstract boolean isConstructor();

    abstract boolean isStatic();

    abstract boolean isVarargs();

    abstract Class[] getParamTypes();

    abstract String getName();

    CallableMemberDescriptor() {
    }
}
