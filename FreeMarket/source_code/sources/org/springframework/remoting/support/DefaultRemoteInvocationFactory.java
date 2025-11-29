package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/remoting/support/DefaultRemoteInvocationFactory.class */
public class DefaultRemoteInvocationFactory implements RemoteInvocationFactory {
    @Override // org.springframework.remoting.support.RemoteInvocationFactory
    public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
        return new RemoteInvocation(methodInvocation);
    }
}
