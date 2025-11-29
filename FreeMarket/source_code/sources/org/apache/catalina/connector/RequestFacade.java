package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade.class */
public class RequestFacade implements HttpServletRequest {
    private static final StringManager sm = StringManager.getManager((Class<?>) RequestFacade.class);
    protected Request request;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetAttributePrivilegedAction.class */
    private final class GetAttributePrivilegedAction implements PrivilegedAction<Enumeration<String>> {
        private GetAttributePrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Enumeration<String> run() {
            return RequestFacade.this.request.getAttributeNames();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetParameterMapPrivilegedAction.class */
    private final class GetParameterMapPrivilegedAction implements PrivilegedAction<Map<String, String[]>> {
        private GetParameterMapPrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Map<String, String[]> run() {
            return RequestFacade.this.request.getParameterMap();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetRequestDispatcherPrivilegedAction.class */
    private final class GetRequestDispatcherPrivilegedAction implements PrivilegedAction<RequestDispatcher> {
        private final String path;

        GetRequestDispatcherPrivilegedAction(String path) {
            this.path = path;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public RequestDispatcher run() {
            return RequestFacade.this.request.getRequestDispatcher(this.path);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetParameterPrivilegedAction.class */
    private final class GetParameterPrivilegedAction implements PrivilegedAction<String> {
        public String name;

        GetParameterPrivilegedAction(String name) {
            this.name = name;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public String run() {
            return RequestFacade.this.request.getParameter(this.name);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetParameterNamesPrivilegedAction.class */
    private final class GetParameterNamesPrivilegedAction implements PrivilegedAction<Enumeration<String>> {
        private GetParameterNamesPrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Enumeration<String> run() {
            return RequestFacade.this.request.getParameterNames();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetParameterValuePrivilegedAction.class */
    private final class GetParameterValuePrivilegedAction implements PrivilegedAction<String[]> {
        public String name;

        GetParameterValuePrivilegedAction(String name) {
            this.name = name;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public String[] run() {
            return RequestFacade.this.request.getParameterValues(this.name);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetCookiesPrivilegedAction.class */
    private final class GetCookiesPrivilegedAction implements PrivilegedAction<Cookie[]> {
        private GetCookiesPrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Cookie[] run() {
            return RequestFacade.this.request.getCookies();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetCharacterEncodingPrivilegedAction.class */
    private final class GetCharacterEncodingPrivilegedAction implements PrivilegedAction<String> {
        private GetCharacterEncodingPrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public String run() {
            return RequestFacade.this.request.getCharacterEncoding();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetHeadersPrivilegedAction.class */
    private final class GetHeadersPrivilegedAction implements PrivilegedAction<Enumeration<String>> {
        private final String name;

        GetHeadersPrivilegedAction(String name) {
            this.name = name;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Enumeration<String> run() {
            return RequestFacade.this.request.getHeaders(this.name);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetHeaderNamesPrivilegedAction.class */
    private final class GetHeaderNamesPrivilegedAction implements PrivilegedAction<Enumeration<String>> {
        private GetHeaderNamesPrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Enumeration<String> run() {
            return RequestFacade.this.request.getHeaderNames();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetLocalePrivilegedAction.class */
    private final class GetLocalePrivilegedAction implements PrivilegedAction<Locale> {
        private GetLocalePrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Locale run() {
            return RequestFacade.this.request.getLocale();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetLocalesPrivilegedAction.class */
    private final class GetLocalesPrivilegedAction implements PrivilegedAction<Enumeration<Locale>> {
        private GetLocalesPrivilegedAction() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Enumeration<Locale> run() {
            return RequestFacade.this.request.getLocales();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/RequestFacade$GetSessionPrivilegedAction.class */
    private final class GetSessionPrivilegedAction implements PrivilegedAction<HttpSession> {
        private final boolean create;

        GetSessionPrivilegedAction(boolean create) {
            this.create = create;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public HttpSession run() {
            return RequestFacade.this.request.getSession(this.create);
        }
    }

    public RequestFacade(Request request) {
        this.request = null;
        this.request = request;
    }

    public void clear() {
        this.request = null;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override // javax.servlet.ServletRequest
    public Object getAttribute(String name) {
        checkFacade();
        return this.request.getAttribute(name);
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<String> getAttributeNames() {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (Enumeration) AccessController.doPrivileged(new GetAttributePrivilegedAction());
        }
        return this.request.getAttributeNames();
    }

    @Override // javax.servlet.ServletRequest
    public String getCharacterEncoding() {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (String) AccessController.doPrivileged(new GetCharacterEncodingPrivilegedAction());
        }
        return this.request.getCharacterEncoding();
    }

    @Override // javax.servlet.ServletRequest
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        checkFacade();
        this.request.setCharacterEncoding(env);
    }

    @Override // javax.servlet.ServletRequest
    public int getContentLength() {
        checkFacade();
        return this.request.getContentLength();
    }

    @Override // javax.servlet.ServletRequest
    public String getContentType() {
        checkFacade();
        return this.request.getContentType();
    }

    @Override // javax.servlet.ServletRequest
    public ServletInputStream getInputStream() throws IOException {
        checkFacade();
        return this.request.getInputStream();
    }

    @Override // javax.servlet.ServletRequest
    public String getParameter(String name) {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (String) AccessController.doPrivileged(new GetParameterPrivilegedAction(name));
        }
        return this.request.getParameter(name);
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (Enumeration) AccessController.doPrivileged(new GetParameterNamesPrivilegedAction());
        }
        return this.request.getParameterNames();
    }

    @Override // javax.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        String[] ret;
        checkFacade();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            ret = (String[]) AccessController.doPrivileged(new GetParameterValuePrivilegedAction(name));
            if (ret != null) {
                ret = (String[]) ret.clone();
            }
        } else {
            ret = this.request.getParameterValues(name);
        }
        return ret;
    }

    @Override // javax.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (Map) AccessController.doPrivileged(new GetParameterMapPrivilegedAction());
        }
        return this.request.getParameterMap();
    }

    @Override // javax.servlet.ServletRequest
    public String getProtocol() {
        checkFacade();
        return this.request.getProtocol();
    }

    @Override // javax.servlet.ServletRequest
    public String getScheme() {
        checkFacade();
        return this.request.getScheme();
    }

    @Override // javax.servlet.ServletRequest
    public String getServerName() {
        checkFacade();
        return this.request.getServerName();
    }

    @Override // javax.servlet.ServletRequest
    public int getServerPort() {
        checkFacade();
        return this.request.getServerPort();
    }

    @Override // javax.servlet.ServletRequest
    public BufferedReader getReader() throws IOException {
        checkFacade();
        return this.request.getReader();
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteAddr() {
        checkFacade();
        return this.request.getRemoteAddr();
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteHost() {
        checkFacade();
        return this.request.getRemoteHost();
    }

    @Override // javax.servlet.ServletRequest
    public void setAttribute(String name, Object o) throws IOException {
        checkFacade();
        this.request.setAttribute(name, o);
    }

    @Override // javax.servlet.ServletRequest
    public void removeAttribute(String name) {
        checkFacade();
        this.request.removeAttribute(name);
    }

    @Override // javax.servlet.ServletRequest
    public Locale getLocale() {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (Locale) AccessController.doPrivileged(new GetLocalePrivilegedAction());
        }
        return this.request.getLocale();
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<Locale> getLocales() {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (Enumeration) AccessController.doPrivileged(new GetLocalesPrivilegedAction());
        }
        return this.request.getLocales();
    }

    @Override // javax.servlet.ServletRequest
    public boolean isSecure() {
        checkFacade();
        return this.request.isSecure();
    }

    @Override // javax.servlet.ServletRequest
    public RequestDispatcher getRequestDispatcher(String path) {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (RequestDispatcher) AccessController.doPrivileged(new GetRequestDispatcherPrivilegedAction(path));
        }
        return this.request.getRequestDispatcher(path);
    }

    @Override // javax.servlet.ServletRequest
    public String getRealPath(String path) {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("requestFacade.nullRequest"));
        }
        return this.request.getRealPath(path);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getAuthType() {
        checkFacade();
        return this.request.getAuthType();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Cookie[] getCookies() {
        Cookie[] ret;
        checkFacade();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            ret = (Cookie[]) AccessController.doPrivileged(new GetCookiesPrivilegedAction());
            if (ret != null) {
                ret = (Cookie[]) ret.clone();
            }
        } else {
            ret = this.request.getCookies();
        }
        return ret;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public long getDateHeader(String name) {
        checkFacade();
        return this.request.getDateHeader(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getHeader(String name) {
        checkFacade();
        return this.request.getHeader(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaders(String name) {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (Enumeration) AccessController.doPrivileged(new GetHeadersPrivilegedAction(name));
        }
        return this.request.getHeaders(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaderNames() {
        checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return (Enumeration) AccessController.doPrivileged(new GetHeaderNamesPrivilegedAction());
        }
        return this.request.getHeaderNames();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public int getIntHeader(String name) {
        checkFacade();
        return this.request.getIntHeader(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpServletMapping getHttpServletMapping() {
        checkFacade();
        return this.request.getHttpServletMapping();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getMethod() {
        checkFacade();
        return this.request.getMethod();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathInfo() {
        checkFacade();
        return this.request.getPathInfo();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathTranslated() {
        checkFacade();
        return this.request.getPathTranslated();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getContextPath() {
        checkFacade();
        return this.request.getContextPath();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getQueryString() {
        checkFacade();
        return this.request.getQueryString();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRemoteUser() {
        checkFacade();
        return this.request.getRemoteUser();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isUserInRole(String role) {
        checkFacade();
        return this.request.isUserInRole(role);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Principal getUserPrincipal() {
        checkFacade();
        return this.request.getUserPrincipal();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestedSessionId() {
        checkFacade();
        return this.request.getRequestedSessionId();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestURI() {
        checkFacade();
        return this.request.getRequestURI();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public StringBuffer getRequestURL() {
        checkFacade();
        return this.request.getRequestURL();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getServletPath() {
        checkFacade();
        return this.request.getServletPath();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession(boolean create) {
        checkFacade();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (HttpSession) AccessController.doPrivileged(new GetSessionPrivilegedAction(create));
        }
        return this.request.getSession(create);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession() {
        checkFacade();
        return getSession(true);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String changeSessionId() {
        checkFacade();
        return this.request.changeSessionId();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdValid() {
        checkFacade();
        return this.request.isRequestedSessionIdValid();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromCookie() {
        checkFacade();
        return this.request.isRequestedSessionIdFromCookie();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromURL() {
        checkFacade();
        return this.request.isRequestedSessionIdFromURL();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromUrl() {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("requestFacade.nullRequest"));
        }
        return this.request.isRequestedSessionIdFromURL();
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalAddr() {
        checkFacade();
        return this.request.getLocalAddr();
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalName() {
        checkFacade();
        return this.request.getLocalName();
    }

    @Override // javax.servlet.ServletRequest
    public int getLocalPort() {
        checkFacade();
        return this.request.getLocalPort();
    }

    @Override // javax.servlet.ServletRequest
    public int getRemotePort() {
        checkFacade();
        return this.request.getRemotePort();
    }

    @Override // javax.servlet.ServletRequest
    public ServletContext getServletContext() {
        checkFacade();
        return this.request.getServletContext();
    }

    @Override // javax.servlet.ServletRequest
    public AsyncContext startAsync() throws IllegalStateException {
        checkFacade();
        return this.request.startAsync();
    }

    @Override // javax.servlet.ServletRequest
    public AsyncContext startAsync(ServletRequest request, ServletResponse response) throws IllegalStateException {
        checkFacade();
        return this.request.startAsync(request, response);
    }

    @Override // javax.servlet.ServletRequest
    public boolean isAsyncStarted() {
        checkFacade();
        return this.request.isAsyncStarted();
    }

    @Override // javax.servlet.ServletRequest
    public boolean isAsyncSupported() {
        checkFacade();
        return this.request.isAsyncSupported();
    }

    @Override // javax.servlet.ServletRequest
    public AsyncContext getAsyncContext() {
        checkFacade();
        return this.request.getAsyncContext();
    }

    @Override // javax.servlet.ServletRequest
    public DispatcherType getDispatcherType() {
        checkFacade();
        return this.request.getDispatcherType();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean authenticate(HttpServletResponse response) throws ServletException, IOException {
        checkFacade();
        return this.request.authenticate(response);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public void login(String username, String password) throws ServletException {
        checkFacade();
        this.request.login(username, password);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public void logout() throws ServletException {
        checkFacade();
        this.request.logout();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Collection<Part> getParts() throws IllegalStateException, ServletException, IOException {
        checkFacade();
        return this.request.getParts();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Part getPart(String name) throws IllegalStateException, ServletException, IOException {
        checkFacade();
        return this.request.getPart(name);
    }

    public boolean getAllowTrace() {
        checkFacade();
        return this.request.getConnector().getAllowTrace();
    }

    @Override // javax.servlet.ServletRequest
    public long getContentLengthLong() {
        checkFacade();
        return this.request.getContentLengthLong();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> cls) throws ServletException, IOException {
        checkFacade();
        return (T) this.request.upgrade(cls);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public PushBuilder newPushBuilder() {
        checkFacade();
        return this.request.newPushBuilder();
    }

    public PushBuilder newPushBuilder(HttpServletRequest request) {
        checkFacade();
        return this.request.newPushBuilder(request);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isTrailerFieldsReady() {
        checkFacade();
        return this.request.isTrailerFieldsReady();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Map<String, String> getTrailerFields() {
        checkFacade();
        return this.request.getTrailerFields();
    }

    private void checkFacade() {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("requestFacade.nullRequest"));
        }
    }
}
