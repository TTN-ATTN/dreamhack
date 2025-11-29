package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/SimpleBufferingAsyncClientHttpRequest.class */
final class SimpleBufferingAsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest {
    private final HttpURLConnection connection;
    private final boolean outputStreaming;
    private final AsyncListenableTaskExecutor taskExecutor;

    SimpleBufferingAsyncClientHttpRequest(HttpURLConnection connection, boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {
        this.connection = connection;
        this.outputStreaming = outputStreaming;
        this.taskExecutor = taskExecutor;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.connection.getRequestMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    @Override // org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        return this.taskExecutor.submitListenable(() -> {
            SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
            if (getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
                this.connection.setDoOutput(false);
            }
            if (this.connection.getDoOutput() && this.outputStreaming) {
                this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
            }
            this.connection.connect();
            if (this.connection.getDoOutput()) {
                FileCopyUtils.copy(bufferedOutput, this.connection.getOutputStream());
            } else {
                this.connection.getResponseCode();
            }
            return new SimpleClientHttpResponse(this.connection);
        });
    }
}
