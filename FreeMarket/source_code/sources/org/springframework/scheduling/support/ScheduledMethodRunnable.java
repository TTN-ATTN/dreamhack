package org.springframework.scheduling.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/support/ScheduledMethodRunnable.class */
public class ScheduledMethodRunnable implements Runnable {
    private final Object target;
    private final Method method;

    public ScheduledMethodRunnable(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public ScheduledMethodRunnable(Object target, String methodName) throws NoSuchMethodException {
        this.target = target;
        this.method = target.getClass().getMethod(methodName, new Class[0]);
    }

    public Object getTarget() {
        return this.target;
    }

    public Method getMethod() {
        return this.method;
    }

    @Override // java.lang.Runnable
    public void run() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(this.target, new Object[0]);
        } catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        } catch (InvocationTargetException ex2) {
            ReflectionUtils.rethrowRuntimeException(ex2.getTargetException());
        }
    }

    public String toString() {
        return this.method.getDeclaringClass().getName() + "." + this.method.getName();
    }
}
