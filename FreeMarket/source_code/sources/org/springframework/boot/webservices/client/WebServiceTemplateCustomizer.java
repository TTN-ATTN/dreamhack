package org.springframework.boot.webservices.client;

import org.springframework.ws.client.core.WebServiceTemplate;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/webservices/client/WebServiceTemplateCustomizer.class */
public interface WebServiceTemplateCustomizer {
    void customize(WebServiceTemplate webServiceTemplate);
}
