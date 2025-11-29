package org.springframework.web;

import javax.servlet.ServletException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/HttpSessionRequiredException.class */
public class HttpSessionRequiredException extends ServletException {

    @Nullable
    private final String expectedAttribute;

    public HttpSessionRequiredException(String msg) {
        super(msg);
        this.expectedAttribute = null;
    }

    public HttpSessionRequiredException(String msg, String expectedAttribute) {
        super(msg);
        this.expectedAttribute = expectedAttribute;
    }

    @Nullable
    public String getExpectedAttribute() {
        return this.expectedAttribute;
    }
}
