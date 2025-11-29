package org.springframework.remoting.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/remoting/rmi/RemoteInvocationSerializingExporter.class */
public abstract class RemoteInvocationSerializingExporter extends RemoteInvocationBasedExporter implements InitializingBean {
    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
    private String contentType = "application/x-java-serialized-object";
    private boolean acceptProxyClasses = true;
    private Object proxy;

    public void setContentType(String contentType) {
        Assert.notNull(contentType, "'contentType' must not be null");
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setAcceptProxyClasses(boolean acceptProxyClasses) {
        this.acceptProxyClasses = acceptProxyClasses;
    }

    public boolean isAcceptProxyClasses() {
        return this.acceptProxyClasses;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        prepare();
    }

    public void prepare() {
        this.proxy = getProxyForService();
    }

    protected final Object getProxy() {
        if (this.proxy == null) {
            throw new IllegalStateException(ClassUtils.getShortName(getClass()) + " has not been initialized");
        }
        return this.proxy;
    }

    protected ObjectInputStream createObjectInputStream(InputStream is) throws IOException {
        return new CodebaseAwareObjectInputStream(is, getBeanClassLoader(), isAcceptProxyClasses());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.rmi.RemoteException */
    protected RemoteInvocation doReadRemoteInvocation(ObjectInputStream ois) throws ClassNotFoundException, IOException, RemoteException {
        Object obj = ois.readObject();
        if (!(obj instanceof RemoteInvocation)) {
            throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocation.class.getName() + "]: " + ClassUtils.getDescriptiveType(obj));
        }
        return (RemoteInvocation) obj;
    }

    protected ObjectOutputStream createObjectOutputStream(OutputStream os) throws IOException {
        return new ObjectOutputStream(os);
    }

    protected void doWriteRemoteInvocationResult(RemoteInvocationResult result, ObjectOutputStream oos) throws IOException {
        oos.writeObject(result);
    }
}
