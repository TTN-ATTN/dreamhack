package freemarker.ext.beans;

import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._DelayedConversionToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedMethods.class */
final class OverloadedMethods {
    private final OverloadedMethodsSubset fixArgMethods;
    private OverloadedMethodsSubset varargMethods;
    private final boolean bugfixed;

    OverloadedMethods(boolean bugfixed) {
        this.bugfixed = bugfixed;
        this.fixArgMethods = new OverloadedFixArgsMethods(bugfixed);
    }

    void addMethod(Method method) {
        Class[] paramTypes = method.getParameterTypes();
        addCallableMemberDescriptor(new ReflectionCallableMemberDescriptor(method, paramTypes));
    }

    void addConstructor(Constructor constr) {
        Class[] paramTypes = constr.getParameterTypes();
        addCallableMemberDescriptor(new ReflectionCallableMemberDescriptor(constr, paramTypes));
    }

    private void addCallableMemberDescriptor(ReflectionCallableMemberDescriptor memberDesc) {
        this.fixArgMethods.addCallableMemberDescriptor(memberDesc);
        if (memberDesc.isVarargs()) {
            if (this.varargMethods == null) {
                this.varargMethods = new OverloadedVarArgsMethods(this.bugfixed);
            }
            this.varargMethods.addCallableMemberDescriptor(memberDesc);
        }
    }

    MemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
        MaybeEmptyMemberAndArguments varargsRes;
        MaybeEmptyMemberAndArguments fixArgsRes = this.fixArgMethods.getMemberAndArguments(tmArgs, unwrapper);
        if (fixArgsRes instanceof MemberAndArguments) {
            return (MemberAndArguments) fixArgsRes;
        }
        if (this.varargMethods != null) {
            varargsRes = this.varargMethods.getMemberAndArguments(tmArgs, unwrapper);
            if (varargsRes instanceof MemberAndArguments) {
                return (MemberAndArguments) varargsRes;
            }
        } else {
            varargsRes = null;
        }
        _ErrorDescriptionBuilder edb = new _ErrorDescriptionBuilder(toCompositeErrorMessage((EmptyMemberAndArguments) fixArgsRes, (EmptyMemberAndArguments) varargsRes, tmArgs), "\nThe matching overload was searched among these members:\n", memberListToString());
        if (!this.bugfixed) {
            edb.tip("You seem to use BeansWrapper with incompatibleImprovements set below 2.3.21. If you think this error is unfounded, enabling 2.3.21 fixes may helps. See version history for more.");
        }
        addMarkupBITipAfterNoNoMarchIfApplicable(edb, tmArgs);
        throw new _TemplateModelException(edb);
    }

    private Object[] toCompositeErrorMessage(EmptyMemberAndArguments fixArgsEmptyRes, EmptyMemberAndArguments varargsEmptyRes, List tmArgs) {
        Object[] argsErrorMsg;
        if (varargsEmptyRes != null) {
            if (fixArgsEmptyRes == null || fixArgsEmptyRes.isNumberOfArgumentsWrong()) {
                argsErrorMsg = toErrorMessage(varargsEmptyRes, tmArgs);
            } else {
                argsErrorMsg = new Object[]{"When trying to call the non-varargs overloads:\n", toErrorMessage(fixArgsEmptyRes, tmArgs), "\nWhen trying to call the varargs overloads:\n", toErrorMessage(varargsEmptyRes, null)};
            }
        } else {
            argsErrorMsg = toErrorMessage(fixArgsEmptyRes, tmArgs);
        }
        return argsErrorMsg;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object[] toErrorMessage(EmptyMemberAndArguments emptyMemberAndArguments, List list) {
        Object[] unwrappedArguments = emptyMemberAndArguments.getUnwrappedArguments();
        Object[] objArr = new Object[3];
        objArr[0] = emptyMemberAndArguments.getErrorDescription();
        objArr[1] = list != null ? new Object[]{"\nThe FTL type of the argument values were: ", getTMActualParameterTypes(list), "."} : "";
        objArr[2] = unwrappedArguments != null ? new Object[]{"\nThe Java type of the argument values were: ", getUnwrappedActualParameterTypes(unwrappedArguments) + "."} : "";
        return objArr;
    }

    private _DelayedConversionToString memberListToString() {
        return new _DelayedConversionToString(null) { // from class: freemarker.ext.beans.OverloadedMethods.1
            @Override // freemarker.core._DelayedConversionToString
            protected String doConversion(Object obj) {
                Iterator fixArgMethodsIter = OverloadedMethods.this.fixArgMethods.getMemberDescriptors();
                Iterator varargMethodsIter = OverloadedMethods.this.varargMethods != null ? OverloadedMethods.this.varargMethods.getMemberDescriptors() : null;
                boolean hasMethods = fixArgMethodsIter.hasNext() || (varargMethodsIter != null && varargMethodsIter.hasNext());
                if (hasMethods) {
                    StringBuilder sb = new StringBuilder();
                    HashSet fixArgMethods = new HashSet();
                    while (fixArgMethodsIter.hasNext()) {
                        if (sb.length() != 0) {
                            sb.append(",\n");
                        }
                        sb.append("    ");
                        CallableMemberDescriptor callableMemberDesc = (CallableMemberDescriptor) fixArgMethodsIter.next();
                        fixArgMethods.add(callableMemberDesc);
                        sb.append(callableMemberDesc.getDeclaration());
                    }
                    if (varargMethodsIter != null) {
                        while (varargMethodsIter.hasNext()) {
                            CallableMemberDescriptor callableMemberDesc2 = (CallableMemberDescriptor) varargMethodsIter.next();
                            if (!fixArgMethods.contains(callableMemberDesc2)) {
                                if (sb.length() != 0) {
                                    sb.append(",\n");
                                }
                                sb.append("    ");
                                sb.append(callableMemberDesc2.getDeclaration());
                            }
                        }
                    }
                    return sb.toString();
                }
                return "No members";
            }
        };
    }

    private void addMarkupBITipAfterNoNoMarchIfApplicable(_ErrorDescriptionBuilder edb, List tmArgs) {
        for (int argIdx = 0; argIdx < tmArgs.size(); argIdx++) {
            Object tmArg = tmArgs.get(argIdx);
            if (tmArg instanceof TemplateMarkupOutputModel) {
                Iterator membDescs = this.fixArgMethods.getMemberDescriptors();
                while (membDescs.hasNext()) {
                    CallableMemberDescriptor membDesc = (CallableMemberDescriptor) membDescs.next();
                    Class[] paramTypes = membDesc.getParamTypes();
                    Class paramType = null;
                    if (membDesc.isVarargs() && argIdx >= paramTypes.length - 1) {
                        paramType = paramTypes[paramTypes.length - 1];
                        if (paramType.isArray()) {
                            paramType = paramType.getComponentType();
                        }
                    }
                    if (paramType == null && argIdx < paramTypes.length) {
                        paramType = paramTypes[argIdx];
                    }
                    if (paramType != null && paramType.isAssignableFrom(String.class) && !paramType.isAssignableFrom(tmArg.getClass())) {
                        edb.tip("A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.");
                        return;
                    }
                }
            }
        }
    }

    private _DelayedConversionToString getTMActualParameterTypes(List arguments) {
        String[] argumentTypeDescs = new String[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            argumentTypeDescs[i] = ClassUtil.getFTLTypeDescription((TemplateModel) arguments.get(i));
        }
        return new DelayedCallSignatureToString(argumentTypeDescs) { // from class: freemarker.ext.beans.OverloadedMethods.2
            @Override // freemarker.ext.beans.OverloadedMethods.DelayedCallSignatureToString
            String argumentToString(Object argType) {
                return (String) argType;
            }
        };
    }

    private Object getUnwrappedActualParameterTypes(Object[] unwrappedArgs) {
        Class[] argumentTypes = new Class[unwrappedArgs.length];
        for (int i = 0; i < unwrappedArgs.length; i++) {
            Object unwrappedArg = unwrappedArgs[i];
            argumentTypes[i] = unwrappedArg != null ? unwrappedArg.getClass() : null;
        }
        return new DelayedCallSignatureToString(argumentTypes) { // from class: freemarker.ext.beans.OverloadedMethods.3
            @Override // freemarker.ext.beans.OverloadedMethods.DelayedCallSignatureToString
            String argumentToString(Object argType) {
                if (argType != null) {
                    return ClassUtil.getShortClassName((Class) argType);
                }
                return ClassUtil.getShortClassNameOfObject(null);
            }
        };
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedMethods$DelayedCallSignatureToString.class */
    private abstract class DelayedCallSignatureToString extends _DelayedConversionToString {
        abstract String argumentToString(Object obj);

        public DelayedCallSignatureToString(Object[] argTypeArray) {
            super(argTypeArray);
        }

        @Override // freemarker.core._DelayedConversionToString
        protected String doConversion(Object obj) {
            Object[] argTypes = (Object[]) obj;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < argTypes.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(argumentToString(argTypes[i]));
            }
            return sb.toString();
        }
    }
}
