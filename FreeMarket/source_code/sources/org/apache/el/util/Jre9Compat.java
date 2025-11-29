package org.apache.el.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:org/apache/el/util/Jre9Compat.class */
class Jre9Compat extends JreCompat {
    private static final Method canAccessMethod;

    Jre9Compat() {
    }

    static {
        Method m1 = null;
        try {
            m1 = AccessibleObject.class.getMethod("canAccess", Object.class);
        } catch (NoSuchMethodException e) {
        }
        canAccessMethod = m1;
    }

    public static boolean isSupported() {
        return canAccessMethod != null;
    }

    @Override // org.apache.el.util.JreCompat
    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        try {
            return ((Boolean) canAccessMethod.invoke(accessibleObject, base)).booleanValue();
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            return false;
        }
    }
}
