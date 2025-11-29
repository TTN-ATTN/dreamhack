package org.apache.catalina.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/ResponseFacade.class */
public class ResponseFacade implements HttpServletResponse {
    protected static final StringManager sm = StringManager.getManager((Class<?>) ResponseFacade.class);
    protected Response response;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/ResponseFacade$SetContentTypePrivilegedAction.class */
    private final class SetContentTypePrivilegedAction implements PrivilegedAction<Void> {
        private final String contentType;

        SetContentTypePrivilegedAction(String contentType) {
            this.contentType = contentType;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Void run() {
            ResponseFacade.this.response.setContentType(this.contentType);
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/ResponseFacade$DateHeaderPrivilegedAction.class */
    private final class DateHeaderPrivilegedAction implements PrivilegedAction<Void> {
        private final String name;
        private final long value;
        private final boolean add;

        DateHeaderPrivilegedAction(String name, long value, boolean add) {
            this.name = name;
            this.value = value;
            this.add = add;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Void run() {
            if (this.add) {
                ResponseFacade.this.response.addDateHeader(this.name, this.value);
                return null;
            }
            ResponseFacade.this.response.setDateHeader(this.name, this.value);
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/connector/ResponseFacade$FlushBufferPrivilegedAction.class */
    private static class FlushBufferPrivilegedAction implements PrivilegedExceptionAction<Void> {
        private final Response response;

        FlushBufferPrivilegedAction(Response response) {
            this.response = response;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws IOException {
            this.response.setAppCommitted(true);
            this.response.flushBuffer();
            return null;
        }
    }

    public ResponseFacade(Response response) {
        this.response = null;
        this.response = response;
    }

    public void clear() {
        this.response = null;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void finish() {
        checkFacade();
        this.response.setSuspended(true);
    }

    public boolean isFinished() {
        checkFacade();
        return this.response.isSuspended();
    }

    public long getContentWritten() {
        checkFacade();
        return this.response.getContentWritten();
    }

    @Override // javax.servlet.ServletResponse
    public String getCharacterEncoding() {
        checkFacade();
        return this.response.getCharacterEncoding();
    }

    @Override // javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        checkFacade();
        ServletOutputStream sos = this.response.getOutputStream();
        if (isFinished()) {
            this.response.setSuspended(true);
        }
        return sos;
    }

    @Override // javax.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        checkFacade();
        PrintWriter writer = this.response.getWriter();
        if (isFinished()) {
            this.response.setSuspended(true);
        }
        return writer;
    }

    @Override // javax.servlet.ServletResponse
    public void setContentLength(int len) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.setContentLength(len);
    }

    @Override // javax.servlet.ServletResponse
    public void setContentLengthLong(long length) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.setContentLengthLong(length);
    }

    @Override // javax.servlet.ServletResponse
    public void setContentType(String type) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            AccessController.doPrivileged(new SetContentTypePrivilegedAction(type));
        } else {
            this.response.setContentType(type);
        }
    }

    @Override // javax.servlet.ServletResponse
    public void setBufferSize(int size) {
        checkCommitted("coyoteResponse.setBufferSize.ise");
        this.response.setBufferSize(size);
    }

    @Override // javax.servlet.ServletResponse
    public int getBufferSize() {
        checkFacade();
        return this.response.getBufferSize();
    }

    @Override // javax.servlet.ServletResponse
    public void flushBuffer() throws PrivilegedActionException, IOException {
        checkFacade();
        if (isFinished()) {
            return;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new FlushBufferPrivilegedAction(this.response));
                return;
            } catch (PrivilegedActionException e) {
                Exception ex = e.getException();
                if (ex instanceof IOException) {
                    throw ((IOException) ex);
                }
                return;
            }
        }
        this.response.setAppCommitted(true);
        this.response.flushBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public void resetBuffer() {
        checkCommitted("coyoteResponse.resetBuffer.ise");
        this.response.resetBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public boolean isCommitted() {
        checkFacade();
        return this.response.isAppCommitted();
    }

    @Override // javax.servlet.ServletResponse
    public void reset() throws IllegalStateException {
        checkCommitted("coyoteResponse.reset.ise");
        this.response.reset();
    }

    @Override // javax.servlet.ServletResponse
    public void setLocale(Locale loc) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.setLocale(loc);
    }

    @Override // javax.servlet.ServletResponse
    public Locale getLocale() {
        checkFacade();
        return this.response.getLocale();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.addCookie(cookie);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public boolean containsHeader(String name) {
        checkFacade();
        return this.response.containsHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeURL(String url) {
        checkFacade();
        return this.response.encodeURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeRedirectURL(String url) {
        checkFacade();
        return this.response.encodeRedirectURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeUrl(String url) {
        checkFacade();
        return this.response.encodeURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeRedirectUrl(String url) {
        checkFacade();
        return this.response.encodeRedirectURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        checkCommitted("coyoteResponse.sendError.ise");
        this.response.setAppCommitted(true);
        this.response.sendError(sc, msg);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        checkCommitted("coyoteResponse.sendError.ise");
        this.response.setAppCommitted(true);
        this.response.sendError(sc);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        checkCommitted("coyoteResponse.sendRedirect.ise");
        this.response.setAppCommitted(true);
        this.response.sendRedirect(location);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long date) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        if (Globals.IS_SECURITY_ENABLED) {
            AccessController.doPrivileged(new DateHeaderPrivilegedAction(name, date, false));
        } else {
            this.response.setDateHeader(name, date);
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long date) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        if (Globals.IS_SECURITY_ENABLED) {
            AccessController.doPrivileged(new DateHeaderPrivilegedAction(name, date, true));
        } else {
            this.response.addDateHeader(name, date);
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.setHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.addHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.setIntHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.addIntHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        checkFacade();
        if (isCommitted()) {
            return;
        }
        this.response.setStatus(sc);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setStatus(int sc, String sm2) {
        if (isCommitted()) {
            return;
        }
        this.response.setStatus(sc, sm2);
    }

    @Override // javax.servlet.ServletResponse
    public String getContentType() {
        checkFacade();
        return this.response.getContentType();
    }

    @Override // javax.servlet.ServletResponse
    public void setCharacterEncoding(String arg0) {
        checkFacade();
        this.response.setCharacterEncoding(arg0);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public int getStatus() {
        checkFacade();
        return this.response.getStatus();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String getHeader(String name) {
        checkFacade();
        return this.response.getHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public Collection<String> getHeaderNames() {
        checkFacade();
        return this.response.getHeaderNames();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public Collection<String> getHeaders(String name) {
        checkFacade();
        return this.response.getHeaders(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        checkFacade();
        this.response.setTrailerFields(supplier);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public Supplier<Map<String, String>> getTrailerFields() {
        checkFacade();
        return this.response.getTrailerFields();
    }

    private void checkFacade() {
        if (this.response == null) {
            throw new IllegalStateException(sm.getString("responseFacade.nullResponse"));
        }
    }

    private void checkCommitted(String messageKey) {
        checkFacade();
        if (isCommitted()) {
            throw new IllegalStateException(sm.getString(messageKey));
        }
    }
}
