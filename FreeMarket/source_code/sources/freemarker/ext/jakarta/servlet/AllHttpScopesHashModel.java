package freemarker.ext.jakarta.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.NullArgumentException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/AllHttpScopesHashModel.class */
public class AllHttpScopesHashModel extends SimpleHash {
    private final ServletContext context;
    private final HttpServletRequest request;
    private final Map unlistedModels;

    public AllHttpScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request) {
        super(objectWrapper);
        this.unlistedModels = new HashMap();
        NullArgumentException.check("wrapper", objectWrapper);
        this.context = context;
        this.request = request;
    }

    public void putUnlistedModel(String key, TemplateModel model) {
        this.unlistedModels.put(key, model);
    }

    @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        Object obj;
        TemplateModel model = super.get(key);
        if (model != null) {
            return model;
        }
        TemplateModel model2 = (TemplateModel) this.unlistedModels.get(key);
        if (model2 != null) {
            return model2;
        }
        Object obj2 = this.request.getAttribute(key);
        if (obj2 != null) {
            return wrap(obj2);
        }
        HttpSession session = this.request.getSession(false);
        if (session != null && (obj = session.getAttribute(key)) != null) {
            return wrap(obj);
        }
        Object obj3 = this.context.getAttribute(key);
        if (obj3 != null) {
            return wrap(obj3);
        }
        return wrap(null);
    }
}
