package org.apache.naming.factory;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.LookupRef;
import org.apache.naming.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/naming/factory/LookupFactory.class */
public class LookupFactory implements ObjectFactory {
    private static final Log log = LogFactory.getLog((Class<?>) LookupFactory.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) LookupFactory.class);
    private static final ThreadLocal<Set<String>> names = ThreadLocal.withInitial(HashSet::new);

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Class<?> factoryClass;
        String lookupName = null;
        Object result = null;
        if (obj instanceof LookupRef) {
            Reference ref = (Reference) obj;
            ObjectFactory factory = null;
            RefAddr lookupNameRefAddr = ref.get(LookupRef.LOOKUP_NAME);
            if (lookupNameRefAddr != null) {
                lookupName = lookupNameRefAddr.getContent().toString();
            }
            if (lookupName != null) {
                try {
                    if (!names.get().add(lookupName)) {
                        String msg = sm.getString("lookupFactory.circularReference", lookupName);
                        Throwable namingException = new NamingException(msg);
                        log.warn(msg, namingException);
                        throw namingException;
                    }
                } catch (Throwable th) {
                    names.get().remove(lookupName);
                    throw th;
                }
            }
            RefAddr factoryRefAddr = ref.get(Constants.FACTORY);
            if (factoryRefAddr != null) {
                String factoryClassName = factoryRefAddr.getContent().toString();
                ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                if (tcl != null) {
                    try {
                        factoryClass = tcl.loadClass(factoryClassName);
                    } catch (ClassNotFoundException e) {
                        NamingException ex = new NamingException(sm.getString("lookupFactory.loadFailed"));
                        ex.initCause(e);
                        throw ex;
                    }
                } else {
                    try {
                        factoryClass = Class.forName(factoryClassName);
                    } catch (ClassNotFoundException e2) {
                        NamingException ex2 = new NamingException(sm.getString("lookupFactory.loadFailed"));
                        ex2.initCause(e2);
                        throw ex2;
                    }
                }
                if (factoryClass != null) {
                    try {
                        factory = (ObjectFactory) factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (Throwable th2) {
                        if (th2 instanceof NamingException) {
                            throw th2;
                        }
                        NamingException ex3 = new NamingException(sm.getString("lookupFactory.createFailed"));
                        ex3.initCause(th2);
                        throw ex3;
                    }
                }
            }
            if (factory != null) {
                result = factory.getObjectInstance(obj, name, nameCtx, environment);
            } else {
                if (lookupName == null) {
                    throw new NamingException(sm.getString("lookupFactory.createFailed"));
                }
                result = new InitialContext().lookup(lookupName);
            }
            Class<?> clazz = Class.forName(ref.getClassName());
            if (result != null && !clazz.isAssignableFrom(result.getClass())) {
                String msg2 = sm.getString("lookupFactory.typeMismatch", name, ref.getClassName(), lookupName, result.getClass().getName());
                Throwable namingException2 = new NamingException(msg2);
                log.warn(msg2, namingException2);
                if (result instanceof AutoCloseable) {
                    try {
                        ((AutoCloseable) result).close();
                    } catch (Exception e3) {
                    }
                }
                throw namingException2;
            }
            names.get().remove(lookupName);
        }
        return result;
    }
}
