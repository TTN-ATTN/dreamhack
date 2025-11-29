package org.springframework.boot.webservices.client;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.function.Supplier;
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/webservices/client/HttpWebServiceMessageSenderBuilder.class */
public class HttpWebServiceMessageSenderBuilder {
    private Duration connectTimeout;
    private Duration readTimeout;
    private Supplier<ClientHttpRequestFactory> requestFactorySupplier;

    public HttpWebServiceMessageSenderBuilder setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpWebServiceMessageSenderBuilder setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public HttpWebServiceMessageSenderBuilder requestFactory(Supplier<ClientHttpRequestFactory> requestFactorySupplier) {
        Assert.notNull(requestFactorySupplier, "RequestFactory Supplier must not be null");
        this.requestFactorySupplier = requestFactorySupplier;
        return this;
    }

    public WebServiceMessageSender build() {
        ClientHttpRequestFactory requestFactory = this.requestFactorySupplier != null ? this.requestFactorySupplier.get() : new ClientHttpRequestFactorySupplier().get();
        if (this.connectTimeout != null) {
            new TimeoutRequestFactoryCustomizer(this.connectTimeout, "setConnectTimeout").customize(requestFactory);
        }
        if (this.readTimeout != null) {
            new TimeoutRequestFactoryCustomizer(this.readTimeout, "setReadTimeout").customize(requestFactory);
        }
        return new ClientHttpRequestMessageSender(requestFactory);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/webservices/client/HttpWebServiceMessageSenderBuilder$TimeoutRequestFactoryCustomizer.class */
    private static class TimeoutRequestFactoryCustomizer {
        private final Duration timeout;
        private final String methodName;

        TimeoutRequestFactoryCustomizer(Duration timeout, String methodName) {
            this.timeout = timeout;
            this.methodName = methodName;
        }

        void customize(ClientHttpRequestFactory factory) {
            ReflectionUtils.invokeMethod(findMethod(factory), factory, Integer.valueOf(Math.toIntExact(this.timeout.toMillis())));
        }

        private Method findMethod(ClientHttpRequestFactory factory) {
            Method method = ReflectionUtils.findMethod(factory.getClass(), this.methodName, Integer.TYPE);
            if (method != null) {
                return method;
            }
            throw new IllegalStateException("Request factory " + factory.getClass() + " does not have a " + this.methodName + "(int) method");
        }
    }
}
