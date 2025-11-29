package freemarker.ext.beans;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import freemarker.core.BugException;
import freemarker.core._JavaVersions;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelCache;
import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ClassIntrospector.class */
class ClassIntrospector {
    private static final String JREBEL_SDK_CLASS_NAME = "org.zeroturnaround.javarebel.ClassEventListener";
    private static final String JREBEL_INTEGRATION_ERROR_MSG = "Error initializing JRebel integration. JRebel integration disabled.";
    private static final ClassChangeNotifier CLASS_CHANGE_NOTIFIER;
    private static final Object ARG_TYPES_BY_METHOD_KEY;
    static final Object CONSTRUCTORS_KEY;
    static final Object GENERIC_GET_KEY;
    static final Object TO_STRING_HIDDEN_FLAG_KEY;
    final int exposureLevel;
    final boolean exposeFields;
    final MemberAccessPolicy memberAccessPolicy;
    final MethodAppearanceFineTuner methodAppearanceFineTuner;
    final MethodSorter methodSorter;
    final boolean treatDefaultMethodsAsBeanMembers;
    final ZeroArgumentNonVoidMethodPolicy defaultZeroArgumentNonVoidMethodPolicy;
    final ZeroArgumentNonVoidMethodPolicy recordZeroArgumentNonVoidMethodPolicy;
    private final boolean recordAware;
    final Version incompatibleImprovements;
    private final boolean hasSharedInstanceRestrictions;
    private final boolean shared;
    private final Object sharedLock;
    private final Map<Class<?>, Map<Object, Object>> cache = new ConcurrentHashMap(0, 0.75f, 16);
    private final Set<String> cacheClassNames = new HashSet(0);
    private final Set<Class<?>> classIntrospectionsInProgress = new HashSet(0);
    private final List<WeakReference<Object>> modelFactories = new LinkedList();
    private final ReferenceQueue<Object> modelFactoriesRefQueue = new ReferenceQueue<>();
    private int clearingCounter;
    private static final Logger LOG = Logger.getLogger("freemarker.beans");
    private static final ExecutableMemberSignature GET_STRING_SIGNATURE = new ExecutableMemberSignature(BeanUtil.PREFIX_GETTER_GET, new Class[]{String.class});
    private static final ExecutableMemberSignature GET_OBJECT_SIGNATURE = new ExecutableMemberSignature(BeanUtil.PREFIX_GETTER_GET, new Class[]{Object.class});
    private static final ExecutableMemberSignature TO_STRING_SIGNATURE = new ExecutableMemberSignature("toString", CollectionUtils.EMPTY_CLASS_ARRAY);
    static final boolean DEVELOPMENT_MODE = "true".equals(SecurityUtilities.getSystemProperty("freemarker.development", "false"));

    static {
        boolean jRebelAvailable;
        ClassChangeNotifier classChangeNotifier;
        try {
            Class.forName(JREBEL_SDK_CLASS_NAME);
            jRebelAvailable = true;
        } catch (Throwable e) {
            jRebelAvailable = false;
            try {
                if (!(e instanceof ClassNotFoundException)) {
                    LOG.error(JREBEL_INTEGRATION_ERROR_MSG, e);
                }
            } catch (Throwable th) {
            }
        }
        if (jRebelAvailable) {
            try {
                classChangeNotifier = (ClassChangeNotifier) Class.forName("freemarker.ext.beans.JRebelClassChangeNotifier").newInstance();
            } catch (Throwable e2) {
                classChangeNotifier = null;
                try {
                    LOG.error(JREBEL_INTEGRATION_ERROR_MSG, e2);
                } catch (Throwable th2) {
                }
            }
        } else {
            classChangeNotifier = null;
        }
        CLASS_CHANGE_NOTIFIER = classChangeNotifier;
        ARG_TYPES_BY_METHOD_KEY = new Object();
        CONSTRUCTORS_KEY = new Object();
        GENERIC_GET_KEY = new Object();
        TO_STRING_HIDDEN_FLAG_KEY = new Object();
    }

    ClassIntrospector(ClassIntrospectorBuilder builder, Object sharedLock, boolean hasSharedInstanceRestrictions, boolean shared) {
        NullArgumentException.check("sharedLock", sharedLock);
        this.exposureLevel = builder.getExposureLevel();
        this.exposeFields = builder.getExposeFields();
        this.memberAccessPolicy = builder.getMemberAccessPolicy();
        this.methodAppearanceFineTuner = builder.getMethodAppearanceFineTuner();
        this.methodSorter = builder.getMethodSorter();
        this.treatDefaultMethodsAsBeanMembers = builder.getTreatDefaultMethodsAsBeanMembers();
        this.defaultZeroArgumentNonVoidMethodPolicy = builder.getDefaultZeroArgumentNonVoidMethodPolicy();
        this.recordZeroArgumentNonVoidMethodPolicy = builder.getRecordZeroArgumentNonVoidMethodPolicy();
        this.recordAware = this.defaultZeroArgumentNonVoidMethodPolicy != this.recordZeroArgumentNonVoidMethodPolicy;
        if (this.recordAware && _JavaVersions.JAVA_16 == null) {
            throw new IllegalArgumentException("defaultZeroArgumentNonVoidMethodPolicy != recordZeroArgumentNonVoidMethodPolicy, but record support is not available (as Java 16 support is not available).");
        }
        this.incompatibleImprovements = builder.getIncompatibleImprovements();
        this.sharedLock = sharedLock;
        this.hasSharedInstanceRestrictions = hasSharedInstanceRestrictions;
        this.shared = shared;
        if (CLASS_CHANGE_NOTIFIER != null) {
            CLASS_CHANGE_NOTIFIER.subscribe(this);
        }
    }

    ClassIntrospectorBuilder createBuilder() {
        return new ClassIntrospectorBuilder(this);
    }

    Map<Object, Object> get(Class<?> clazz) {
        Map<Object, Object> introspData = this.cache.get(clazz);
        if (introspData != null) {
            return introspData;
        }
        synchronized (this.sharedLock) {
            Map<Object, Object> introspData2 = this.cache.get(clazz);
            if (introspData2 != null) {
                return introspData2;
            }
            String className = clazz.getName();
            if (this.cacheClassNames.contains(className)) {
                onSameNameClassesDetected(className);
            }
            while (introspData2 == null && this.classIntrospectionsInProgress.contains(clazz)) {
                try {
                    this.sharedLock.wait();
                    introspData2 = this.cache.get(clazz);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Class introspection data lookup aborted: " + e);
                }
            }
            if (introspData2 != null) {
                return introspData2;
            }
            this.classIntrospectionsInProgress.add(clazz);
            try {
                Map<Object, Object> introspData3 = createClassIntrospectionData(clazz);
                synchronized (this.sharedLock) {
                    this.cache.put(clazz, introspData3);
                    this.cacheClassNames.add(className);
                }
                synchronized (this.sharedLock) {
                    this.classIntrospectionsInProgress.remove(clazz);
                    this.sharedLock.notifyAll();
                }
                return introspData3;
            } catch (Throwable th) {
                synchronized (this.sharedLock) {
                    this.classIntrospectionsInProgress.remove(clazz);
                    this.sharedLock.notifyAll();
                    throw th;
                }
            }
        }
    }

    private Map<Object, Object> createClassIntrospectionData(Class<?> clazz) throws SecurityException {
        Map<Object, Object> introspData = new HashMap<>();
        MemberAccessPolicy effMemberAccessPolicy = getEffectiveMemberAccessPolicy();
        ClassMemberAccessPolicy effClassMemberAccessPolicy = effMemberAccessPolicy.forClass(clazz);
        if (this.exposeFields) {
            addFieldsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
        }
        Map<ExecutableMemberSignature, List<Method>> accessibleMethods = discoverAccessibleMethods(clazz);
        if (!effMemberAccessPolicy.isToStringAlwaysExposed()) {
            addToStringHiddenFlagToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
        }
        addGenericGetToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
        if (this.exposureLevel != 3) {
            try {
                addBeanInfoToClassIntrospectionData(introspData, clazz, accessibleMethods, effClassMemberAccessPolicy);
            } catch (IntrospectionException e) {
                LOG.warn("Couldn't properly perform introspection for class " + clazz, e);
                introspData.clear();
            }
        }
        addConstructorsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
        if (introspData.size() > 1) {
            return introspData;
        }
        if (introspData.isEmpty()) {
            return Collections.emptyMap();
        }
        Map.Entry<Object, Object> e2 = introspData.entrySet().iterator().next();
        return Collections.singletonMap(e2.getKey(), e2.getValue());
    }

    private void addFieldsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws SecurityException {
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if ((field.getModifiers() & 8) == 0 && effClassMemberAccessPolicy.isFieldExposed(field)) {
                introspData.put(field.getName(), field);
            }
        }
    }

    private void addBeanInfoToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws SecurityException, IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        boolean treatClassAsRecord = this.recordAware && _JavaVersions.JAVA_16.isRecord(clazz);
        ZeroArgumentNonVoidMethodPolicy zeroArgumentNonVoidMethodPolicy = treatClassAsRecord ? this.recordZeroArgumentNonVoidMethodPolicy : this.defaultZeroArgumentNonVoidMethodPolicy;
        Set<String> beanPropertyReadMethodNameCollector = zeroArgumentNonVoidMethodPolicy != ZeroArgumentNonVoidMethodPolicy.METHOD_ONLY ? new HashSet<>() : null;
        List<PropertyDescriptor> pdas = getPropertyDescriptors(beanInfo, clazz);
        int pdasLength = pdas.size();
        for (int i = pdasLength - 1; i >= 0; i--) {
            addPropertyDescriptorToClassIntrospectionData(introspData, pdas.get(i), false, accessibleMethods, beanPropertyReadMethodNameCollector, effClassMemberAccessPolicy);
        }
        if (this.exposureLevel < 2) {
            BeansWrapper.MethodAppearanceDecision decision = new BeansWrapper.MethodAppearanceDecision();
            BeansWrapper.MethodAppearanceDecisionInput decisionInput = null;
            List<MethodDescriptor> mds = getMethodDescriptors(beanInfo, clazz);
            sortMethodDescriptors(mds);
            int mdsSize = mds.size();
            IdentityHashMap<Method, Void> argTypesUsedByIndexerPropReaders = null;
            for (int i2 = mdsSize - 1; i2 >= 0; i2--) {
                Method method = getMatchingAccessibleMethod(mds.get(i2).getMethod(), accessibleMethods);
                if (method != null && effClassMemberAccessPolicy.isMethodExposed(method)) {
                    ZeroArgumentNonVoidMethodPolicy appliedZeroArgumentNonVoidMethodPolicy = getAppliedZeroArgumentNonVoidMethodPolicy(method, beanPropertyReadMethodNameCollector, zeroArgumentNonVoidMethodPolicy);
                    decision.setDefaults(method, appliedZeroArgumentNonVoidMethodPolicy);
                    if (this.methodAppearanceFineTuner != null) {
                        if (decisionInput == null) {
                            decisionInput = new BeansWrapper.MethodAppearanceDecisionInput();
                        }
                        decisionInput.setContainingClass(clazz);
                        decisionInput.setMethod(method);
                        this.methodAppearanceFineTuner.process(decisionInput, decision);
                    }
                    String exposedMethodName = decision.getExposeMethodAs();
                    PropertyDescriptor propDesc = decision.getExposeAsProperty();
                    if (propDesc != null && (decision.getReplaceExistingProperty() || isExistingIntropsDataNotPropertyOrNewAddsMethodSupport(introspData, propDesc.getName(), decision))) {
                        boolean methodInsteadOfPropertyValueBeforeCall = decision.isMethodInsteadOfPropertyValueBeforeCall();
                        addPropertyDescriptorToClassIntrospectionData(introspData, propDesc, methodInsteadOfPropertyValueBeforeCall, accessibleMethods, null, effClassMemberAccessPolicy);
                        if (methodInsteadOfPropertyValueBeforeCall && exposedMethodName != null && exposedMethodName.equals(propDesc.getName())) {
                            exposedMethodName = null;
                        }
                    }
                    if (exposedMethodName != null) {
                        Object previous = introspData.get(exposedMethodName);
                        if (previous instanceof Method) {
                            OverloadedMethods overloadedMethods = new OverloadedMethods(is2321Bugfixed());
                            overloadedMethods.addMethod((Method) previous);
                            overloadedMethods.addMethod(method);
                            introspData.put(exposedMethodName, overloadedMethods);
                            if (argTypesUsedByIndexerPropReaders == null || !argTypesUsedByIndexerPropReaders.containsKey(previous)) {
                                getArgTypesByMethod(introspData).remove(previous);
                            }
                        } else if (previous instanceof OverloadedMethods) {
                            ((OverloadedMethods) previous).addMethod(method);
                        } else if (decision.getMethodShadowsProperty() || !(previous instanceof FastPropertyDescriptor)) {
                            introspData.put(exposedMethodName, method);
                            Class<?>[] replaced = getArgTypesByMethod(introspData).put(method, method.getParameterTypes());
                            if (replaced != null) {
                                if (argTypesUsedByIndexerPropReaders == null) {
                                    argTypesUsedByIndexerPropReaders = new IdentityHashMap<>();
                                }
                                argTypesUsedByIndexerPropReaders.put(method, null);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isExistingIntropsDataNotPropertyOrNewAddsMethodSupport(Map<Object, Object> introspData, String introspDataKey, BeansWrapper.MethodAppearanceDecision decision) {
        Object prevIntrospDataValue = introspData.get(introspDataKey);
        if (prevIntrospDataValue instanceof FastPropertyDescriptor) {
            return decision.isMethodInsteadOfPropertyValueBeforeCall() && !((FastPropertyDescriptor) prevIntrospDataValue).isMethodInsteadOfPropertyValueBeforeCall();
        }
        return true;
    }

    private static ZeroArgumentNonVoidMethodPolicy getAppliedZeroArgumentNonVoidMethodPolicy(Method method, Set<String> beanPropertyReadMethodNameCollector, ZeroArgumentNonVoidMethodPolicy zeroArgumentNonVoidMethodPolicy) {
        if (method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE) {
            return null;
        }
        return (beanPropertyReadMethodNameCollector == null || !beanPropertyReadMethodNameCollector.contains(method.getName())) ? zeroArgumentNonVoidMethodPolicy : ZeroArgumentNonVoidMethodPolicy.METHOD_ONLY;
    }

    private List<PropertyDescriptor> getPropertyDescriptors(BeanInfo beanInfo, Class<?> clazz) throws SecurityException {
        Method readMethod;
        Method indexedReadMethod;
        PropertyDescriptor indexedPropertyDescriptor;
        String propName;
        PropertyDescriptor[] introspectorPDsArray = beanInfo.getPropertyDescriptors();
        List<PropertyDescriptor> introspectorPDs = introspectorPDsArray != null ? Arrays.asList(introspectorPDsArray) : Collections.emptyList();
        if (!this.treatDefaultMethodsAsBeanMembers) {
            return introspectorPDs;
        }
        LinkedHashMap<String, Object> mergedPRMPs = null;
        for (Method method : clazz.getMethods()) {
            if (method.isDefault() && method.getReturnType() != Void.TYPE && !method.isBridge()) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if ((paramTypes.length == 0 || (paramTypes.length == 1 && paramTypes[0] == Integer.TYPE)) && (propName = _MethodUtil.getBeanPropertyNameFromReaderMethodName(method.getName(), method.getReturnType())) != null) {
                    if (mergedPRMPs == null) {
                        mergedPRMPs = new LinkedHashMap<>();
                    }
                    if (paramTypes.length == 0) {
                        mergeInPropertyReaderMethod(mergedPRMPs, propName, method);
                    } else {
                        mergeInPropertyReaderMethodPair(mergedPRMPs, propName, new PropertyReaderMethodPair(null, method));
                    }
                }
            }
        }
        if (mergedPRMPs == null) {
            return introspectorPDs;
        }
        for (PropertyDescriptor introspectorPD : introspectorPDs) {
            mergeInPropertyDescriptor(mergedPRMPs, introspectorPD);
        }
        List<PropertyDescriptor> mergedPDs = new ArrayList<>(mergedPRMPs.size());
        for (Map.Entry<String, Object> entry : mergedPRMPs.entrySet()) {
            String propName2 = entry.getKey();
            Object propDescObj = entry.getValue();
            if (propDescObj instanceof PropertyDescriptor) {
                mergedPDs.add((PropertyDescriptor) propDescObj);
            } else {
                if (propDescObj instanceof Method) {
                    readMethod = (Method) propDescObj;
                    indexedReadMethod = null;
                } else if (propDescObj instanceof PropertyReaderMethodPair) {
                    PropertyReaderMethodPair prmp = (PropertyReaderMethodPair) propDescObj;
                    readMethod = prmp.readMethod;
                    indexedReadMethod = prmp.indexedReadMethod;
                    if (readMethod != null && indexedReadMethod != null && indexedReadMethod.getReturnType() != readMethod.getReturnType().getComponentType()) {
                        indexedReadMethod = null;
                    }
                } else {
                    throw new BugException();
                }
                if (indexedReadMethod != null) {
                    try {
                        indexedPropertyDescriptor = new IndexedPropertyDescriptor(propName2, readMethod, (Method) null, indexedReadMethod, (Method) null);
                    } catch (IntrospectionException e) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("Failed creating property descriptor for " + clazz.getName() + " property " + propName2, e);
                        }
                    }
                } else {
                    indexedPropertyDescriptor = new PropertyDescriptor(propName2, readMethod, (Method) null);
                }
                mergedPDs.add(indexedPropertyDescriptor);
            }
        }
        return mergedPDs;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ClassIntrospector$PropertyReaderMethodPair.class */
    private static class PropertyReaderMethodPair {
        private final Method readMethod;
        private final Method indexedReadMethod;

        PropertyReaderMethodPair(Method readerMethod, Method indexedReaderMethod) {
            this.readMethod = readerMethod;
            this.indexedReadMethod = indexedReaderMethod;
        }

        PropertyReaderMethodPair(PropertyDescriptor pd) {
            this(pd.getReadMethod(), pd instanceof IndexedPropertyDescriptor ? ((IndexedPropertyDescriptor) pd).getIndexedReadMethod() : null);
        }

        static PropertyReaderMethodPair from(Object obj) {
            if (obj instanceof PropertyReaderMethodPair) {
                return (PropertyReaderMethodPair) obj;
            }
            if (obj instanceof PropertyDescriptor) {
                return new PropertyReaderMethodPair((PropertyDescriptor) obj);
            }
            if (obj instanceof Method) {
                return new PropertyReaderMethodPair((Method) obj, null);
            }
            throw new BugException("Unexpected obj type: " + obj.getClass().getName());
        }

        static PropertyReaderMethodPair merge(PropertyReaderMethodPair oldMethods, PropertyReaderMethodPair newMethods) {
            return new PropertyReaderMethodPair(newMethods.readMethod != null ? newMethods.readMethod : oldMethods.readMethod, newMethods.indexedReadMethod != null ? newMethods.indexedReadMethod : oldMethods.indexedReadMethod);
        }

        public int hashCode() {
            int result = (31 * 1) + (this.indexedReadMethod == null ? 0 : this.indexedReadMethod.hashCode());
            return (31 * result) + (this.readMethod == null ? 0 : this.readMethod.hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            PropertyReaderMethodPair other = (PropertyReaderMethodPair) obj;
            return other.readMethod == this.readMethod && other.indexedReadMethod == this.indexedReadMethod;
        }
    }

    private void mergeInPropertyDescriptor(LinkedHashMap<String, Object> mergedPRMPs, PropertyDescriptor pd) {
        String propName = pd.getName();
        Object replaced = mergedPRMPs.put(propName, pd);
        if (replaced != null) {
            PropertyReaderMethodPair newPRMP = new PropertyReaderMethodPair(pd);
            putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRMP);
        }
    }

    private void mergeInPropertyReaderMethodPair(LinkedHashMap<String, Object> mergedPRMPs, String propName, PropertyReaderMethodPair newPRM) {
        Object replaced = mergedPRMPs.put(propName, newPRM);
        if (replaced != null) {
            putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRM);
        }
    }

    private void mergeInPropertyReaderMethod(LinkedHashMap<String, Object> mergedPRMPs, String propName, Method readerMethod) {
        Object replaced = mergedPRMPs.put(propName, readerMethod);
        if (replaced != null) {
            putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, new PropertyReaderMethodPair(readerMethod, null));
        }
    }

    private void putIfMergedPropertyReaderMethodPairDiffers(LinkedHashMap<String, Object> mergedPRMPs, String propName, Object replaced, PropertyReaderMethodPair newPRMP) {
        PropertyReaderMethodPair replacedPRMP = PropertyReaderMethodPair.from(replaced);
        PropertyReaderMethodPair mergedPRMP = PropertyReaderMethodPair.merge(replacedPRMP, newPRMP);
        if (!mergedPRMP.equals(newPRMP)) {
            mergedPRMPs.put(propName, mergedPRMP);
        }
    }

    private List<MethodDescriptor> getMethodDescriptors(BeanInfo beanInfo, Class<?> clazz) throws SecurityException {
        MethodDescriptor[] introspectorMDArray = beanInfo.getMethodDescriptors();
        List<MethodDescriptor> introspectionMDs = (introspectorMDArray == null || introspectorMDArray.length == 0) ? Collections.emptyList() : Arrays.asList(introspectorMDArray);
        if (!this.treatDefaultMethodsAsBeanMembers) {
            return introspectionMDs;
        }
        Map<String, List<Method>> defaultMethodsToAddByName = null;
        for (Method method : clazz.getMethods()) {
            if (method.isDefault() && !method.isBridge()) {
                if (defaultMethodsToAddByName == null) {
                    defaultMethodsToAddByName = new HashMap<>();
                }
                List<Method> overloads = defaultMethodsToAddByName.get(method.getName());
                if (overloads == null) {
                    overloads = new ArrayList<>(0);
                    defaultMethodsToAddByName.put(method.getName(), overloads);
                }
                overloads.add(method);
            }
        }
        if (defaultMethodsToAddByName == null) {
            return introspectionMDs;
        }
        ArrayList<MethodDescriptor> newIntrospectionMDs = new ArrayList<>(introspectionMDs.size() + 16);
        for (MethodDescriptor introspectorMD : introspectionMDs) {
            Method introspectorM = introspectorMD.getMethod();
            if (!containsMethodWithSameParameterTypes(defaultMethodsToAddByName.get(introspectorM.getName()), introspectorM)) {
                newIntrospectionMDs.add(introspectorMD);
            }
        }
        for (Map.Entry<String, List<Method>> entry : defaultMethodsToAddByName.entrySet()) {
            Iterator<Method> it = entry.getValue().iterator();
            while (it.hasNext()) {
                newIntrospectionMDs.add(new MethodDescriptor(it.next()));
            }
        }
        return newIntrospectionMDs;
    }

    private boolean containsMethodWithSameParameterTypes(List<Method> overloads, Method m) {
        if (overloads == null) {
            return false;
        }
        Class<?>[] paramTypes = m.getParameterTypes();
        for (Method overload : overloads) {
            if (Arrays.equals(overload.getParameterTypes(), paramTypes)) {
                return true;
            }
        }
        return false;
    }

    private void addPropertyDescriptorToClassIntrospectionData(Map<Object, Object> introspData, PropertyDescriptor pd, boolean methodInsteadOfPropertyValueBeforeCall, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, Set<String> beanPropertyReadMethodNameCollector, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
        Method indexedReadMethod;
        Method readMethod = getMatchingAccessibleMethod(pd.getReadMethod(), accessibleMethods);
        if (readMethod != null && !effClassMemberAccessPolicy.isMethodExposed(readMethod)) {
            readMethod = null;
        }
        if (pd instanceof IndexedPropertyDescriptor) {
            indexedReadMethod = getMatchingAccessibleMethod(((IndexedPropertyDescriptor) pd).getIndexedReadMethod(), accessibleMethods);
            if (indexedReadMethod != null && !effClassMemberAccessPolicy.isMethodExposed(indexedReadMethod)) {
                indexedReadMethod = null;
            }
            if (indexedReadMethod != null) {
                getArgTypesByMethod(introspData).put(indexedReadMethod, indexedReadMethod.getParameterTypes());
            }
        } else {
            indexedReadMethod = null;
        }
        if (readMethod != null || indexedReadMethod != null) {
            introspData.put(pd.getName(), new FastPropertyDescriptor(readMethod, indexedReadMethod, methodInsteadOfPropertyValueBeforeCall));
        }
        if (readMethod != null && beanPropertyReadMethodNameCollector != null) {
            beanPropertyReadMethodNameCollector.add(readMethod.getName());
        }
    }

    private void addGenericGetToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
        Method genericGet = getFirstAccessibleMethod(GET_STRING_SIGNATURE, accessibleMethods);
        if (genericGet == null) {
            genericGet = getFirstAccessibleMethod(GET_OBJECT_SIGNATURE, accessibleMethods);
        }
        if (genericGet != null && effClassMemberAccessPolicy.isMethodExposed(genericGet)) {
            introspData.put(GENERIC_GET_KEY, genericGet);
        }
    }

    private void addToStringHiddenFlagToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
        Method toStringMethod = getFirstAccessibleMethod(TO_STRING_SIGNATURE, accessibleMethods);
        if (toStringMethod == null) {
            throw new BugException("toString() method not found");
        }
        if (!effClassMemberAccessPolicy.isMethodExposed(toStringMethod)) {
            introspData.put(TO_STRING_HIDDEN_FLAG_KEY, true);
        }
    }

    private void addConstructorsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws SecurityException {
        Object ctorsIntrospData;
        try {
            Constructor<?>[] ctorsUnfiltered = clazz.getConstructors();
            List<Constructor<?>> ctors = new ArrayList<>(ctorsUnfiltered.length);
            for (Constructor<?> ctor : ctorsUnfiltered) {
                if (effClassMemberAccessPolicy.isConstructorExposed(ctor)) {
                    ctors.add(ctor);
                }
            }
            if (!ctors.isEmpty()) {
                if (ctors.size() == 1) {
                    Constructor<?> ctor2 = ctors.get(0);
                    ctorsIntrospData = new SimpleMethod(ctor2, ctor2.getParameterTypes());
                } else {
                    OverloadedMethods overloadedCtors = new OverloadedMethods(is2321Bugfixed());
                    Iterator<Constructor<?>> it = ctors.iterator();
                    while (it.hasNext()) {
                        overloadedCtors.addConstructor(it.next());
                    }
                    ctorsIntrospData = overloadedCtors;
                }
                introspData.put(CONSTRUCTORS_KEY, ctorsIntrospData);
            }
        } catch (SecurityException e) {
            LOG.warn("Can't discover constructors for class " + clazz.getName(), e);
        }
    }

    private static Map<ExecutableMemberSignature, List<Method>> discoverAccessibleMethods(Class<?> clazz) throws SecurityException {
        Map<ExecutableMemberSignature, List<Method>> accessibles = new HashMap<>();
        discoverAccessibleMethods(clazz, accessibles);
        return accessibles;
    }

    private static void discoverAccessibleMethods(Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibles) throws SecurityException {
        if (Modifier.isPublic(clazz.getModifiers()) && (_JavaVersions.JAVA_9 == null || _JavaVersions.JAVA_9.isAccessibleAccordingToModuleExports(clazz))) {
            try {
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                        ExecutableMemberSignature sig = new ExecutableMemberSignature(method);
                        List<Method> methodList = accessibles.get(sig);
                        if (methodList == null) {
                            methodList = new LinkedList();
                            accessibles.put(sig, methodList);
                        }
                        methodList.add(method);
                    }
                }
                return;
            } catch (SecurityException e) {
                LOG.warn("Could not discover accessible methods of class " + clazz.getName() + ", attemping superclasses/interfaces.", e);
            }
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            discoverAccessibleMethods(anInterface, accessibles);
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            discoverAccessibleMethods(superclass, accessibles);
        }
    }

    private static Method getMatchingAccessibleMethod(Method m, Map<ExecutableMemberSignature, List<Method>> accessibles) {
        List<Method> ams;
        if (m == null || (ams = accessibles.get(new ExecutableMemberSignature(m))) == null) {
            return null;
        }
        return _MethodUtil.getMethodWithClosestNonSubReturnType(m.getReturnType(), ams);
    }

    private static Method getFirstAccessibleMethod(ExecutableMemberSignature sig, Map<ExecutableMemberSignature, List<Method>> accessibles) {
        List<Method> ams = accessibles.get(sig);
        if (ams == null || ams.isEmpty()) {
            return null;
        }
        return ams.get(0);
    }

    private void sortMethodDescriptors(List<MethodDescriptor> methodDescriptors) {
        if (this.methodSorter != null) {
            this.methodSorter.sortMethodDescriptors(methodDescriptors);
        }
    }

    MemberAccessPolicy getEffectiveMemberAccessPolicy() {
        return this.exposureLevel < 1 ? AllowAllMemberAccessPolicy.INSTANCE : this.memberAccessPolicy;
    }

    private boolean is2321Bugfixed() {
        return BeansWrapper.is2321Bugfixed(this.incompatibleImprovements);
    }

    private static Map<Method, Class<?>[]> getArgTypesByMethod(Map<Object, Object> classInfo) {
        Map<Method, Class<?>[]> argTypes = (Map) classInfo.get(ARG_TYPES_BY_METHOD_KEY);
        if (argTypes == null) {
            argTypes = new HashMap();
            classInfo.put(ARG_TYPES_BY_METHOD_KEY, argTypes);
        }
        return argTypes;
    }

    void clearCache() {
        if (getHasSharedInstanceRestrictions()) {
            throw new IllegalStateException("It's not allowed to clear the whole cache in a read-only " + getClass().getName() + "instance. Use removeFromClassIntrospectionCache(String prefix) instead.");
        }
        forcedClearCache();
    }

    private void forcedClearCache() {
        synchronized (this.sharedLock) {
            this.cache.clear();
            this.cacheClassNames.clear();
            this.clearingCounter++;
            for (WeakReference<Object> regedMfREf : this.modelFactories) {
                Object regedMf = regedMfREf.get();
                if (regedMf != null) {
                    if (regedMf instanceof ClassBasedModelFactory) {
                        ((ClassBasedModelFactory) regedMf).clearCache();
                    } else if (regedMf instanceof ModelCache) {
                        ((ModelCache) regedMf).clearCache();
                    } else {
                        throw new BugException();
                    }
                }
            }
            removeClearedModelFactoryReferences();
        }
    }

    void remove(Class<?> clazz) {
        synchronized (this.sharedLock) {
            this.cache.remove(clazz);
            this.cacheClassNames.remove(clazz.getName());
            this.clearingCounter++;
            for (WeakReference<Object> regedMfREf : this.modelFactories) {
                Object regedMf = regedMfREf.get();
                if (regedMf != null) {
                    if (regedMf instanceof ClassBasedModelFactory) {
                        ((ClassBasedModelFactory) regedMf).removeFromCache(clazz);
                    } else if (regedMf instanceof ModelCache) {
                        ((ModelCache) regedMf).clearCache();
                    } else {
                        throw new BugException();
                    }
                }
            }
            removeClearedModelFactoryReferences();
        }
    }

    int getClearingCounter() {
        int i;
        synchronized (this.sharedLock) {
            i = this.clearingCounter;
        }
        return i;
    }

    private void onSameNameClassesDetected(String className) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Detected multiple classes with the same name, \"" + className + "\". Assuming it was a class-reloading. Clearing class introspection caches to release old data.");
        }
        forcedClearCache();
    }

    void registerModelFactory(ClassBasedModelFactory mf) {
        registerModelFactory((Object) mf);
    }

    void registerModelFactory(ModelCache mf) {
        registerModelFactory((Object) mf);
    }

    private void registerModelFactory(Object mf) {
        synchronized (this.sharedLock) {
            this.modelFactories.add(new WeakReference<>(mf, this.modelFactoriesRefQueue));
            removeClearedModelFactoryReferences();
        }
    }

    void unregisterModelFactory(ClassBasedModelFactory mf) {
        unregisterModelFactory((Object) mf);
    }

    void unregisterModelFactory(ModelCache mf) {
        unregisterModelFactory((Object) mf);
    }

    void unregisterModelFactory(Object mf) {
        synchronized (this.sharedLock) {
            Iterator<WeakReference<Object>> it = this.modelFactories.iterator();
            while (it.hasNext()) {
                Object regedMf = it.next().get();
                if (regedMf == mf) {
                    it.remove();
                }
            }
        }
    }

    private void removeClearedModelFactoryReferences() {
        while (true) {
            Reference<?> cleardRef = this.modelFactoriesRefQueue.poll();
            if (cleardRef != null) {
                synchronized (this.sharedLock) {
                    Iterator<WeakReference<Object>> it = this.modelFactories.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (it.next() == cleardRef) {
                            it.remove();
                            break;
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    static Class<?>[] getArgTypes(Map<Object, Object> classInfo, Method method) {
        Map<Method, Class<?>[]> argTypesByMethod = (Map) classInfo.get(ARG_TYPES_BY_METHOD_KEY);
        return argTypesByMethod.get(method);
    }

    int keyCount(Class<?> clazz) {
        Map<Object, Object> map = get(clazz);
        int count = map.size();
        if (map.containsKey(CONSTRUCTORS_KEY)) {
            count--;
        }
        if (map.containsKey(GENERIC_GET_KEY)) {
            count--;
        }
        if (map.containsKey(ARG_TYPES_BY_METHOD_KEY)) {
            count--;
        }
        return count;
    }

    Set<Object> keySet(Class<?> clazz) {
        Set<Object> set = new HashSet<>(get(clazz).keySet());
        set.remove(CONSTRUCTORS_KEY);
        set.remove(GENERIC_GET_KEY);
        set.remove(ARG_TYPES_BY_METHOD_KEY);
        return set;
    }

    int getExposureLevel() {
        return this.exposureLevel;
    }

    boolean getExposeFields() {
        return this.exposeFields;
    }

    MemberAccessPolicy getMemberAccessPolicy() {
        return this.memberAccessPolicy;
    }

    boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.treatDefaultMethodsAsBeanMembers;
    }

    ZeroArgumentNonVoidMethodPolicy getDefaultZeroArgumentNonVoidMethodPolicy() {
        return this.defaultZeroArgumentNonVoidMethodPolicy;
    }

    ZeroArgumentNonVoidMethodPolicy getRecordZeroArgumentNonVoidMethodPolicy() {
        return this.recordZeroArgumentNonVoidMethodPolicy;
    }

    MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.methodAppearanceFineTuner;
    }

    MethodSorter getMethodSorter() {
        return this.methodSorter;
    }

    boolean getHasSharedInstanceRestrictions() {
        return this.hasSharedInstanceRestrictions;
    }

    boolean isShared() {
        return this.shared;
    }

    Object getSharedLock() {
        return this.sharedLock;
    }

    Object[] getRegisteredModelFactoriesSnapshot() {
        Object[] array;
        synchronized (this.sharedLock) {
            array = this.modelFactories.toArray();
        }
        return array;
    }
}
