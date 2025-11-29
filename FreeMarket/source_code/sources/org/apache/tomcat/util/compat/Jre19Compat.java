package org.apache.tomcat.util.compat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.jar.JarFile;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/compat/Jre19Compat.class */
public class Jre19Compat extends Jre16Compat {
    private static final Log log = LogFactory.getLog((Class<?>) Jre19Compat.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Jre19Compat.class);
    private static final boolean supported;

    @Override // org.apache.tomcat.util.compat.Jre16Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ SocketChannel openUnixDomainSocketChannel() {
        return super.openUnixDomainSocketChannel();
    }

    @Override // org.apache.tomcat.util.compat.Jre16Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ ServerSocketChannel openUnixDomainServerSocketChannel() {
        return super.openUnixDomainServerSocketChannel();
    }

    @Override // org.apache.tomcat.util.compat.Jre16Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ SocketAddress getUnixDomainSocketAddress(String str) {
        return super.getUnixDomainSocketAddress(str);
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ String getModuleName(Class cls) {
        return super.getModuleName(cls);
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ boolean isExported(Class cls) {
        return super.isExported(cls);
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ boolean canAccess(Object obj, AccessibleObject accessibleObject) {
        return super.canAccess(obj, accessibleObject);
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ int jarFileRuntimeMajorVersion() {
        return super.jarFileRuntimeMajorVersion();
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ boolean jarFileIsMultiRelease(JarFile jarFile) {
        return super.jarFileIsMultiRelease(jarFile);
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ JarFile jarFileNewInstance(File file) throws IOException {
        return super.jarFileNewInstance(file);
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ void addBootModulePath(Deque deque) {
        super.addBootModulePath(deque);
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ void disableCachingForJarUrlConnections() throws IOException {
        super.disableCachingForJarUrlConnections();
    }

    @Override // org.apache.tomcat.util.compat.Jre9Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ boolean isInstanceOfInaccessibleObjectException(Throwable th) {
        return super.isInstanceOfInaccessibleObjectException(th);
    }

    static {
        Class<?> c1 = null;
        try {
            c1 = Class.forName("java.lang.WrongThreadException");
        } catch (ClassNotFoundException cnfe) {
            log.debug(sm.getString("jre19Compat.javaPre19"), cnfe);
        }
        supported = c1 != null;
    }

    static boolean isSupported() {
        return supported;
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public Object getExecutor(Thread thread) throws IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException {
        Object result = super.getExecutor(thread);
        if (result == null) {
            try {
                Field holderField = thread.getClass().getDeclaredField("holder");
                holderField.setAccessible(true);
                Object holder = holderField.get(thread);
                Field taskField = holder.getClass().getDeclaredField("task");
                taskField.setAccessible(true);
                Object task = taskField.get(holder);
                if (task != null && task.getClass().getCanonicalName() != null && (task.getClass().getCanonicalName().equals("org.apache.tomcat.util.threads.ThreadPoolExecutor.Worker") || task.getClass().getCanonicalName().equals("java.util.concurrent.ThreadPoolExecutor.Worker"))) {
                    Field executorField = task.getClass().getDeclaredField("this$0");
                    executorField.setAccessible(true);
                    result = executorField.get(task);
                }
            } catch (NoSuchFieldException e) {
                return null;
            }
        }
        return result;
    }
}
