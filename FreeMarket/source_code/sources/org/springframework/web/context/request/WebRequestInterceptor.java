package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/request/WebRequestInterceptor.class */
public interface WebRequestInterceptor {
    void preHandle(WebRequest request) throws Exception;

    void postHandle(WebRequest request, @Nullable ModelMap model) throws Exception;

    void afterCompletion(WebRequest request, @Nullable Exception ex) throws Exception;
}
