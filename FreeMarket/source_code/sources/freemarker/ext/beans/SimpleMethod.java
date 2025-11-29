package freemarker.ext.beans;

import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedOrdinal;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/SimpleMethod.class */
class SimpleMethod {
    static final String MARKUP_OUTPUT_TO_STRING_TIP = "A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.";
    private final Member member;
    private final Class[] argTypes;

    protected SimpleMethod(Member member, Class[] argTypes) {
        this.member = member;
        this.argTypes = argTypes;
    }

    Object[] unwrapArguments(List arguments, BeansWrapper wrapper) throws TemplateModelException {
        if (arguments == null) {
            arguments = Collections.EMPTY_LIST;
        }
        boolean isVarArg = _MethodUtil.isVarargs(this.member);
        int typesLen = this.argTypes.length;
        if (isVarArg) {
            if (typesLen - 1 > arguments.size()) {
                Object[] objArr = new Object[7];
                objArr[0] = _MethodUtil.invocationErrorMessageStart(this.member);
                objArr[1] = " takes at least ";
                objArr[2] = Integer.valueOf(typesLen - 1);
                objArr[3] = typesLen - 1 == 1 ? " argument" : " arguments";
                objArr[4] = ", but ";
                objArr[5] = Integer.valueOf(arguments.size());
                objArr[6] = " was given.";
                throw new _TemplateModelException(objArr);
            }
        } else if (typesLen != arguments.size()) {
            Object[] objArr2 = new Object[7];
            objArr2[0] = _MethodUtil.invocationErrorMessageStart(this.member);
            objArr2[1] = " takes ";
            objArr2[2] = Integer.valueOf(typesLen);
            objArr2[3] = typesLen == 1 ? " argument" : " arguments";
            objArr2[4] = ", but ";
            objArr2[5] = Integer.valueOf(arguments.size());
            objArr2[6] = " was given.";
            throw new _TemplateModelException(objArr2);
        }
        Object[] args = unwrapArguments(arguments, this.argTypes, isVarArg, wrapper);
        return args;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object[] unwrapArguments(List args, Class[] argTypes, boolean isVarargs, BeansWrapper beansWrapper) throws TemplateModelException, ArrayIndexOutOfBoundsException, IllegalArgumentException, NegativeArraySizeException {
        Object unwrappedArgVal;
        if (args == null) {
            return null;
        }
        int typesLen = argTypes.length;
        int argsLen = args.size();
        Object[] unwrappedArgs = new Object[typesLen];
        Iterator it = args.iterator();
        int normalArgCnt = isVarargs ? typesLen - 1 : typesLen;
        int argIdx = 0;
        while (argIdx < normalArgCnt) {
            Class argType = argTypes[argIdx];
            TemplateModel argVal = (TemplateModel) it.next();
            Object unwrappedArgVal2 = beansWrapper.tryUnwrapTo(argVal, argType);
            if (unwrappedArgVal2 == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                throw createArgumentTypeMismarchException(argIdx, argVal, argType);
            }
            if (unwrappedArgVal2 == null && argType.isPrimitive()) {
                throw createNullToPrimitiveArgumentException(argIdx, argType);
            }
            int i = argIdx;
            argIdx++;
            unwrappedArgs[i] = unwrappedArgVal2;
        }
        if (isVarargs) {
            Class varargType = argTypes[typesLen - 1];
            Class varargItemType = varargType.getComponentType();
            if (!it.hasNext()) {
                int i2 = argIdx;
                int i3 = argIdx + 1;
                unwrappedArgs[i2] = Array.newInstance((Class<?>) varargItemType, 0);
            } else {
                TemplateModel argVal2 = (TemplateModel) it.next();
                if (argsLen - argIdx == 1 && (unwrappedArgVal = beansWrapper.tryUnwrapTo(argVal2, varargType)) != ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                    int i4 = argIdx;
                    int i5 = argIdx + 1;
                    unwrappedArgs[i4] = unwrappedArgVal;
                } else {
                    int varargArrayLen = argsLen - argIdx;
                    Object varargArray = Array.newInstance((Class<?>) varargItemType, varargArrayLen);
                    int varargIdx = 0;
                    while (varargIdx < varargArrayLen) {
                        TemplateModel varargVal = (TemplateModel) (varargIdx == 0 ? argVal2 : it.next());
                        Object unwrappedVarargVal = beansWrapper.tryUnwrapTo(varargVal, varargItemType);
                        if (unwrappedVarargVal == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                            throw createArgumentTypeMismarchException(argIdx + varargIdx, varargVal, varargItemType);
                        }
                        if (unwrappedVarargVal == null && varargItemType.isPrimitive()) {
                            throw createNullToPrimitiveArgumentException(argIdx + varargIdx, varargItemType);
                        }
                        Array.set(varargArray, varargIdx, unwrappedVarargVal);
                        varargIdx++;
                    }
                    int i6 = argIdx;
                    int i7 = argIdx + 1;
                    unwrappedArgs[i6] = varargArray;
                }
            }
        }
        return unwrappedArgs;
    }

    private TemplateModelException createArgumentTypeMismarchException(int argIdx, TemplateModel argVal, Class targetType) {
        _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder(_MethodUtil.invocationErrorMessageStart(this.member), " couldn't be called: Can't convert the ", new _DelayedOrdinal(Integer.valueOf(argIdx + 1)), " argument's value to the target Java type, ", ClassUtil.getShortClassName(targetType), ". The type of the actual value was: ", new _DelayedFTLTypeDescription(argVal));
        if ((argVal instanceof TemplateMarkupOutputModel) && targetType.isAssignableFrom(String.class)) {
            desc.tip(MARKUP_OUTPUT_TO_STRING_TIP);
        }
        return new _TemplateModelException(desc);
    }

    private TemplateModelException createNullToPrimitiveArgumentException(int argIdx, Class targetType) {
        return new _TemplateModelException(_MethodUtil.invocationErrorMessageStart(this.member), " couldn't be called: The value of the ", new _DelayedOrdinal(Integer.valueOf(argIdx + 1)), " argument was null, but the target Java parameter type (", ClassUtil.getShortClassName(targetType), ") is primitive and so can't store null.");
    }

    protected Member getMember() {
        return this.member;
    }
}
