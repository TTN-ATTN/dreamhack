package org.apache.tomcat.util.descriptor.web;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/NamingResources.class */
public interface NamingResources {
    void addEnvironment(ContextEnvironment contextEnvironment);

    void removeEnvironment(String str);

    void addResource(ContextResource contextResource);

    void removeResource(String str);

    void addResourceLink(ContextResourceLink contextResourceLink);

    void removeResourceLink(String str);

    Object getContainer();
}
