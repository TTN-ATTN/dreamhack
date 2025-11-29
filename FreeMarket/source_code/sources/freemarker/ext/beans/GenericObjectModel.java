package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.MethodCallAwareTemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/GenericObjectModel.class */
public class GenericObjectModel extends StringModel implements MethodCallAwareTemplateHashModel {
    static final ModelFactory FACTORY = (object, wrapper) -> {
        return new GenericObjectModel(object, (BeansWrapper) wrapper);
    };

    public GenericObjectModel(Object object, BeansWrapper wrapper) {
        super(object, wrapper);
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.TemplateHashModel
    public final TemplateModel get(String key) throws TemplateModelException {
        return super.get(key);
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.MethodCallAwareTemplateHashModel
    public TemplateModel getBeforeMethodCall(String key) throws TemplateModelException, MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException {
        return super.getBeforeMethodCall(key);
    }
}
