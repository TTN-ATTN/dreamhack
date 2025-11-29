package freemarker.ext.beans;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedMethodsSubset.class */
abstract class OverloadedMethodsSubset {
    static final int[] ALL_ZEROS_ARRAY = new int[0];
    private static final int[][] ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY = new int[1];
    private Class[][] unwrappingHintsByParamCount;
    private int[][] typeFlagsByParamCount;
    private final Map argTypesToMemberDescCache = new ConcurrentHashMap(6, 0.75f, 1);
    private final List memberDescs = new LinkedList();
    protected final boolean bugfixed;

    abstract Class[] preprocessParameterTypes(CallableMemberDescriptor callableMemberDescriptor);

    abstract void afterWideningUnwrappingHints(Class[] clsArr, int[] iArr);

    abstract MaybeEmptyMemberAndArguments getMemberAndArguments(List list, BeansWrapper beansWrapper) throws TemplateModelException;

    /* JADX WARN: Type inference failed for: r0v3, types: [int[], int[][]] */
    static {
        ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY[0] = ALL_ZEROS_ARRAY;
    }

    OverloadedMethodsSubset(boolean bugfixed) {
        this.bugfixed = bugfixed;
    }

    /* JADX WARN: Type inference failed for: r0v23, types: [java.lang.Class[], java.lang.Class[][], java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v26, types: [java.lang.Class[], java.lang.Class[][]] */
    void addCallableMemberDescriptor(ReflectionCallableMemberDescriptor memberDesc) {
        this.memberDescs.add(memberDesc);
        Class[] prepedParamTypes = preprocessParameterTypes(memberDesc);
        int paramCount = prepedParamTypes.length;
        if (this.unwrappingHintsByParamCount == null) {
            this.unwrappingHintsByParamCount = new Class[paramCount + 1];
            this.unwrappingHintsByParamCount[paramCount] = (Class[]) prepedParamTypes.clone();
        } else if (this.unwrappingHintsByParamCount.length <= paramCount) {
            ?? r0 = new Class[paramCount + 1];
            System.arraycopy(this.unwrappingHintsByParamCount, 0, r0, 0, this.unwrappingHintsByParamCount.length);
            this.unwrappingHintsByParamCount = r0;
            this.unwrappingHintsByParamCount[paramCount] = (Class[]) prepedParamTypes.clone();
        } else {
            Class[] unwrappingHints = this.unwrappingHintsByParamCount[paramCount];
            if (unwrappingHints == null) {
                this.unwrappingHintsByParamCount[paramCount] = (Class[]) prepedParamTypes.clone();
            } else {
                for (int paramIdx = 0; paramIdx < prepedParamTypes.length; paramIdx++) {
                    unwrappingHints[paramIdx] = getCommonSupertypeForUnwrappingHint(unwrappingHints[paramIdx], prepedParamTypes[paramIdx]);
                }
            }
        }
        int[] typeFlagsByParamIdx = ALL_ZEROS_ARRAY;
        if (this.bugfixed) {
            for (int paramIdx2 = 0; paramIdx2 < paramCount; paramIdx2++) {
                int typeFlags = TypeFlags.classToTypeFlags(prepedParamTypes[paramIdx2]);
                if (typeFlags != 0) {
                    if (typeFlagsByParamIdx == ALL_ZEROS_ARRAY) {
                        typeFlagsByParamIdx = new int[paramCount];
                    }
                    typeFlagsByParamIdx[paramIdx2] = typeFlags;
                }
            }
            mergeInTypesFlags(paramCount, typeFlagsByParamIdx);
        }
        afterWideningUnwrappingHints(this.bugfixed ? prepedParamTypes : this.unwrappingHintsByParamCount[paramCount], typeFlagsByParamIdx);
    }

    Class[][] getUnwrappingHintsByParamCount() {
        return this.unwrappingHintsByParamCount;
    }

    @SuppressFBWarnings(value = {"JLM_JSR166_UTILCONCURRENT_MONITORENTER"}, justification = "Locks for member descriptor creation only")
    final MaybeEmptyCallableMemberDescriptor getMemberDescriptorForArgs(Object[] args, boolean varArg) {
        ArgumentTypes argTypes = new ArgumentTypes(args, this.bugfixed);
        MaybeEmptyCallableMemberDescriptor memberDesc = (MaybeEmptyCallableMemberDescriptor) this.argTypesToMemberDescCache.get(argTypes);
        if (memberDesc == null) {
            synchronized (this.argTypesToMemberDescCache) {
                memberDesc = (MaybeEmptyCallableMemberDescriptor) this.argTypesToMemberDescCache.get(argTypes);
                if (memberDesc == null) {
                    memberDesc = argTypes.getMostSpecific(this.memberDescs, varArg);
                    this.argTypesToMemberDescCache.put(argTypes, memberDesc);
                }
            }
        }
        return memberDesc;
    }

    Iterator getMemberDescriptors() {
        return this.memberDescs.iterator();
    }

    protected Class getCommonSupertypeForUnwrappingHint(Class c1, Class c2) {
        boolean c1WasPrim;
        boolean c2WasPrim;
        if (c1 == c2) {
            return c1;
        }
        if (this.bugfixed) {
            if (c1.isPrimitive()) {
                c1 = ClassUtil.primitiveClassToBoxingClass(c1);
                c1WasPrim = true;
            } else {
                c1WasPrim = false;
            }
            if (c2.isPrimitive()) {
                c2 = ClassUtil.primitiveClassToBoxingClass(c2);
                c2WasPrim = true;
            } else {
                c2WasPrim = false;
            }
            if (c1 == c2) {
                return c1;
            }
            if (Number.class.isAssignableFrom(c1) && Number.class.isAssignableFrom(c2)) {
                return Number.class;
            }
            if (c1WasPrim || c2WasPrim) {
                return Object.class;
            }
        } else if (c2.isPrimitive()) {
            if (c2 == Byte.TYPE) {
                c2 = Byte.class;
            } else if (c2 == Short.TYPE) {
                c2 = Short.class;
            } else if (c2 == Character.TYPE) {
                c2 = Character.class;
            } else if (c2 == Integer.TYPE) {
                c2 = Integer.class;
            } else if (c2 == Float.TYPE) {
                c2 = Float.class;
            } else if (c2 == Long.TYPE) {
                c2 = Long.class;
            } else if (c2 == Double.TYPE) {
                c2 = Double.class;
            }
        }
        Set<Class> commonTypes = _MethodUtil.getAssignables(c1, c2);
        commonTypes.retainAll(_MethodUtil.getAssignables(c2, c1));
        if (commonTypes.isEmpty()) {
            return Object.class;
        }
        List max = new ArrayList();
        for (Class clazz : commonTypes) {
            Iterator maxIter = max.iterator();
            while (true) {
                if (maxIter.hasNext()) {
                    Class maxClazz = (Class) maxIter.next();
                    if (_MethodUtil.isMoreOrSameSpecificParameterType(maxClazz, clazz, false, 0) != 0) {
                        break;
                    }
                    if (_MethodUtil.isMoreOrSameSpecificParameterType(clazz, maxClazz, false, 0) != 0) {
                        maxIter.remove();
                    }
                } else {
                    max.add(clazz);
                    break;
                }
            }
        }
        if (max.size() > 1) {
            if (this.bugfixed) {
                Iterator it = max.iterator();
                while (it.hasNext()) {
                    Class maxCl = (Class) it.next();
                    if (!maxCl.isInterface()) {
                        if (maxCl != Object.class) {
                            return maxCl;
                        }
                        it.remove();
                    }
                }
                max.remove(Cloneable.class);
                if (max.size() > 1) {
                    max.remove(Serializable.class);
                    if (max.size() > 1) {
                        max.remove(Comparable.class);
                        if (max.size() > 1) {
                            return Object.class;
                        }
                    }
                }
            } else {
                return Object.class;
            }
        }
        return (Class) max.get(0);
    }

    protected final int[] getTypeFlags(int paramCount) {
        if (this.typeFlagsByParamCount == null || this.typeFlagsByParamCount.length <= paramCount) {
            return null;
        }
        return this.typeFlagsByParamCount[paramCount];
    }

    /* JADX WARN: Type inference failed for: r0v9, types: [int[], int[][], java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v28, types: [int[], int[][]] */
    protected final void mergeInTypesFlags(int dstParamCount, int[] srcTypeFlagsByParamIdx) {
        int srcParamTypeFlags;
        int[] dstTypeFlagsByParamIdx;
        NullArgumentException.check("srcTypesFlagsByParamIdx", srcTypeFlagsByParamIdx);
        if (dstParamCount == 0) {
            if (this.typeFlagsByParamCount == null) {
                this.typeFlagsByParamCount = ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY;
                return;
            } else {
                if (this.typeFlagsByParamCount != ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY) {
                    this.typeFlagsByParamCount[0] = ALL_ZEROS_ARRAY;
                    return;
                }
                return;
            }
        }
        if (this.typeFlagsByParamCount == null) {
            this.typeFlagsByParamCount = new int[dstParamCount + 1];
        } else if (this.typeFlagsByParamCount.length <= dstParamCount) {
            ?? r0 = new int[dstParamCount + 1];
            System.arraycopy(this.typeFlagsByParamCount, 0, r0, 0, this.typeFlagsByParamCount.length);
            this.typeFlagsByParamCount = r0;
        }
        int[] dstTypeFlagsByParamIdx2 = this.typeFlagsByParamCount[dstParamCount];
        if (dstTypeFlagsByParamIdx2 == null) {
            if (srcTypeFlagsByParamIdx != ALL_ZEROS_ARRAY) {
                int srcParamCount = srcTypeFlagsByParamIdx.length;
                dstTypeFlagsByParamIdx = new int[dstParamCount];
                int paramIdx = 0;
                while (paramIdx < dstParamCount) {
                    dstTypeFlagsByParamIdx[paramIdx] = srcTypeFlagsByParamIdx[paramIdx < srcParamCount ? paramIdx : srcParamCount - 1];
                    paramIdx++;
                }
            } else {
                dstTypeFlagsByParamIdx = ALL_ZEROS_ARRAY;
            }
            this.typeFlagsByParamCount[dstParamCount] = dstTypeFlagsByParamIdx;
            return;
        }
        if (srcTypeFlagsByParamIdx == dstTypeFlagsByParamIdx2) {
            return;
        }
        if (dstTypeFlagsByParamIdx2 == ALL_ZEROS_ARRAY && dstParamCount > 0) {
            dstTypeFlagsByParamIdx2 = new int[dstParamCount];
            this.typeFlagsByParamCount[dstParamCount] = dstTypeFlagsByParamIdx2;
        }
        int paramIdx2 = 0;
        while (paramIdx2 < dstParamCount) {
            if (srcTypeFlagsByParamIdx != ALL_ZEROS_ARRAY) {
                int srcParamCount2 = srcTypeFlagsByParamIdx.length;
                srcParamTypeFlags = srcTypeFlagsByParamIdx[paramIdx2 < srcParamCount2 ? paramIdx2 : srcParamCount2 - 1];
            } else {
                srcParamTypeFlags = 0;
            }
            int dstParamTypesFlags = dstTypeFlagsByParamIdx2[paramIdx2];
            if (dstParamTypesFlags != srcParamTypeFlags) {
                int mergedTypeFlags = dstParamTypesFlags | srcParamTypeFlags;
                if ((mergedTypeFlags & 2044) != 0) {
                    mergedTypeFlags |= 1;
                }
                dstTypeFlagsByParamIdx2[paramIdx2] = mergedTypeFlags;
            }
            paramIdx2++;
        }
    }

    protected void forceNumberArgumentsToParameterTypes(Object[] args, Class[] paramTypes, int[] typeFlagsByParamIndex) {
        int paramTypesLen = paramTypes.length;
        int argsLen = args.length;
        int argIdx = 0;
        while (argIdx < argsLen) {
            int paramTypeIdx = argIdx < paramTypesLen ? argIdx : paramTypesLen - 1;
            int typeFlags = typeFlagsByParamIndex[paramTypeIdx];
            if ((typeFlags & 1) != 0) {
                Object arg = args[argIdx];
                if (arg instanceof Number) {
                    Class targetType = paramTypes[paramTypeIdx];
                    Number convertedArg = BeansWrapper.forceUnwrappedNumberToType((Number) arg, targetType, this.bugfixed);
                    if (convertedArg != null) {
                        args[argIdx] = convertedArg;
                    }
                }
            }
            argIdx++;
        }
    }
}
