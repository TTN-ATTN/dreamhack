package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ArgumentTypes.class */
final class ArgumentTypes {
    private static final int CONVERSION_DIFFICULTY_REFLECTION = 0;
    private static final int CONVERSION_DIFFICULTY_FREEMARKER = 1;
    private static final int CONVERSION_DIFFICULTY_IMPOSSIBLE = 2;
    private final Class<?>[] types;
    private final boolean bugfixed;

    ArgumentTypes(Object[] args, boolean bugfixed) {
        Class cls;
        int ln = args.length;
        Class<?>[] typesTmp = new Class[ln];
        for (int i = 0; i < ln; i++) {
            Object arg = args[i];
            int i2 = i;
            if (arg == null) {
                cls = bugfixed ? Null.class : Object.class;
            } else {
                cls = arg.getClass();
            }
            typesTmp[i2] = cls;
        }
        this.types = typesTmp;
        this.bugfixed = bugfixed;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.types.length; i++) {
            hash ^= this.types[i].hashCode();
        }
        return hash;
    }

    public boolean equals(Object o) {
        if (o instanceof ArgumentTypes) {
            ArgumentTypes cs = (ArgumentTypes) o;
            if (cs.types.length != this.types.length) {
                return false;
            }
            for (int i = 0; i < this.types.length; i++) {
                if (cs.types[i] != this.types[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    MaybeEmptyCallableMemberDescriptor getMostSpecific(List<ReflectionCallableMemberDescriptor> memberDescs, boolean varArg) {
        LinkedList<CallableMemberDescriptor> applicables = getApplicables(memberDescs, varArg);
        if (applicables.isEmpty()) {
            return EmptyCallableMemberDescriptor.NO_SUCH_METHOD;
        }
        if (applicables.size() == 1) {
            return applicables.getFirst();
        }
        LinkedList<CallableMemberDescriptor> maximals = new LinkedList<>();
        Iterator<CallableMemberDescriptor> it = applicables.iterator();
        while (it.hasNext()) {
            CallableMemberDescriptor applicable = it.next();
            boolean lessSpecific = false;
            Iterator<CallableMemberDescriptor> maximalsIter = maximals.iterator();
            while (maximalsIter.hasNext()) {
                CallableMemberDescriptor maximal = maximalsIter.next();
                int cmpRes = compareParameterListPreferability(applicable.getParamTypes(), maximal.getParamTypes(), varArg);
                if (cmpRes > 0) {
                    maximalsIter.remove();
                } else if (cmpRes < 0) {
                    lessSpecific = true;
                }
            }
            if (!lessSpecific) {
                maximals.addLast(applicable);
            }
        }
        if (maximals.size() > 1) {
            return EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD;
        }
        return maximals.getFirst();
    }

    int compareParameterListPreferability(Class<?>[] paramTypes1, Class<?>[] paramTypes2, boolean varArg) {
        int r;
        int numConvPrice1;
        int numConvPrice2;
        int winerParam;
        int argTypesLen = this.types.length;
        int paramTypes1Len = paramTypes1.length;
        int paramTypes2Len = paramTypes2.length;
        if (this.bugfixed) {
            int paramList1WeakWinCnt = 0;
            int paramList2WeakWinCnt = 0;
            int paramList1WinCnt = 0;
            int paramList2WinCnt = 0;
            int paramList1StrongWinCnt = 0;
            int paramList2StrongWinCnt = 0;
            int paramList1VeryStrongWinCnt = 0;
            int paramList2VeryStrongWinCnt = 0;
            int firstWinerParamList = 0;
            for (int i = 0; i < argTypesLen; i++) {
                Class<?> paramType1 = getParamType(paramTypes1, paramTypes1Len, i, varArg);
                Class<?> paramType2 = getParamType(paramTypes2, paramTypes2Len, i, varArg);
                if (paramType1 == paramType2) {
                    winerParam = 0;
                } else {
                    Class<?> argType = this.types[i];
                    boolean argIsNum = Number.class.isAssignableFrom(argType);
                    if (argIsNum && ClassUtil.isNumerical(paramType1)) {
                        Class<?> nonPrimParamType1 = paramType1.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType1) : paramType1;
                        numConvPrice1 = OverloadedNumberUtil.getArgumentConversionPrice(argType, nonPrimParamType1);
                    } else {
                        numConvPrice1 = Integer.MAX_VALUE;
                    }
                    if (argIsNum && ClassUtil.isNumerical(paramType2)) {
                        Class<?> nonPrimParamType2 = paramType2.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType2) : paramType2;
                        numConvPrice2 = OverloadedNumberUtil.getArgumentConversionPrice(argType, nonPrimParamType2);
                    } else {
                        numConvPrice2 = Integer.MAX_VALUE;
                    }
                    if (numConvPrice1 == Integer.MAX_VALUE) {
                        if (numConvPrice2 == Integer.MAX_VALUE) {
                            if (List.class.isAssignableFrom(argType) && (paramType1.isArray() || paramType2.isArray())) {
                                if (paramType1.isArray()) {
                                    if (paramType2.isArray()) {
                                        int r2 = compareParameterListPreferability_cmpTypeSpecificty(paramType1.getComponentType(), paramType2.getComponentType());
                                        if (r2 > 0) {
                                            winerParam = 2;
                                            paramList2StrongWinCnt++;
                                        } else if (r2 < 0) {
                                            winerParam = 1;
                                            paramList1StrongWinCnt++;
                                        } else {
                                            winerParam = 0;
                                        }
                                    } else if (Collection.class.isAssignableFrom(paramType2)) {
                                        winerParam = 2;
                                        paramList2StrongWinCnt++;
                                    } else {
                                        winerParam = 1;
                                        paramList1WeakWinCnt++;
                                    }
                                } else if (Collection.class.isAssignableFrom(paramType1)) {
                                    winerParam = 1;
                                    paramList1StrongWinCnt++;
                                } else {
                                    winerParam = 2;
                                    paramList2WeakWinCnt++;
                                }
                            } else if (argType.isArray() && (List.class.isAssignableFrom(paramType1) || List.class.isAssignableFrom(paramType2))) {
                                if (List.class.isAssignableFrom(paramType1)) {
                                    if (List.class.isAssignableFrom(paramType2)) {
                                        winerParam = 0;
                                    } else {
                                        winerParam = 2;
                                        paramList2VeryStrongWinCnt++;
                                    }
                                } else {
                                    winerParam = 1;
                                    paramList1VeryStrongWinCnt++;
                                }
                            } else {
                                int r3 = compareParameterListPreferability_cmpTypeSpecificty(paramType1, paramType2);
                                if (r3 > 0) {
                                    winerParam = 1;
                                    if (r3 > 1) {
                                        paramList1WinCnt++;
                                    } else {
                                        paramList1WeakWinCnt++;
                                    }
                                } else if (r3 < 0) {
                                    winerParam = -1;
                                    if (r3 < -1) {
                                        paramList2WinCnt++;
                                    } else {
                                        paramList2WeakWinCnt++;
                                    }
                                } else {
                                    winerParam = 0;
                                }
                            }
                        } else {
                            winerParam = -1;
                            paramList2WinCnt++;
                        }
                    } else if (numConvPrice2 == Integer.MAX_VALUE) {
                        winerParam = 1;
                        paramList1WinCnt++;
                    } else if (numConvPrice1 != numConvPrice2) {
                        if (numConvPrice1 < numConvPrice2) {
                            winerParam = 1;
                            if (numConvPrice1 < 40000 && numConvPrice2 > 40000) {
                                paramList1StrongWinCnt++;
                            } else {
                                paramList1WinCnt++;
                            }
                        } else {
                            winerParam = -1;
                            if (numConvPrice2 < 40000 && numConvPrice1 > 40000) {
                                paramList2StrongWinCnt++;
                            } else {
                                paramList2WinCnt++;
                            }
                        }
                    } else {
                        winerParam = (paramType1.isPrimitive() ? 1 : 0) - (paramType2.isPrimitive() ? 1 : 0);
                        if (winerParam == 1) {
                            paramList1WeakWinCnt++;
                        } else if (winerParam == -1) {
                            paramList2WeakWinCnt++;
                        }
                    }
                }
                if (firstWinerParamList == 0 && winerParam != 0) {
                    firstWinerParamList = winerParam;
                }
            }
            if (paramList1VeryStrongWinCnt != paramList2VeryStrongWinCnt) {
                return paramList1VeryStrongWinCnt - paramList2VeryStrongWinCnt;
            }
            if (paramList1StrongWinCnt != paramList2StrongWinCnt) {
                return paramList1StrongWinCnt - paramList2StrongWinCnt;
            }
            if (paramList1WinCnt != paramList2WinCnt) {
                return paramList1WinCnt - paramList2WinCnt;
            }
            if (paramList1WeakWinCnt != paramList2WeakWinCnt) {
                return paramList1WeakWinCnt - paramList2WeakWinCnt;
            }
            if (firstWinerParamList != 0) {
                return firstWinerParamList;
            }
            if (varArg) {
                if (paramTypes1Len == paramTypes2Len) {
                    if (argTypesLen == paramTypes1Len - 1) {
                        Class<?> paramType12 = getParamType(paramTypes1, paramTypes1Len, argTypesLen, true);
                        Class<?> paramType22 = getParamType(paramTypes2, paramTypes2Len, argTypesLen, true);
                        return (ClassUtil.isNumerical(paramType12) && ClassUtil.isNumerical(paramType22) && (r = OverloadedNumberUtil.compareNumberTypeSpecificity(paramType12, paramType22)) != 0) ? r : compareParameterListPreferability_cmpTypeSpecificty(paramType12, paramType22);
                    }
                    return 0;
                }
                return paramTypes1Len - paramTypes2Len;
            }
            return 0;
        }
        boolean paramTypes1HasAMoreSpecific = false;
        boolean paramTypes2HasAMoreSpecific = false;
        for (int i2 = 0; i2 < paramTypes1Len; i2++) {
            Class<?> paramType13 = getParamType(paramTypes1, paramTypes1Len, i2, varArg);
            Class<?> paramType23 = getParamType(paramTypes2, paramTypes2Len, i2, varArg);
            if (paramType13 != paramType23) {
                paramTypes1HasAMoreSpecific = paramTypes1HasAMoreSpecific || _MethodUtil.isMoreOrSameSpecificParameterType(paramType13, paramType23, false, 0) != 0;
                paramTypes2HasAMoreSpecific = paramTypes2HasAMoreSpecific || _MethodUtil.isMoreOrSameSpecificParameterType(paramType23, paramType13, false, 0) != 0;
            }
        }
        if (paramTypes1HasAMoreSpecific) {
            return paramTypes2HasAMoreSpecific ? 0 : 1;
        }
        if (paramTypes2HasAMoreSpecific) {
            return -1;
        }
        return 0;
    }

    private int compareParameterListPreferability_cmpTypeSpecificty(Class<?> paramType1, Class<?> paramType2) {
        Class<?> nonPrimParamType1 = paramType1.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType1) : paramType1;
        Class<?> nonPrimParamType2 = paramType2.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType2) : paramType2;
        if (nonPrimParamType1 == nonPrimParamType2) {
            if (nonPrimParamType1 != paramType1) {
                if (nonPrimParamType2 != paramType2) {
                    return 0;
                }
                return 1;
            }
            if (nonPrimParamType2 != paramType2) {
                return -1;
            }
            return 0;
        }
        if (nonPrimParamType2.isAssignableFrom(nonPrimParamType1)) {
            return 2;
        }
        if (nonPrimParamType1.isAssignableFrom(nonPrimParamType2)) {
            return -2;
        }
        if (nonPrimParamType1 == Character.class && nonPrimParamType2.isAssignableFrom(String.class)) {
            return 2;
        }
        if (nonPrimParamType2 == Character.class && nonPrimParamType1.isAssignableFrom(String.class)) {
            return -2;
        }
        return 0;
    }

    private static Class<?> getParamType(Class<?>[] paramTypes, int paramTypesLen, int i, boolean varArg) {
        return (!varArg || i < paramTypesLen - 1) ? paramTypes[i] : paramTypes[paramTypesLen - 1].getComponentType();
    }

    LinkedList<CallableMemberDescriptor> getApplicables(List<ReflectionCallableMemberDescriptor> memberDescs, boolean varArg) {
        LinkedList<CallableMemberDescriptor> applicables = new LinkedList<>();
        for (ReflectionCallableMemberDescriptor memberDesc : memberDescs) {
            int difficulty = isApplicable(memberDesc, varArg);
            if (difficulty != 2) {
                if (difficulty == 0) {
                    applicables.add(memberDesc);
                } else if (difficulty == 1) {
                    applicables.add(new SpecialConversionCallableMemberDescriptor(memberDesc));
                } else {
                    throw new BugException();
                }
            }
        }
        return applicables;
    }

    private int isApplicable(ReflectionCallableMemberDescriptor memberDesc, boolean varArg) {
        Class<?>[] paramTypes = memberDesc.getParamTypes();
        int cl = this.types.length;
        int fl = paramTypes.length - (varArg ? 1 : 0);
        if (varArg) {
            if (cl < fl) {
                return 2;
            }
        } else if (cl != fl) {
            return 2;
        }
        int maxDifficulty = 0;
        for (int i = 0; i < fl; i++) {
            int difficulty = isMethodInvocationConvertible(paramTypes[i], this.types[i]);
            if (difficulty == 2) {
                return 2;
            }
            if (maxDifficulty < difficulty) {
                maxDifficulty = difficulty;
            }
        }
        if (varArg) {
            Class<?> varArgParamType = paramTypes[fl].getComponentType();
            for (int i2 = fl; i2 < cl; i2++) {
                int difficulty2 = isMethodInvocationConvertible(varArgParamType, this.types[i2]);
                if (difficulty2 == 2) {
                    return 2;
                }
                if (maxDifficulty < difficulty2) {
                    maxDifficulty = difficulty2;
                }
            }
        }
        return maxDifficulty;
    }

    private int isMethodInvocationConvertible(Class<?> formal, Class<?> actual) {
        Class<?> formalNP;
        if (formal.isAssignableFrom(actual) && actual != CharacterOrString.class) {
            return 0;
        }
        if (this.bugfixed) {
            if (formal.isPrimitive()) {
                if (actual == Null.class) {
                    return 2;
                }
                formalNP = ClassUtil.primitiveClassToBoxingClass(formal);
                if (actual == formalNP) {
                    return 0;
                }
            } else {
                if (actual == Null.class) {
                    return 0;
                }
                formalNP = formal;
            }
            if (Number.class.isAssignableFrom(actual) && Number.class.isAssignableFrom(formalNP)) {
                return OverloadedNumberUtil.getArgumentConversionPrice(actual, formalNP) == Integer.MAX_VALUE ? 2 : 0;
            }
            if (formal.isArray()) {
                return List.class.isAssignableFrom(actual) ? 1 : 2;
            }
            if (actual.isArray() && formal.isAssignableFrom(List.class)) {
                return 1;
            }
            if (actual == CharacterOrString.class) {
                if (formal.isAssignableFrom(String.class) || formal.isAssignableFrom(Character.class) || formal == Character.TYPE) {
                    return 1;
                }
                return 2;
            }
            return 2;
        }
        if (formal.isPrimitive()) {
            if (formal == Boolean.TYPE) {
                return actual == Boolean.class ? 0 : 2;
            }
            if (formal == Double.TYPE && (actual == Double.class || actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return 0;
            }
            if (formal == Integer.TYPE && (actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return 0;
            }
            if (formal == Long.TYPE && (actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return 0;
            }
            if (formal == Float.TYPE && (actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return 0;
            }
            if (formal == Character.TYPE) {
                return actual == Character.class ? 0 : 2;
            }
            if (formal == Byte.TYPE && actual == Byte.class) {
                return 0;
            }
            if (formal == Short.TYPE && (actual == Short.class || actual == Byte.class)) {
                return 0;
            }
            if (BigDecimal.class.isAssignableFrom(actual) && ClassUtil.isNumerical(formal)) {
                return 0;
            }
            return 2;
        }
        return 2;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ArgumentTypes$Null.class */
    private static class Null {
        private Null() {
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ArgumentTypes$SpecialConversionCallableMemberDescriptor.class */
    private static final class SpecialConversionCallableMemberDescriptor extends CallableMemberDescriptor {
        private final ReflectionCallableMemberDescriptor callableMemberDesc;

        SpecialConversionCallableMemberDescriptor(ReflectionCallableMemberDescriptor callableMemberDesc) {
            this.callableMemberDesc = callableMemberDesc;
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        TemplateModel invokeMethod(BeansWrapper bw, Object obj, Object[] args) throws IllegalAccessException, TemplateModelException, InvocationTargetException {
            convertArgsToReflectionCompatible(bw, args);
            return this.callableMemberDesc.invokeMethod(bw, obj, args);
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        Object invokeConstructor(BeansWrapper bw, Object[] args) throws IllegalAccessException, TemplateModelException, InstantiationException, IllegalArgumentException, InvocationTargetException {
            convertArgsToReflectionCompatible(bw, args);
            return this.callableMemberDesc.invokeConstructor(bw, args);
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        String getDeclaration() {
            return this.callableMemberDesc.getDeclaration();
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        boolean isConstructor() {
            return this.callableMemberDesc.isConstructor();
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        boolean isStatic() {
            return this.callableMemberDesc.isStatic();
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        boolean isVarargs() {
            return this.callableMemberDesc.isVarargs();
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        Class<?>[] getParamTypes() {
            return this.callableMemberDesc.getParamTypes();
        }

        @Override // freemarker.ext.beans.CallableMemberDescriptor
        String getName() {
            return this.callableMemberDesc.getName();
        }

        private void convertArgsToReflectionCompatible(BeansWrapper bw, Object[] args) throws TemplateModelException {
            Class[] paramTypes = this.callableMemberDesc.getParamTypes();
            int ln = paramTypes.length;
            for (int i = 0; i < ln; i++) {
                Class paramType = paramTypes[i];
                Object arg = args[i];
                if (arg != null) {
                    if (paramType.isArray() && (arg instanceof List)) {
                        args[i] = bw.listToArray((List) arg, paramType, null);
                    }
                    if (arg.getClass().isArray() && paramType.isAssignableFrom(List.class)) {
                        args[i] = bw.arrayToList(arg);
                    }
                    if (arg instanceof CharacterOrString) {
                        if (paramType == Character.class || paramType == Character.TYPE || (!paramType.isAssignableFrom(String.class) && paramType.isAssignableFrom(Character.class))) {
                            args[i] = Character.valueOf(((CharacterOrString) arg).getAsChar());
                        } else {
                            args[i] = ((CharacterOrString) arg).getAsString();
                        }
                    }
                }
            }
        }
    }
}
