package org.springframework.http.converter;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/converter/HttpMessageConversionException.class */
public class HttpMessageConversionException extends NestedRuntimeException {
    public HttpMessageConversionException(String msg) {
        super(msg);
    }

    public HttpMessageConversionException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
