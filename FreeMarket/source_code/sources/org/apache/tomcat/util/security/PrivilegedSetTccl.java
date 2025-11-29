package org.apache.tomcat.util.security;

import java.security.PrivilegedAction;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/security/PrivilegedSetTccl.class */
public class PrivilegedSetTccl implements PrivilegedAction<Void> {
    private final ClassLoader cl;
    private final Thread t;

    @Deprecated
    public PrivilegedSetTccl(ClassLoader cl) {
        this(Thread.currentThread(), cl);
    }

    public PrivilegedSetTccl(Thread t, ClassLoader cl) {
        this.t = t;
        this.cl = cl;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Void run() {
        this.t.setContextClassLoader(this.cl);
        return null;
    }
}
