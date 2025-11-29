package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/filter/RelativeRedirectResponseWrapper.class */
final class RelativeRedirectResponseWrapper extends HttpServletResponseWrapper {
    private final HttpStatus redirectStatus;

    private RelativeRedirectResponseWrapper(HttpServletResponse response, HttpStatus redirectStatus) {
        super(response);
        Assert.notNull(redirectStatus, "'redirectStatus' is required");
        this.redirectStatus = redirectStatus;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        resetBuffer();
        setStatus(this.redirectStatus.value());
        setHeader("Location", location);
        flushBuffer();
    }

    public static HttpServletResponse wrapIfNecessary(HttpServletResponse response, HttpStatus redirectStatus) {
        RelativeRedirectResponseWrapper wrapper = (RelativeRedirectResponseWrapper) WebUtils.getNativeResponse(response, RelativeRedirectResponseWrapper.class);
        return wrapper != null ? response : new RelativeRedirectResponseWrapper(response, redirectStatus);
    }
}
