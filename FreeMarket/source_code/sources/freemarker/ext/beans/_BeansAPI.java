package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.CollectionUtils;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/_BeansAPI.class */
public class _BeansAPI {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/_BeansAPI$_BeansWrapperSubclassFactory.class */
    public interface _BeansWrapperSubclassFactory<BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> {
        BW create(BWC bwc);
    }

    private _BeansAPI() {
    }

    public static String getAsClassicCompatibleString(BeanModel bm) {
        return bm.getAsClassicCompatibleString();
    }

    public static Object newInstance(Class<?> pClass, Object[] args, BeansWrapper bw) throws IllegalAccessException, TemplateModelException, NoSuchMethodException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        return newInstance(getConstructorDescriptor(pClass, args), args, bw);
    }

    private static CallableMemberDescriptor getConstructorDescriptor(Class<?> pClass, Object[] args) throws NoSuchMethodException, SecurityException {
        if (args == null) {
            args = CollectionUtils.EMPTY_OBJECT_ARRAY;
        }
        ArgumentTypes argTypes = new ArgumentTypes(args, true);
        List<ReflectionCallableMemberDescriptor> fixedArgMemberDescs = new ArrayList<>();
        List<ReflectionCallableMemberDescriptor> varArgsMemberDescs = new ArrayList<>();
        Constructor<?>[] constrs = pClass.getConstructors();
        for (Constructor<?> constr : constrs) {
            ReflectionCallableMemberDescriptor memberDesc = new ReflectionCallableMemberDescriptor(constr, constr.getParameterTypes());
            if (!_MethodUtil.isVarargs(constr)) {
                fixedArgMemberDescs.add(memberDesc);
            } else {
                varArgsMemberDescs.add(memberDesc);
            }
        }
        MaybeEmptyCallableMemberDescriptor contrDesc = argTypes.getMostSpecific(fixedArgMemberDescs, false);
        if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
            contrDesc = argTypes.getMostSpecific(varArgsMemberDescs, true);
        }
        if (contrDesc instanceof EmptyCallableMemberDescriptor) {
            if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
                throw new NoSuchMethodException("There's no public " + pClass.getName() + " constructor with compatible parameter list.");
            }
            if (contrDesc == EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD) {
                throw new NoSuchMethodException("There are multiple public " + pClass.getName() + " constructors that match the compatible parameter list with the same preferability.");
            }
            throw new NoSuchMethodException();
        }
        return (CallableMemberDescriptor) contrDesc;
    }

    private static Object newInstance(CallableMemberDescriptor constrDesc, Object[] args, BeansWrapper bw) throws IllegalAccessException, TemplateModelException, InstantiationException, ArrayIndexOutOfBoundsException, IllegalArgumentException, NegativeArraySizeException, InvocationTargetException {
        Object[] packedArgs;
        if (args == null) {
            args = CollectionUtils.EMPTY_OBJECT_ARRAY;
        }
        if (constrDesc.isVarargs()) {
            Class<?>[] paramTypes = constrDesc.getParamTypes();
            int fixedArgCnt = paramTypes.length - 1;
            packedArgs = new Object[fixedArgCnt + 1];
            for (int i = 0; i < fixedArgCnt; i++) {
                packedArgs[i] = args[i];
            }
            Class<?> compType = paramTypes[fixedArgCnt].getComponentType();
            int varArgCnt = args.length - fixedArgCnt;
            Object varArgsArray = Array.newInstance(compType, varArgCnt);
            for (int i2 = 0; i2 < varArgCnt; i2++) {
                Array.set(varArgsArray, i2, args[fixedArgCnt + i2]);
            }
            packedArgs[fixedArgCnt] = varArgsArray;
        } else {
            packedArgs = args;
        }
        return constrDesc.invokeConstructor(bw, packedArgs);
    }

    public static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> BW getBeansWrapperSubclassSingleton(BWC bwc, Map<ClassLoader, Map<BWC, WeakReference<BW>>> map, ReferenceQueue<BW> referenceQueue, _BeansWrapperSubclassFactory<BW, BWC> _beanswrappersubclassfactory) {
        Map<BWC, WeakReference<BW>> map2;
        WeakReference<BW> weakReference;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        synchronized (map) {
            map2 = map.get(contextClassLoader);
            if (map2 == null) {
                map2 = new HashMap();
                map.put(contextClassLoader, map2);
                weakReference = null;
            } else {
                weakReference = map2.get(bwc);
            }
        }
        BW bw = weakReference != null ? weakReference.get() : null;
        if (bw != null) {
            return bw;
        }
        BeansWrapperConfiguration beansWrapperConfigurationClone = clone(bwc);
        BeansWrapper beansWrapperCreate = _beanswrappersubclassfactory.create(beansWrapperConfigurationClone);
        if (!beansWrapperCreate.isWriteProtected()) {
            throw new BugException();
        }
        synchronized (map) {
            WeakReference<BW> weakReference2 = map2.get(beansWrapperConfigurationClone);
            BW bw2 = weakReference2 != null ? weakReference2.get() : null;
            if (bw2 == null) {
                map2.put(beansWrapperConfigurationClone, new WeakReference(beansWrapperCreate, referenceQueue));
            } else {
                beansWrapperCreate = bw2;
            }
        }
        removeClearedReferencesFromCache(map, referenceQueue);
        return (BW) beansWrapperCreate;
    }

    private static <BWC extends BeansWrapperConfiguration> BWC clone(BWC settings) {
        return (BWC) settings.clone(true);
    }

    private static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> void removeClearedReferencesFromCache(Map<ClassLoader, Map<BWC, WeakReference<BW>>> instanceCache, ReferenceQueue<BW> instanceCacheRefQue) {
        while (true) {
            Reference<? extends BW> clearedRef = instanceCacheRefQue.poll();
            if (clearedRef != null) {
                synchronized (instanceCache) {
                    Iterator<Map<BWC, WeakReference<BW>>> it = instanceCache.values().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        Map<BWC, WeakReference<BW>> tcclScopedCache = it.next();
                        Iterator<WeakReference<BW>> it2 = tcclScopedCache.values().iterator();
                        while (it2.hasNext()) {
                            if (it2.next() == clearedRef) {
                                it2.remove();
                                break;
                            }
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    public static ClassIntrospectorBuilder getClassIntrospectorBuilder(BeansWrapperConfiguration bwc) {
        return bwc.getClassIntrospectorBuilder();
    }
}
