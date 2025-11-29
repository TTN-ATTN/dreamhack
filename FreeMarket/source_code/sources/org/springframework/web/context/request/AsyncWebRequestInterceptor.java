package org.springframework.web.context.request;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/request/AsyncWebRequestInterceptor.class */
public interface AsyncWebRequestInterceptor extends WebRequestInterceptor {
    void afterConcurrentHandlingStarted(WebRequest request);
}
