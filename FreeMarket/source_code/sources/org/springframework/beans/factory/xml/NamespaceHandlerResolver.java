package org.springframework.beans.factory.xml;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/xml/NamespaceHandlerResolver.class */
public interface NamespaceHandlerResolver {
    @Nullable
    NamespaceHandler resolve(String str);
}
