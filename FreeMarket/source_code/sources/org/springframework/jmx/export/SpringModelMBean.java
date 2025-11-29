package org.springframework.jmx.export;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.RequiredModelMBean;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jmx/export/SpringModelMBean.class */
public class SpringModelMBean extends RequiredModelMBean {
    private ClassLoader managedResourceClassLoader;

    public SpringModelMBean() throws MBeanException, RuntimeOperationsException {
        this.managedResourceClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public SpringModelMBean(ModelMBeanInfo mbi) throws MBeanException, RuntimeOperationsException {
        super(mbi);
        this.managedResourceClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void setManagedResource(Object managedResource, String managedResourceType) throws InvalidTargetObjectTypeException, MBeanException, InstanceNotFoundException {
        this.managedResourceClassLoader = managedResource.getClass().getClassLoader();
        super.setManagedResource(managedResource, managedResourceType);
    }

    public Object invoke(String opName, Object[] opArgs, String[] sig) throws MBeanException, ReflectionException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            Object objInvoke = super.invoke(opName, opArgs, sig);
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            return objInvoke;
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            throw th;
        }
    }

    public Object getAttribute(String attrName) throws MBeanException, AttributeNotFoundException, ReflectionException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            Object attribute = super.getAttribute(attrName);
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            return attribute;
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            throw th;
        }
    }

    public AttributeList getAttributes(String[] attrNames) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            AttributeList attributes = super.getAttributes(attrNames);
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            return attributes;
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            throw th;
        }
    }

    public void setAttribute(Attribute attribute) throws InvalidAttributeValueException, MBeanException, AttributeNotFoundException, ReflectionException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            super.setAttribute(attribute);
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            throw th;
        }
    }

    public AttributeList setAttributes(AttributeList attributes) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            AttributeList attributes2 = super.setAttributes(attributes);
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            return attributes2;
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            throw th;
        }
    }
}
