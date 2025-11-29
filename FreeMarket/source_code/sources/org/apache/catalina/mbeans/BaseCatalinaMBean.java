package org.apache.catalina.mbeans;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import org.apache.tomcat.util.modeler.BaseModelMBean;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/mbeans/BaseCatalinaMBean.class */
public abstract class BaseCatalinaMBean<T> extends BaseModelMBean {
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.management.MBeanException */
    protected T doGetManagedResource() throws MBeanException {
        try {
            return (T) getManagedResource();
        } catch (InstanceNotFoundException | RuntimeOperationsException | InvalidTargetObjectTypeException e) {
            throw new MBeanException(e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.management.MBeanException */
    protected static Object newInstance(String type) throws MBeanException {
        try {
            return Class.forName(type).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw new MBeanException(e);
        }
    }
}
