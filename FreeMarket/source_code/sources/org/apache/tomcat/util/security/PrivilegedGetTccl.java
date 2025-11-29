package org.apache.tomcat.util.security;

import java.security.PrivilegedAction;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/security/PrivilegedGetTccl.class */
public class PrivilegedGetTccl implements PrivilegedAction<ClassLoader> {
    private final Thread currentThread;

    @Deprecated
    public PrivilegedGetTccl() {
        this(Thread.currentThread());
    }

    public PrivilegedGetTccl(Thread currentThread) {
        this.currentThread = currentThread;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public ClassLoader run() {
        return this.currentThread.getContextClassLoader();
    }
}
