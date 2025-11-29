package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter.class */
public class ResourceUrlEncodingFilter extends GenericFilterBean {
    private static final Log logger = LogFactory.getLog((Class<?>) ResourceUrlEncodingFilter.class);

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("ResourceUrlEncodingFilter only supports HTTP requests");
        }
        ResourceUrlEncodingRequestWrapper wrappedRequest = new ResourceUrlEncodingRequestWrapper((HttpServletRequest) request);
        ResourceUrlEncodingResponseWrapper wrappedResponse = new ResourceUrlEncodingResponseWrapper(wrappedRequest, (HttpServletResponse) response);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter$ResourceUrlEncodingRequestWrapper.class */
    private static class ResourceUrlEncodingRequestWrapper extends HttpServletRequestWrapper {

        @Nullable
        private ResourceUrlProvider resourceUrlProvider;

        @Nullable
        private Integer indexLookupPath;
        private String prefixLookupPath;

        ResourceUrlEncodingRequestWrapper(HttpServletRequest request) {
            super(request);
            this.prefixLookupPath = "";
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public void setAttribute(String name, Object value) {
            super.setAttribute(name, value);
            if (ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR.equals(name) && (value instanceof ResourceUrlProvider)) {
                initLookupPath((ResourceUrlProvider) value);
            }
        }

        private void initLookupPath(ResourceUrlProvider urlProvider) {
            this.resourceUrlProvider = urlProvider;
            if (this.indexLookupPath == null) {
                UrlPathHelper pathHelper = this.resourceUrlProvider.getUrlPathHelper();
                String requestUri = pathHelper.getRequestUri(this);
                String lookupPath = pathHelper.getLookupPathForRequest(this);
                this.indexLookupPath = Integer.valueOf(requestUri.lastIndexOf(lookupPath));
                if (this.indexLookupPath.intValue() == -1) {
                    throw new LookupPathIndexException(lookupPath, requestUri);
                }
                this.prefixLookupPath = requestUri.substring(0, this.indexLookupPath.intValue());
                if (StringUtils.matchesCharacter(lookupPath, '/') && !StringUtils.matchesCharacter(requestUri, '/')) {
                    String contextPath = pathHelper.getContextPath(this);
                    if (requestUri.equals(contextPath)) {
                        this.indexLookupPath = Integer.valueOf(requestUri.length());
                        this.prefixLookupPath = requestUri;
                    }
                }
            }
        }

        @Nullable
        public String resolveUrlPath(String url) {
            if (this.resourceUrlProvider == null) {
                ResourceUrlEncodingFilter.logger.trace("ResourceUrlProvider not available via request attribute " + ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR);
                return null;
            }
            if (this.indexLookupPath != null && url.startsWith(this.prefixLookupPath)) {
                int suffixIndex = getEndPathIndex(url);
                String suffix = url.substring(suffixIndex);
                String lookupPath = this.resourceUrlProvider.getForLookupPath(url.substring(this.indexLookupPath.intValue(), suffixIndex));
                if (lookupPath != null) {
                    return this.prefixLookupPath + lookupPath + suffix;
                }
                return null;
            }
            return null;
        }

        private int getEndPathIndex(String path) {
            int end = path.indexOf(63);
            int fragmentIndex = path.indexOf(35);
            if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
                end = fragmentIndex;
            }
            if (end == -1) {
                end = path.length();
            }
            return end;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter$ResourceUrlEncodingResponseWrapper.class */
    private static class ResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {
        private final ResourceUrlEncodingRequestWrapper request;

        ResourceUrlEncodingResponseWrapper(ResourceUrlEncodingRequestWrapper request, HttpServletResponse wrapped) {
            super(wrapped);
            this.request = request;
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public String encodeURL(String url) {
            String urlPath = this.request.resolveUrlPath(url);
            if (urlPath != null) {
                return super.encodeURL(urlPath);
            }
            return super.encodeURL(url);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter$LookupPathIndexException.class */
    static class LookupPathIndexException extends IllegalArgumentException {
        LookupPathIndexException(String lookupPath, String requestUri) {
            super("Failed to find lookupPath '" + lookupPath + "' within requestUri '" + requestUri + "'. This could be because the path has invalid encoded characters or isn't normalized.");
        }
    }
}
