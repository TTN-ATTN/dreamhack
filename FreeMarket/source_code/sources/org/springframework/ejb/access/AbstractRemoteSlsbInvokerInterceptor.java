package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/ejb/access/AbstractRemoteSlsbInvokerInterceptor.class */
public abstract class AbstractRemoteSlsbInvokerInterceptor extends AbstractSlsbInvokerInterceptor {
    private boolean refreshHomeOnConnectFailure = false;
    private volatile boolean homeAsComponent;

    @Nullable
    protected abstract Object doInvoke(MethodInvocation invocation) throws Throwable;

    public void setRefreshHomeOnConnectFailure(boolean refreshHomeOnConnectFailure) {
        this.refreshHomeOnConnectFailure = refreshHomeOnConnectFailure;
    }

    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    protected boolean isHomeRefreshable() {
        return this.refreshHomeOnConnectFailure;
    }

    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    protected Method getCreateMethod(Object home) throws EjbAccessException {
        if (this.homeAsComponent) {
            return null;
        }
        if (!(home instanceof EJBHome)) {
            this.homeAsComponent = true;
            return null;
        }
        return super.getCreateMethod(home);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.rmi.RemoteException */
    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    @Nullable
    public Object invokeInContext(MethodInvocation invocation) throws Throwable {
        try {
            return doInvoke(invocation);
        } catch (RemoteException ex) {
            if (isConnectFailure(ex)) {
                return handleRemoteConnectFailure(invocation, ex);
            }
            throw ex;
        } catch (RemoteConnectFailureException ex2) {
            return handleRemoteConnectFailure(invocation, ex2);
        }
    }

    protected boolean isConnectFailure(RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }

    @Nullable
    private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Exception {
        if (this.refreshHomeOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not connect to remote EJB [" + getJndiName() + "] - retrying", ex);
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn("Could not connect to remote EJB [" + getJndiName() + "] - retrying");
            }
            return refreshAndRetry(invocation);
        }
        throw ex;
    }

    @Nullable
    protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
        try {
            refreshHome();
            return doInvoke(invocation);
        } catch (NamingException ex) {
            throw new RemoteLookupFailureException("Failed to locate remote EJB [" + getJndiName() + "]", ex);
        }
    }

    protected Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Trying to create reference to remote EJB");
        }
        Object ejbInstance = create();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Obtained reference to remote EJB: " + ejbInstance);
        }
        return ejbInstance;
    }

    protected void removeSessionBeanInstance(@Nullable EJBObject ejb) {
        if (ejb != null && !this.homeAsComponent) {
            try {
                ejb.remove();
            } catch (Throwable ex) {
                this.logger.warn("Could not invoke 'remove' on remote EJB proxy", ex);
            }
        }
    }
}
