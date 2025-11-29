package org.apache.catalina.mbeans;

import org.apache.tomcat.util.modeler.ManagedBean;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/mbeans/MemoryUserDatabaseMBean.class */
public class MemoryUserDatabaseMBean extends SparseUserDatabaseMBean {
    protected final ManagedBean managed = this.registry.findManagedBean("MemoryUserDatabase");
}
