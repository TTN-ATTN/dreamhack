package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RemoteHostFilter.class */
public final class RemoteHostFilter extends RequestFilter {
    private final Log log = LogFactory.getLog((Class<?>) RemoteHostFilter.class);

    @Override // org.apache.catalina.filters.RequestFilter, javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        process(request.getRemoteHost(), request, response, chain);
    }

    @Override // org.apache.catalina.filters.FilterBase
    protected Log getLogger() {
        return this.log;
    }
}
