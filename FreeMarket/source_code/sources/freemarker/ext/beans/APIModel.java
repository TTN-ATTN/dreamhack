package freemarker.ext.beans;

import freemarker.template.MethodCallAwareTemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/APIModel.class */
final class APIModel extends BeanModel implements MethodCallAwareTemplateHashModel {
    APIModel(Object object, BeansWrapper wrapper) {
        super(object, wrapper, false);
    }

    protected boolean isMethodsShadowItems() {
        return true;
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.MethodCallAwareTemplateHashModel
    public TemplateModel getBeforeMethodCall(String key) throws TemplateModelException, MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException {
        return super.getBeforeMethodCall(key);
    }
}
