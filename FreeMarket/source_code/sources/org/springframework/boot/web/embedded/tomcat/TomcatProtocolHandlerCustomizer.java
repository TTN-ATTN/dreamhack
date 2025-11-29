package org.springframework.boot.web.embedded.tomcat;

import org.apache.coyote.ProtocolHandler;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/tomcat/TomcatProtocolHandlerCustomizer.class */
public interface TomcatProtocolHandlerCustomizer<T extends ProtocolHandler> {
    void customize(T protocolHandler);
}
