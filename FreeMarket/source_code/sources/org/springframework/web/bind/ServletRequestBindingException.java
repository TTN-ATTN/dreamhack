package org.springframework.web.bind;

import org.springframework.web.util.NestedServletException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/ServletRequestBindingException.class */
public class ServletRequestBindingException extends NestedServletException {
    public ServletRequestBindingException(String msg) {
        super(msg);
    }

    public ServletRequestBindingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
