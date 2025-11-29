package org.springframework.http.server.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ServerHttpResponse.class */
public interface ServerHttpResponse extends ReactiveHttpOutputMessage {
    boolean setStatusCode(@Nullable HttpStatus status);

    @Nullable
    HttpStatus getStatusCode();

    MultiValueMap<String, ResponseCookie> getCookies();

    void addCookie(ResponseCookie cookie);

    default boolean setRawStatusCode(@Nullable Integer value) {
        if (value == null) {
            return setStatusCode(null);
        }
        HttpStatus httpStatus = HttpStatus.resolve(value.intValue());
        if (httpStatus == null) {
            throw new IllegalStateException("Unresolvable HttpStatus for general ServerHttpResponse: " + value);
        }
        return setStatusCode(httpStatus);
    }

    @Nullable
    default Integer getRawStatusCode() {
        HttpStatus httpStatus = getStatusCode();
        if (httpStatus != null) {
            return Integer.valueOf(httpStatus.value());
        }
        return null;
    }
}
