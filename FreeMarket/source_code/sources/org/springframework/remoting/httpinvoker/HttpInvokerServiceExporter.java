package org.springframework.remoting.httpinvoker;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/remoting/httpinvoker/HttpInvokerServiceExporter.class */
public class HttpInvokerServiceExporter extends RemoteInvocationSerializingExporter implements HttpRequestHandler {
    @Override // org.springframework.web.HttpRequestHandler
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RemoteInvocation invocation = readRemoteInvocation(request);
            RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
            writeRemoteInvocationResult(request, response, result);
        } catch (ClassNotFoundException ex) {
            throw new NestedServletException("Class not found during deserialization", ex);
        }
    }

    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request) throws IOException, ClassNotFoundException {
        return readRemoteInvocation(request, request.getInputStream());
    }

    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = createObjectInputStream(decorateInputStream(request, is));
        Throwable th = null;
        try {
            RemoteInvocation remoteInvocationDoReadRemoteInvocation = doReadRemoteInvocation(ois);
            if (ois != null) {
                if (0 != 0) {
                    try {
                        ois.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    ois.close();
                }
            }
            return remoteInvocationDoReadRemoteInvocation;
        } catch (Throwable th3) {
            if (ois != null) {
                if (0 != 0) {
                    try {
                        ois.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    ois.close();
                }
            }
            throw th3;
        }
    }

    protected InputStream decorateInputStream(HttpServletRequest request, InputStream is) throws IOException {
        return is;
    }

    protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result) throws IOException {
        response.setContentType(getContentType());
        writeRemoteInvocationResult(request, response, result, response.getOutputStream());
    }

    protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result, OutputStream os) throws IOException {
        ObjectOutputStream oos = createObjectOutputStream(new FlushGuardedOutputStream(decorateOutputStream(request, response, os)));
        Throwable th = null;
        try {
            try {
                doWriteRemoteInvocationResult(result, oos);
                if (oos != null) {
                    if (0 != 0) {
                        try {
                            oos.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    oos.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (oos != null) {
                if (th != null) {
                    try {
                        oos.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    oos.close();
                }
            }
            throw th4;
        }
    }

    protected OutputStream decorateOutputStream(HttpServletRequest request, HttpServletResponse response, OutputStream os) throws IOException {
        return os;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/remoting/httpinvoker/HttpInvokerServiceExporter$FlushGuardedOutputStream.class */
    private static class FlushGuardedOutputStream extends FilterOutputStream {
        public FlushGuardedOutputStream(OutputStream out) {
            super(out);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
        }
    }
}
