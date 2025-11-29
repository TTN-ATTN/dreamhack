package org.springframework.web.client;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/ResourceAccessException.class */
public class ResourceAccessException extends RestClientException {
    private static final long serialVersionUID = -8513182514355844870L;

    public ResourceAccessException(String msg) {
        super(msg);
    }

    public ResourceAccessException(String msg, IOException ex) {
        super(msg, ex);
    }
}
