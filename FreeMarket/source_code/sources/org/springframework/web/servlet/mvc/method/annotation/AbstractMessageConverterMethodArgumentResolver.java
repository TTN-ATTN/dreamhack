package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodArgumentResolver.class */
public abstract class AbstractMessageConverterMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Set<HttpMethod> SUPPORTED_METHODS = EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);
    private static final Object NO_VALUE = new Object();
    protected final Log logger;
    protected final List<HttpMessageConverter<?>> messageConverters;
    private final RequestResponseBodyAdviceChain advice;

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters) {
        this(converters, null);
    }

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters, @Nullable List<Object> requestResponseBodyAdvice) {
        this.logger = LogFactory.getLog(getClass());
        Assert.notEmpty(converters, "'messageConverters' must not be empty");
        this.messageConverters = converters;
        this.advice = new RequestResponseBodyAdviceChain(requestResponseBodyAdvice);
    }

    RequestResponseBodyAdviceChain getAdvice() {
        return this.advice;
    }

    @Nullable
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws HttpMediaTypeNotSupportedException, IOException, HttpMessageNotReadableException {
        HttpInputMessage inputMessage = createInputMessage(webRequest);
        return readWithMessageConverters(inputMessage, parameter, paramType);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00eb A[Catch: IOException -> 0x015e, all -> 0x016d, TryCatch #2 {IOException -> 0x015e, blocks: (B:22:0x0074, B:23:0x0089, B:25:0x0093, B:27:0x00ae, B:31:0x00be, B:38:0x00e3, B:40:0x00eb, B:42:0x00ff, B:44:0x0119, B:43:0x010e, B:45:0x012f, B:36:0x00d5), top: B:85:0x0074, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x012f A[Catch: IOException -> 0x015e, all -> 0x016d, TryCatch #2 {IOException -> 0x015e, blocks: (B:22:0x0074, B:23:0x0089, B:25:0x0093, B:27:0x00ae, B:31:0x00be, B:38:0x00e3, B:40:0x00eb, B:42:0x00ff, B:44:0x0119, B:43:0x010e, B:45:0x012f, B:36:0x00d5), top: B:85:0x0074, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x014a A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x01cb  */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected <T> java.lang.Object readWithMessageConverters(org.springframework.http.HttpInputMessage r8, org.springframework.core.MethodParameter r9, java.lang.reflect.Type r10) throws org.springframework.web.HttpMediaTypeNotSupportedException, java.io.IOException, org.springframework.http.converter.HttpMessageNotReadableException {
        /*
            Method dump skipped, instructions count: 486
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.readWithMessageConverters(org.springframework.http.HttpInputMessage, org.springframework.core.MethodParameter, java.lang.reflect.Type):java.lang.Object");
    }

    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        return new ServletServerHttpRequest(servletRequest);
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
            if (validationHints != null) {
                binder.validate(validationHints);
                return;
            }
        }
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
        return !hasBindingResult;
    }

    protected List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        Set<MediaType> mediaTypeSet = new LinkedHashSet<>();
        for (HttpMessageConverter<?> converter : this.messageConverters) {
            mediaTypeSet.addAll(converter.getSupportedMediaTypes(clazz));
        }
        List<MediaType> result = new ArrayList<>(mediaTypeSet);
        MediaType.sortBySpecificity(result);
        return result;
    }

    @Nullable
    protected Object adaptArgumentIfNecessary(@Nullable Object arg, MethodParameter parameter) {
        if (parameter.getParameterType() == Optional.class) {
            if (arg == null || (((arg instanceof Collection) && ((Collection) arg).isEmpty()) || ((arg instanceof Object[]) && ((Object[]) arg).length == 0))) {
                return Optional.empty();
            }
            return Optional.of(arg);
        }
        return arg;
    }

    void closeStreamIfNecessary(InputStream body) {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodArgumentResolver$EmptyBodyCheckingHttpInputMessage.class */
    private static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {
        private final HttpHeaders headers;

        @Nullable
        private final InputStream body;

        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = inputStream.read() != -1 ? inputStream : null;
                inputStream.reset();
                return;
            }
            PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
            int b = pushbackInputStream.read();
            if (b == -1) {
                this.body = null;
            } else {
                this.body = pushbackInputStream;
                pushbackInputStream.unread(b);
            }
        }

        @Override // org.springframework.http.HttpMessage
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override // org.springframework.http.HttpInputMessage
        public InputStream getBody() {
            return this.body != null ? this.body : StreamUtils.emptyInput();
        }

        public boolean hasBody() {
            return this.body != null;
        }
    }
}
