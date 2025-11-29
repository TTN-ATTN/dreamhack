package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/request/NativeWebRequest.class */
public interface NativeWebRequest extends WebRequest {
    Object getNativeRequest();

    @Nullable
    Object getNativeResponse();

    @Nullable
    <T> T getNativeRequest(@Nullable Class<T> requiredType);

    @Nullable
    <T> T getNativeResponse(@Nullable Class<T> requiredType);
}
