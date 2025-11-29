package org.springframework.boot.web.servlet.error;

import java.util.Collections;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.web.context.request.WebRequest;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/error/ErrorAttributes.class */
public interface ErrorAttributes {
    public static final String ERROR_ATTRIBUTE = ErrorAttributes.class.getName() + ".error";

    Throwable getError(WebRequest webRequest);

    default Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        return Collections.emptyMap();
    }
}
