package org.apache.tomcat;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/SimpleInstanceManager.class */
public class SimpleInstanceManager implements InstanceManager {
    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(Class<?> clazz) throws IllegalAccessException, NoSuchMethodException, InstantiationException, NamingException, InvocationTargetException {
        return prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(String className) throws IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NamingException, InvocationTargetException {
        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        return prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(String fqcn, ClassLoader classLoader) throws IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NamingException, InvocationTargetException {
        Class<?> clazz = classLoader.loadClass(fqcn);
        return prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override // org.apache.tomcat.InstanceManager
    public void newInstance(Object o) throws IllegalAccessException, NamingException, InvocationTargetException {
    }

    @Override // org.apache.tomcat.InstanceManager
    public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
    }

    private Object prepareInstance(Object o) {
        return o;
    }
}
