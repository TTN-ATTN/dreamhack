package org.apache.catalina.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RestCsrfPreventionFilter.class */
public class RestCsrfPreventionFilter extends CsrfPreventionFilterBase {
    private static final Pattern NON_MODIFYING_METHODS_PATTERN = Pattern.compile("GET|HEAD|OPTIONS");
    private static final Predicate<String> nonModifyingMethods = m -> {
        return Objects.nonNull(m) && NON_MODIFYING_METHODS_PATTERN.matcher(m).matches();
    };
    private Set<String> pathsAcceptingParams = new HashSet();
    private String pathsDelimiter = ",";

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$MethodType.class */
    private enum MethodType {
        NON_MODIFYING_METHOD,
        MODIFYING_METHOD
    }

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$NonceConsumer.class */
    private interface NonceConsumer<T> {
        void setNonce(T t, String str, String str2);
    }

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$NonceSupplier.class */
    private interface NonceSupplier<T, R> {
        R getNonce(T t, String str);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$RestCsrfPreventionStrategy.class */
    private interface RestCsrfPreventionStrategy {
        public static final NonceSupplier<HttpServletRequest, String> nonceFromRequestHeader = (v0, v1) -> {
            return v0.getHeader(v1);
        };
        public static final NonceSupplier<HttpServletRequest, String[]> nonceFromRequestParams = (v0, v1) -> {
            return v0.getParameterValues(v1);
        };
        public static final NonceSupplier<HttpSession, String> nonceFromSession = (s, k) -> {
            if (Objects.isNull(s)) {
                return null;
            }
            return (String) s.getAttribute(k);
        };
        public static final NonceConsumer<HttpServletResponse> nonceToResponse = (v0, v1, v2) -> {
            v0.setHeader(v1, v2);
        };
        public static final NonceConsumer<HttpSession> nonceToSession = (v0, v1, v2) -> {
            v0.setAttribute(v1, v2);
        };

        boolean apply(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
    }

    @Override // org.apache.catalina.filters.CsrfPreventionFilterBase, org.apache.catalina.filters.FilterBase, javax.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException, ClassNotFoundException {
        super.init(filterConfig);
        filterConfig.getServletContext().setAttribute(Constants.CSRF_REST_NONCE_HEADER_NAME_KEY, Constants.CSRF_REST_NONCE_HEADER_NAME);
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        RestCsrfPreventionStrategy strategy;
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            MethodType mType = MethodType.MODIFYING_METHOD;
            if (nonModifyingMethods.test(((HttpServletRequest) request).getMethod())) {
                mType = MethodType.NON_MODIFYING_METHOD;
            }
            switch (mType) {
                case NON_MODIFYING_METHOD:
                    strategy = new FetchRequest();
                    break;
                default:
                    strategy = new StateChangingRequest();
                    break;
            }
            if (!strategy.apply((HttpServletRequest) request, (HttpServletResponse) response)) {
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$StateChangingRequest.class */
    private class StateChangingRequest implements RestCsrfPreventionStrategy {
        private StateChangingRequest() {
        }

        @Override // org.apache.catalina.filters.RestCsrfPreventionFilter.RestCsrfPreventionStrategy
        public boolean apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String nonceRequest = extractNonceFromRequest(request);
            HttpSession session = request.getSession(false);
            String nonceSession = nonceFromSession.getNonce(session, Constants.CSRF_REST_NONCE_SESSION_ATTR_NAME);
            if (isValidStateChangingRequest(nonceRequest, nonceSession)) {
                return true;
            }
            nonceToResponse.setNonce(response, Constants.CSRF_REST_NONCE_HEADER_NAME, Constants.CSRF_REST_NONCE_HEADER_REQUIRED_VALUE);
            response.sendError(RestCsrfPreventionFilter.this.getDenyStatus(), FilterBase.sm.getString("restCsrfPreventionFilter.invalidNonce"));
            if (RestCsrfPreventionFilter.this.getLogger().isDebugEnabled()) {
                Log logger = RestCsrfPreventionFilter.this.getLogger();
                StringManager stringManager = FilterBase.sm;
                Object[] objArr = new Object[6];
                objArr[0] = request.getMethod();
                objArr[1] = request.getRequestURI();
                objArr[2] = Boolean.valueOf(request.getRequestedSessionId() != null);
                objArr[3] = session;
                objArr[4] = Boolean.valueOf(nonceRequest != null);
                objArr[5] = Boolean.valueOf(nonceSession != null);
                logger.debug(stringManager.getString("restCsrfPreventionFilter.invalidNonce.debug", objArr));
                return false;
            }
            return false;
        }

        private boolean isValidStateChangingRequest(String reqNonce, String sessionNonce) {
            return Objects.nonNull(reqNonce) && Objects.nonNull(sessionNonce) && Objects.equals(reqNonce, sessionNonce);
        }

        private String extractNonceFromRequest(HttpServletRequest request) {
            String nonceFromRequest = nonceFromRequestHeader.getNonce(request, Constants.CSRF_REST_NONCE_HEADER_NAME);
            if ((Objects.isNull(nonceFromRequest) || Objects.equals("", nonceFromRequest)) && !RestCsrfPreventionFilter.this.getPathsAcceptingParams().isEmpty() && RestCsrfPreventionFilter.this.getPathsAcceptingParams().contains(RestCsrfPreventionFilter.this.getRequestedPath(request))) {
                nonceFromRequest = extractNonceFromRequestParams(request);
            }
            return nonceFromRequest;
        }

        private String extractNonceFromRequestParams(HttpServletRequest request) {
            String[] params = nonceFromRequestParams.getNonce(request, Constants.CSRF_REST_NONCE_HEADER_NAME);
            if (Objects.nonNull(params) && params.length > 0) {
                String nonce = params[0];
                for (String param : params) {
                    if (!Objects.equals(param, nonce)) {
                        if (RestCsrfPreventionFilter.this.getLogger().isDebugEnabled()) {
                            RestCsrfPreventionFilter.this.getLogger().debug(FilterBase.sm.getString("restCsrfPreventionFilter.multipleNonce.debug", request.getMethod(), request.getRequestURI()));
                            return null;
                        }
                        return null;
                    }
                }
                return nonce;
            }
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$FetchRequest.class */
    private class FetchRequest implements RestCsrfPreventionStrategy {
        private final Predicate<String> fetchRequest;

        private FetchRequest() {
            String str = Constants.CSRF_REST_NONCE_HEADER_FETCH_VALUE;
            this.fetchRequest = str::equalsIgnoreCase;
        }

        @Override // org.apache.catalina.filters.RestCsrfPreventionFilter.RestCsrfPreventionStrategy
        public boolean apply(HttpServletRequest request, HttpServletResponse response) {
            if (this.fetchRequest.test(nonceFromRequestHeader.getNonce(request, Constants.CSRF_REST_NONCE_HEADER_NAME))) {
                String nonceFromSessionStr = nonceFromSession.getNonce(request.getSession(false), Constants.CSRF_REST_NONCE_SESSION_ATTR_NAME);
                if (nonceFromSessionStr == null) {
                    nonceFromSessionStr = RestCsrfPreventionFilter.this.generateNonce(request);
                    nonceToSession.setNonce((HttpSession) Objects.requireNonNull(request.getSession(true)), Constants.CSRF_REST_NONCE_SESSION_ATTR_NAME, nonceFromSessionStr);
                }
                nonceToResponse.setNonce(response, Constants.CSRF_REST_NONCE_HEADER_NAME, nonceFromSessionStr);
                if (RestCsrfPreventionFilter.this.getLogger().isDebugEnabled()) {
                    RestCsrfPreventionFilter.this.getLogger().debug(FilterBase.sm.getString("restCsrfPreventionFilter.fetch.debug", request.getMethod(), request.getRequestURI()));
                    return true;
                }
                return true;
            }
            return true;
        }
    }

    public void setPathsAcceptingParams(String pathsList) {
        if (Objects.nonNull(pathsList)) {
            Arrays.asList(pathsList.split(this.pathsDelimiter)).forEach(e -> {
                this.pathsAcceptingParams.add(e.trim());
            });
        }
    }

    public Set<String> getPathsAcceptingParams() {
        return this.pathsAcceptingParams;
    }
}
