package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitterReturnValueHandler.class */
public class ResponseBodyEmitterReturnValueHandler implements HandlerMethodReturnValueHandler {
    private final List<HttpMessageConverter<?>> sseMessageConverters;
    private final ReactiveTypeHandler reactiveHandler;

    public ResponseBodyEmitterReturnValueHandler(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "HttpMessageConverter List must not be empty");
        this.sseMessageConverters = initSseConverters(messageConverters);
        this.reactiveHandler = new ReactiveTypeHandler();
    }

    public ResponseBodyEmitterReturnValueHandler(List<HttpMessageConverter<?>> messageConverters, ReactiveAdapterRegistry registry, TaskExecutor executor, ContentNegotiationManager manager) {
        Assert.notEmpty(messageConverters, "HttpMessageConverter List must not be empty");
        this.sseMessageConverters = initSseConverters(messageConverters);
        this.reactiveHandler = new ReactiveTypeHandler(registry, executor, manager);
    }

    private static List<HttpMessageConverter<?>> initSseConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter.canWrite(String.class, MediaType.TEXT_PLAIN)) {
                return converters;
            }
        }
        List<HttpMessageConverter<?>> result = new ArrayList<>(converters.size() + 1);
        result.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        result.addAll(converters);
        return result;
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> parameterType;
        if (ResponseEntity.class.isAssignableFrom(returnType.getParameterType())) {
            parameterType = ResolvableType.forMethodParameter(returnType).getGeneric(new int[0]).resolve();
        } else {
            parameterType = returnType.getParameterType();
        }
        Class<?> bodyType = parameterType;
        return bodyType != null && (ResponseBodyEmitter.class.isAssignableFrom(bodyType) || this.reactiveHandler.isReactiveType(bodyType));
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        ResponseBodyEmitter emitter;
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        if (returnValue instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity) returnValue;
            response.setStatus(responseEntity.getStatusCodeValue());
            outputMessage.getHeaders().putAll(responseEntity.getHeaders());
            returnValue = responseEntity.getBody();
            returnType = returnType.nested();
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                outputMessage.flush();
                return;
            }
        }
        ServletRequest request = (ServletRequest) webRequest.getNativeRequest(ServletRequest.class);
        Assert.state(request != null, "No ServletRequest");
        if (returnValue instanceof ResponseBodyEmitter) {
            emitter = (ResponseBodyEmitter) returnValue;
        } else {
            emitter = this.reactiveHandler.handleValue(returnValue, returnType, mavContainer, webRequest);
            if (emitter == null) {
                outputMessage.getHeaders().forEach((headerName, headerValues) -> {
                    Iterator it = headerValues.iterator();
                    while (it.hasNext()) {
                        String headerValue = (String) it.next();
                        response.addHeader(headerName, headerValue);
                    }
                });
                return;
            }
        }
        emitter.extendResponse(outputMessage);
        ShallowEtagHeaderFilter.disableContentCaching(request);
        ServerHttpResponse outputMessage2 = new StreamingServletServerHttpResponse(outputMessage);
        try {
            DeferredResult<?> deferredResult = new DeferredResult<>(emitter.getTimeout());
            WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(deferredResult, mavContainer);
            HttpMessageConvertingHandler handler = new HttpMessageConvertingHandler(outputMessage2, deferredResult);
            emitter.initialize(handler);
        } catch (Throwable ex) {
            emitter.initializeWithError(ex);
            throw ex;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitterReturnValueHandler$HttpMessageConvertingHandler.class */
    private class HttpMessageConvertingHandler implements ResponseBodyEmitter.Handler {
        private final ServerHttpResponse outputMessage;
        private final DeferredResult<?> deferredResult;

        public HttpMessageConvertingHandler(ServerHttpResponse outputMessage, DeferredResult<?> deferredResult) {
            this.outputMessage = outputMessage;
            this.deferredResult = deferredResult;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.Handler
        public void send(Object data, @Nullable MediaType mediaType) throws IOException, HttpMessageNotWritableException {
            sendInternal(data, mediaType);
        }

        private <T> void sendInternal(T data, @Nullable MediaType mediaType) throws IOException, HttpMessageNotWritableException {
            for (HttpMessageConverter<?> converter : ResponseBodyEmitterReturnValueHandler.this.sseMessageConverters) {
                if (converter.canWrite(data.getClass(), mediaType)) {
                    converter.write(data, mediaType, this.outputMessage);
                    this.outputMessage.flush();
                    return;
                }
            }
            throw new IllegalArgumentException("No suitable converter for " + data.getClass());
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.Handler
        public void complete() {
            try {
                this.outputMessage.flush();
                this.deferredResult.setResult(null);
            } catch (IOException ex) {
                this.deferredResult.setErrorResult(ex);
            }
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.Handler
        public void completeWithError(Throwable failure) {
            this.deferredResult.setErrorResult(failure);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.Handler
        public void onTimeout(Runnable callback) {
            this.deferredResult.onTimeout(callback);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.Handler
        public void onError(Consumer<Throwable> callback) {
            this.deferredResult.onError(callback);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.Handler
        public void onCompletion(Runnable callback) {
            this.deferredResult.onCompletion(callback);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitterReturnValueHandler$StreamingServletServerHttpResponse.class */
    private static class StreamingServletServerHttpResponse extends DelegatingServerHttpResponse {
        private final HttpHeaders mutableHeaders;

        public StreamingServletServerHttpResponse(ServerHttpResponse delegate) {
            super(delegate);
            this.mutableHeaders = new HttpHeaders();
            this.mutableHeaders.putAll(delegate.getHeaders());
        }

        @Override // org.springframework.http.server.DelegatingServerHttpResponse, org.springframework.http.HttpMessage
        public HttpHeaders getHeaders() {
            return this.mutableHeaders;
        }
    }
}
