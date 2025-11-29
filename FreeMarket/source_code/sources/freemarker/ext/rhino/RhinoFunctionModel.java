package freemarker.ext.rhino;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/rhino/RhinoFunctionModel.class */
public class RhinoFunctionModel extends RhinoScriptableModel implements TemplateMethodModelEx {
    private final Scriptable fnThis;

    public RhinoFunctionModel(Function function, Scriptable fnThis, BeansWrapper wrapper) {
        super(function, wrapper);
        this.fnThis = fnThis;
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List arguments) throws TemplateModelException {
        Context cx = Context.getCurrentContext();
        Object[] args = arguments.toArray();
        BeansWrapper wrapper = getWrapper();
        for (int i = 0; i < args.length; i++) {
            args[i] = wrapper.unwrap((TemplateModel) args[i]);
        }
        return wrapper.wrap(getScriptable().call(cx, ScriptableObject.getTopLevelScope(this.fnThis), this.fnThis, args));
    }
}
