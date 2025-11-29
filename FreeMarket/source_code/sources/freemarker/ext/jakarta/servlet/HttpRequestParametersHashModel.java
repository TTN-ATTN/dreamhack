package freemarker.ext.jakarta.servlet;

import freemarker.template.SimpleCollection;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/HttpRequestParametersHashModel.class */
public class HttpRequestParametersHashModel implements TemplateHashModelEx {
    private final HttpServletRequest request;
    private List keys;

    public HttpRequestParametersHashModel(HttpServletRequest request) {
        this.request = request;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) {
        String value = this.request.getParameter(key);
        if (value == null) {
            return null;
        }
        return new SimpleScalar(value);
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return !this.request.getParameterNames().hasMoreElements();
    }

    @Override // freemarker.template.TemplateHashModelEx
    public int size() {
        return getKeys().size();
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() {
        return new SimpleCollection(getKeys().iterator());
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() {
        final Iterator iter = getKeys().iterator();
        return new SimpleCollection(new Iterator() { // from class: freemarker.ext.jakarta.servlet.HttpRequestParametersHashModel.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override // java.util.Iterator
            public Object next() {
                return HttpRequestParametersHashModel.this.request.getParameter((String) iter.next());
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }

    protected String transcode(String string) {
        return string;
    }

    private synchronized List getKeys() {
        if (this.keys == null) {
            this.keys = new ArrayList();
            Enumeration enumeration = this.request.getParameterNames();
            while (enumeration.hasMoreElements()) {
                this.keys.add(enumeration.nextElement());
            }
        }
        return this.keys;
    }
}
