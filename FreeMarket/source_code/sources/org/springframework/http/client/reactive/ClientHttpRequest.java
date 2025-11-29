package org.springframework.http.client.reactive;

import java.net.URI;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.util.MultiValueMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/reactive/ClientHttpRequest.class */
public interface ClientHttpRequest extends ReactiveHttpOutputMessage {
    HttpMethod getMethod();

    URI getURI();

    MultiValueMap<String, HttpCookie> getCookies();

    <T> T getNativeRequest();
}
