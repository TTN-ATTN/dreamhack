package org.apache.tomcat.util.descriptor.web;

import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/Injectable.class */
public interface Injectable {
    String getName();

    void addInjectionTarget(String str, String str2);

    List<InjectionTarget> getInjectionTargets();
}
