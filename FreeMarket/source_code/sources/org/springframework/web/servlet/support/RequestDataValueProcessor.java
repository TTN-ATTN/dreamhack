package org.springframework.web.servlet.support;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/support/RequestDataValueProcessor.class */
public interface RequestDataValueProcessor {
    String processAction(HttpServletRequest request, String action, String httpMethod);

    String processFormFieldValue(HttpServletRequest request, @Nullable String name, String value, String type);

    @Nullable
    Map<String, String> getExtraHiddenFields(HttpServletRequest request);

    String processUrl(HttpServletRequest request, String url);
}
