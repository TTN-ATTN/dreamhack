package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MapModel.class */
public class MapModel extends StringModel implements TemplateMethodModelEx {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.MapModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new MapModel((Map) object, (BeansWrapper) wrapper);
        }
    };

    public MapModel(Map map, BeansWrapper wrapper) {
        super(map, wrapper);
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List arguments) throws TemplateModelException {
        Object key = unwrap((TemplateModel) arguments.get(0));
        return wrap(((Map) this.object).get(key));
    }

    @Override // freemarker.ext.beans.BeanModel
    protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key) throws TemplateModelException {
        Map map = (Map) this.object;
        Object val = map.get(key);
        if (val == null) {
            if (key.length() == 1) {
                Character charKey = Character.valueOf(key.charAt(0));
                val = map.get(charKey);
                if (val == null && !map.containsKey(key) && !map.containsKey(charKey)) {
                    return UNKNOWN;
                }
            } else if (!map.containsKey(key)) {
                return UNKNOWN;
            }
        }
        return wrap(val);
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return ((Map) this.object).isEmpty() && super.isEmpty();
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.TemplateHashModelEx
    public int size() {
        return keySet().size();
    }

    @Override // freemarker.ext.beans.BeanModel
    protected Set keySet() {
        Set set = super.keySet();
        set.addAll(((Map) this.object).keySet());
        return set;
    }
}
