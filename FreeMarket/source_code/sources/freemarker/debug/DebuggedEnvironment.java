package freemarker.debug;

import java.rmi.RemoteException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/DebuggedEnvironment.class */
public interface DebuggedEnvironment extends DebugModel {
    void resume() throws RemoteException;

    void stop() throws RemoteException;

    long getId() throws RemoteException;
}
