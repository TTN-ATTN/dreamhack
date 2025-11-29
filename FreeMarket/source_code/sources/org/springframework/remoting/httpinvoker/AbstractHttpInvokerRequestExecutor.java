package org.springframework.remoting.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.remoting.rmi.CodebaseAwareObjectInputStream;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/remoting/httpinvoker/AbstractHttpInvokerRequestExecutor.class */
public abstract class AbstractHttpInvokerRequestExecutor implements HttpInvokerRequestExecutor, BeanClassLoaderAware {
    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
    private static final int SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE = 1024;
    protected static final String HTTP_METHOD_POST = "POST";
    protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";
    protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
    protected static final String ENCODING_GZIP = "gzip";
    protected final Log logger = LogFactory.getLog(getClass());
    private String contentType = "application/x-java-serialized-object";
    private boolean acceptGzipEncoding = true;

    @Nullable
    private ClassLoader beanClassLoader;

    protected abstract RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config, ByteArrayOutputStream baos) throws Exception;

    public void setContentType(String contentType) {
        Assert.notNull(contentType, "'contentType' must not be null");
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setAcceptGzipEncoding(boolean acceptGzipEncoding) {
        this.acceptGzipEncoding = acceptGzipEncoding;
    }

    public boolean isAcceptGzipEncoding() {
        return this.acceptGzipEncoding;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Nullable
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override // org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor
    public final RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration config, RemoteInvocation invocation) throws Exception {
        ByteArrayOutputStream baos = getByteArrayOutputStream(invocation);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Sending HTTP invoker request for service at [" + config.getServiceUrl() + "], with size " + baos.size());
        }
        return doExecuteRequest(config, baos);
    }

    protected ByteArrayOutputStream getByteArrayOutputStream(RemoteInvocation invocation) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        writeRemoteInvocation(invocation, baos);
        return baos;
    }

    protected void writeRemoteInvocation(RemoteInvocation invocation, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(decorateOutputStream(os));
        Throwable th = null;
        try {
            doWriteRemoteInvocation(invocation, oos);
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
            if (oos != null) {
                if (0 != 0) {
                    try {
                        oos.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    oos.close();
                }
            }
            throw th3;
        }
    }

    protected OutputStream decorateOutputStream(OutputStream os) throws IOException {
        return os;
    }

    protected void doWriteRemoteInvocation(RemoteInvocation invocation, ObjectOutputStream oos) throws IOException {
        oos.writeObject(invocation);
    }

    protected RemoteInvocationResult readRemoteInvocationResult(InputStream is, @Nullable String codebaseUrl) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = createObjectInputStream(decorateInputStream(is), codebaseUrl);
        Throwable th = null;
        try {
            RemoteInvocationResult remoteInvocationResultDoReadRemoteInvocationResult = doReadRemoteInvocationResult(ois);
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
            return remoteInvocationResultDoReadRemoteInvocationResult;
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

    protected InputStream decorateInputStream(InputStream is) throws IOException {
        return is;
    }

    protected ObjectInputStream createObjectInputStream(InputStream is, @Nullable String codebaseUrl) throws IOException {
        return new CodebaseAwareObjectInputStream(is, getBeanClassLoader(), codebaseUrl);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.rmi.RemoteException */
    protected RemoteInvocationResult doReadRemoteInvocationResult(ObjectInputStream ois) throws ClassNotFoundException, IOException, RemoteException {
        Object obj = ois.readObject();
        if (!(obj instanceof RemoteInvocationResult)) {
            throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocationResult.class.getName() + "]: " + ClassUtils.getDescriptiveType(obj));
        }
        return (RemoteInvocationResult) obj;
    }
}
