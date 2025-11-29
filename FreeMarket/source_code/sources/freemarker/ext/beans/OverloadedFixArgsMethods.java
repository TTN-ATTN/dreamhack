package freemarker.ext.beans;

import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedFixArgsMethods.class */
class OverloadedFixArgsMethods extends OverloadedMethodsSubset {
    OverloadedFixArgsMethods(boolean bugfixed) {
        super(bugfixed);
    }

    @Override // freemarker.ext.beans.OverloadedMethodsSubset
    Class[] preprocessParameterTypes(CallableMemberDescriptor memberDesc) {
        return memberDesc.getParamTypes();
    }

    @Override // freemarker.ext.beans.OverloadedMethodsSubset
    void afterWideningUnwrappingHints(Class[] paramTypes, int[] paramNumericalTypes) {
    }

    @Override // freemarker.ext.beans.OverloadedMethodsSubset
    MaybeEmptyMemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
        if (tmArgs == null) {
            tmArgs = Collections.EMPTY_LIST;
        }
        int argCount = tmArgs.size();
        Class[][] unwrappingHintsByParamCount = getUnwrappingHintsByParamCount();
        if (unwrappingHintsByParamCount.length <= argCount) {
            return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
        }
        Class[] unwarppingHints = unwrappingHintsByParamCount[argCount];
        if (unwarppingHints == null) {
            return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
        }
        Object[] pojoArgs = new Object[argCount];
        int[] typeFlags = getTypeFlags(argCount);
        if (typeFlags == ALL_ZEROS_ARRAY) {
            typeFlags = null;
        }
        Iterator it = tmArgs.iterator();
        for (int i = 0; i < argCount; i++) {
            Object pojo = unwrapper.tryUnwrapTo((TemplateModel) it.next(), unwarppingHints[i], typeFlags != null ? typeFlags[i] : 0);
            if (pojo == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                return EmptyMemberAndArguments.noCompatibleOverload(i + 1);
            }
            pojoArgs[i] = pojo;
        }
        MaybeEmptyCallableMemberDescriptor maybeEmtpyMemberDesc = getMemberDescriptorForArgs(pojoArgs, false);
        if (maybeEmtpyMemberDesc instanceof CallableMemberDescriptor) {
            CallableMemberDescriptor memberDesc = (CallableMemberDescriptor) maybeEmtpyMemberDesc;
            if (this.bugfixed) {
                if (typeFlags != null) {
                    forceNumberArgumentsToParameterTypes(pojoArgs, memberDesc.getParamTypes(), typeFlags);
                }
            } else {
                BeansWrapper.coerceBigDecimals((Class<?>[]) memberDesc.getParamTypes(), pojoArgs);
            }
            return new MemberAndArguments(memberDesc, pojoArgs);
        }
        return EmptyMemberAndArguments.from((EmptyCallableMemberDescriptor) maybeEmtpyMemberDesc, pojoArgs);
    }
}
