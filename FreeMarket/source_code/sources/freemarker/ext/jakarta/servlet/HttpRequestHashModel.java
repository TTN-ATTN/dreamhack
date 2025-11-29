package freemarker.ext.jakarta.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/HttpRequestHashModel.class */
public final class HttpRequestHashModel implements TemplateHashModelEx {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ObjectWrapper wrapper;

    public HttpRequestHashModel(HttpServletRequest request, ObjectWrapper wrapper) {
        this(request, null, wrapper);
    }

    public HttpRequestHashModel(HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
        this.request = request;
        this.response = response;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        return this.wrapper.wrap(this.request.getAttribute(key));
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return !this.request.getAttributeNames().hasMoreElements();
    }

    @Override // freemarker.template.TemplateHashModelEx
    public int size() {
        int result = 0;
        Enumeration enumeration = this.request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            result++;
        }
        return result;
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() {
        ArrayList keys = new ArrayList();
        Enumeration enumeration = this.request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            keys.add(enumeration.nextElement());
        }
        return new SimpleCollection(keys.iterator());
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() {
        ArrayList values = new ArrayList();
        Enumeration enumeration = this.request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            values.add(this.request.getAttribute((String) enumeration.nextElement()));
        }
        return new SimpleCollection(values.iterator(), this.wrapper);
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public ObjectWrapper getObjectWrapper() {
        return this.wrapper;
    }
}
