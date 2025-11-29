package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/multipart/MultipartResolver.class */
public interface MultipartResolver {
    boolean isMultipart(HttpServletRequest request);

    MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException;

    void cleanupMultipart(MultipartHttpServletRequest request);
}
