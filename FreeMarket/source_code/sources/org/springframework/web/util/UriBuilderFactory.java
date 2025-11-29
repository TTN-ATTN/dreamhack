package org.springframework.web.util;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/util/UriBuilderFactory.class */
public interface UriBuilderFactory extends UriTemplateHandler {
    UriBuilder uriString(String uriTemplate);

    UriBuilder builder();
}
