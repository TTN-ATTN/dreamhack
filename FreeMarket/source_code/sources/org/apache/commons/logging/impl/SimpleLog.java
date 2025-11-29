package org.apache.commons.logging.impl;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-jcl-5.3.27.jar:org/apache/commons/logging/impl/SimpleLog.class */
public class SimpleLog extends NoOpLog {
    public SimpleLog(String name) {
        super(name);
        System.out.println(SimpleLog.class.getName() + " is deprecated and equivalent to NoOpLog in spring-jcl. Use a standard LogFactory.getLog(Class/String) call instead.");
    }
}
