package org.apache.tomcat.util.compat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.jar.JarFile;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.validation.DataBinder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/compat/JreCompat.class */
public class JreCompat {
    private static final int RUNTIME_MAJOR_VERSION = 8;
    private static final JreCompat instance;
    private static final boolean graalAvailable;
    private static final boolean jre9Available;
    private static final boolean jre11Available;
    private static final boolean jre16Available;
    private static final boolean jre19Available;
    private static final StringManager sm = StringManager.getManager((Class<?>) JreCompat.class);
    protected static final Method setApplicationProtocolsMethod;
    protected static final Method getApplicationProtocolMethod;

    static {
        boolean result = false;
        try {
            Class<?> nativeImageClazz = Class.forName("org.graalvm.nativeimage.ImageInfo");
            result = Boolean.TRUE.equals(nativeImageClazz.getMethod("inImageCode", new Class[0]).invoke(null, new Object[0]));
        } catch (ClassNotFoundException e) {
        } catch (IllegalArgumentException | ReflectiveOperationException e2) {
        }
        graalAvailable = result || System.getProperty("org.graalvm.nativeimage.imagecode") != null;
        if (Jre19Compat.isSupported()) {
            instance = new Jre19Compat();
            jre19Available = true;
            jre16Available = true;
            jre9Available = true;
        } else if (Jre16Compat.isSupported()) {
            instance = new Jre16Compat();
            jre19Available = false;
            jre16Available = true;
            jre9Available = true;
        } else if (Jre9Compat.isSupported()) {
            instance = new Jre9Compat();
            jre19Available = false;
            jre16Available = false;
            jre9Available = true;
        } else {
            instance = new JreCompat();
            jre19Available = false;
            jre16Available = false;
            jre9Available = false;
        }
        jre11Available = instance.jarFileRuntimeMajorVersion() >= 11;
        Method m1 = null;
        Method m2 = null;
        try {
            m1 = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            m2 = SSLEngine.class.getMethod("getApplicationProtocol", new Class[0]);
        } catch (IllegalArgumentException | ReflectiveOperationException e3) {
        }
        setApplicationProtocolsMethod = m1;
        getApplicationProtocolMethod = m2;
    }

    public static JreCompat getInstance() {
        return instance;
    }

    public static boolean isGraalAvailable() {
        return graalAvailable;
    }

    public static boolean isAlpnSupported() {
        return (setApplicationProtocolsMethod == null || getApplicationProtocolMethod == null) ? false : true;
    }

    public static boolean isJre9Available() {
        return jre9Available;
    }

    public static boolean isJre11Available() {
        return jre11Available;
    }

    public static boolean isJre16Available() {
        return jre16Available;
    }

    public static boolean isJre19Available() {
        return jre19Available;
    }

    public boolean isInstanceOfInaccessibleObjectException(Throwable t) {
        return false;
    }

    public void setApplicationProtocols(SSLParameters sslParameters, String[] protocols) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (setApplicationProtocolsMethod != null) {
            try {
                setApplicationProtocolsMethod.invoke(sslParameters, protocols);
                return;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        throw new UnsupportedOperationException(sm.getString("jreCompat.noApplicationProtocols"));
    }

    public String getApplicationProtocol(SSLEngine sslEngine) {
        if (getApplicationProtocolMethod != null) {
            try {
                return (String) getApplicationProtocolMethod.invoke(sslEngine, new Object[0]);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        throw new UnsupportedOperationException(sm.getString("jreCompat.noApplicationProtocol"));
    }

    public void disableCachingForJarUrlConnections() throws IOException {
        URL url = new URL("jar:file://dummy.jar!/");
        URLConnection uConn = url.openConnection();
        uConn.setDefaultUseCaches(false);
    }

    public void addBootModulePath(Deque<URL> classPathUrlsToProcess) {
    }

    public final JarFile jarFileNewInstance(String s) throws IOException {
        return jarFileNewInstance(new File(s));
    }

    public JarFile jarFileNewInstance(File f) throws IOException {
        return new JarFile(f);
    }

    public boolean jarFileIsMultiRelease(JarFile jarFile) {
        return false;
    }

    public int jarFileRuntimeMajorVersion() {
        return 8;
    }

    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        return true;
    }

    public boolean isExported(Class<?> type) {
        return true;
    }

    public String getModuleName(Class<?> type) {
        return "NO_MODULE_JAVA_8";
    }

    public SocketAddress getUnixDomainSocketAddress(String path) {
        return null;
    }

    public ServerSocketChannel openUnixDomainServerSocketChannel() {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noUnixDomainSocket"));
    }

    public SocketChannel openUnixDomainSocketChannel() {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noUnixDomainSocket"));
    }

    public Object getExecutor(Thread thread) throws IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException {
        Object result = null;
        Object target = null;
        for (String fieldName : new String[]{DataBinder.DEFAULT_OBJECT_NAME, "runnable", "action"}) {
            try {
                Field targetField = thread.getClass().getDeclaredField(fieldName);
                targetField.setAccessible(true);
                target = targetField.get(thread);
                break;
            } catch (NoSuchFieldException e) {
            }
        }
        if (target != null && target.getClass().getCanonicalName() != null && (target.getClass().getCanonicalName().equals("org.apache.tomcat.util.threads.ThreadPoolExecutor.Worker") || target.getClass().getCanonicalName().equals("java.util.concurrent.ThreadPoolExecutor.Worker"))) {
            Field executorField = target.getClass().getDeclaredField("this$0");
            executorField.setAccessible(true);
            result = executorField.get(target);
        }
        return result;
    }
}
