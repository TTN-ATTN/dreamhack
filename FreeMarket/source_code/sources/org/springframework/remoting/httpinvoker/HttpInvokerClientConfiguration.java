package org.springframework.remoting.httpinvoker;

import org.springframework.lang.Nullable;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/remoting/httpinvoker/HttpInvokerClientConfiguration.class */
public interface HttpInvokerClientConfiguration {
    String getServiceUrl();

    @Nullable
    String getCodebaseUrl();
}
