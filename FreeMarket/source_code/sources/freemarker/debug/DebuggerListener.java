package freemarker.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/DebuggerListener.class */
public interface DebuggerListener extends Remote, EventListener {
    void environmentSuspended(EnvironmentSuspendedEvent environmentSuspendedEvent) throws RemoteException;
}
