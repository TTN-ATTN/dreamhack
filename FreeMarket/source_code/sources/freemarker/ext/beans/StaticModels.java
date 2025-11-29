package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/StaticModels.class */
class StaticModels extends ClassBasedModelFactory {
    StaticModels(BeansWrapper wrapper) {
        super(wrapper);
    }

    @Override // freemarker.ext.beans.ClassBasedModelFactory
    protected TemplateModel createModel(Class clazz) throws TemplateModelException {
        return new StaticModel(clazz, getWrapper());
    }
}
