package org.springframework.web.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/handler/ResponseStatusExceptionHandler.class */
public class ResponseStatusExceptionHandler implements WebExceptionHandler {
    private static final Log logger = LogFactory.getLog((Class<?>) ResponseStatusExceptionHandler.class);

    @Nullable
    private Log warnLogger;

    public void setWarnLogCategory(String loggerName) {
        this.warnLogger = LogFactory.getLog(loggerName);
    }

    @Override // org.springframework.web.server.WebExceptionHandler
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (!updateResponse(exchange.getResponse(), ex)) {
            return Mono.error(ex);
        }
        String logPrefix = exchange.getLogPrefix();
        if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
            this.warnLogger.warn(logPrefix + formatError(ex, exchange.getRequest()));
        } else if (logger.isDebugEnabled()) {
            logger.debug(logPrefix + formatError(ex, exchange.getRequest()));
        }
        return exchange.getResponse().setComplete();
    }

    private String formatError(Throwable ex, ServerHttpRequest request) {
        String className = ex.getClass().getSimpleName();
        String message = LogFormatUtils.formatValue(ex.getMessage(), -1, true);
        String path = request.getURI().getRawPath();
        return "Resolved [" + className + ": " + message + "] for HTTP " + request.getMethod() + " " + path;
    }

    private boolean updateResponse(ServerHttpResponse response, Throwable ex) {
        boolean result = false;
        HttpStatus httpStatus = determineStatus(ex);
        int code = httpStatus != null ? httpStatus.value() : determineRawStatusCode(ex);
        if (code != -1) {
            if (response.setRawStatusCode(Integer.valueOf(code))) {
                if (ex instanceof ResponseStatusException) {
                    ((ResponseStatusException) ex).getResponseHeaders().forEach((name, values) -> {
                        values.forEach(value -> {
                            response.getHeaders().add(name, value);
                        });
                    });
                }
                result = true;
            }
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                result = updateResponse(response, cause);
            }
        }
        return result;
    }

    @Nullable
    @Deprecated
    protected HttpStatus determineStatus(Throwable ex) {
        return null;
    }

    protected int determineRawStatusCode(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException) ex).getRawStatusCode();
        }
        return -1;
    }
}
