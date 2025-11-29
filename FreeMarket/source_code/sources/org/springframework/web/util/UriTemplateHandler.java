package org.springframework.web.util;

import java.net.URI;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/util/UriTemplateHandler.class */
public interface UriTemplateHandler {
    URI expand(String uriTemplate, Map<String, ?> uriVariables);

    URI expand(String uriTemplate, Object... uriVariables);
}
