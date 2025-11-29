package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.server.RequestPath;
import org.springframework.web.util.ServletRequestPathUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/filter/ServletRequestPathFilter.class */
public class ServletRequestPathFilter implements Filter {
    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        RequestPath previousRequestPath = (RequestPath) request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
        ServletRequestPathUtils.parseAndCache((HttpServletRequest) request);
        try {
            chain.doFilter(request, response);
            ServletRequestPathUtils.setParsedRequestPath(previousRequestPath, request);
        } catch (Throwable th) {
            ServletRequestPathUtils.setParsedRequestPath(previousRequestPath, request);
            throw th;
        }
    }
}
