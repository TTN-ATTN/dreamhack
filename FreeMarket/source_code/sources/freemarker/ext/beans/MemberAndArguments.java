package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.InvocationTargetException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MemberAndArguments.class */
class MemberAndArguments extends MaybeEmptyMemberAndArguments {
    private final CallableMemberDescriptor callableMemberDesc;
    private final Object[] args;

    MemberAndArguments(CallableMemberDescriptor memberDesc, Object[] args) {
        this.callableMemberDesc = memberDesc;
        this.args = args;
    }

    Object[] getArgs() {
        return this.args;
    }

    TemplateModel invokeMethod(BeansWrapper bw, Object obj) throws IllegalAccessException, TemplateModelException, InvocationTargetException {
        return this.callableMemberDesc.invokeMethod(bw, obj, this.args);
    }

    Object invokeConstructor(BeansWrapper bw) throws IllegalAccessException, TemplateModelException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        return this.callableMemberDesc.invokeConstructor(bw, this.args);
    }

    CallableMemberDescriptor getCallableMemberDescriptor() {
        return this.callableMemberDesc;
    }
}
