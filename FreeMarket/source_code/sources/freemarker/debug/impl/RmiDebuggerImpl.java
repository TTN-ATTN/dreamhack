package freemarker.debug.impl;

import freemarker.debug.Breakpoint;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/impl/RmiDebuggerImpl.class */
class RmiDebuggerImpl extends UnicastRemoteObject implements Debugger {
    private static final long serialVersionUID = 1;
    private final RmiDebuggerService service;

    protected RmiDebuggerImpl(RmiDebuggerService service) throws RemoteException {
        this.service = service;
    }

    @Override // freemarker.debug.Debugger
    public void addBreakpoint(Breakpoint breakpoint) {
        this.service.addBreakpoint(breakpoint);
    }

    @Override // freemarker.debug.Debugger
    public Object addDebuggerListener(DebuggerListener listener) {
        return this.service.addDebuggerListener(listener);
    }

    @Override // freemarker.debug.Debugger
    public List getBreakpoints() {
        return this.service.getBreakpointsSpi();
    }

    @Override // freemarker.debug.Debugger
    public List getBreakpoints(String templateName) {
        return this.service.getBreakpointsSpi(templateName);
    }

    @Override // freemarker.debug.Debugger
    public Collection getSuspendedEnvironments() {
        return this.service.getSuspendedEnvironments();
    }

    @Override // freemarker.debug.Debugger
    public void removeBreakpoint(Breakpoint breakpoint) {
        this.service.removeBreakpoint(breakpoint);
    }

    @Override // freemarker.debug.Debugger
    public void removeDebuggerListener(Object id) {
        this.service.removeDebuggerListener(id);
    }

    @Override // freemarker.debug.Debugger
    public void removeBreakpoints() {
        this.service.removeBreakpoints();
    }

    @Override // freemarker.debug.Debugger
    public void removeBreakpoints(String templateName) {
        this.service.removeBreakpoints(templateName);
    }
}
