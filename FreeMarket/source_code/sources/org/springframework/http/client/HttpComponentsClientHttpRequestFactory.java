package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.function.BiFunction;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/HttpComponentsClientHttpRequestFactory.class */
public class HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean {
    private HttpClient httpClient;

    @Nullable
    private RequestConfig requestConfig;
    private boolean bufferRequestBody;

    @Nullable
    private BiFunction<HttpMethod, URI, HttpContext> httpContextFactory;

    public HttpComponentsClientHttpRequestFactory() {
        this.bufferRequestBody = true;
        this.httpClient = HttpClients.createSystem();
    }

    public HttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        this.bufferRequestBody = true;
        this.httpClient = httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setConnectTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = requestConfigBuilder().setConnectTimeout(timeout).build();
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.requestConfig = requestConfigBuilder().setConnectionRequestTimeout(connectionRequestTimeout).build();
    }

    public void setReadTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = requestConfigBuilder().setSocketTimeout(timeout).build();
    }

    public void setBufferRequestBody(boolean bufferRequestBody) {
        this.bufferRequestBody = bufferRequestBody;
    }

    public void setHttpContextFactory(BiFunction<HttpMethod, URI, HttpContext> httpContextFactory) {
        this.httpContextFactory = httpContextFactory;
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpClient client = getHttpClient();
        Configurable configurableCreateHttpUriRequest = createHttpUriRequest(httpMethod, uri);
        postProcessHttpRequest(configurableCreateHttpUriRequest);
        HttpClientContext httpClientContextCreateHttpContext = createHttpContext(httpMethod, uri);
        if (httpClientContextCreateHttpContext == null) {
            httpClientContextCreateHttpContext = HttpClientContext.create();
        }
        if (httpClientContextCreateHttpContext.getAttribute("http.request-config") == null) {
            RequestConfig config = null;
            if (configurableCreateHttpUriRequest instanceof Configurable) {
                config = configurableCreateHttpUriRequest.getConfig();
            }
            if (config == null) {
                config = createRequestConfig(client);
            }
            if (config != null) {
                httpClientContextCreateHttpContext.setAttribute("http.request-config", config);
            }
        }
        if (this.bufferRequestBody) {
            return new HttpComponentsClientHttpRequest(client, configurableCreateHttpUriRequest, httpClientContextCreateHttpContext);
        }
        return new HttpComponentsStreamingClientHttpRequest(client, configurableCreateHttpUriRequest, httpClientContextCreateHttpContext);
    }

    private RequestConfig.Builder requestConfigBuilder() {
        return this.requestConfig != null ? RequestConfig.copy(this.requestConfig) : RequestConfig.custom();
    }

    @Nullable
    protected RequestConfig createRequestConfig(Object client) {
        if (client instanceof Configurable) {
            RequestConfig clientRequestConfig = ((Configurable) client).getConfig();
            return mergeRequestConfig(clientRequestConfig);
        }
        return this.requestConfig;
    }

    protected RequestConfig mergeRequestConfig(RequestConfig clientConfig) {
        if (this.requestConfig == null) {
            return clientConfig;
        }
        RequestConfig.Builder builder = RequestConfig.copy(clientConfig);
        int connectTimeout = this.requestConfig.getConnectTimeout();
        if (connectTimeout >= 0) {
            builder.setConnectTimeout(connectTimeout);
        }
        int connectionRequestTimeout = this.requestConfig.getConnectionRequestTimeout();
        if (connectionRequestTimeout >= 0) {
            builder.setConnectionRequestTimeout(connectionRequestTimeout);
        }
        int socketTimeout = this.requestConfig.getSocketTimeout();
        if (socketTimeout >= 0) {
            builder.setSocketTimeout(socketTimeout);
        }
        return builder.build();
    }

    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        switch (httpMethod) {
            case GET:
                return new HttpGet(uri);
            case HEAD:
                return new HttpHead(uri);
            case POST:
                return new HttpPost(uri);
            case PUT:
                return new HttpPut(uri);
            case PATCH:
                return new HttpPatch(uri);
            case DELETE:
                return new HttpDelete(uri);
            case OPTIONS:
                return new HttpOptions(uri);
            case TRACE:
                return new HttpTrace(uri);
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
    }

    protected void postProcessHttpRequest(HttpUriRequest request) {
    }

    @Nullable
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        if (this.httpContextFactory != null) {
            return this.httpContextFactory.apply(httpMethod, uri);
        }
        return null;
    }

    public void destroy() throws Exception {
        HttpClient httpClient = getHttpClient();
        if (httpClient instanceof Closeable) {
            ((Closeable) httpClient).close();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/HttpComponentsClientHttpRequestFactory$HttpDelete.class */
    private static class HttpDelete extends HttpEntityEnclosingRequestBase {
        public HttpDelete(URI uri) {
            setURI(uri);
        }

        @Override // org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
        public String getMethod() {
            return "DELETE";
        }
    }
}
