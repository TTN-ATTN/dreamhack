package org.springframework.jmx.export;

import javax.management.ObjectName;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jmx/export/MBeanExportOperations.class */
public interface MBeanExportOperations {
    ObjectName registerManagedResource(Object managedResource) throws MBeanExportException;

    void registerManagedResource(Object managedResource, ObjectName objectName) throws MBeanExportException;

    void unregisterManagedResource(ObjectName objectName);
}
