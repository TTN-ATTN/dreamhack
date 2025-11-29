package org.springframework.boot.web.server;

import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/ErrorPage.class */
public class ErrorPage {
    private final HttpStatus status;
    private final Class<? extends Throwable> exception;
    private final String path;

    public ErrorPage(String path) {
        this.status = null;
        this.exception = null;
        this.path = path;
    }

    public ErrorPage(HttpStatus status, String path) {
        this.status = status;
        this.exception = null;
        this.path = path;
    }

    public ErrorPage(Class<? extends Throwable> exception, String path) {
        this.status = null;
        this.exception = exception;
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public Class<? extends Throwable> getException() {
        return this.exception;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public int getStatusCode() {
        if (this.status != null) {
            return this.status.value();
        }
        return 0;
    }

    public String getExceptionName() {
        if (this.exception != null) {
            return this.exception.getName();
        }
        return null;
    }

    public boolean isGlobal() {
        return this.status == null && this.exception == null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof ErrorPage)) {
            ErrorPage other = (ErrorPage) obj;
            return ObjectUtils.nullSafeEquals(getExceptionName(), other.getExceptionName()) && ObjectUtils.nullSafeEquals(this.path, other.path) && this.status == other.status;
        }
        return false;
    }

    public int hashCode() {
        int result = (31 * 1) + ObjectUtils.nullSafeHashCode(getExceptionName());
        return (31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.path))) + getStatusCode();
    }
}
