package javax.el;

import java.lang.reflect.AccessibleObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/JreCompat.class */
class JreCompat {
    private static final JreCompat instance;

    JreCompat() {
    }

    static {
        if (Jre9Compat.isSupported()) {
            instance = new Jre9Compat();
        } else {
            instance = new JreCompat();
        }
    }

    public static JreCompat getInstance() {
        return instance;
    }

    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        return true;
    }

    public boolean isExported(Class<?> type) {
        return true;
    }
}
