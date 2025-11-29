package org.springframework.http.server.reactive;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ServletServerHttpResponse.class */
class ServletServerHttpResponse extends AbstractListenerServerHttpResponse {
    private final HttpServletResponse response;
    private final ServletOutputStream outputStream;
    private final int bufferSize;

    @Nullable
    private volatile ResponseBodyFlushProcessor bodyFlushProcessor;

    @Nullable
    private volatile ResponseBodyProcessor bodyProcessor;
    private volatile boolean flushOnNext;
    private final ServletServerHttpRequest request;
    private final ResponseAsyncListener asyncListener;

    public ServletServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        this(new HttpHeaders(), response, asyncContext, bufferFactory, bufferSize, request);
    }

    public ServletServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        super(bufferFactory, headers);
        Assert.notNull(response, "HttpServletResponse must not be null");
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        Assert.isTrue(bufferSize > 0, "Buffer size must be greater than 0");
        this.response = response;
        this.outputStream = response.getOutputStream();
        this.bufferSize = bufferSize;
        this.request = request;
        this.asyncListener = new ResponseAsyncListener();
        response.getOutputStream().setWriteListener(new ResponseBodyWriteListener());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    public HttpStatus getStatusCode() {
        HttpStatus status = super.getStatusCode();
        return status != null ? status : HttpStatus.resolve(this.response.getStatus());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return Integer.valueOf(status != null ? status.intValue() : this.response.getStatus());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        Integer status = super.getRawStatusCode();
        if (status != null) {
            this.response.setStatus(status.intValue());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
        getHeaders().forEach((headerName, headerValues) -> {
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                this.response.addHeader(headerName, headerValue);
            }
        });
        MediaType contentType = null;
        try {
            contentType = getHeaders().getContentType();
        } catch (Exception e) {
            String rawContentType = getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            this.response.setContentType(rawContentType);
        }
        if (this.response.getContentType() == null && contentType != null) {
            this.response.setContentType(contentType.toString());
        }
        Charset charset = contentType != null ? contentType.getCharset() : null;
        if (this.response.getCharacterEncoding() == null && charset != null) {
            this.response.setCharacterEncoding(charset.name());
        }
        long contentLength = getHeaders().getContentLength();
        if (contentLength != -1) {
            this.response.setContentLengthLong(contentLength);
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        Iterator<ResponseCookie> it = getCookies().values().iterator();
        while (it.hasNext()) {
            List<ResponseCookie> cookies = (List) it.next();
            for (ResponseCookie cookie : cookies) {
                this.response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }
        }
    }

    AsyncListener getAsyncListener() {
        return this.asyncListener;
    }

    @Override // org.springframework.http.server.reactive.AbstractListenerServerHttpResponse
    protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
        ResponseBodyFlushProcessor processor = new ResponseBodyFlushProcessor();
        this.bodyFlushProcessor = processor;
        return processor;
    }

    protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
        int bytesRead;
        ServletOutputStream outputStream = this.outputStream;
        InputStream input = dataBuffer.asInputStream();
        int bytesWritten = 0;
        byte[] buffer = new byte[this.bufferSize];
        while (outputStream.isReady() && (bytesRead = input.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            bytesWritten += bytesRead;
        }
        return bytesWritten;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void flush() throws IOException {
        ServletOutputStream outputStream = this.outputStream;
        if (outputStream.isReady()) {
            try {
                outputStream.flush();
                this.flushOnNext = false;
                return;
            } catch (IOException ex) {
                this.flushOnNext = true;
                throw ex;
            }
        }
        this.flushOnNext = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWritePossible() {
        return this.outputStream.isReady();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseAsyncListener.class */
    private final class ResponseAsyncListener implements AsyncListener {
        private ResponseAsyncListener() {
        }

        @Override // javax.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) {
        }

        @Override // javax.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            handleError(ex != null ? ex : new IllegalStateException("Async operation timeout."));
        }

        @Override // javax.servlet.AsyncListener
        public void onError(AsyncEvent event) {
            handleError(event.getThrowable());
        }

        public void handleError(Throwable ex) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                if (processor != null) {
                    processor.cancel();
                    processor.onError(ex);
                }
                flushProcessor.onError(ex);
            }
        }

        @Override // javax.servlet.AsyncListener
        public void onComplete(AsyncEvent event) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                if (processor != null) {
                    processor.cancel();
                    processor.onComplete();
                }
                flushProcessor.onComplete();
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyWriteListener.class */
    private class ResponseBodyWriteListener implements WriteListener {
        private ResponseBodyWriteListener() {
        }

        @Override // javax.servlet.WriteListener
        public void onWritePossible() {
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor == null) {
                ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
                if (flushProcessor != null) {
                    flushProcessor.onFlushPossible();
                    return;
                }
                return;
            }
            processor.onWritePossible();
        }

        @Override // javax.servlet.WriteListener
        public void onError(Throwable ex) {
            ServletServerHttpResponse.this.asyncListener.handleError(ex);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyFlushProcessor.class */
    private class ResponseBodyFlushProcessor extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        public ResponseBodyFlushProcessor() {
            super(ServletServerHttpResponse.this.request.getLogPrefix());
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.new ResponseBodyProcessor();
            ServletServerHttpResponse.this.bodyProcessor = processor;
            return processor;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected void flush() throws IOException {
            if (rsWriteFlushLogger.isTraceEnabled()) {
                rsWriteFlushLogger.trace(getLogPrefix() + "flushing");
            }
            ServletServerHttpResponse.this.flush();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isFlushPending() {
            return ServletServerHttpResponse.this.flushOnNext;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyProcessor.class */
    private class ResponseBodyProcessor extends AbstractListenerWriteProcessor<DataBuffer> {
        public ResponseBodyProcessor() {
            super(ServletServerHttpResponse.this.request.getLogPrefix());
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean isDataEmpty(DataBuffer dataBuffer) {
            return dataBuffer.readableByteCount() == 0;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean write(DataBuffer dataBuffer) throws IOException {
            if (ServletServerHttpResponse.this.flushOnNext) {
                if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace(getLogPrefix() + "flushing");
                }
                ServletServerHttpResponse.this.flush();
            }
            boolean ready = ServletServerHttpResponse.this.isWritePossible();
            int remaining = dataBuffer.readableByteCount();
            if (ready && remaining > 0) {
                int written = ServletServerHttpResponse.this.writeToOutputStream(dataBuffer);
                if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace(getLogPrefix() + "Wrote " + written + " of " + remaining + " bytes");
                }
                if (written == remaining) {
                    DataBufferUtils.release(dataBuffer);
                    return true;
                }
                return false;
            }
            if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace(getLogPrefix() + "ready: " + ready + ", remaining: " + remaining);
                return false;
            }
            return false;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected void writingComplete() {
            ServletServerHttpResponse.this.bodyProcessor = null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
        }
    }
}
