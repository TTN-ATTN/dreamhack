package org.springframework.web.servlet.function;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ServerResponse;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/AbstractServerResponse.class */
abstract class AbstractServerResponse extends ErrorHandlingServerResponse {
    private static final Set<HttpMethod> SAFE_METHODS = EnumSet.of(HttpMethod.GET, HttpMethod.HEAD);
    final int statusCode;
    private final HttpHeaders headers;
    private final MultiValueMap<String, Cookie> cookies;

    @Nullable
    protected abstract ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException;

    protected AbstractServerResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies) {
        this.statusCode = statusCode;
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
        this.cookies = CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(cookies));
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public final HttpStatus statusCode() {
        return HttpStatus.valueOf(this.statusCode);
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public int rawStatusCode() {
        return this.statusCode;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public final HttpHeaders headers() {
        return this.headers;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public MultiValueMap<String, Cookie> cookies() {
        return this.cookies;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public ModelAndView writeTo(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
        try {
            writeStatusAndHeaders(response);
            long lastModified = headers().getLastModified();
            ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
            HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
            if (SAFE_METHODS.contains(httpMethod) && servletWebRequest.checkNotModified(headers().getETag(), lastModified)) {
                return null;
            }
            return writeToInternal(request, response, context);
        } catch (Throwable throwable) {
            return handleError(throwable, request, response, context);
        }
    }

    private void writeStatusAndHeaders(HttpServletResponse response) {
        response.setStatus(this.statusCode);
        writeHeaders(response);
        writeCookies(response);
    }

    private void writeHeaders(HttpServletResponse servletResponse) {
        this.headers.forEach((headerName, headerValues) -> {
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                servletResponse.addHeader(headerName, headerValue);
            }
        });
        if (servletResponse.getContentType() == null && this.headers.getContentType() != null) {
            servletResponse.setContentType(this.headers.getContentType().toString());
        }
        if (servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers.getContentType().getCharset() != null) {
            servletResponse.setCharacterEncoding(this.headers.getContentType().getCharset().name());
        }
    }

    private void writeCookies(HttpServletResponse servletResponse) {
        Stream<R> streamFlatMap = this.cookies.values().stream().flatMap((v0) -> {
            return v0.stream();
        });
        servletResponse.getClass();
        streamFlatMap.forEach(servletResponse::addCookie);
    }
}
