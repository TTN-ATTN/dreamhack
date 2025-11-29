package javax.servlet;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/Filter.class */
public interface Filter {
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException;

    default void init(FilterConfig filterConfig) throws ServletException {
    }

    default void destroy() {
    }
}
