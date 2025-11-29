package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/LastModified.class */
public interface LastModified {
    long getLastModified(HttpServletRequest request);
}
