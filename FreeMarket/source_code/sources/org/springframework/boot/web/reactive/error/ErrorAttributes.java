package org.springframework.boot.web.reactive.error;

import java.util.Collections;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/error/ErrorAttributes.class */
public interface ErrorAttributes {
    public static final String ERROR_ATTRIBUTE = ErrorAttributes.class.getName() + ".error";

    Throwable getError(ServerRequest request);

    void storeErrorInformation(Throwable error, ServerWebExchange exchange);

    default Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        return Collections.emptyMap();
    }
}
