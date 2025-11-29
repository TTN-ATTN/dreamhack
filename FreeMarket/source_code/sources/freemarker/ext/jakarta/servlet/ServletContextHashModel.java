package freemarker.ext.jakarta.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/ServletContextHashModel.class */
public final class ServletContextHashModel implements TemplateHashModel {
    private final GenericServlet servlet;
    private final ServletContext servletctx;
    private final ObjectWrapper wrapper;

    public ServletContextHashModel(GenericServlet servlet, ObjectWrapper wrapper) {
        this.servlet = servlet;
        this.servletctx = servlet.getServletContext();
        this.wrapper = wrapper;
    }

    @Deprecated
    public ServletContextHashModel(ServletContext servletctx, ObjectWrapper wrapper) {
        this.servlet = null;
        this.servletctx = servletctx;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        return this.wrapper.wrap(this.servletctx.getAttribute(key));
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return !this.servletctx.getAttributeNames().hasMoreElements();
    }

    public GenericServlet getServlet() {
        return this.servlet;
    }
}
