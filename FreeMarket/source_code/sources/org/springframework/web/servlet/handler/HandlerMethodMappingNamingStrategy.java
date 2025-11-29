package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/handler/HandlerMethodMappingNamingStrategy.class */
public interface HandlerMethodMappingNamingStrategy<T> {
    String getName(HandlerMethod handlerMethod, T mapping);
}
