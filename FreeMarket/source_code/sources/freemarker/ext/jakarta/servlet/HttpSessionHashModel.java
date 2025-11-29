package freemarker.ext.jakarta.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/HttpSessionHashModel.class */
public final class HttpSessionHashModel implements TemplateHashModel, Serializable {
    private static final long serialVersionUID = 1;
    private transient HttpSession session;
    private final transient ObjectWrapper wrapper;
    private final transient FreemarkerServlet servlet;
    private final transient HttpServletRequest request;
    private final transient HttpServletResponse response;

    public HttpSessionHashModel(HttpSession session, ObjectWrapper wrapper) {
        this.session = session;
        this.wrapper = wrapper;
        this.servlet = null;
        this.request = null;
        this.response = null;
    }

    public HttpSessionHashModel(FreemarkerServlet servlet, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
        this.wrapper = wrapper;
        this.servlet = servlet;
        this.request = request;
        this.response = response;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException, ServletException {
        checkSessionExistence();
        return this.wrapper.wrap(this.session != null ? this.session.getAttribute(key) : null);
    }

    private void checkSessionExistence() throws TemplateModelException, ServletException {
        if (this.session == null && this.request != null) {
            this.session = this.request.getSession(false);
            if (this.session != null && this.servlet != null) {
                try {
                    this.servlet.initializeSessionAndInstallModel(this.request, this.response, this, this.session);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new TemplateModelException(e2);
                }
            }
        }
    }

    boolean isOrphaned(HttpSession currentSession) {
        return !(this.session == null || this.session == currentSession) || (this.session == null && this.request == null);
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() throws TemplateModelException, ServletException {
        checkSessionExistence();
        return this.session == null || !this.session.getAttributeNames().hasMoreElements();
    }
}
