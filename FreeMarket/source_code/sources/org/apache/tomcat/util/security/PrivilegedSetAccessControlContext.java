package org.apache.tomcat.util.security;

import java.lang.reflect.Field;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/security/PrivilegedSetAccessControlContext.class */
public class PrivilegedSetAccessControlContext implements PrivilegedAction<Void> {
    private static final Log log = LogFactory.getLog((Class<?>) PrivilegedSetAccessControlContext.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) PrivilegedSetAccessControlContext.class);
    private static final AccessControlContext acc = AccessController.getContext();
    private static final Field field;
    private final Thread t;

    static {
        Field f = null;
        try {
            f = Thread.class.getDeclaredField("inheritedAccessControlContext");
            f.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            log.warn(sm.getString("privilegedSetAccessControlContext.lookupFailed"), e);
        }
        field = f;
    }

    public PrivilegedSetAccessControlContext(Thread t) {
        this.t = t;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Void run() throws IllegalAccessException, IllegalArgumentException {
        try {
            if (field != null) {
                field.set(this.t, acc);
            }
            return null;
        } catch (IllegalAccessException | IllegalArgumentException e) {
            log.warn(sm.getString("privilegedSetAccessControlContext.setFailed"), e);
            return null;
        }
    }
}
