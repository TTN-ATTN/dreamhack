package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/support/SessionAttributeStore.class */
public interface SessionAttributeStore {
    void storeAttribute(WebRequest request, String attributeName, Object attributeValue);

    @Nullable
    Object retrieveAttribute(WebRequest request, String attributeName);

    void cleanupAttribute(WebRequest request, String attributeName);
}
