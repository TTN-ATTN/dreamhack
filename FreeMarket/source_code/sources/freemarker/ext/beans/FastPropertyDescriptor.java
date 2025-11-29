package freemarker.ext.beans;

import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/FastPropertyDescriptor.class */
final class FastPropertyDescriptor {
    private final Method readMethod;
    private final Method indexedReadMethod;
    private final boolean methodInsteadOfPropertyValueBeforeCall;

    public FastPropertyDescriptor(Method readMethod, Method indexedReadMethod, boolean methodInsteadOfPropertyValueBeforeCall) {
        this.readMethod = readMethod;
        this.indexedReadMethod = indexedReadMethod;
        this.methodInsteadOfPropertyValueBeforeCall = methodInsteadOfPropertyValueBeforeCall;
    }

    public Method getReadMethod() {
        return this.readMethod;
    }

    public Method getIndexedReadMethod() {
        return this.indexedReadMethod;
    }

    public boolean isMethodInsteadOfPropertyValueBeforeCall() {
        return this.methodInsteadOfPropertyValueBeforeCall;
    }
}
