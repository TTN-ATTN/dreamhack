package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/condition/MediaTypeExpression.class */
public interface MediaTypeExpression {
    MediaType getMediaType();

    boolean isNegated();
}
