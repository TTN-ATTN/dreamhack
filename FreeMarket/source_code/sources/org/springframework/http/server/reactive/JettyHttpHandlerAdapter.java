package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter.class */
public class JettyHttpHandlerAdapter extends ServletHttpHandlerAdapter {
    private static final boolean jetty10Present = ClassUtils.isPresent("org.eclipse.jetty.http.CookieCutter", JettyHttpHandlerAdapter.class.getClassLoader());

    public JettyHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws URISyntaxException, IOException {
        if (jetty10Present) {
            return super.createRequest(request, context);
        }
        Assert.state(getServletPath() != null, "Servlet path is not initialized");
        return new JettyServerHttpRequest(request, context, getServletPath(), getDataBufferFactory(), getBufferSize());
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
        if (jetty10Present) {
            return new BaseJettyServerHttpResponse(response, context, getDataBufferFactory(), getBufferSize(), request);
        }
        return new JettyServerHttpResponse(response, context, getDataBufferFactory(), getBufferSize(), request);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter$JettyServerHttpRequest.class */
    private static final class JettyServerHttpRequest extends ServletServerHttpRequest {
        JettyServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws URISyntaxException, IOException {
            super(createHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
        }

        private static MultiValueMap<String, String> createHeaders(HttpServletRequest servletRequest) {
            Request request = getRequest(servletRequest);
            HttpFields fields = request.getMetaData().getFields();
            return new JettyHeadersAdapter(fields);
        }

        private static Request getRequest(HttpServletRequest request) {
            if (request instanceof Request) {
                return (Request) request;
            }
            if (request instanceof HttpServletRequestWrapper) {
                HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) request;
                HttpServletRequest wrappedRequest = (HttpServletRequest) wrapper.getRequest();
                return getRequest(wrappedRequest);
            }
            throw new IllegalArgumentException("Cannot convert [" + request.getClass() + "] to org.eclipse.jetty.server.Request");
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter$BaseJettyServerHttpResponse.class */
    private static class BaseJettyServerHttpResponse extends ServletServerHttpResponse {
        BaseJettyServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(response, asyncContext, bufferFactory, bufferSize, request);
        }

        BaseJettyServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(headers, response, asyncContext, bufferFactory, bufferSize, request);
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpResponse
        protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
            ByteBuffer input = dataBuffer.asByteBuffer();
            int len = input.remaining();
            ServletResponse response = (ServletResponse) getNativeResponse();
            response.getOutputStream().write(input);
            return len;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter$JettyServerHttpResponse.class */
    private static final class JettyServerHttpResponse extends BaseJettyServerHttpResponse {
        JettyServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(createHeaders(response), response, asyncContext, bufferFactory, bufferSize, request);
        }

        private static HttpHeaders createHeaders(HttpServletResponse servletResponse) {
            Response response = getResponse(servletResponse);
            HttpFields fields = response.getHttpFields();
            return new HttpHeaders(new JettyHeadersAdapter(fields));
        }

        private static Response getResponse(HttpServletResponse response) {
            if (response instanceof Response) {
                return (Response) response;
            }
            if (response instanceof HttpServletResponseWrapper) {
                HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper) response;
                HttpServletResponse wrappedResponse = (HttpServletResponse) wrapper.getResponse();
                return getResponse(wrappedResponse);
            }
            throw new IllegalArgumentException("Cannot convert [" + response.getClass() + "] to org.eclipse.jetty.server.Response");
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpResponse, org.springframework.http.server.reactive.AbstractServerHttpResponse
        protected void applyHeaders() {
            HttpServletResponse response = (HttpServletResponse) getNativeResponse();
            MediaType contentType = null;
            try {
                contentType = getHeaders().getContentType();
            } catch (Exception e) {
                String rawContentType = getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
                response.setContentType(rawContentType);
            }
            if (response.getContentType() == null && contentType != null) {
                response.setContentType(contentType.toString());
            }
            Charset charset = contentType != null ? contentType.getCharset() : null;
            if (response.getCharacterEncoding() == null && charset != null) {
                response.setCharacterEncoding(charset.name());
            }
            long contentLength = getHeaders().getContentLength();
            if (contentLength != -1) {
                response.setContentLengthLong(contentLength);
            }
        }
    }
}
