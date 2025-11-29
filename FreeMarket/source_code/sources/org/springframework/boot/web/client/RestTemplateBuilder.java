package org.springframework.boot.web.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/client/RestTemplateBuilder.class */
public class RestTemplateBuilder {
    private final RequestFactoryCustomizer requestFactoryCustomizer;
    private final boolean detectRequestFactory;
    private final String rootUri;
    private final Set<HttpMessageConverter<?>> messageConverters;
    private final Set<ClientHttpRequestInterceptor> interceptors;
    private final Supplier<ClientHttpRequestFactory> requestFactory;
    private final UriTemplateHandler uriTemplateHandler;
    private final ResponseErrorHandler errorHandler;
    private final BasicAuthentication basicAuthentication;
    private final Map<String, List<String>> defaultHeaders;
    private final Set<RestTemplateCustomizer> customizers;
    private final Set<RestTemplateRequestCustomizer<?>> requestCustomizers;

    public RestTemplateBuilder(RestTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.requestFactoryCustomizer = new RequestFactoryCustomizer();
        this.detectRequestFactory = true;
        this.rootUri = null;
        this.messageConverters = null;
        this.interceptors = Collections.emptySet();
        this.requestFactory = null;
        this.uriTemplateHandler = null;
        this.errorHandler = null;
        this.basicAuthentication = null;
        this.defaultHeaders = Collections.emptyMap();
        this.customizers = copiedSetOf(customizers);
        this.requestCustomizers = Collections.emptySet();
    }

    private RestTemplateBuilder(RequestFactoryCustomizer requestFactoryCustomizer, boolean detectRequestFactory, String rootUri, Set<HttpMessageConverter<?>> messageConverters, Set<ClientHttpRequestInterceptor> interceptors, Supplier<ClientHttpRequestFactory> requestFactorySupplier, UriTemplateHandler uriTemplateHandler, ResponseErrorHandler errorHandler, BasicAuthentication basicAuthentication, Map<String, List<String>> defaultHeaders, Set<RestTemplateCustomizer> customizers, Set<RestTemplateRequestCustomizer<?>> requestCustomizers) {
        this.requestFactoryCustomizer = requestFactoryCustomizer;
        this.detectRequestFactory = detectRequestFactory;
        this.rootUri = rootUri;
        this.messageConverters = messageConverters;
        this.interceptors = interceptors;
        this.requestFactory = requestFactorySupplier;
        this.uriTemplateHandler = uriTemplateHandler;
        this.errorHandler = errorHandler;
        this.basicAuthentication = basicAuthentication;
        this.defaultHeaders = defaultHeaders;
        this.customizers = customizers;
        this.requestCustomizers = requestCustomizers;
    }

    public RestTemplateBuilder detectRequestFactory(boolean detectRequestFactory) {
        return new RestTemplateBuilder(this.requestFactoryCustomizer, detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder rootUri(String rootUri) {
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder messageConverters(HttpMessageConverter<?>... messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return messageConverters(Arrays.asList(messageConverters));
    }

    public RestTemplateBuilder messageConverters(Collection<? extends HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, copiedSetOf(messageConverters), this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder additionalMessageConverters(HttpMessageConverter<?>... messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return additionalMessageConverters(Arrays.asList(messageConverters));
    }

    public RestTemplateBuilder additionalMessageConverters(Collection<? extends HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, append(this.messageConverters, messageConverters), this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder defaultMessageConverters() {
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, copiedSetOf(new RestTemplate().getMessageConverters()), this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder interceptors(ClientHttpRequestInterceptor... interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return interceptors(Arrays.asList(interceptors));
    }

    public RestTemplateBuilder interceptors(Collection<ClientHttpRequestInterceptor> interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, copiedSetOf(interceptors), this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder additionalInterceptors(ClientHttpRequestInterceptor... interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return additionalInterceptors(Arrays.asList(interceptors));
    }

    public RestTemplateBuilder additionalInterceptors(Collection<? extends ClientHttpRequestInterceptor> interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, append(this.interceptors, interceptors), this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder requestFactory(Class<? extends ClientHttpRequestFactory> requestFactory) {
        Assert.notNull(requestFactory, "RequestFactory must not be null");
        return requestFactory(() -> {
            return createRequestFactory(requestFactory);
        });
    }

    private ClientHttpRequestFactory createRequestFactory(Class<? extends ClientHttpRequestFactory> requestFactory) throws NoSuchMethodException, SecurityException {
        try {
            Constructor<?> constructor = requestFactory.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public RestTemplateBuilder requestFactory(Supplier<ClientHttpRequestFactory> requestFactory) {
        Assert.notNull(requestFactory, "RequestFactory Supplier must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder uriTemplateHandler(UriTemplateHandler uriTemplateHandler) {
        Assert.notNull(uriTemplateHandler, "UriTemplateHandler must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder errorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder basicAuthentication(String username, String password) {
        return basicAuthentication(username, password, null);
    }

    public RestTemplateBuilder basicAuthentication(String username, String password, Charset charset) {
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, new BasicAuthentication(username, password, charset), this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder defaultHeader(String name, String... values) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(values, "Values must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, append(this.defaultHeaders, name, values), this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder setConnectTimeout(Duration connectTimeout) {
        return new RestTemplateBuilder(this.requestFactoryCustomizer.connectTimeout(connectTimeout), this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder setReadTimeout(Duration readTimeout) {
        return new RestTemplateBuilder(this.requestFactoryCustomizer.readTimeout(readTimeout), this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder setBufferRequestBody(boolean bufferRequestBody) {
        return new RestTemplateBuilder(this.requestFactoryCustomizer.bufferRequestBody(bufferRequestBody), this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder customizers(RestTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public RestTemplateBuilder customizers(Collection<? extends RestTemplateCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, copiedSetOf(customizers), this.requestCustomizers);
    }

    public RestTemplateBuilder additionalCustomizers(RestTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public RestTemplateBuilder additionalCustomizers(Collection<? extends RestTemplateCustomizer> customizers) {
        Assert.notNull(customizers, "RestTemplateCustomizers must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, append(this.customizers, customizers), this.requestCustomizers);
    }

    public RestTemplateBuilder requestCustomizers(RestTemplateRequestCustomizer<?>... requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return requestCustomizers(Arrays.asList(requestCustomizers));
    }

    public RestTemplateBuilder requestCustomizers(Collection<? extends RestTemplateRequestCustomizer<?>> requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, copiedSetOf(requestCustomizers));
    }

    public RestTemplateBuilder additionalRequestCustomizers(RestTemplateRequestCustomizer<?>... requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return additionalRequestCustomizers(Arrays.asList(requestCustomizers));
    }

    public RestTemplateBuilder additionalRequestCustomizers(Collection<? extends RestTemplateRequestCustomizer<?>> requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return new RestTemplateBuilder(this.requestFactoryCustomizer, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, append(this.requestCustomizers, requestCustomizers));
    }

    public RestTemplate build() {
        return configure(new RestTemplate());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends RestTemplate> T build(Class<T> cls) {
        return (T) configure((RestTemplate) BeanUtils.instantiateClass(cls));
    }

    public <T extends RestTemplate> T configure(T restTemplate) throws IllegalArgumentException {
        ClientHttpRequestFactory requestFactory = buildRequestFactory();
        if (requestFactory != null) {
            restTemplate.setRequestFactory(requestFactory);
        }
        addClientHttpRequestInitializer(restTemplate);
        if (!CollectionUtils.isEmpty(this.messageConverters)) {
            restTemplate.setMessageConverters(new ArrayList(this.messageConverters));
        }
        if (this.uriTemplateHandler != null) {
            restTemplate.setUriTemplateHandler(this.uriTemplateHandler);
        }
        if (this.errorHandler != null) {
            restTemplate.setErrorHandler(this.errorHandler);
        }
        if (this.rootUri != null) {
            RootUriTemplateHandler.addTo(restTemplate, this.rootUri);
        }
        restTemplate.getInterceptors().addAll(this.interceptors);
        if (!CollectionUtils.isEmpty(this.customizers)) {
            for (RestTemplateCustomizer customizer : this.customizers) {
                customizer.customize(restTemplate);
            }
        }
        return restTemplate;
    }

    public ClientHttpRequestFactory buildRequestFactory() throws IllegalArgumentException {
        ClientHttpRequestFactory requestFactory = null;
        if (this.requestFactory != null) {
            requestFactory = this.requestFactory.get();
        } else if (this.detectRequestFactory) {
            requestFactory = new ClientHttpRequestFactorySupplier().get();
        }
        if (requestFactory != null && this.requestFactoryCustomizer != null) {
            this.requestFactoryCustomizer.accept(requestFactory);
        }
        return requestFactory;
    }

    private void addClientHttpRequestInitializer(RestTemplate restTemplate) {
        if (this.basicAuthentication == null && this.defaultHeaders.isEmpty() && this.requestCustomizers.isEmpty()) {
            return;
        }
        restTemplate.getClientHttpRequestInitializers().add(new RestTemplateBuilderClientHttpRequestInitializer(this.basicAuthentication, this.defaultHeaders, this.requestCustomizers));
    }

    private <T> Set<T> copiedSetOf(T... items) {
        return copiedSetOf(Arrays.asList(items));
    }

    private <T> Set<T> copiedSetOf(Collection<? extends T> collection) {
        return Collections.unmodifiableSet(new LinkedHashSet(collection));
    }

    private static <T> List<T> copiedListOf(T[] items) {
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(items, items.length)));
    }

    private static <T> Set<T> append(Collection<? extends T> collection, Collection<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(collection != null ? collection : Collections.emptySet());
        if (additions != null) {
            result.addAll(additions);
        }
        return Collections.unmodifiableSet(result);
    }

    private static <K, V> Map<K, List<V>> append(Map<K, List<V>> map, K key, V[] values) {
        Map<K, List<V>> result = new LinkedHashMap<>((Map<? extends K, ? extends List<V>>) (map != null ? map : Collections.emptyMap()));
        if (values != null) {
            result.put(key, copiedListOf(values));
        }
        return Collections.unmodifiableMap(result);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/client/RestTemplateBuilder$RequestFactoryCustomizer.class */
    private static class RequestFactoryCustomizer implements Consumer<ClientHttpRequestFactory> {
        private final Duration connectTimeout;
        private final Duration readTimeout;
        private final Boolean bufferRequestBody;

        RequestFactoryCustomizer() {
            this(null, null, null);
        }

        private RequestFactoryCustomizer(Duration connectTimeout, Duration readTimeout, Boolean bufferRequestBody) {
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.bufferRequestBody = bufferRequestBody;
        }

        RequestFactoryCustomizer connectTimeout(Duration connectTimeout) {
            return new RequestFactoryCustomizer(connectTimeout, this.readTimeout, this.bufferRequestBody);
        }

        RequestFactoryCustomizer readTimeout(Duration readTimeout) {
            return new RequestFactoryCustomizer(this.connectTimeout, readTimeout, this.bufferRequestBody);
        }

        RequestFactoryCustomizer bufferRequestBody(boolean bufferRequestBody) {
            return new RequestFactoryCustomizer(this.connectTimeout, this.readTimeout, Boolean.valueOf(bufferRequestBody));
        }

        @Override // java.util.function.Consumer
        public void accept(ClientHttpRequestFactory requestFactory) {
            ClientHttpRequestFactory unwrappedRequestFactory = unwrapRequestFactoryIfNecessary(requestFactory);
            if (this.connectTimeout != null) {
                setConnectTimeout(unwrappedRequestFactory);
            }
            if (this.readTimeout != null) {
                setReadTimeout(unwrappedRequestFactory);
            }
            if (this.bufferRequestBody != null) {
                setBufferRequestBody(unwrappedRequestFactory);
            }
        }

        private ClientHttpRequestFactory unwrapRequestFactoryIfNecessary(ClientHttpRequestFactory requestFactory) {
            if (!(requestFactory instanceof AbstractClientHttpRequestFactoryWrapper)) {
                return requestFactory;
            }
            Field field = ReflectionUtils.findField(AbstractClientHttpRequestFactoryWrapper.class, "requestFactory");
            ReflectionUtils.makeAccessible(field);
            ClientHttpRequestFactory clientHttpRequestFactory = requestFactory;
            while (true) {
                ClientHttpRequestFactory unwrappedRequestFactory = clientHttpRequestFactory;
                if (unwrappedRequestFactory instanceof AbstractClientHttpRequestFactoryWrapper) {
                    clientHttpRequestFactory = (ClientHttpRequestFactory) ReflectionUtils.getField(field, unwrappedRequestFactory);
                } else {
                    return unwrappedRequestFactory;
                }
            }
        }

        private void setConnectTimeout(ClientHttpRequestFactory factory) {
            Method method = findMethod(factory, "setConnectTimeout", Integer.TYPE);
            int timeout = Math.toIntExact(this.connectTimeout.toMillis());
            invoke(factory, method, Integer.valueOf(timeout));
        }

        private void setReadTimeout(ClientHttpRequestFactory factory) {
            Method method = findMethod(factory, "setReadTimeout", Integer.TYPE);
            int timeout = Math.toIntExact(this.readTimeout.toMillis());
            invoke(factory, method, Integer.valueOf(timeout));
        }

        private void setBufferRequestBody(ClientHttpRequestFactory factory) {
            Method method = findMethod(factory, "setBufferRequestBody", Boolean.TYPE);
            invoke(factory, method, this.bufferRequestBody);
        }

        private Method findMethod(ClientHttpRequestFactory requestFactory, String methodName, Class<?>... parameters) {
            Method method = ReflectionUtils.findMethod(requestFactory.getClass(), methodName, parameters);
            if (method != null) {
                return method;
            }
            throw new IllegalStateException("Request factory " + requestFactory.getClass() + " does not have a suitable " + methodName + " method");
        }

        private void invoke(ClientHttpRequestFactory requestFactory, Method method, Object... parameters) {
            ReflectionUtils.invokeMethod(method, requestFactory, parameters);
        }
    }
}
