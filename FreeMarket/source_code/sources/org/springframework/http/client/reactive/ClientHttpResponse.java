package org.springframework.http.client.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/reactive/ClientHttpResponse.class */
public interface ClientHttpResponse extends ReactiveHttpInputMessage {
    HttpStatus getStatusCode();

    int getRawStatusCode();

    MultiValueMap<String, ResponseCookie> getCookies();

    default String getId() {
        return ObjectUtils.getIdentityHexString(this);
    }
}
