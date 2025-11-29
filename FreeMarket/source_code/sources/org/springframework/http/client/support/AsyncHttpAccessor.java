package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/support/AsyncHttpAccessor.class */
public class AsyncHttpAccessor {
    protected final Log logger = HttpLogging.forLogName(getClass());

    @Nullable
    private AsyncClientHttpRequestFactory asyncRequestFactory;

    public void setAsyncRequestFactory(AsyncClientHttpRequestFactory asyncRequestFactory) {
        Assert.notNull(asyncRequestFactory, "AsyncClientHttpRequestFactory must not be null");
        this.asyncRequestFactory = asyncRequestFactory;
    }

    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        Assert.state(this.asyncRequestFactory != null, "No AsyncClientHttpRequestFactory set");
        return this.asyncRequestFactory;
    }

    protected AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method) throws IOException {
        AsyncClientHttpRequest request = getAsyncRequestFactory().createAsyncRequest(url, method);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Created asynchronous " + method.name() + " request for \"" + url + "\"");
        }
        return request;
    }
}
