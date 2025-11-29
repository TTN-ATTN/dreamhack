package freemarker.ext.beans;

import freemarker.core.CollectionAndSequence;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.MapKeyValuePairIterator;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.RichObjectWrapper;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/SimpleMapModel.class */
public class SimpleMapModel extends WrappingTemplateModel implements TemplateHashModelEx2, TemplateMethodModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.SimpleMapModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new SimpleMapModel((Map) object, (BeansWrapper) wrapper);
        }
    };
    private final Map map;

    public SimpleMapModel(Map map, BeansWrapper wrapper) {
        super(wrapper);
        this.map = map;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        Object val = this.map.get(key);
        if (val == null) {
            if (key.length() == 1) {
                Character charKey = Character.valueOf(key.charAt(0));
                val = this.map.get(charKey);
                if (val == null && !this.map.containsKey(key) && !this.map.containsKey(charKey)) {
                    return null;
                }
            } else if (!this.map.containsKey(key)) {
                return null;
            }
        }
        return wrap(val);
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List args) throws TemplateModelException {
        Object key = ((BeansWrapper) getObjectWrapper()).unwrap((TemplateModel) args.get(0));
        Object value = this.map.get(key);
        if (value == null && !this.map.containsKey(key)) {
            return null;
        }
        return wrap(value);
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // freemarker.template.TemplateHashModelEx
    public int size() {
        return this.map.size();
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() {
        return new CollectionAndSequence(new SimpleSequence(this.map.keySet(), getObjectWrapper()));
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() {
        return new CollectionAndSequence(new SimpleSequence(this.map.values(), getObjectWrapper()));
    }

    @Override // freemarker.template.TemplateHashModelEx2
    public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
        return new MapKeyValuePairIterator(this.map, getObjectWrapper());
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return this.map;
    }

    @Override // freemarker.ext.util.WrapperTemplateModel
    public Object getWrappedObject() {
        return this.map;
    }

    @Override // freemarker.template.TemplateModelWithAPISupport
    public TemplateModel getAPI() throws TemplateModelException {
        return ((RichObjectWrapper) getObjectWrapper()).wrapAsAPI(this.map);
    }
}
