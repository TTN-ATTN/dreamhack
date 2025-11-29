package freemarker.template;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultMapAdapter.class */
public class DefaultMapAdapter extends WrappingTemplateModel implements TemplateHashModelEx2, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {
    private final Map map;

    public static DefaultMapAdapter adapt(Map map, ObjectWrapperWithAPISupport wrapper) {
        return new DefaultMapAdapter(map, wrapper);
    }

    private DefaultMapAdapter(Map map, ObjectWrapper wrapper) {
        super(wrapper);
        this.map = map;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        try {
            Object val = this.map.get(key);
            if (val == null) {
                if (key.length() == 1 && !(this.map instanceof SortedMap)) {
                    Character charKey = Character.valueOf(key.charAt(0));
                    try {
                        val = this.map.get(charKey);
                        if (val == null) {
                            TemplateModel wrappedNull = wrap(null);
                            if (wrappedNull == null) {
                                return null;
                            }
                            if (!this.map.containsKey(key)) {
                                if (!this.map.containsKey(charKey)) {
                                    return null;
                                }
                            }
                            return wrappedNull;
                        }
                    } catch (ClassCastException e) {
                        throw new _TemplateModelException(e, "Class casting exception while getting Map entry with Character key ", new _DelayedJQuote(charKey));
                    } catch (NullPointerException e2) {
                        throw new _TemplateModelException(e2, "NullPointerException while getting Map entry with Character key ", new _DelayedJQuote(charKey));
                    }
                } else {
                    TemplateModel wrappedNull2 = wrap(null);
                    if (wrappedNull2 == null || !this.map.containsKey(key)) {
                        return null;
                    }
                    return wrappedNull2;
                }
            }
            return wrap(val);
        } catch (ClassCastException e3) {
            throw new _TemplateModelException(e3, "ClassCastException while getting Map entry with String key ", new _DelayedJQuote(key));
        } catch (NullPointerException e4) {
            throw new _TemplateModelException(e4, "NullPointerException while getting Map entry with String key ", new _DelayedJQuote(key));
        }
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
        return new SimpleCollection((Collection) this.map.keySet(), getObjectWrapper());
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() {
        return new SimpleCollection(this.map.values(), getObjectWrapper());
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
        return ((ObjectWrapperWithAPISupport) getObjectWrapper()).wrapAsAPI(this.map);
    }
}
