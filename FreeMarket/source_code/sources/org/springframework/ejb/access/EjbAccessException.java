package org.springframework.ejb.access;

import org.springframework.core.NestedRuntimeException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/ejb/access/EjbAccessException.class */
public class EjbAccessException extends NestedRuntimeException {
    public EjbAccessException(String msg) {
        super(msg);
    }

    public EjbAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
