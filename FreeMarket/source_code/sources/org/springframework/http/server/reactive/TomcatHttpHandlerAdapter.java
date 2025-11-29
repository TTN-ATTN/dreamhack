package org.springframework.http.server.reactive;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.CoyoteOutputStream;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/TomcatHttpHandlerAdapter.class */
public class TomcatHttpHandlerAdapter extends ServletHttpHandlerAdapter {
    public TomcatHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext asyncContext) throws URISyntaxException, IOException {
        Assert.state(getServletPath() != null, "Servlet path is not initialized");
        return new TomcatServerHttpRequest(request, asyncContext, getServletPath(), getDataBufferFactory(), getBufferSize());
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext asyncContext, ServletServerHttpRequest request) throws IOException {
        return new TomcatServerHttpResponse(response, asyncContext, getDataBufferFactory(), getBufferSize(), request);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/TomcatHttpHandlerAdapter$TomcatServerHttpRequest.class */
    private static final class TomcatServerHttpRequest extends ServletServerHttpRequest {
        private static final Field COYOTE_REQUEST_FIELD;
        private final int bufferSize;
        private final DataBufferFactory factory;

        static {
            Field field = ReflectionUtils.findField(RequestFacade.class, "request");
            Assert.state(field != null, "Incompatible Tomcat implementation");
            ReflectionUtils.makeAccessible(field);
            COYOTE_REQUEST_FIELD = field;
        }

        TomcatServerHttpRequest(HttpServletRequest request, AsyncContext context, String servletPath, DataBufferFactory factory, int bufferSize) throws URISyntaxException, IOException {
            super(createTomcatHttpHeaders(request), request, context, servletPath, factory, bufferSize);
            this.factory = factory;
            this.bufferSize = bufferSize;
        }

        private static MultiValueMap<String, String> createTomcatHttpHeaders(HttpServletRequest request) {
            RequestFacade requestFacade = getRequestFacade(request);
            Request connectorRequest = (Request) ReflectionUtils.getField(COYOTE_REQUEST_FIELD, requestFacade);
            Assert.state(connectorRequest != null, "No Tomcat connector request");
            org.apache.coyote.Request tomcatRequest = connectorRequest.getCoyoteRequest();
            return new TomcatHeadersAdapter(tomcatRequest.getMimeHeaders());
        }

        private static RequestFacade getRequestFacade(HttpServletRequest request) {
            if (request instanceof RequestFacade) {
                return (RequestFacade) request;
            }
            if (request instanceof HttpServletRequestWrapper) {
                HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) request;
                HttpServletRequest wrappedRequest = (HttpServletRequest) wrapper.getRequest();
                return getRequestFacade(wrappedRequest);
            }
            throw new IllegalArgumentException("Cannot convert [" + request.getClass() + "] to org.apache.catalina.connector.RequestFacade");
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpRequest
        protected DataBuffer readFromInputStream() throws IOException {
            ServletInputStream inputStream = ((ServletRequest) getNativeRequest()).getInputStream();
            if (!(inputStream instanceof CoyoteInputStream)) {
                return super.readFromInputStream();
            }
            int capacity = this.bufferSize;
            DataBuffer dataBuffer = this.factory.allocateBuffer(capacity);
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, capacity);
                int read = ((CoyoteInputStream) inputStream).read(byteBuffer);
                logBytesRead(read);
                if (read > 0) {
                    dataBuffer.writePosition(read);
                    if (0 != 0) {
                        DataBufferUtils.release(dataBuffer);
                    }
                    return dataBuffer;
                }
                if (read == -1) {
                    DataBuffer dataBuffer2 = EOF_BUFFER;
                    if (1 != 0) {
                        DataBufferUtils.release(dataBuffer);
                    }
                    return dataBuffer2;
                }
                DataBuffer dataBuffer3 = AbstractListenerReadPublisher.EMPTY_BUFFER;
                if (1 != 0) {
                    DataBufferUtils.release(dataBuffer);
                }
                return dataBuffer3;
            } catch (Throwable th) {
                if (1 != 0) {
                    DataBufferUtils.release(dataBuffer);
                }
                throw th;
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/TomcatHttpHandlerAdapter$TomcatServerHttpResponse.class */
    private static final class TomcatServerHttpResponse extends ServletServerHttpResponse {
        private static final Field COYOTE_RESPONSE_FIELD;

        static {
            Field field = ReflectionUtils.findField(ResponseFacade.class, "response");
            Assert.state(field != null, "Incompatible Tomcat implementation");
            ReflectionUtils.makeAccessible(field);
            COYOTE_RESPONSE_FIELD = field;
        }

        TomcatServerHttpResponse(HttpServletResponse response, AsyncContext context, DataBufferFactory factory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(createTomcatHttpHeaders(response), response, context, factory, bufferSize, request);
        }

        private static HttpHeaders createTomcatHttpHeaders(HttpServletResponse response) {
            ResponseFacade responseFacade = getResponseFacade(response);
            Response connectorResponse = (Response) ReflectionUtils.getField(COYOTE_RESPONSE_FIELD, responseFacade);
            Assert.state(connectorResponse != null, "No Tomcat connector response");
            org.apache.coyote.Response tomcatResponse = connectorResponse.getCoyoteResponse();
            TomcatHeadersAdapter headers = new TomcatHeadersAdapter(tomcatResponse.getMimeHeaders());
            return new HttpHeaders(headers);
        }

        private static ResponseFacade getResponseFacade(HttpServletResponse response) {
            if (response instanceof ResponseFacade) {
                return (ResponseFacade) response;
            }
            if (response instanceof HttpServletResponseWrapper) {
                HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper) response;
                HttpServletResponse wrappedResponse = (HttpServletResponse) wrapper.getResponse();
                return getResponseFacade(wrappedResponse);
            }
            throw new IllegalArgumentException("Cannot convert [" + response.getClass() + "] to org.apache.catalina.connector.ResponseFacade");
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
            getHeaders().remove(HttpHeaders.CONTENT_TYPE);
            Charset charset = contentType != null ? contentType.getCharset() : null;
            if (response.getCharacterEncoding() == null && charset != null) {
                response.setCharacterEncoding(charset.name());
            }
            long contentLength = getHeaders().getContentLength();
            if (contentLength != -1) {
                response.setContentLengthLong(contentLength);
            }
            getHeaders().remove(HttpHeaders.CONTENT_LENGTH);
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpResponse
        protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
            ByteBuffer input = dataBuffer.asByteBuffer();
            int len = input.remaining();
            ServletResponse response = (ServletResponse) getNativeResponse();
            ((CoyoteOutputStream) response.getOutputStream()).write(input);
            return len;
        }
    }
}
