package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jmx/access/InvocationFailureException.class */
public class InvocationFailureException extends JmxException {
    public InvocationFailureException(String msg) {
        super(msg);
    }

    public InvocationFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
