package org.apache.catalina.mbeans;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/mbeans/ClassNameMBean.class */
public class ClassNameMBean<T> extends BaseCatalinaMBean<T> {
    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public String getClassName() {
        return this.resource.getClass().getName();
    }
}
