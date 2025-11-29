package freemarker.ext.beans;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import java.util.Collections;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedMethodsModel.class */
public class OverloadedMethodsModel implements TemplateMethodModelEx, TemplateSequenceModel {
    private final Object object;
    private final OverloadedMethods overloadedMethods;
    private final BeansWrapper wrapper;

    OverloadedMethodsModel(Object object, OverloadedMethods overloadedMethods, BeansWrapper wrapper) {
        this.object = object;
        this.overloadedMethods = overloadedMethods;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List arguments) throws TemplateModelException {
        MemberAndArguments maa = this.overloadedMethods.getMemberAndArguments(arguments, this.wrapper);
        try {
            return maa.invokeMethod(this.wrapper, this.object);
        } catch (Exception e) {
            if (e instanceof TemplateModelException) {
                throw ((TemplateModelException) e);
            }
            throw _MethodUtil.newInvocationTemplateModelException(this.object, maa.getCallableMemberDescriptor(), e);
        }
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        return (TemplateModel) exec(Collections.singletonList(new SimpleNumber(Integer.valueOf(index))));
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        throw new TemplateModelException("?size is unsupported for " + getClass().getName());
    }
}
