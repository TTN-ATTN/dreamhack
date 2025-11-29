package org.springframework.http.client.reactive;

import java.lang.reflect.Method;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/reactive/Jetty10HttpFieldsHelper.class */
abstract class Jetty10HttpFieldsHelper {
    private static final boolean jetty10Present;
    private static final Method requestGetHeadersMethod;
    private static final Method responseGetHeadersMethod;
    private static final Method getNameMethod;
    private static final Method getValueMethod;

    Jetty10HttpFieldsHelper() {
    }

    static {
        try {
            ClassLoader classLoader = JettyClientHttpResponse.class.getClassLoader();
            Class<?> httpFieldsClass = classLoader.loadClass("org.eclipse.jetty.http.HttpFields");
            jetty10Present = httpFieldsClass.isInterface();
            requestGetHeadersMethod = Request.class.getMethod("getHeaders", new Class[0]);
            responseGetHeadersMethod = Response.class.getMethod("getHeaders", new Class[0]);
            Class<?> httpFieldClass = classLoader.loadClass("org.eclipse.jetty.http.HttpField");
            getNameMethod = httpFieldClass.getMethod("getName", new Class[0]);
            getValueMethod = httpFieldClass.getMethod("getValue", new Class[0]);
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new IllegalStateException("No compatible Jetty version found", ex);
        }
    }

    public static boolean jetty10Present() {
        return jetty10Present;
    }

    public static HttpHeaders getHttpHeaders(Request request) {
        Iterable<?> iterator = (Iterable) ReflectionUtils.invokeMethod(requestGetHeadersMethod, request);
        return getHttpHeadersInternal(iterator);
    }

    public static HttpHeaders getHttpHeaders(Response response) {
        Iterable<?> iterator = (Iterable) ReflectionUtils.invokeMethod(responseGetHeadersMethod, response);
        return getHttpHeadersInternal(iterator);
    }

    private static HttpHeaders getHttpHeadersInternal(@Nullable Iterable<?> iterator) {
        Assert.notNull(iterator, "Iterator must not be null");
        HttpHeaders headers = new HttpHeaders();
        for (Object field : iterator) {
            String name = (String) ReflectionUtils.invokeMethod(getNameMethod, field);
            Assert.notNull(name, "Header name must not be null");
            String value = (String) ReflectionUtils.invokeMethod(getValueMethod, field);
            headers.add(name, value);
        }
        return headers;
    }
}
