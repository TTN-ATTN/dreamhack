package org.apache.catalina.valves;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.ServletException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.StoreManager;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/valves/PersistentValve.class */
public class PersistentValve extends ValveBase {
    private static final ClassLoader MY_CLASSLOADER = PersistentValve.class.getClassLoader();
    private volatile boolean clBindRequired;
    protected Pattern filter;

    public PersistentValve() {
        super(true);
        this.filter = null;
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Contained
    public void setContainer(Container container) {
        super.setContainer(container);
        if ((container instanceof Engine) || (container instanceof Host)) {
            this.clBindRequired = true;
        } else {
            this.clBindRequired = false;
        }
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws ServletException, IOException {
        Session hsess;
        Store store;
        if (isRequestWithoutSession(request.getDecodedRequestURI())) {
            getNext().invoke(request, response);
            return;
        }
        Context context = request.getContext();
        if (context == null) {
            response.sendError(500, sm.getString("standardHost.noContext"));
            return;
        }
        String sessionId = request.getRequestedSessionId();
        Manager manager = context.getManager();
        if (sessionId != null && (manager instanceof StoreManager) && (store = ((StoreManager) manager).getStore()) != null) {
            Session session = null;
            try {
                session = store.load(sessionId);
            } catch (Exception e) {
                this.container.getLogger().error("deserializeError");
            }
            if (session != null) {
                if (!session.isValid() || isSessionStale(session, System.currentTimeMillis())) {
                    if (this.container.getLogger().isDebugEnabled()) {
                        this.container.getLogger().debug("session swapped in is invalid or expired");
                    }
                    session.expire();
                    store.remove(sessionId);
                } else {
                    session.setManager(manager);
                    manager.add(session);
                    session.access();
                    session.endAccess();
                }
            }
        }
        if (this.container.getLogger().isDebugEnabled()) {
            this.container.getLogger().debug("sessionId: " + sessionId);
        }
        getNext().invoke(request, response);
        if (!request.isAsync()) {
            try {
                hsess = request.getSessionInternal(false);
            } catch (Exception e2) {
                hsess = null;
            }
            String newsessionId = null;
            if (hsess != null) {
                newsessionId = hsess.getIdInternal();
            }
            if (this.container.getLogger().isDebugEnabled()) {
                this.container.getLogger().debug("newsessionId: " + newsessionId);
            }
            if (newsessionId != null) {
                try {
                    bind(context);
                    if (manager instanceof StoreManager) {
                        Session session2 = manager.findSession(newsessionId);
                        Store store2 = ((StoreManager) manager).getStore();
                        boolean stored = false;
                        if (session2 != null) {
                            synchronized (session2) {
                                if (store2 != null) {
                                    if (session2.isValid() && !isSessionStale(session2, System.currentTimeMillis())) {
                                        store2.save(session2);
                                        ((StoreManager) manager).removeSuper(session2);
                                        session2.recycle();
                                        stored = true;
                                    }
                                }
                            }
                        }
                        if (!stored && this.container.getLogger().isDebugEnabled()) {
                            this.container.getLogger().debug("newsessionId store: " + store2 + " session: " + session2 + " valid: " + (session2 == null ? "N/A" : Boolean.toString(session2.isValid())) + " stale: " + isSessionStale(session2, System.currentTimeMillis()));
                        }
                    } else if (this.container.getLogger().isDebugEnabled()) {
                        this.container.getLogger().debug("newsessionId Manager: " + manager);
                    }
                } finally {
                    unbind(context);
                }
            }
        }
    }

    protected boolean isSessionStale(Session session, long timeNow) {
        int maxInactiveInterval;
        if (session != null && (maxInactiveInterval = session.getMaxInactiveInterval()) >= 0) {
            int timeIdle = (int) (session.getIdleTimeInternal() / 1000);
            if (timeIdle >= maxInactiveInterval) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void bind(Context context) {
        if (this.clBindRequired) {
            context.bind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        }
    }

    private void unbind(Context context) {
        if (this.clBindRequired) {
            context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        }
    }

    protected boolean isRequestWithoutSession(String uri) {
        Pattern f = this.filter;
        return f != null && f.matcher(uri).matches();
    }

    public String getFilter() {
        if (this.filter == null) {
            return null;
        }
        return this.filter.toString();
    }

    public void setFilter(String filter) {
        if (filter == null || filter.length() == 0) {
            this.filter = null;
            return;
        }
        try {
            this.filter = Pattern.compile(filter);
        } catch (PatternSyntaxException pse) {
            this.container.getLogger().error(sm.getString("persistentValve.filter.failure", filter), pse);
        }
    }
}
