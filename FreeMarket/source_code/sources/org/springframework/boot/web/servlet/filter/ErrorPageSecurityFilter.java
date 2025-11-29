package org.springframework.boot.web.servlet.filter;

import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/filter/ErrorPageSecurityFilter.class */
public class ErrorPageSecurityFilter implements Filter {
    private static final WebInvocationPrivilegeEvaluator ALWAYS = new AlwaysAllowWebInvocationPrivilegeEvaluator();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private final ApplicationContext context;
    private volatile WebInvocationPrivilegeEvaluator privilegeEvaluator;

    public ErrorPageSecurityFilter(ApplicationContext context) {
        this.context = context;
        this.urlPathHelper.setAlwaysUseFullPath(true);
    }

    @Override // javax.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Integer errorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (DispatcherType.ERROR.equals(request.getDispatcherType()) && !isAllowed(request, errorCode)) {
            response.sendError(errorCode != null ? errorCode.intValue() : 401);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isAllowed(HttpServletRequest request, Integer errorCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isUnauthenticated(authentication) && isNotAuthenticationError(errorCode)) {
            return true;
        }
        return getPrivilegeEvaluator().isAllowed(this.urlPathHelper.getPathWithinApplication(request), authentication);
    }

    private boolean isUnauthenticated(Authentication authentication) {
        return authentication == null || (authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean isNotAuthenticationError(Integer errorCode) {
        return errorCode == null || !(errorCode.intValue() == 401 || errorCode.intValue() == 403);
    }

    private WebInvocationPrivilegeEvaluator getPrivilegeEvaluator() {
        WebInvocationPrivilegeEvaluator privilegeEvaluator = this.privilegeEvaluator;
        if (privilegeEvaluator == null) {
            privilegeEvaluator = getPrivilegeEvaluatorBean();
            this.privilegeEvaluator = privilegeEvaluator;
        }
        return privilegeEvaluator;
    }

    private WebInvocationPrivilegeEvaluator getPrivilegeEvaluatorBean() {
        try {
            return (WebInvocationPrivilegeEvaluator) this.context.getBean(WebInvocationPrivilegeEvaluator.class);
        } catch (NoSuchBeanDefinitionException e) {
            return ALWAYS;
        }
    }

    @Override // javax.servlet.Filter
    public void destroy() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/filter/ErrorPageSecurityFilter$AlwaysAllowWebInvocationPrivilegeEvaluator.class */
    private static class AlwaysAllowWebInvocationPrivilegeEvaluator implements WebInvocationPrivilegeEvaluator {
        private AlwaysAllowWebInvocationPrivilegeEvaluator() {
        }

        public boolean isAllowed(String uri, Authentication authentication) {
            return true;
        }

        public boolean isAllowed(String contextPath, String uri, String method, Authentication authentication) {
            return true;
        }
    }
}
