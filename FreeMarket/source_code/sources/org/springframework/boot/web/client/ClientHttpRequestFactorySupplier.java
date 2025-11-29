package org.springframework.boot.web.client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/client/ClientHttpRequestFactorySupplier.class */
public class ClientHttpRequestFactorySupplier implements Supplier<ClientHttpRequestFactory> {
    private static final Map<String, String> REQUEST_FACTORY_CANDIDATES;

    static {
        Map<String, String> candidates = new LinkedHashMap<>();
        candidates.put("org.apache.http.client.HttpClient", "org.springframework.http.client.HttpComponentsClientHttpRequestFactory");
        candidates.put("okhttp3.OkHttpClient", "org.springframework.http.client.OkHttp3ClientHttpRequestFactory");
        REQUEST_FACTORY_CANDIDATES = Collections.unmodifiableMap(candidates);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.function.Supplier
    public ClientHttpRequestFactory get() throws IllegalArgumentException {
        for (Map.Entry<String, String> candidate : REQUEST_FACTORY_CANDIDATES.entrySet()) {
            ClassLoader classLoader = getClass().getClassLoader();
            if (ClassUtils.isPresent(candidate.getKey(), classLoader)) {
                Class<?> factoryClass = ClassUtils.resolveClassName(candidate.getValue(), classLoader);
                return (ClientHttpRequestFactory) BeanUtils.instantiateClass(factoryClass);
            }
        }
        return new SimpleClientHttpRequestFactory();
    }
}
