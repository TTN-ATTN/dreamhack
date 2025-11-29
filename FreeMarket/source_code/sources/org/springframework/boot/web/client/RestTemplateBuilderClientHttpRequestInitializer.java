package org.springframework.boot.web.client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/client/RestTemplateBuilderClientHttpRequestInitializer.class */
class RestTemplateBuilderClientHttpRequestInitializer implements ClientHttpRequestInitializer {
    private final BasicAuthentication basicAuthentication;
    private final Map<String, List<String>> defaultHeaders;
    private final Set<RestTemplateRequestCustomizer<?>> requestCustomizers;

    RestTemplateBuilderClientHttpRequestInitializer(BasicAuthentication basicAuthentication, Map<String, List<String>> defaultHeaders, Set<RestTemplateRequestCustomizer<?>> requestCustomizers) {
        this.basicAuthentication = basicAuthentication;
        this.defaultHeaders = defaultHeaders;
        this.requestCustomizers = requestCustomizers;
    }

    @Override // org.springframework.http.client.ClientHttpRequestInitializer
    public void initialize(ClientHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        if (this.basicAuthentication != null) {
            this.basicAuthentication.applyTo(headers);
        }
        Map<String, List<String>> map = this.defaultHeaders;
        headers.getClass();
        map.forEach((v1, v2) -> {
            r1.putIfAbsent(v1, v2);
        });
        LambdaSafe.callbacks(RestTemplateRequestCustomizer.class, this.requestCustomizers, request, new Object[0]).invoke(customizer -> {
            customizer.customize(request);
        });
    }
}
