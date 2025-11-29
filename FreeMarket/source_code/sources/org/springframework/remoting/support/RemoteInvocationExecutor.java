package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/remoting/support/RemoteInvocationExecutor.class */
public interface RemoteInvocationExecutor {
    Object invoke(RemoteInvocation invocation, Object targetObject) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;
}
