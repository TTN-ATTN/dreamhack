package org.springframework.web.server;

import java.util.Collections;
import java.util.Map;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/ResponseStatusException.class */
public class ResponseStatusException extends NestedRuntimeException {
    private final int status;

    @Nullable
    private final String reason;

    public ResponseStatusException(HttpStatus status) {
        this(status, null);
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason) {
        super("");
        Assert.notNull(status, "HttpStatus is required");
        this.status = status.value();
        this.reason = reason;
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        Assert.notNull(status, "HttpStatus is required");
        this.status = status.value();
        this.reason = reason;
    }

    public ResponseStatusException(int rawStatusCode, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        this.status = rawStatusCode;
        this.reason = reason;
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(this.status);
    }

    public int getRawStatusCode() {
        return this.status;
    }

    @Deprecated
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    public HttpHeaders getResponseHeaders() {
        Map<String, String> headers = getHeaders();
        if (headers.isEmpty()) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders result = new HttpHeaders();
        Map<String, String> headers2 = getHeaders();
        result.getClass();
        headers2.forEach(result::add);
        return result;
    }

    @Nullable
    public String getReason() {
        return this.reason;
    }

    @Override // org.springframework.core.NestedRuntimeException, java.lang.Throwable
    public String getMessage() {
        HttpStatus code = HttpStatus.resolve(this.status);
        String msg = (code != null ? code : Integer.valueOf(this.status)) + (this.reason != null ? " \"" + this.reason + "\"" : "");
        return NestedExceptionUtils.buildMessage(msg, getCause());
    }
}
