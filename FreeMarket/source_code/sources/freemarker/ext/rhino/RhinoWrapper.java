package freemarker.ext.rhino;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.Wrapper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/rhino/RhinoWrapper.class */
public class RhinoWrapper extends BeansWrapper {
    private static final Object UNDEFINED_INSTANCE;

    static {
        try {
            UNDEFINED_INSTANCE = AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: freemarker.ext.rhino.RhinoWrapper.1
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    return Undefined.class.getField("instance").get(null);
                }
            });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e2) {
            throw new UndeclaredThrowableException(e2);
        }
    }

    @Override // freemarker.ext.beans.BeansWrapper, freemarker.template.ObjectWrapper
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj == UNDEFINED_INSTANCE || obj == UniqueTag.NOT_FOUND) {
            return null;
        }
        if (obj == UniqueTag.NULL_VALUE) {
            return super.wrap(null);
        }
        if (obj instanceof Wrapper) {
            obj = ((Wrapper) obj).unwrap();
        }
        return super.wrap(obj);
    }

    @Override // freemarker.ext.beans.BeansWrapper
    protected ModelFactory getModelFactory(Class clazz) {
        if (Scriptable.class.isAssignableFrom(clazz)) {
            return RhinoScriptableModel.FACTORY;
        }
        return super.getModelFactory(clazz);
    }
}
