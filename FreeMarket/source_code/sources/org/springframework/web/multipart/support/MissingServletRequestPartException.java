package org.springframework.web.multipart.support;

import javax.servlet.ServletException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/multipart/support/MissingServletRequestPartException.class */
public class MissingServletRequestPartException extends ServletException {
    private final String requestPartName;

    public MissingServletRequestPartException(String requestPartName) {
        super("Required request part '" + requestPartName + "' is not present");
        this.requestPartName = requestPartName;
    }

    public String getRequestPartName() {
        return this.requestPartName;
    }
}
