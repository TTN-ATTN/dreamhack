package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/method/support/AsyncHandlerMethodReturnValueHandler.class */
public interface AsyncHandlerMethodReturnValueHandler extends HandlerMethodReturnValueHandler {
    boolean isAsyncReturnValue(@Nullable Object returnValue, MethodParameter returnType);
}
