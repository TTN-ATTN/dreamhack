package org.springframework.web;

import java.util.List;
import org.springframework.http.MediaType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/HttpMediaTypeNotAcceptableException.class */
public class HttpMediaTypeNotAcceptableException extends HttpMediaTypeException {
    public HttpMediaTypeNotAcceptableException(String message) {
        super(message);
    }

    public HttpMediaTypeNotAcceptableException(List<MediaType> supportedMediaTypes) {
        super("Could not find acceptable representation", supportedMediaTypes);
    }
}
