package org.springframework.web.bind.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/support/WebArgumentResolver.class */
public interface WebArgumentResolver {
    public static final Object UNRESOLVED = new Object();

    @Nullable
    Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception;
}
