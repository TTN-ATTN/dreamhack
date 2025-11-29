package freemarker.ext.jakarta.jsp;

import freemarker.core.Environment;
import freemarker.ext.jakarta.jsp.TagTransformModel;
import freemarker.ext.jakarta.servlet.HttpRequestHashModel;
import freemarker.ext.jakarta.servlet.ServletContextHashModel;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template._VersionInts;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.UndeclaredThrowableException;
import jakarta.el.ELContext;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspApplicationContext;
import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.el.ELException;
import jakarta.servlet.jsp.el.ExpressionEvaluator;
import jakarta.servlet.jsp.el.VariableResolver;
import jakarta.servlet.jsp.tagext.BodyContent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/FreeMarkerPageContext.class */
class FreeMarkerPageContext extends PageContext implements TemplateModel {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private final GenericServlet servlet;
    private HttpSession session;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ObjectWrapper wrapper;
    private final ObjectWrapperAndUnwrapper unwrapper;
    private JspWriter jspOut;
    private ELContext elContext;
    private List tags = new ArrayList();
    private List outs = new ArrayList();
    private final Environment environment = Environment.getCurrentEnvironment();
    private final int incompatibleImprovements = this.environment.getConfiguration().getIncompatibleImprovements().intValue();

    static {
        if (JspFactory.getDefaultFactory() == null) {
            JspFactory.setDefaultFactory(new FreeMarkerJspFactory());
        }
        LOG.debug("Using JspFactory implementation class " + JspFactory.getDefaultFactory().getClass().getName());
    }

    protected FreeMarkerPageContext() throws TemplateModelException {
        TemplateModel appModel = this.environment.getGlobalVariable("__FreeMarkerServlet.Application__");
        appModel = appModel instanceof ServletContextHashModel ? appModel : this.environment.getGlobalVariable("Application");
        if (appModel instanceof ServletContextHashModel) {
            this.servlet = ((ServletContextHashModel) appModel).getServlet();
            TemplateModel requestModel = this.environment.getGlobalVariable("__FreeMarkerServlet.Request__");
            requestModel = requestModel instanceof HttpRequestHashModel ? requestModel : this.environment.getGlobalVariable("Request");
            if (requestModel instanceof HttpRequestHashModel) {
                HttpRequestHashModel reqHash = (HttpRequestHashModel) requestModel;
                this.request = reqHash.getRequest();
                this.session = this.request.getSession(false);
                this.response = reqHash.getResponse();
                this.wrapper = reqHash.getObjectWrapper();
                this.unwrapper = this.wrapper instanceof ObjectWrapperAndUnwrapper ? (ObjectWrapperAndUnwrapper) this.wrapper : null;
                setAttribute("jakarta.servlet.jsp.jspRequest", this.request);
                setAttribute("jakarta.servlet.jsp.jspResponse", this.response);
                if (this.session != null) {
                    setAttribute("jakarta.servlet.jsp.jspSession", this.session);
                }
                setAttribute("jakarta.servlet.jsp.jspPage", this.servlet);
                setAttribute("jakarta.servlet.jsp.jspConfig", this.servlet.getServletConfig());
                setAttribute("jakarta.servlet.jsp.jspPageContext", this);
                setAttribute("jakarta.servlet.jsp.jspApplication", this.servlet.getServletContext());
                return;
            }
            throw new TemplateModelException("Could not find an instance of " + HttpRequestHashModel.class.getName() + " in the data model under either the name __FreeMarkerServlet.Request__ or Request");
        }
        throw new TemplateModelException("Could not find an instance of " + ServletContextHashModel.class.getName() + " in the data model under either the name __FreeMarkerServlet.Application__ or Application");
    }

    ObjectWrapper getObjectWrapper() {
        return this.wrapper;
    }

    public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) {
        throw new UnsupportedOperationException();
    }

    public void release() {
    }

    public void setAttribute(String name, Object value) {
        setAttribute(name, value, 1);
    }

    public void setAttribute(String name, Object value, int scope) {
        switch (scope) {
            case 1:
                try {
                    this.environment.setGlobalVariable(name, this.wrapper.wrap(value));
                    return;
                } catch (TemplateModelException e) {
                    throw new UndeclaredThrowableException(e);
                }
            case 2:
                getRequest().setAttribute(name, value);
                return;
            case 3:
                getSession(true).setAttribute(name, value);
                return;
            case 4:
                getServletContext().setAttribute(name, value);
                return;
            default:
                throw new IllegalArgumentException("Invalid scope " + scope);
        }
    }

    public Object getAttribute(String name) {
        return getAttribute(name, 1);
    }

    public Object getAttribute(String name, int scope) {
        switch (scope) {
            case 1:
                try {
                    TemplateModel tm = this.environment.getGlobalNamespace().get(name);
                    if (this.incompatibleImprovements >= _VersionInts.V_2_3_22 && this.unwrapper != null) {
                        return this.unwrapper.unwrap(tm);
                    }
                    if (tm instanceof AdapterTemplateModel) {
                        return ((AdapterTemplateModel) tm).getAdaptedObject(Object.class);
                    }
                    if (tm instanceof WrapperTemplateModel) {
                        return ((WrapperTemplateModel) tm).getWrappedObject();
                    }
                    if (tm instanceof TemplateScalarModel) {
                        return ((TemplateScalarModel) tm).getAsString();
                    }
                    if (tm instanceof TemplateNumberModel) {
                        return ((TemplateNumberModel) tm).getAsNumber();
                    }
                    if (tm instanceof TemplateBooleanModel) {
                        return Boolean.valueOf(((TemplateBooleanModel) tm).getAsBoolean());
                    }
                    if (this.incompatibleImprovements >= _VersionInts.V_2_3_22 && (tm instanceof TemplateDateModel)) {
                        return ((TemplateDateModel) tm).getAsDate();
                    }
                    return tm;
                } catch (TemplateModelException e) {
                    throw new UndeclaredThrowableException("Failed to unwrapp FTL global variable", e);
                }
            case 2:
                return getRequest().getAttribute(name);
            case 3:
                HttpSession session = getSession(false);
                if (session == null) {
                    return null;
                }
                return session.getAttribute(name);
            case 4:
                return getServletContext().getAttribute(name);
            default:
                throw new IllegalArgumentException("Invalid scope " + scope);
        }
    }

    public Object findAttribute(String name) {
        Object retval = getAttribute(name, 1);
        if (retval != null) {
            return retval;
        }
        Object retval2 = getAttribute(name, 2);
        if (retval2 != null) {
            return retval2;
        }
        Object retval3 = getAttribute(name, 3);
        return retval3 != null ? retval3 : getAttribute(name, 4);
    }

    public void removeAttribute(String name) {
        removeAttribute(name, 1);
        removeAttribute(name, 2);
        removeAttribute(name, 3);
        removeAttribute(name, 4);
    }

    public void removeAttribute(String name, int scope) {
        switch (scope) {
            case 1:
                this.environment.getGlobalNamespace().remove(name);
                return;
            case 2:
                getRequest().removeAttribute(name);
                return;
            case 3:
                HttpSession session = getSession(false);
                if (session != null) {
                    session.removeAttribute(name);
                    return;
                }
                return;
            case 4:
                getServletContext().removeAttribute(name);
                return;
            default:
                throw new IllegalArgumentException("Invalid scope: " + scope);
        }
    }

    public int getAttributesScope(String name) {
        if (getAttribute(name, 1) != null) {
            return 1;
        }
        if (getAttribute(name, 2) != null) {
            return 2;
        }
        if (getAttribute(name, 3) != null) {
            return 3;
        }
        return getAttribute(name, 4) != null ? 4 : 0;
    }

    public Enumeration getAttributeNamesInScope(int scope) {
        switch (scope) {
            case 1:
                try {
                    return new TemplateHashModelExEnumeration(this.environment.getGlobalNamespace());
                } catch (TemplateModelException e) {
                    throw new UndeclaredThrowableException(e);
                }
            case 2:
                return getRequest().getAttributeNames();
            case 3:
                HttpSession session = getSession(false);
                if (session != null) {
                    return session.getAttributeNames();
                }
                return Collections.enumeration(Collections.EMPTY_SET);
            case 4:
                return getServletContext().getAttributeNames();
            default:
                throw new IllegalArgumentException("Invalid scope " + scope);
        }
    }

    public JspWriter getOut() {
        return this.jspOut;
    }

    private HttpSession getSession(boolean create) {
        if (this.session == null) {
            this.session = this.request.getSession(create);
            if (this.session != null) {
                setAttribute("jakarta.servlet.jsp.jspSession", this.session);
            }
        }
        return this.session;
    }

    public HttpSession getSession() {
        return getSession(false);
    }

    public Object getPage() {
        return this.servlet;
    }

    public ServletRequest getRequest() {
        return this.request;
    }

    public ServletResponse getResponse() {
        return this.response;
    }

    public Exception getException() {
        throw new UnsupportedOperationException();
    }

    public ServletConfig getServletConfig() {
        return this.servlet.getServletConfig();
    }

    public ServletContext getServletContext() {
        return this.servlet.getServletContext();
    }

    public void forward(String url) throws IOException, ServletException {
        this.request.getRequestDispatcher(url).forward(this.request, this.response);
    }

    public void include(String url) throws IOException, ServletException {
        this.jspOut.flush();
        this.request.getRequestDispatcher(url).include(this.request, this.response);
    }

    public void include(String url, boolean flush) throws IOException, ServletException {
        if (flush) {
            this.jspOut.flush();
        }
        final PrintWriter pw = new PrintWriter((Writer) this.jspOut);
        this.request.getRequestDispatcher(url).include(this.request, new HttpServletResponseWrapper(this.response) { // from class: freemarker.ext.jakarta.jsp.FreeMarkerPageContext.1
            public PrintWriter getWriter() {
                return pw;
            }

            public ServletOutputStream getOutputStream() {
                throw new UnsupportedOperationException("JSP-included resource must use getWriter()");
            }
        });
        pw.flush();
    }

    public void handlePageException(Exception e) {
        throw new UnsupportedOperationException();
    }

    public void handlePageException(Throwable e) {
        throw new UnsupportedOperationException();
    }

    public BodyContent pushBody() {
        return pushWriter(new TagTransformModel.BodyContentImpl(getOut(), true));
    }

    public JspWriter pushBody(Writer w) {
        return pushWriter(new JspWriterAdapter(w));
    }

    public JspWriter popBody() {
        popWriter();
        return (JspWriter) getAttribute("jakarta.servlet.jsp.jspOut");
    }

    Object peekTopTag(Class tagClass) {
        ListIterator iter = this.tags.listIterator(this.tags.size());
        while (iter.hasPrevious()) {
            Object tag = iter.previous();
            if (tagClass.isInstance(tag)) {
                return tag;
            }
        }
        return null;
    }

    void popTopTag() {
        this.tags.remove(this.tags.size() - 1);
    }

    void popWriter() {
        this.jspOut = (JspWriter) this.outs.remove(this.outs.size() - 1);
        setAttribute("jakarta.servlet.jsp.jspOut", this.jspOut);
    }

    void pushTopTag(Object tag) {
        this.tags.add(tag);
    }

    JspWriter pushWriter(JspWriter out) {
        this.outs.add(this.jspOut);
        this.jspOut = out;
        setAttribute("jakarta.servlet.jsp.jspOut", this.jspOut);
        return out;
    }

    public ExpressionEvaluator getExpressionEvaluator() throws ClassNotFoundException {
        try {
            Class type = ((ClassLoader) AccessController.doPrivileged(new PrivilegedAction() { // from class: freemarker.ext.jakarta.jsp.FreeMarkerPageContext.2
                @Override // java.security.PrivilegedAction
                public Object run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            })).loadClass("org.apache.commons.el.ExpressionEvaluatorImpl");
            return (ExpressionEvaluator) type.newInstance();
        } catch (Exception e) {
            throw new UnsupportedOperationException("In order for the getExpressionEvaluator() method to work, you must have downloaded the apache commons-el jar and made it available in the classpath.");
        }
    }

    public VariableResolver getVariableResolver() {
        return new VariableResolver() { // from class: freemarker.ext.jakarta.jsp.FreeMarkerPageContext.3
            public Object resolveVariable(String name) throws ELException {
                return this.findAttribute(name);
            }
        };
    }

    public ELContext getELContext() {
        if (this.elContext == null) {
            JspApplicationContext jspctx = JspFactory.getDefaultFactory().getJspApplicationContext(getServletContext());
            if (jspctx instanceof FreeMarkerJspApplicationContext) {
                this.elContext = ((FreeMarkerJspApplicationContext) jspctx).createNewELContext(this);
                this.elContext.putContext(JspContext.class, this);
            } else {
                throw new UnsupportedOperationException("Can not create an ELContext using a foreign JspApplicationContext (of class " + ClassUtil.getShortClassNameOfObject(jspctx) + ").\nHint: The cause of this is often that you are trying to use JSTL tags/functions in FTL. In that case, know that that's not really suppored, and you are supposed to use FTL constrcuts instead, like #list instead of JSTL's forEach, etc.");
            }
        }
        return this.elContext;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/FreeMarkerPageContext$TemplateHashModelExEnumeration.class */
    private static class TemplateHashModelExEnumeration implements Enumeration {
        private final TemplateModelIterator it;

        private TemplateHashModelExEnumeration(TemplateHashModelEx hashEx) throws TemplateModelException {
            this.it = hashEx.keys().iterator();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            try {
                return this.it.hasNext();
            } catch (TemplateModelException tme) {
                throw new UndeclaredThrowableException(tme);
            }
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            try {
                return ((TemplateScalarModel) this.it.next()).getAsString();
            } catch (TemplateModelException tme) {
                throw new UndeclaredThrowableException(tme);
            }
        }
    }
}
