package org.springframework.web.bind.support;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/support/WebBindingInitializer.class */
public interface WebBindingInitializer {
    void initBinder(WebDataBinder binder);

    @Deprecated
    default void initBinder(WebDataBinder binder, WebRequest request) {
        initBinder(binder);
    }
}
