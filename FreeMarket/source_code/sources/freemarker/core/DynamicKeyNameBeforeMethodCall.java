package freemarker.core;

import freemarker.template.MethodCallAwareTemplateHashModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DynamicKeyNameBeforeMethodCall.class */
class DynamicKeyNameBeforeMethodCall extends DynamicKeyName {
    DynamicKeyNameBeforeMethodCall(DynamicKeyName dynamicKeyName) {
        super(dynamicKeyName);
    }

    @Override // freemarker.core.DynamicKeyName
    protected TemplateModel getFromHashModelWithStringKey(TemplateHashModel targetModel, String key) throws TemplateException {
        if (targetModel instanceof MethodCallAwareTemplateHashModel) {
            try {
                return ((MethodCallAwareTemplateHashModel) targetModel).getBeforeMethodCall(key);
            } catch (MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException e) {
                String hint = e.getHint();
                throw new NonMethodException(this, e.getActualValue(), hint != null ? new String[]{hint} : null, Environment.getCurrentEnvironment());
            }
        }
        return super.getFromHashModelWithStringKey(targetModel, key);
    }
}
