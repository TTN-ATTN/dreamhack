package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/ejb/access/LocalSlsbInvokerInterceptor.class */
public class LocalSlsbInvokerInterceptor extends AbstractSlsbInvokerInterceptor {
    private volatile boolean homeAsComponent;

    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    @Nullable
    public Object invokeInContext(MethodInvocation invocation) throws Throwable {
        Object ejb = null;
        try {
            try {
                try {
                    Object ejb2 = getSessionBeanInstance();
                    Method method = invocation.getMethod();
                    if (method.getDeclaringClass().isInstance(ejb2)) {
                        Object objInvoke = method.invoke(ejb2, invocation.getArguments());
                        if (ejb2 instanceof EJBLocalObject) {
                            releaseSessionBeanInstance((EJBLocalObject) ejb2);
                        }
                        return objInvoke;
                    }
                    Method ejbMethod = ejb2.getClass().getMethod(method.getName(), method.getParameterTypes());
                    Object objInvoke2 = ejbMethod.invoke(ejb2, invocation.getArguments());
                    if (ejb2 instanceof EJBLocalObject) {
                        releaseSessionBeanInstance((EJBLocalObject) ejb2);
                    }
                    return objInvoke2;
                } catch (InvocationTargetException ex) {
                    Throwable targetEx = ex.getTargetException();
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Method of local EJB [" + getJndiName() + "] threw exception", targetEx);
                    }
                    if (targetEx instanceof CreateException) {
                        throw new EjbAccessException("Could not create local EJB [" + getJndiName() + "]", targetEx);
                    }
                    throw targetEx;
                }
            } catch (NamingException ex2) {
                throw new EjbAccessException("Failed to locate local EJB [" + getJndiName() + "]", ex2);
            } catch (IllegalAccessException ex3) {
                throw new EjbAccessException("Could not access method [" + invocation.getMethod().getName() + "] of local EJB [" + getJndiName() + "]", ex3);
            }
        } catch (Throwable th) {
            if (ejb instanceof EJBLocalObject) {
                releaseSessionBeanInstance((EJBLocalObject) null);
            }
            throw th;
        }
    }

    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    protected Method getCreateMethod(Object home) throws EjbAccessException {
        if (this.homeAsComponent) {
            return null;
        }
        if (!(home instanceof EJBLocalHome)) {
            this.homeAsComponent = true;
            return null;
        }
        return super.getCreateMethod(home);
    }

    protected Object getSessionBeanInstance() throws NamingException, InvocationTargetException {
        return newSessionBeanInstance();
    }

    protected void releaseSessionBeanInstance(EJBLocalObject ejb) {
        removeSessionBeanInstance(ejb);
    }

    protected Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Trying to create reference to local EJB");
        }
        Object ejbInstance = create();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Obtained reference to local EJB: " + ejbInstance);
        }
        return ejbInstance;
    }

    protected void removeSessionBeanInstance(@Nullable EJBLocalObject ejb) {
        if (ejb != null && !this.homeAsComponent) {
            try {
                ejb.remove();
            } catch (Throwable ex) {
                this.logger.warn("Could not invoke 'remove' on local EJB proxy", ex);
            }
        }
    }
}
