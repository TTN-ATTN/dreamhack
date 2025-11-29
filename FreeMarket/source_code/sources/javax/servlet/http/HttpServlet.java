package javax.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import org.springframework.http.HttpHeaders;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpServlet.class */
public abstract class HttpServlet extends GenericServlet {
    private static final long serialVersionUID = 1;
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private static final List<String> SENSITIVE_HTTP_HEADERS = Arrays.asList("authorization", "cookie", "x-forwarded", "forwarded", "proxy-authorization");

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_get_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    protected long getLastModified(HttpServletRequest req) {
        return -1L;
    }

    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (DispatcherType.INCLUDE.equals(req.getDispatcherType())) {
            doGet(req, resp);
            return;
        }
        NoBodyResponse response = new NoBodyResponse(resp);
        doGet(req, response);
        if (req.isAsyncStarted()) {
            req.getAsyncContext().addListener(new NoBodyAsyncContextListener(response));
        } else {
            response.setContentLength();
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_post_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_put_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_delete_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    private void sendMethodNotAllowed(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String protocol = req.getProtocol();
        if (protocol.length() == 0 || protocol.endsWith("0.9") || protocol.endsWith("1.0")) {
            resp.sendError(400, msg);
        } else {
            resp.sendError(405, msg);
        }
    }

    private static Method[] getAllDeclaredMethods(Class<?> c) throws SecurityException {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }
        return thisMethods;
    }

    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
        Method[] methods = getAllDeclaredMethods(getClass());
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        boolean ALLOW_TRACE = true;
        try {
            Class<?> clazz = Class.forName("org.apache.catalina.connector.RequestFacade");
            Method getAllowTrace = clazz.getMethod("getAllowTrace", (Class[]) null);
            ALLOW_TRACE = ((Boolean) getAllowTrace.invoke(req, (Object[]) null)).booleanValue();
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
        }
        for (Method m : methods) {
            if (m.getName().equals("doGet")) {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            if (m.getName().equals("doPost")) {
                ALLOW_POST = true;
            }
            if (m.getName().equals("doPut")) {
                ALLOW_PUT = true;
            }
            if (m.getName().equals("doDelete")) {
                ALLOW_DELETE = true;
            }
        }
        String allow = null;
        if (ALLOW_GET) {
            allow = "GET";
        }
        if (ALLOW_HEAD) {
            if (allow == null) {
                allow = "HEAD";
            } else {
                allow = allow + ", HEAD";
            }
        }
        if (ALLOW_POST) {
            if (allow == null) {
                allow = "POST";
            } else {
                allow = allow + ", POST";
            }
        }
        if (ALLOW_PUT) {
            if (allow == null) {
                allow = METHOD_PUT;
            } else {
                allow = allow + ", PUT";
            }
        }
        if (ALLOW_DELETE) {
            if (allow == null) {
                allow = METHOD_DELETE;
            } else {
                allow = allow + ", DELETE";
            }
        }
        if (ALLOW_TRACE) {
            if (allow == null) {
                allow = METHOD_TRACE;
            } else {
                allow = allow + ", TRACE";
            }
        }
        if (1 != 0) {
            if (allow == null) {
                allow = METHOD_OPTIONS;
            } else {
                allow = allow + ", OPTIONS";
            }
        }
        resp.setHeader(HttpHeaders.ALLOW, allow);
    }

    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI()).append(' ').append(req.getProtocol());
        Enumeration<String> reqHeaderNames = req.getHeaderNames();
        while (reqHeaderNames.hasMoreElements()) {
            String headerName = reqHeaderNames.nextElement();
            if (!isSensitiveHeader(headerName)) {
                Enumeration<String> headerValues = req.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    buffer.append("\r\n").append(headerName).append(": ").append(headerValue);
                }
            }
        }
        buffer.append("\r\n");
        int responseLength = buffer.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        ServletOutputStream out = resp.getOutputStream();
        out.print(buffer.toString());
        out.close();
    }

    private boolean isSensitiveHeader(String headerName) {
        String lcHeaderName = headerName.toLowerCase(Locale.ENGLISH);
        for (String sensitiveHeaderName : SENSITIVE_HTTP_HEADERS) {
            if (lcHeaderName.startsWith(sensitiveHeaderName)) {
                return true;
            }
        }
        return false;
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, NoSuchMethodException, IOException, SecurityException, ClassNotFoundException {
        long ifModifiedSince;
        String method = req.getMethod();
        if (method.equals("GET")) {
            long lastModified = getLastModified(req);
            if (lastModified == -1) {
                doGet(req, resp);
                return;
            }
            try {
                ifModifiedSince = req.getDateHeader("If-Modified-Since");
            } catch (IllegalArgumentException e) {
                ifModifiedSince = -1;
            }
            if (ifModifiedSince < (lastModified / 1000) * 1000) {
                maybeSetLastModified(resp, lastModified);
                doGet(req, resp);
                return;
            } else {
                resp.setStatus(304);
                return;
            }
        }
        if (method.equals("HEAD")) {
            maybeSetLastModified(resp, getLastModified(req));
            doHead(req, resp);
            return;
        }
        if (method.equals("POST")) {
            doPost(req, resp);
            return;
        }
        if (method.equals(METHOD_PUT)) {
            doPut(req, resp);
            return;
        }
        if (method.equals(METHOD_DELETE)) {
            doDelete(req, resp);
            return;
        }
        if (method.equals(METHOD_OPTIONS)) {
            doOptions(req, resp);
        } else {
            if (method.equals(METHOD_TRACE)) {
                doTrace(req, resp);
                return;
            }
            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = {method};
            resp.sendError(501, MessageFormat.format(errMsg, errArgs));
        }
    }

    private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
        if (!resp.containsHeader("Last-Modified") && lastModified >= 0) {
            resp.setDateHeader("Last-Modified", lastModified);
        }
    }

    @Override // javax.servlet.GenericServlet, javax.servlet.Servlet
    public void service(ServletRequest req, ServletResponse res) throws ServletException, NoSuchMethodException, IOException, SecurityException, ClassNotFoundException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            service(request, response);
        } catch (ClassCastException e) {
            throw new ServletException(lStrings.getString("http.non_http"));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpServlet$NoBodyResponse.class */
    private static class NoBodyResponse extends HttpServletResponseWrapper {
        private final NoBodyOutputStream noBodyOutputStream;
        private ServletOutputStream originalOutputStream;
        private NoBodyPrintWriter noBodyWriter;
        private boolean didSetContentLength;

        private NoBodyResponse(HttpServletResponse r) {
            super(r);
            this.noBodyOutputStream = new NoBodyOutputStream(this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setContentLength() {
            if (!this.didSetContentLength) {
                if (this.noBodyWriter != null) {
                    this.noBodyWriter.flush();
                }
                super.setContentLengthLong(this.noBodyOutputStream.getWrittenByteCount());
            }
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public void setContentLength(int len) {
            super.setContentLength(len);
            this.didSetContentLength = true;
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public void setContentLengthLong(long len) {
            super.setContentLengthLong(len);
            this.didSetContentLength = true;
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void setHeader(String name, String value) {
            super.setHeader(name, value);
            checkHeader(name);
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void addHeader(String name, String value) {
            super.addHeader(name, value);
            checkHeader(name);
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void setIntHeader(String name, int value) {
            super.setIntHeader(name, value);
            checkHeader(name);
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void addIntHeader(String name, int value) {
            super.addIntHeader(name, value);
            checkHeader(name);
        }

        private void checkHeader(String name) {
            if ("content-length".equalsIgnoreCase(name)) {
                this.didSetContentLength = true;
            }
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public ServletOutputStream getOutputStream() throws IOException {
            this.originalOutputStream = getResponse().getOutputStream();
            return this.noBodyOutputStream;
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (this.noBodyWriter == null) {
                this.noBodyWriter = new NoBodyPrintWriter(this.noBodyOutputStream, getCharacterEncoding());
            }
            return this.noBodyWriter;
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public void reset() {
            super.reset();
            resetBuffer();
            this.originalOutputStream = null;
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public void resetBuffer() {
            this.noBodyOutputStream.resetBuffer();
            if (this.noBodyWriter == null) {
                return;
            }
            this.noBodyWriter.resetBuffer();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpServlet$NoBodyOutputStream.class */
    private static class NoBodyOutputStream extends ServletOutputStream {
        private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
        private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
        private final NoBodyResponse response;
        private boolean flushed;
        private long writtenByteCount;

        private NoBodyOutputStream(NoBodyResponse response) {
            this.flushed = false;
            this.writtenByteCount = 0L;
            this.response = response;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public long getWrittenByteCount() {
            return this.writtenByteCount;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.writtenByteCount += HttpServlet.serialVersionUID;
            checkCommit();
        }

        @Override // java.io.OutputStream
        public void write(byte[] buf, int offset, int len) throws IOException {
            if (buf == null) {
                throw new NullPointerException(lStrings.getString("err.io.nullArray"));
            }
            if (offset < 0 || len < 0 || offset + len > buf.length) {
                String msg = lStrings.getString("err.io.indexOutOfBounds");
                Object[] msgArgs = {Integer.valueOf(offset), Integer.valueOf(len), Integer.valueOf(buf.length)};
                throw new IndexOutOfBoundsException(MessageFormat.format(msg, msgArgs));
            }
            this.writtenByteCount += len;
            checkCommit();
        }

        @Override // javax.servlet.ServletOutputStream
        public boolean isReady() {
            return true;
        }

        @Override // javax.servlet.ServletOutputStream
        public void setWriteListener(WriteListener listener) {
            this.response.originalOutputStream.setWriteListener(listener);
        }

        private void checkCommit() throws IOException {
            if (!this.flushed && this.writtenByteCount > this.response.getBufferSize()) {
                this.response.flushBuffer();
                this.flushed = true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetBuffer() {
            if (this.flushed) {
                throw new IllegalStateException(lStrings.getString("err.state.commit"));
            }
            this.writtenByteCount = 0L;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpServlet$NoBodyPrintWriter.class */
    private static class NoBodyPrintWriter extends PrintWriter {
        private final NoBodyOutputStream out;
        private final String encoding;
        private PrintWriter pw;

        NoBodyPrintWriter(NoBodyOutputStream out, String encoding) throws UnsupportedEncodingException {
            super(out);
            this.out = out;
            this.encoding = encoding;
            Writer osw = new OutputStreamWriter(out, encoding);
            this.pw = new PrintWriter(osw);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetBuffer() {
            this.out.resetBuffer();
            Writer osw = null;
            try {
                osw = new OutputStreamWriter(this.out, this.encoding);
            } catch (UnsupportedEncodingException e) {
            }
            this.pw = new PrintWriter(osw);
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Flushable
        public void flush() {
            this.pw.flush();
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.pw.close();
        }

        @Override // java.io.PrintWriter
        public boolean checkError() {
            return this.pw.checkError();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(int c) {
            this.pw.write(c);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf, int off, int len) {
            this.pw.write(buf, off, len);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf) {
            this.pw.write(buf);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s, int off, int len) {
            this.pw.write(s, off, len);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s) {
            this.pw.write(s);
        }

        @Override // java.io.PrintWriter
        public void print(boolean b) {
            this.pw.print(b);
        }

        @Override // java.io.PrintWriter
        public void print(char c) {
            this.pw.print(c);
        }

        @Override // java.io.PrintWriter
        public void print(int i) {
            this.pw.print(i);
        }

        @Override // java.io.PrintWriter
        public void print(long l) {
            this.pw.print(l);
        }

        @Override // java.io.PrintWriter
        public void print(float f) {
            this.pw.print(f);
        }

        @Override // java.io.PrintWriter
        public void print(double d) {
            this.pw.print(d);
        }

        @Override // java.io.PrintWriter
        public void print(char[] s) {
            this.pw.print(s);
        }

        @Override // java.io.PrintWriter
        public void print(String s) {
            this.pw.print(s);
        }

        @Override // java.io.PrintWriter
        public void print(Object obj) {
            this.pw.print(obj);
        }

        @Override // java.io.PrintWriter
        public void println() {
            this.pw.println();
        }

        @Override // java.io.PrintWriter
        public void println(boolean x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(char x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(int x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(long x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(float x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(double x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(char[] x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(String x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(Object x) {
            this.pw.println(x);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpServlet$NoBodyAsyncContextListener.class */
    private static class NoBodyAsyncContextListener implements AsyncListener {
        private final NoBodyResponse noBodyResponse;

        NoBodyAsyncContextListener(NoBodyResponse noBodyResponse) {
            this.noBodyResponse = noBodyResponse;
        }

        @Override // javax.servlet.AsyncListener
        public void onComplete(AsyncEvent event) throws IOException {
            this.noBodyResponse.setContentLength();
        }

        @Override // javax.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override // javax.servlet.AsyncListener
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override // javax.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }
}
