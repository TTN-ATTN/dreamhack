package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/_EnumModels.class */
public class _EnumModels extends ClassBasedModelFactory {
    @Override // freemarker.ext.beans.ClassBasedModelFactory, freemarker.template.TemplateHashModel
    public /* bridge */ /* synthetic */ boolean isEmpty() {
        return super.isEmpty();
    }

    @Override // freemarker.ext.beans.ClassBasedModelFactory, freemarker.template.TemplateHashModel
    public /* bridge */ /* synthetic */ TemplateModel get(String str) throws TemplateModelException {
        return super.get(str);
    }

    public _EnumModels(BeansWrapper wrapper) {
        super(wrapper);
    }

    @Override // freemarker.ext.beans.ClassBasedModelFactory
    protected TemplateModel createModel(Class clazz) {
        Object[] obj = clazz.getEnumConstants();
        if (obj == null) {
            return null;
        }
        Map map = new LinkedHashMap();
        for (Object obj2 : obj) {
            Enum value = (Enum) obj2;
            map.put(value.name(), value);
        }
        return new SimpleMapModel(map, getWrapper());
    }
}
