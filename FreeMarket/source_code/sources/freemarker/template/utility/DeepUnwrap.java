package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DeepUnwrap.class */
public class DeepUnwrap {
    private static final Class OBJECT_CLASS = Object.class;

    public static Object unwrap(TemplateModel model) throws TemplateModelException {
        return unwrap(model, false);
    }

    public static Object permissiveUnwrap(TemplateModel model) throws TemplateModelException {
        return unwrap(model, true);
    }

    @Deprecated
    public static Object premissiveUnwrap(TemplateModel model) throws TemplateModelException {
        return unwrap(model, true);
    }

    private static Object unwrap(TemplateModel model, boolean permissive) throws TemplateModelException {
        ObjectWrapper wrapper;
        Environment env = Environment.getCurrentEnvironment();
        TemplateModel nullModel = null;
        if (env != null && (wrapper = env.getObjectWrapper()) != null) {
            nullModel = wrapper.wrap(null);
        }
        return unwrap(model, nullModel, permissive);
    }

    private static Object unwrap(TemplateModel model, TemplateModel nullModel, boolean permissive) throws TemplateModelException {
        if (model instanceof AdapterTemplateModel) {
            return ((AdapterTemplateModel) model).getAdaptedObject(OBJECT_CLASS);
        }
        if (model instanceof WrapperTemplateModel) {
            return ((WrapperTemplateModel) model).getWrappedObject();
        }
        if (model == nullModel) {
            return null;
        }
        if (model instanceof TemplateScalarModel) {
            return ((TemplateScalarModel) model).getAsString();
        }
        if (model instanceof TemplateNumberModel) {
            return ((TemplateNumberModel) model).getAsNumber();
        }
        if (model instanceof TemplateDateModel) {
            return ((TemplateDateModel) model).getAsDate();
        }
        if (model instanceof TemplateBooleanModel) {
            return Boolean.valueOf(((TemplateBooleanModel) model).getAsBoolean());
        }
        if (model instanceof TemplateSequenceModel) {
            TemplateSequenceModel seq = (TemplateSequenceModel) model;
            int size = seq.size();
            ArrayList list = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                list.add(unwrap(seq.get(i), nullModel, permissive));
            }
            return list;
        }
        if (model instanceof TemplateCollectionModel) {
            TemplateCollectionModel coll = (TemplateCollectionModel) model;
            ArrayList list2 = new ArrayList();
            TemplateModelIterator it = coll.iterator();
            while (it.hasNext()) {
                list2.add(unwrap(it.next(), nullModel, permissive));
            }
            return list2;
        }
        if (model instanceof TemplateHashModelEx) {
            TemplateHashModelEx hash = (TemplateHashModelEx) model;
            Map<Object, Object> map = new LinkedHashMap<>();
            if (model instanceof TemplateHashModelEx2) {
                TemplateHashModelEx2.KeyValuePairIterator kvps = ((TemplateHashModelEx2) model).keyValuePairIterator();
                while (kvps.hasNext()) {
                    TemplateHashModelEx2.KeyValuePair kvp = kvps.next();
                    map.put(unwrap(kvp.getKey(), nullModel, permissive), unwrap(kvp.getValue(), nullModel, permissive));
                }
            } else {
                TemplateModelIterator keys = hash.keys().iterator();
                while (keys.hasNext()) {
                    String key = (String) unwrap(keys.next(), nullModel, permissive);
                    map.put(key, unwrap(hash.get(key), nullModel, permissive));
                }
            }
            return map;
        }
        if (permissive) {
            return model;
        }
        throw new TemplateModelException("Cannot deep-unwrap model of type " + model.getClass().getName());
    }
}
