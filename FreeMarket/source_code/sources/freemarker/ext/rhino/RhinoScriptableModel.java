package freemarker.ext.rhino;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/rhino/RhinoScriptableModel.class */
public class RhinoScriptableModel implements TemplateHashModelEx, TemplateSequenceModel, AdapterTemplateModel, TemplateScalarModel, TemplateBooleanModel, TemplateNumberModel {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.rhino.RhinoScriptableModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new RhinoScriptableModel((Scriptable) object, (BeansWrapper) wrapper);
        }
    };
    private final Scriptable scriptable;
    private final BeansWrapper wrapper;

    public RhinoScriptableModel(Scriptable scriptable, BeansWrapper wrapper) {
        this.scriptable = scriptable;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        Object retval = ScriptableObject.getProperty(this.scriptable, key);
        if (retval instanceof Function) {
            return new RhinoFunctionModel((Function) retval, this.scriptable, this.wrapper);
        }
        return this.wrapper.wrap(retval);
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        Object retval = ScriptableObject.getProperty(this.scriptable, index);
        if (retval instanceof Function) {
            return new RhinoFunctionModel((Function) retval, this.scriptable, this.wrapper);
        }
        return this.wrapper.wrap(retval);
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return this.scriptable.getIds().length == 0;
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() throws TemplateModelException {
        return (TemplateCollectionModel) this.wrapper.wrap(this.scriptable.getIds());
    }

    @Override // freemarker.template.TemplateHashModelEx
    public int size() {
        return this.scriptable.getIds().length;
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() throws TemplateModelException {
        Object[] ids = this.scriptable.getIds();
        Object[] values = new Object[ids.length];
        for (int i = 0; i < values.length; i++) {
            Object id = ids[i];
            if (id instanceof Number) {
                values[i] = ScriptableObject.getProperty(this.scriptable, ((Number) id).intValue());
            } else {
                values[i] = ScriptableObject.getProperty(this.scriptable, String.valueOf(id));
            }
        }
        return (TemplateCollectionModel) this.wrapper.wrap(values);
    }

    @Override // freemarker.template.TemplateBooleanModel
    public boolean getAsBoolean() {
        return Context.toBoolean(this.scriptable);
    }

    @Override // freemarker.template.TemplateNumberModel
    public Number getAsNumber() {
        return Double.valueOf(Context.toNumber(this.scriptable));
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() {
        return Context.toString(this.scriptable);
    }

    Scriptable getScriptable() {
        return this.scriptable;
    }

    BeansWrapper getWrapper() {
        return this.wrapper;
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        try {
            return NativeJavaObject.coerceType(hint, this.scriptable);
        } catch (EvaluatorException e) {
            return NativeJavaObject.coerceType(Object.class, this.scriptable);
        }
    }
}
