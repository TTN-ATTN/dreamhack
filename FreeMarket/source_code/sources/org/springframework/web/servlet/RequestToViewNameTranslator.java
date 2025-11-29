package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/RequestToViewNameTranslator.class */
public interface RequestToViewNameTranslator {
    @Nullable
    String getViewName(HttpServletRequest request) throws Exception;
}
