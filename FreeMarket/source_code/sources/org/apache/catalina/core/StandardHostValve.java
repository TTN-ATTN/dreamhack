package org.apache.catalina.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.ActionCode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/core/StandardHostValve.class */
final class StandardHostValve extends ValveBase {
    private static final Log log = LogFactory.getLog((Class<?>) StandardHostValve.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) StandardHostValve.class);
    private static final ClassLoader MY_CLASSLOADER = StandardHostValve.class.getClassLoader();
    static final boolean STRICT_SERVLET_COMPLIANCE = Globals.STRICT_SERVLET_COMPLIANCE;
    static final boolean ACCESS_SESSION;

    static {
        String accessSession = System.getProperty("org.apache.catalina.core.StandardHostValve.ACCESS_SESSION");
        if (accessSession == null) {
            ACCESS_SESSION = STRICT_SERVLET_COMPLIANCE;
        } else {
            ACCESS_SESSION = Boolean.parseBoolean(accessSession);
        }
    }

    StandardHostValve() {
        super(true);
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws ServletException, IOException {
        Context context = request.getContext();
        if (context == null) {
            if (response.isError()) {
                return;
            }
            response.sendError(404);
            return;
        }
        if (request.isAsyncSupported()) {
            request.setAsyncSupported(context.getPipeline().isAsyncSupported());
        }
        boolean asyncAtStart = request.isAsync();
        try {
            context.bind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
            if (!asyncAtStart && !context.fireRequestInitEvent(request.getRequest())) {
                if (ACCESS_SESSION) {
                    request.getSession(false);
                }
                context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
                return;
            }
            try {
                if (!response.isErrorReportRequired()) {
                    context.getPipeline().getFirst().invoke(request, response);
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.container.getLogger().error("Exception Processing " + request.getRequestURI(), t);
                if (!response.isErrorReportRequired()) {
                    request.setAttribute("javax.servlet.error.exception", t);
                    throwable(request, response, t);
                }
            }
            response.setSuspended(false);
            Throwable t2 = (Throwable) request.getAttribute("javax.servlet.error.exception");
            if (!context.getState().isAvailable()) {
                if (ACCESS_SESSION) {
                    request.getSession(false);
                }
                context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
                return;
            }
            if (response.isErrorReportRequired()) {
                AtomicBoolean result = new AtomicBoolean(false);
                response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, result);
                if (result.get()) {
                    if (t2 != null) {
                        throwable(request, response, t2);
                    } else {
                        status(request, response);
                    }
                }
            }
            if (!request.isAsync() && !asyncAtStart) {
                context.fireRequestDestroyEvent(request.getRequest());
            }
            if (ACCESS_SESSION) {
                request.getSession(false);
            }
            context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        } catch (Throwable th) {
            if (ACCESS_SESSION) {
                request.getSession(false);
            }
            context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
            throw th;
        }
    }

    private void status(Request request, Response response) throws IOException {
        int statusCode = response.getStatus();
        Context context = request.getContext();
        if (context == null || !response.isError()) {
            return;
        }
        ErrorPage errorPage = context.findErrorPage(statusCode);
        if (errorPage == null) {
            errorPage = context.findErrorPage(0);
        }
        if (errorPage != null && response.isErrorReportRequired()) {
            response.setAppCommitted(false);
            request.setAttribute("javax.servlet.error.status_code", Integer.valueOf(statusCode));
            String message = response.getMessage();
            if (message == null) {
                message = "";
            }
            request.setAttribute("javax.servlet.error.message", message);
            request.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR, errorPage.getLocation());
            request.setAttribute(Globals.DISPATCHER_TYPE_ATTR, DispatcherType.ERROR);
            Wrapper wrapper = request.getWrapper();
            if (wrapper != null) {
                request.setAttribute("javax.servlet.error.servlet_name", wrapper.getName());
            }
            request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
            if (custom(request, response, errorPage)) {
                response.setErrorReported();
                try {
                    response.finishResponse();
                } catch (ClientAbortException e) {
                } catch (IOException e2) {
                    this.container.getLogger().warn("Exception Processing " + errorPage, e2);
                }
            }
        }
    }

    protected void throwable(Request request, Response response, Throwable throwable) {
        Context context = request.getContext();
        if (context == null) {
            return;
        }
        Throwable realError = throwable;
        if (realError instanceof ServletException) {
            realError = ((ServletException) realError).getRootCause();
            if (realError == null) {
                realError = throwable;
            }
        }
        if (realError instanceof ClientAbortException) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("standardHost.clientAbort", realError.getCause().getMessage()));
                return;
            }
            return;
        }
        ErrorPage errorPage = context.findErrorPage(throwable);
        if (errorPage == null && realError != throwable) {
            errorPage = context.findErrorPage(realError);
        }
        if (errorPage != null) {
            if (response.setErrorReported()) {
                response.setAppCommitted(false);
                request.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR, errorPage.getLocation());
                request.setAttribute(Globals.DISPATCHER_TYPE_ATTR, DispatcherType.ERROR);
                request.setAttribute("javax.servlet.error.status_code", 500);
                request.setAttribute("javax.servlet.error.message", throwable.getMessage());
                request.setAttribute("javax.servlet.error.exception", realError);
                Wrapper wrapper = request.getWrapper();
                if (wrapper != null) {
                    request.setAttribute("javax.servlet.error.servlet_name", wrapper.getName());
                }
                request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
                request.setAttribute("javax.servlet.error.exception_type", realError.getClass());
                if (custom(request, response, errorPage)) {
                    try {
                        response.finishResponse();
                        return;
                    } catch (IOException e) {
                        this.container.getLogger().warn("Exception Processing " + errorPage, e);
                        return;
                    }
                }
                return;
            }
            return;
        }
        response.setStatus(500);
        response.setError();
        status(request, response);
    }

    private boolean custom(Request request, Response response, ErrorPage errorPage) {
        if (this.container.getLogger().isDebugEnabled()) {
            this.container.getLogger().debug("Processing " + errorPage);
        }
        try {
            ServletContext servletContext = request.getContext().getServletContext();
            RequestDispatcher rd = servletContext.getRequestDispatcher(errorPage.getLocation());
            if (rd == null) {
                this.container.getLogger().error(sm.getString("standardHostValue.customStatusFailed", errorPage.getLocation()));
                return false;
            }
            if (response.isCommitted()) {
                rd.include(request.getRequest(), response.getResponse());
                try {
                    response.flushBuffer();
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                }
                response.getCoyoteResponse().action(ActionCode.CLOSE_NOW, request.getAttribute("javax.servlet.error.exception"));
                return true;
            }
            response.resetBuffer(true);
            response.setContentLength(-1);
            rd.forward(request.getRequest(), response.getResponse());
            response.setSuspended(false);
            return true;
        } catch (Throwable t2) {
            ExceptionUtils.handleThrowable(t2);
            this.container.getLogger().error("Exception Processing " + errorPage, t2);
            return false;
        }
    }
}
