package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.springframework.lang.Nullable;
import org.springframework.remoting.support.RemoteInvocation;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/remoting/rmi/RmiInvocationHandler.class */
public interface RmiInvocationHandler extends Remote {
    @Nullable
    String getTargetInterfaceName() throws RemoteException;

    @Nullable
    Object invoke(RemoteInvocation invocation) throws IllegalAccessException, NoSuchMethodException, RemoteException, InvocationTargetException;
}
