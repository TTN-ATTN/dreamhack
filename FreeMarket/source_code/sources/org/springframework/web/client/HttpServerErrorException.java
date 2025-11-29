package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/HttpServerErrorException.class */
public class HttpServerErrorException extends HttpStatusCodeException {
    private static final long serialVersionUID = -2915754006618138282L;

    public HttpServerErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    public HttpServerErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public HttpServerErrorException(HttpStatus statusCode, String statusText, @Nullable byte[] body, @Nullable Charset charset) {
        super(statusCode, statusText, body, charset);
    }

    public HttpServerErrorException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset charset) {
        super(statusCode, statusText, headers, body, charset);
    }

    public HttpServerErrorException(String message, HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset charset) {
        super(message, statusCode, statusText, headers, body, charset);
    }

    public static HttpServerErrorException create(HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
        return create(null, statusCode, statusText, headers, body, charset);
    }

    public static HttpServerErrorException create(@Nullable String message, HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
        switch (statusCode) {
            case INTERNAL_SERVER_ERROR:
                return message != null ? new InternalServerError(message, statusText, headers, body, charset) : new InternalServerError(statusText, headers, body, charset);
            case NOT_IMPLEMENTED:
                return message != null ? new NotImplemented(message, statusText, headers, body, charset) : new NotImplemented(statusText, headers, body, charset);
            case BAD_GATEWAY:
                return message != null ? new BadGateway(message, statusText, headers, body, charset) : new BadGateway(statusText, headers, body, charset);
            case SERVICE_UNAVAILABLE:
                return message != null ? new ServiceUnavailable(message, statusText, headers, body, charset) : new ServiceUnavailable(statusText, headers, body, charset);
            case GATEWAY_TIMEOUT:
                return message != null ? new GatewayTimeout(message, statusText, headers, body, charset) : new GatewayTimeout(statusText, headers, body, charset);
            default:
                return message != null ? new HttpServerErrorException(message, statusCode, statusText, headers, body, charset) : new HttpServerErrorException(statusCode, statusText, headers, body, charset);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/HttpServerErrorException$InternalServerError.class */
    public static final class InternalServerError extends HttpServerErrorException {
        private InternalServerError(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.INTERNAL_SERVER_ERROR, statusText, headers, body, charset);
        }

        private InternalServerError(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.INTERNAL_SERVER_ERROR, statusText, headers, body, charset);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/HttpServerErrorException$NotImplemented.class */
    public static final class NotImplemented extends HttpServerErrorException {
        private NotImplemented(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.NOT_IMPLEMENTED, statusText, headers, body, charset);
        }

        private NotImplemented(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.NOT_IMPLEMENTED, statusText, headers, body, charset);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/HttpServerErrorException$BadGateway.class */
    public static final class BadGateway extends HttpServerErrorException {
        private BadGateway(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.BAD_GATEWAY, statusText, headers, body, charset);
        }

        private BadGateway(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.BAD_GATEWAY, statusText, headers, body, charset);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/HttpServerErrorException$ServiceUnavailable.class */
    public static final class ServiceUnavailable extends HttpServerErrorException {
        private ServiceUnavailable(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.SERVICE_UNAVAILABLE, statusText, headers, body, charset);
        }

        private ServiceUnavailable(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.SERVICE_UNAVAILABLE, statusText, headers, body, charset);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/HttpServerErrorException$GatewayTimeout.class */
    public static final class GatewayTimeout extends HttpServerErrorException {
        private GatewayTimeout(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.GATEWAY_TIMEOUT, statusText, headers, body, charset);
        }

        private GatewayTimeout(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.GATEWAY_TIMEOUT, statusText, headers, body, charset);
        }
    }
}
