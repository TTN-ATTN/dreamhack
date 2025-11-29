package freemarker.core;

import freemarker.template.MethodCallAwareTemplateHashModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DotBeforeMethodCall.class */
class DotBeforeMethodCall extends Dot {
    public DotBeforeMethodCall(Dot dot) {
        super(dot);
    }

    @Override // freemarker.core.Dot
    protected TemplateModel evalOnHash(TemplateHashModel leftModel) throws TemplateException {
        if (leftModel instanceof MethodCallAwareTemplateHashModel) {
            try {
                return ((MethodCallAwareTemplateHashModel) leftModel).getBeforeMethodCall(this.key);
            } catch (MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException e) {
                String hint = e.getHint();
                throw new NonMethodException(this, e.getActualValue(), hint != null ? new String[]{hint} : null, Environment.getCurrentEnvironment());
            }
        }
        return super.evalOnHash(leftModel);
    }
}
