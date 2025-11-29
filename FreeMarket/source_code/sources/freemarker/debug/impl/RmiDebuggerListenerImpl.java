package freemarker.debug.impl;

import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;
import freemarker.log.Logger;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/impl/RmiDebuggerListenerImpl.class */
public class RmiDebuggerListenerImpl extends UnicastRemoteObject implements DebuggerListener, Unreferenced {
    private static final Logger LOG = Logger.getLogger("freemarker.debug.client");
    private static final long serialVersionUID = 1;
    private final DebuggerListener listener;

    public void unreferenced() {
        try {
            UnicastRemoteObject.unexportObject(this, false);
        } catch (NoSuchObjectException e) {
            LOG.warn("Failed to unexport RMI debugger listener", e);
        }
    }

    public RmiDebuggerListenerImpl(DebuggerListener listener) throws RemoteException {
        this.listener = listener;
    }

    @Override // freemarker.debug.DebuggerListener
    public void environmentSuspended(EnvironmentSuspendedEvent e) throws RemoteException {
        this.listener.environmentSuspended(e);
    }
}
