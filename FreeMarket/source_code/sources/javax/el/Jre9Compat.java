package javax.el;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Jre9Compat.class */
class Jre9Compat extends JreCompat {
    private static final Method canAccessMethod;
    private static final Method getModuleMethod;
    private static final Method isExportedMethod;

    Jre9Compat() {
    }

    static {
        Method m1 = null;
        Method m2 = null;
        Method m3 = null;
        try {
            m1 = AccessibleObject.class.getMethod("canAccess", Object.class);
            m2 = Class.class.getMethod("getModule", new Class[0]);
            Class<?> moduleClass = Class.forName("java.lang.Module");
            m3 = moduleClass.getMethod("isExported", String.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e2) {
        }
        canAccessMethod = m1;
        getModuleMethod = m2;
        isExportedMethod = m3;
    }

    public static boolean isSupported() {
        return canAccessMethod != null;
    }

    @Override // javax.el.JreCompat
    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        try {
            return ((Boolean) canAccessMethod.invoke(accessibleObject, base)).booleanValue();
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            return false;
        }
    }

    @Override // javax.el.JreCompat
    public boolean isExported(Class<?> type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            String packageName = type.getPackage().getName();
            Object module = getModuleMethod.invoke(type, new Object[0]);
            return ((Boolean) isExportedMethod.invoke(module, packageName)).booleanValue();
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
