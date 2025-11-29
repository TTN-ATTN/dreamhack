package org.springframework.jmx.export;

import javax.management.ObjectName;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jmx/export/MBeanExporterListener.class */
public interface MBeanExporterListener {
    void mbeanRegistered(ObjectName objectName);

    void mbeanUnregistered(ObjectName objectName);
}
