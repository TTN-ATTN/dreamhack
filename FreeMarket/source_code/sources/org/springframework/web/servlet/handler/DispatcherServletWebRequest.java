package org.springframework.web.servlet.handler;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/handler/DispatcherServletWebRequest.class */
public class DispatcherServletWebRequest extends ServletWebRequest {
    public DispatcherServletWebRequest(HttpServletRequest request) {
        super(request);
    }

    public DispatcherServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override // org.springframework.web.context.request.ServletWebRequest, org.springframework.web.context.request.WebRequest
    public Locale getLocale() {
        return RequestContextUtils.getLocale(getRequest());
    }
}
