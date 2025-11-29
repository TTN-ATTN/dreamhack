package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/multipart/MultipartHttpServletRequest.class */
public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {
    @Nullable
    HttpMethod getRequestMethod();

    HttpHeaders getRequestHeaders();

    @Nullable
    HttpHeaders getMultipartHeaders(String paramOrFileName);
}
