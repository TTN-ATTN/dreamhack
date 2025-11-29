package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedVarArgsMethods.class */
class OverloadedVarArgsMethods extends OverloadedMethodsSubset {
    OverloadedVarArgsMethods(boolean bugfixed) {
        super(bugfixed);
    }

    @Override // freemarker.ext.beans.OverloadedMethodsSubset
    Class[] preprocessParameterTypes(CallableMemberDescriptor memberDesc) {
        Class[] preprocessedParamTypes = (Class[]) memberDesc.getParamTypes().clone();
        int ln = preprocessedParamTypes.length;
        Class varArgsCompType = preprocessedParamTypes[ln - 1].getComponentType();
        if (varArgsCompType == null) {
            throw new BugException("Only varargs methods should be handled here");
        }
        preprocessedParamTypes[ln - 1] = varArgsCompType;
        return preprocessedParamTypes;
    }

    @Override // freemarker.ext.beans.OverloadedMethodsSubset
    void afterWideningUnwrappingHints(Class[] paramTypes, int[] paramNumericalTypes) {
        Class[] oneLongerHints;
        int paramCount = paramTypes.length;
        Class[][] unwrappingHintsByParamCount = getUnwrappingHintsByParamCount();
        int i = paramCount - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            Class[] previousHints = unwrappingHintsByParamCount[i];
            if (previousHints != null) {
                widenHintsToCommonSupertypes(paramCount, previousHints, getTypeFlags(i));
                break;
            }
            i--;
        }
        if (paramCount + 1 < unwrappingHintsByParamCount.length && (oneLongerHints = unwrappingHintsByParamCount[paramCount + 1]) != null) {
            widenHintsToCommonSupertypes(paramCount, oneLongerHints, getTypeFlags(paramCount + 1));
        }
        for (int i2 = paramCount + 1; i2 < unwrappingHintsByParamCount.length; i2++) {
            widenHintsToCommonSupertypes(i2, paramTypes, paramNumericalTypes);
        }
        if (paramCount > 0) {
            widenHintsToCommonSupertypes(paramCount - 1, paramTypes, paramNumericalTypes);
        }
    }

    private void widenHintsToCommonSupertypes(int paramCountOfWidened, Class[] wideningTypes, int[] wideningTypeFlags) {
        Class[] typesToWiden = getUnwrappingHintsByParamCount()[paramCountOfWidened];
        if (typesToWiden == null) {
            return;
        }
        int typesToWidenLen = typesToWiden.length;
        int wideningTypesLen = wideningTypes.length;
        int min = Math.min(wideningTypesLen, typesToWidenLen);
        for (int i = 0; i < min; i++) {
            typesToWiden[i] = getCommonSupertypeForUnwrappingHint(typesToWiden[i], wideningTypes[i]);
        }
        if (typesToWidenLen > wideningTypesLen) {
            Class varargsComponentType = wideningTypes[wideningTypesLen - 1];
            for (int i2 = wideningTypesLen; i2 < typesToWidenLen; i2++) {
                typesToWiden[i2] = getCommonSupertypeForUnwrappingHint(typesToWiden[i2], varargsComponentType);
            }
        }
        if (this.bugfixed) {
            mergeInTypesFlags(paramCountOfWidened, wideningTypeFlags);
        }
    }

    @Override // freemarker.ext.beans.OverloadedMethodsSubset
    MaybeEmptyMemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException, ArrayIndexOutOfBoundsException, IllegalArgumentException, NegativeArraySizeException {
        if (tmArgs == null) {
            tmArgs = Collections.EMPTY_LIST;
        }
        int argsLen = tmArgs.size();
        Class[][] unwrappingHintsByParamCount = getUnwrappingHintsByParamCount();
        Object[] pojoArgs = new Object[argsLen];
        int[] typesFlags = null;
        int paramCount = Math.min(argsLen + 1, unwrappingHintsByParamCount.length - 1);
        loop0: while (paramCount >= 0) {
            Class[] unwarappingHints = unwrappingHintsByParamCount[paramCount];
            if (unwarappingHints == null) {
                if (paramCount == 0) {
                    return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
                }
            } else {
                typesFlags = getTypeFlags(paramCount);
                if (typesFlags == ALL_ZEROS_ARRAY) {
                    typesFlags = null;
                }
                Iterator it = tmArgs.iterator();
                int i = 0;
                while (i < argsLen) {
                    int paramIdx = i < paramCount ? i : paramCount - 1;
                    Object pojo = unwrapper.tryUnwrapTo((TemplateModel) it.next(), unwarappingHints[paramIdx], typesFlags != null ? typesFlags[paramIdx] : 0);
                    if (pojo == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                        break;
                    }
                    pojoArgs[i] = pojo;
                    i++;
                }
                break loop0;
            }
            paramCount--;
        }
        MaybeEmptyCallableMemberDescriptor maybeEmtpyMemberDesc = getMemberDescriptorForArgs(pojoArgs, true);
        if (maybeEmtpyMemberDesc instanceof CallableMemberDescriptor) {
            CallableMemberDescriptor memberDesc = (CallableMemberDescriptor) maybeEmtpyMemberDesc;
            Object argsOrErrorIdx = replaceVarargsSectionWithArray(pojoArgs, tmArgs, memberDesc, unwrapper);
            if (argsOrErrorIdx instanceof Object[]) {
                Object[] pojoArgsWithArray = (Object[]) argsOrErrorIdx;
                if (this.bugfixed) {
                    if (typesFlags != null) {
                        forceNumberArgumentsToParameterTypes(pojoArgsWithArray, memberDesc.getParamTypes(), typesFlags);
                    }
                } else {
                    BeansWrapper.coerceBigDecimals((Class<?>[]) memberDesc.getParamTypes(), pojoArgsWithArray);
                }
                return new MemberAndArguments(memberDesc, pojoArgsWithArray);
            }
            return EmptyMemberAndArguments.noCompatibleOverload(((Integer) argsOrErrorIdx).intValue());
        }
        return EmptyMemberAndArguments.from((EmptyCallableMemberDescriptor) maybeEmtpyMemberDesc, pojoArgs);
    }

    private Object replaceVarargsSectionWithArray(Object[] args, List modelArgs, CallableMemberDescriptor memberDesc, BeansWrapper unwrapper) throws TemplateModelException, ArrayIndexOutOfBoundsException, IllegalArgumentException, NegativeArraySizeException {
        Class[] paramTypes = memberDesc.getParamTypes();
        int paramCount = paramTypes.length;
        Class varArgsCompType = paramTypes[paramCount - 1].getComponentType();
        int totalArgCount = args.length;
        int fixArgCount = paramCount - 1;
        if (args.length != paramCount) {
            Object[] packedArgs = new Object[paramCount];
            System.arraycopy(args, 0, packedArgs, 0, fixArgCount);
            Object varargs = Array.newInstance((Class<?>) varArgsCompType, totalArgCount - fixArgCount);
            for (int i = fixArgCount; i < totalArgCount; i++) {
                Object val = unwrapper.tryUnwrapTo((TemplateModel) modelArgs.get(i), varArgsCompType);
                if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                    return Integer.valueOf(i + 1);
                }
                Array.set(varargs, i - fixArgCount, val);
            }
            packedArgs[fixArgCount] = varargs;
            return packedArgs;
        }
        Object val2 = unwrapper.tryUnwrapTo((TemplateModel) modelArgs.get(fixArgCount), varArgsCompType);
        if (val2 == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
            return Integer.valueOf(fixArgCount + 1);
        }
        Object array = Array.newInstance((Class<?>) varArgsCompType, 1);
        Array.set(array, 0, val2);
        args[fixArgCount] = array;
        return args;
    }
}
