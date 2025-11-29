package freemarker.debug.impl;

import freemarker.core.DebugBreak;
import freemarker.core.Environment;
import freemarker.core.TemplateElement;
import freemarker.core._CoreAPI;
import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;
import freemarker.template.Template;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/impl/RmiDebuggerService.class */
class RmiDebuggerService extends DebuggerService {
    private final Map templateDebugInfos = new HashMap();
    private final HashSet suspendedEnvironments = new HashSet();
    private final Map listeners = new HashMap();
    private final ReferenceQueue refQueue = new ReferenceQueue();
    private final RmiDebuggerImpl debugger;
    private DebuggerServer server;

    RmiDebuggerService() {
        try {
            this.debugger = new RmiDebuggerImpl(this);
            this.server = new DebuggerServer(RemoteObject.toStub(this.debugger));
            this.server.start();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override // freemarker.debug.impl.DebuggerService
    List getBreakpointsSpi(String templateName) {
        List list;
        synchronized (this.templateDebugInfos) {
            TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
            list = tdi == null ? Collections.EMPTY_LIST : tdi.breakpoints;
        }
        return list;
    }

    List getBreakpointsSpi() {
        List sumlist = new ArrayList();
        synchronized (this.templateDebugInfos) {
            Iterator iter = this.templateDebugInfos.values().iterator();
            while (iter.hasNext()) {
                sumlist.addAll(((TemplateDebugInfo) iter.next()).breakpoints);
            }
        }
        Collections.sort(sumlist);
        return sumlist;
    }

    @Override // freemarker.debug.impl.DebuggerService
    @SuppressFBWarnings(value = {"UW_UNCOND_WAIT", "WA_NOT_IN_LOOP"}, justification = "Will have to be re-desigend; postponed.")
    boolean suspendEnvironmentSpi(Environment env, String templateName, int line) throws RemoteException {
        RmiDebuggedEnvironmentImpl denv = (RmiDebuggedEnvironmentImpl) RmiDebuggedEnvironmentImpl.getCachedWrapperFor(env);
        synchronized (this.suspendedEnvironments) {
            this.suspendedEnvironments.add(denv);
        }
        try {
            EnvironmentSuspendedEvent breakpointEvent = new EnvironmentSuspendedEvent(this, templateName, line, denv);
            synchronized (this.listeners) {
                for (DebuggerListener listener : this.listeners.values()) {
                    listener.environmentSuspended(breakpointEvent);
                }
            }
            synchronized (denv) {
                try {
                    denv.wait();
                } catch (InterruptedException e) {
                }
            }
            boolean zIsStopped = denv.isStopped();
            synchronized (this.suspendedEnvironments) {
                this.suspendedEnvironments.remove(denv);
            }
            return zIsStopped;
        } catch (Throwable th) {
            synchronized (this.suspendedEnvironments) {
                this.suspendedEnvironments.remove(denv);
                throw th;
            }
        }
    }

    @Override // freemarker.debug.impl.DebuggerService
    void registerTemplateSpi(Template template) {
        String templateName = template.getName();
        synchronized (this.templateDebugInfos) {
            TemplateDebugInfo tdi = createTemplateDebugInfo(templateName);
            tdi.templates.add(new TemplateReference(templateName, template, this.refQueue));
            for (Breakpoint breakpoint : tdi.breakpoints) {
                insertDebugBreak(template, breakpoint);
            }
        }
    }

    Collection getSuspendedEnvironments() {
        return (Collection) this.suspendedEnvironments.clone();
    }

    Object addDebuggerListener(DebuggerListener listener) {
        Long lValueOf;
        synchronized (this.listeners) {
            lValueOf = Long.valueOf(System.currentTimeMillis());
            this.listeners.put(lValueOf, listener);
        }
        return lValueOf;
    }

    void removeDebuggerListener(Object id) {
        synchronized (this.listeners) {
            this.listeners.remove(id);
        }
    }

    void addBreakpoint(Breakpoint breakpoint) {
        String templateName = breakpoint.getTemplateName();
        synchronized (this.templateDebugInfos) {
            TemplateDebugInfo tdi = createTemplateDebugInfo(templateName);
            List breakpoints = tdi.breakpoints;
            int pos = Collections.binarySearch(breakpoints, breakpoint);
            if (pos < 0) {
                breakpoints.add((-pos) - 1, breakpoint);
                Iterator iter = tdi.templates.iterator();
                while (iter.hasNext()) {
                    TemplateReference ref = (TemplateReference) iter.next();
                    Template t = ref.getTemplate();
                    if (t == null) {
                        iter.remove();
                    } else {
                        insertDebugBreak(t, breakpoint);
                    }
                }
            }
        }
    }

    private static void insertDebugBreak(Template t, Breakpoint breakpoint) {
        TemplateElement te = findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
        if (te == null) {
            return;
        }
        TemplateElement parent = _CoreAPI.getParentElement(te);
        DebugBreak db = new DebugBreak(te);
        parent.setChildAt(parent.getIndex(te), db);
    }

    private static TemplateElement findTemplateElement(TemplateElement te, int line) {
        if (te.getBeginLine() > line || te.getEndLine() < line) {
            return null;
        }
        List childMatches = new ArrayList();
        Enumeration children = te.children();
        while (children.hasMoreElements()) {
            TemplateElement child = (TemplateElement) children.nextElement();
            TemplateElement childmatch = findTemplateElement(child, line);
            if (childmatch != null) {
                childMatches.add(childmatch);
            }
        }
        TemplateElement bestMatch = null;
        int i = 0;
        while (true) {
            if (i >= childMatches.size()) {
                break;
            }
            TemplateElement e = (TemplateElement) childMatches.get(i);
            if (bestMatch == null) {
                bestMatch = e;
            }
            if (e.getBeginLine() == line && e.getEndLine() > line) {
                bestMatch = e;
            }
            if (e.getBeginLine() != e.getEndLine() || e.getBeginLine() != line) {
                i++;
            } else {
                bestMatch = e;
                break;
            }
        }
        if (bestMatch != null) {
            return bestMatch;
        }
        return te;
    }

    private TemplateDebugInfo findTemplateDebugInfo(String templateName) {
        processRefQueue();
        return (TemplateDebugInfo) this.templateDebugInfos.get(templateName);
    }

    private TemplateDebugInfo createTemplateDebugInfo(String templateName) {
        TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
        if (tdi == null) {
            tdi = new TemplateDebugInfo();
            this.templateDebugInfos.put(templateName, tdi);
        }
        return tdi;
    }

    void removeBreakpoint(Breakpoint breakpoint) {
        String templateName = breakpoint.getTemplateName();
        synchronized (this.templateDebugInfos) {
            TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
            if (tdi != null) {
                List breakpoints = tdi.breakpoints;
                int pos = Collections.binarySearch(breakpoints, breakpoint);
                if (pos >= 0) {
                    breakpoints.remove(pos);
                    Iterator iter = tdi.templates.iterator();
                    while (iter.hasNext()) {
                        TemplateReference ref = (TemplateReference) iter.next();
                        Template t = ref.getTemplate();
                        if (t == null) {
                            iter.remove();
                        } else {
                            removeDebugBreak(t, breakpoint);
                        }
                    }
                }
                if (tdi.isEmpty()) {
                    this.templateDebugInfos.remove(templateName);
                }
            }
        }
    }

    private void removeDebugBreak(Template t, Breakpoint breakpoint) {
        TemplateElement te = findTemplateElement(t.getRootTreeNode(), breakpoint.getLine());
        if (te == null) {
            return;
        }
        DebugBreak db = null;
        while (true) {
            if (te == null) {
                break;
            }
            if (te instanceof DebugBreak) {
                db = (DebugBreak) te;
                break;
            }
            te = _CoreAPI.getParentElement(te);
        }
        if (db == null) {
            return;
        }
        TemplateElement parent = _CoreAPI.getParentElement(db);
        parent.setChildAt(parent.getIndex(db), _CoreAPI.getChildElement(db, 0));
    }

    void removeBreakpoints(String templateName) {
        synchronized (this.templateDebugInfos) {
            TemplateDebugInfo tdi = findTemplateDebugInfo(templateName);
            if (tdi != null) {
                removeBreakpoints(tdi);
                if (tdi.isEmpty()) {
                    this.templateDebugInfos.remove(templateName);
                }
            }
        }
    }

    void removeBreakpoints() {
        synchronized (this.templateDebugInfos) {
            Iterator iter = this.templateDebugInfos.values().iterator();
            while (iter.hasNext()) {
                TemplateDebugInfo tdi = (TemplateDebugInfo) iter.next();
                removeBreakpoints(tdi);
                if (tdi.isEmpty()) {
                    iter.remove();
                }
            }
        }
    }

    private void removeBreakpoints(TemplateDebugInfo tdi) {
        tdi.breakpoints.clear();
        Iterator iter = tdi.templates.iterator();
        while (iter.hasNext()) {
            TemplateReference ref = (TemplateReference) iter.next();
            Template t = ref.getTemplate();
            if (t == null) {
                iter.remove();
            } else {
                removeDebugBreaks(t.getRootTreeNode());
            }
        }
    }

    private void removeDebugBreaks(TemplateElement te) {
        TemplateElement child;
        int count = te.getChildCount();
        for (int i = 0; i < count; i++) {
            TemplateElement childElement = _CoreAPI.getChildElement(te, i);
            while (true) {
                child = childElement;
                if (child instanceof DebugBreak) {
                    TemplateElement dbchild = _CoreAPI.getChildElement(child, 0);
                    te.setChildAt(i, dbchild);
                    childElement = dbchild;
                }
            }
            removeDebugBreaks(child);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/impl/RmiDebuggerService$TemplateDebugInfo.class */
    private static final class TemplateDebugInfo {
        final List templates;
        final List breakpoints;

        private TemplateDebugInfo() {
            this.templates = new ArrayList();
            this.breakpoints = new ArrayList();
        }

        boolean isEmpty() {
            return this.templates.isEmpty() && this.breakpoints.isEmpty();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/impl/RmiDebuggerService$TemplateReference.class */
    private static final class TemplateReference extends WeakReference {
        final String templateName;

        TemplateReference(String templateName, Template template, ReferenceQueue queue) {
            super(template, queue);
            this.templateName = templateName;
        }

        Template getTemplate() {
            return (Template) get();
        }
    }

    private void processRefQueue() {
        while (true) {
            TemplateReference ref = (TemplateReference) this.refQueue.poll();
            if (ref != null) {
                TemplateDebugInfo tdi = findTemplateDebugInfo(ref.templateName);
                if (tdi != null) {
                    tdi.templates.remove(ref);
                    if (tdi.isEmpty()) {
                        this.templateDebugInfos.remove(ref.templateName);
                    }
                }
            } else {
                return;
            }
        }
    }

    @Override // freemarker.debug.impl.DebuggerService
    void shutdownSpi() throws IOException {
        this.server.stop();
        try {
            UnicastRemoteObject.unexportObject(this.debugger, true);
        } catch (Exception e) {
        }
        RmiDebuggedEnvironmentImpl.cleanup();
    }
}
