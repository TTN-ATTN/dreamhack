package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedShortClassName;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.OverloadedNumberUtil;
import freemarker.ext.util.ModelCache;
import freemarker.ext.util.ModelFactory;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.RichObjectWrapper;
import freemarker.template.utility.WriteProtectable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BeansWrapper.class */
public class BeansWrapper implements RichObjectWrapper, WriteProtectable {
    public static final int EXPOSE_ALL = 0;
    public static final int EXPOSE_SAFE = 1;
    public static final int EXPOSE_PROPERTIES_ONLY = 2;
    public static final int EXPOSE_NOTHING = 3;
    private final Object sharedIntrospectionLock;
    private ClassIntrospector classIntrospector;
    private final StaticModels staticModels;
    private final ClassBasedModelFactory enumModels;
    private final ModelCache modelCache;
    private final BooleanModel falseModel;
    private final BooleanModel trueModel;
    private volatile boolean writeProtected;
    private TemplateModel nullModel;
    private int defaultDateType;
    private ObjectWrapper outerIdentity;
    private boolean methodsShadowItems;
    private boolean simpleMapWrapper;
    private boolean strict;
    private boolean preferIndexedReadMethod;
    private final Version incompatibleImprovements;
    private static volatile boolean ftmaDeprecationWarnLogged;
    private final ModelFactory BOOLEAN_FACTORY;
    private static final Logger LOG = Logger.getLogger("freemarker.beans");

    @Deprecated
    static final Object CAN_NOT_UNWRAP = ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
    private static final ModelFactory ITERATOR_FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.BeansWrapper.4
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new IteratorModel((Iterator) object, (BeansWrapper) wrapper);
        }
    };
    private static final ModelFactory ENUMERATION_FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.BeansWrapper.5
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new EnumerationModel((Enumeration) object, (BeansWrapper) wrapper);
        }
    };

    @Deprecated
    public BeansWrapper() {
        this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public BeansWrapper(Version incompatibleImprovements) {
        this(new BeansWrapperConfiguration(incompatibleImprovements) { // from class: freemarker.ext.beans.BeansWrapper.1
        }, false);
    }

    protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected) {
        this(bwConf, writeProtected, true);
    }

    protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected, boolean finalizeConstruction) {
        this.nullModel = null;
        this.outerIdentity = this;
        this.methodsShadowItems = true;
        this.BOOLEAN_FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.BeansWrapper.3
            @Override // freemarker.ext.util.ModelFactory
            public TemplateModel create(Object object, ObjectWrapper wrapper) {
                return ((Boolean) object).booleanValue() ? BeansWrapper.this.trueModel : BeansWrapper.this.falseModel;
            }
        };
        if (bwConf.getMethodAppearanceFineTuner() == null) {
            Class<?> thisClass = getClass();
            boolean overridden = false;
            boolean testFailed = false;
            while (!overridden && thisClass != DefaultObjectWrapper.class && thisClass != BeansWrapper.class && thisClass != SimpleObjectWrapper.class) {
                try {
                    try {
                        thisClass.getDeclaredMethod("finetuneMethodAppearance", Class.class, Method.class, MethodAppearanceDecision.class);
                        overridden = true;
                    } catch (NoSuchMethodException e) {
                        thisClass = thisClass.getSuperclass();
                    }
                } catch (Throwable e2) {
                    LOG.info("Failed to check if finetuneMethodAppearance is overridden in " + thisClass.getName() + "; acting like if it was, but this way it won't utilize the shared class introspection cache.", e2);
                    overridden = true;
                    testFailed = true;
                }
            }
            if (overridden) {
                if (!testFailed && !ftmaDeprecationWarnLogged) {
                    LOG.warn("Overriding " + BeansWrapper.class.getName() + ".finetuneMethodAppearance is deprecated and will be banned sometimes in the future. Use setMethodAppearanceFineTuner instead.");
                    ftmaDeprecationWarnLogged = true;
                }
                bwConf = (BeansWrapperConfiguration) bwConf.clone(false);
                bwConf.setMethodAppearanceFineTuner(new MethodAppearanceFineTuner() { // from class: freemarker.ext.beans.BeansWrapper.2
                    @Override // freemarker.ext.beans.MethodAppearanceFineTuner
                    public void process(MethodAppearanceDecisionInput in, MethodAppearanceDecision out) {
                        BeansWrapper.this.finetuneMethodAppearance(in.getContainingClass(), in.getMethod(), out);
                    }
                });
            }
        }
        this.incompatibleImprovements = bwConf.getIncompatibleImprovements();
        this.simpleMapWrapper = bwConf.isSimpleMapWrapper();
        this.preferIndexedReadMethod = bwConf.getPreferIndexedReadMethod();
        this.defaultDateType = bwConf.getDefaultDateType();
        this.outerIdentity = bwConf.getOuterIdentity() != null ? bwConf.getOuterIdentity() : this;
        this.strict = bwConf.isStrict();
        if (!writeProtected) {
            this.sharedIntrospectionLock = new Object();
            this.classIntrospector = new ClassIntrospector(_BeansAPI.getClassIntrospectorBuilder(bwConf), this.sharedIntrospectionLock, false, false);
        } else {
            this.classIntrospector = _BeansAPI.getClassIntrospectorBuilder(bwConf).build();
            this.sharedIntrospectionLock = this.classIntrospector.getSharedLock();
        }
        this.falseModel = new BooleanModel(Boolean.FALSE, this);
        this.trueModel = new BooleanModel(Boolean.TRUE, this);
        this.staticModels = new StaticModels(this);
        this.enumModels = new _EnumModels(this);
        this.modelCache = new BeansModelCache(this);
        setUseCache(bwConf.getUseModelCache());
        finalizeConstruction(writeProtected);
    }

    protected void finalizeConstruction(boolean writeProtected) {
        if (writeProtected) {
            writeProtect();
        }
        registerModelFactories();
    }

    @Override // freemarker.template.utility.WriteProtectable
    public void writeProtect() {
        this.writeProtected = true;
    }

    @Override // freemarker.template.utility.WriteProtectable
    public boolean isWriteProtected() {
        return this.writeProtected;
    }

    Object getSharedIntrospectionLock() {
        return this.sharedIntrospectionLock;
    }

    protected void checkModifiable() {
        if (this.writeProtected) {
            throw new IllegalStateException("Can't modify the " + getClass().getName() + " object, as it was write protected.");
        }
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        checkModifiable();
        this.strict = strict;
    }

    public void setOuterIdentity(ObjectWrapper outerIdentity) {
        checkModifiable();
        this.outerIdentity = outerIdentity;
    }

    public ObjectWrapper getOuterIdentity() {
        return this.outerIdentity;
    }

    public void setSimpleMapWrapper(boolean simpleMapWrapper) {
        checkModifiable();
        this.simpleMapWrapper = simpleMapWrapper;
    }

    public boolean isSimpleMapWrapper() {
        return this.simpleMapWrapper;
    }

    public boolean getPreferIndexedReadMethod() {
        return this.preferIndexedReadMethod;
    }

    public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
        checkModifiable();
        this.preferIndexedReadMethod = preferIndexedReadMethod;
    }

    public void setExposureLevel(int exposureLevel) {
        checkModifiable();
        if (this.classIntrospector.getExposureLevel() != exposureLevel) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setExposureLevel(exposureLevel);
            replaceClassIntrospector(builder);
        }
    }

    public int getExposureLevel() {
        return this.classIntrospector.getExposureLevel();
    }

    public void setExposeFields(boolean exposeFields) {
        checkModifiable();
        if (this.classIntrospector.getExposeFields() != exposeFields) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setExposeFields(exposeFields);
            replaceClassIntrospector(builder);
        }
    }

    public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
        checkModifiable();
        if (this.classIntrospector.getTreatDefaultMethodsAsBeanMembers() != treatDefaultMethodsAsBeanMembers) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
            replaceClassIntrospector(builder);
        }
    }

    public void setDefaultZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy defaultZeroArgumentNonVoidMethodPolicy) {
        checkModifiable();
        if (this.classIntrospector.getDefaultZeroArgumentNonVoidMethodPolicy() != defaultZeroArgumentNonVoidMethodPolicy) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setDefaultZeroArgumentNonVoidMethodPolicy(defaultZeroArgumentNonVoidMethodPolicy);
            replaceClassIntrospector(builder);
        }
    }

    public void setRecordZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy recordZeroArgumentNonVoidMethodPolicy) {
        checkModifiable();
        if (this.classIntrospector.getRecordZeroArgumentNonVoidMethodPolicy() != recordZeroArgumentNonVoidMethodPolicy) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setRecordZeroArgumentNonVoidMethodPolicy(recordZeroArgumentNonVoidMethodPolicy);
            replaceClassIntrospector(builder);
        }
    }

    public boolean isExposeFields() {
        return this.classIntrospector.getExposeFields();
    }

    public boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.classIntrospector.getTreatDefaultMethodsAsBeanMembers();
    }

    public ZeroArgumentNonVoidMethodPolicy getDefaultZeroArgumentNonVoidMethodPolicy() {
        return this.classIntrospector.getDefaultZeroArgumentNonVoidMethodPolicy();
    }

    public ZeroArgumentNonVoidMethodPolicy getRecordZeroArgumentNonVoidMethodPolicy() {
        return this.classIntrospector.getRecordZeroArgumentNonVoidMethodPolicy();
    }

    public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.classIntrospector.getMethodAppearanceFineTuner();
    }

    public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
        checkModifiable();
        if (this.classIntrospector.getMethodAppearanceFineTuner() != methodAppearanceFineTuner) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
            replaceClassIntrospector(builder);
        }
    }

    public MemberAccessPolicy getMemberAccessPolicy() {
        return this.classIntrospector.getMemberAccessPolicy();
    }

    public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
        checkModifiable();
        if (this.classIntrospector.getMemberAccessPolicy() != memberAccessPolicy) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setMemberAccessPolicy(memberAccessPolicy);
            replaceClassIntrospector(builder);
        }
    }

    MethodSorter getMethodSorter() {
        return this.classIntrospector.getMethodSorter();
    }

    void setMethodSorter(MethodSorter methodSorter) {
        checkModifiable();
        if (this.classIntrospector.getMethodSorter() != methodSorter) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setMethodSorter(methodSorter);
            replaceClassIntrospector(builder);
        }
    }

    public boolean isClassIntrospectionCacheRestricted() {
        return this.classIntrospector.getHasSharedInstanceRestrictions();
    }

    private void replaceClassIntrospector(ClassIntrospectorBuilder builder) {
        checkModifiable();
        ClassIntrospector newCI = new ClassIntrospector(builder, this.sharedIntrospectionLock, false, false);
        synchronized (this.sharedIntrospectionLock) {
            ClassIntrospector oldCI = this.classIntrospector;
            if (oldCI != null) {
                if (this.staticModels != null) {
                    oldCI.unregisterModelFactory((ClassBasedModelFactory) this.staticModels);
                    this.staticModels.clearCache();
                }
                if (this.enumModels != null) {
                    oldCI.unregisterModelFactory(this.enumModels);
                    this.enumModels.clearCache();
                }
                if (this.modelCache != null) {
                    oldCI.unregisterModelFactory(this.modelCache);
                    this.modelCache.clearCache();
                }
                if (this.trueModel != null) {
                    this.trueModel.clearMemberCache();
                }
                if (this.falseModel != null) {
                    this.falseModel.clearMemberCache();
                }
            }
            this.classIntrospector = newCI;
            registerModelFactories();
        }
    }

    private void registerModelFactories() {
        if (this.staticModels != null) {
            this.classIntrospector.registerModelFactory((ClassBasedModelFactory) this.staticModels);
        }
        if (this.enumModels != null) {
            this.classIntrospector.registerModelFactory(this.enumModels);
        }
        if (this.modelCache != null) {
            this.classIntrospector.registerModelFactory(this.modelCache);
        }
    }

    public void setMethodsShadowItems(boolean methodsShadowItems) {
        synchronized (this) {
            checkModifiable();
            this.methodsShadowItems = methodsShadowItems;
        }
    }

    boolean isMethodsShadowItems() {
        return this.methodsShadowItems;
    }

    public void setDefaultDateType(int defaultDateType) {
        synchronized (this) {
            checkModifiable();
            this.defaultDateType = defaultDateType;
        }
    }

    public int getDefaultDateType() {
        return this.defaultDateType;
    }

    public void setUseCache(boolean useCache) {
        checkModifiable();
        this.modelCache.setUseCache(useCache);
    }

    public boolean getUseCache() {
        return this.modelCache.getUseCache();
    }

    @Deprecated
    public void setNullModel(TemplateModel nullModel) {
        checkModifiable();
        this.nullModel = nullModel;
    }

    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    boolean is2321Bugfixed() {
        return is2321Bugfixed(getIncompatibleImprovements());
    }

    static boolean is2321Bugfixed(Version version) {
        return version.intValue() >= _VersionInts.V_2_3_21;
    }

    boolean is2324Bugfixed() {
        return is2324Bugfixed(getIncompatibleImprovements());
    }

    static boolean is2324Bugfixed(Version version) {
        return version.intValue() >= _VersionInts.V_2_3_24;
    }

    protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        return incompatibleImprovements.intValue() >= _VersionInts.V_2_3_33 ? Configuration.VERSION_2_3_33 : incompatibleImprovements.intValue() >= _VersionInts.V_2_3_27 ? Configuration.VERSION_2_3_27 : incompatibleImprovements.intValue() == _VersionInts.V_2_3_26 ? Configuration.VERSION_2_3_26 : is2324Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_24 : is2321Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_21 : Configuration.VERSION_2_3_0;
    }

    @Deprecated
    public static final BeansWrapper getDefaultInstance() {
        return BeansWrapperSingletonHolder.INSTANCE;
    }

    @Override // freemarker.template.ObjectWrapper
    public TemplateModel wrap(Object object) throws TemplateModelException {
        return object == null ? this.nullModel : this.modelCache.getInstance(object);
    }

    public TemplateMethodModelEx wrap(Object object, Method method) {
        return new SimpleMethodModel(object, method, method.getParameterTypes(), this);
    }

    @Override // freemarker.template.utility.ObjectWrapperWithAPISupport
    public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
        return new APIModel(obj, this);
    }

    @Deprecated
    protected TemplateModel getInstance(Object object, ModelFactory factory) {
        return factory.create(object, this);
    }

    protected ModelFactory getModelFactory(Class<?> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return this.simpleMapWrapper ? SimpleMapModel.FACTORY : MapModel.FACTORY;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return CollectionModel.FACTORY;
        }
        if (Number.class.isAssignableFrom(clazz)) {
            return NumberModel.FACTORY;
        }
        if (Date.class.isAssignableFrom(clazz)) {
            return DateModel.FACTORY;
        }
        if (Boolean.class == clazz) {
            return this.BOOLEAN_FACTORY;
        }
        if (ResourceBundle.class.isAssignableFrom(clazz)) {
            return ResourceBundleModel.FACTORY;
        }
        if (Iterator.class.isAssignableFrom(clazz)) {
            return ITERATOR_FACTORY;
        }
        if (Enumeration.class.isAssignableFrom(clazz)) {
            return ENUMERATION_FACTORY;
        }
        if (clazz.isArray()) {
            return ArrayModel.FACTORY;
        }
        return GenericObjectModel.FACTORY;
    }

    @Override // freemarker.template.ObjectWrapperAndUnwrapper
    public Object unwrap(TemplateModel model) throws TemplateModelException {
        return unwrap(model, Object.class);
    }

    public Object unwrap(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
        Object obj = tryUnwrapTo(model, targetClass);
        if (obj == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
            throw new TemplateModelException("Can not unwrap model of type " + model.getClass().getName() + " to type " + targetClass.getName());
        }
        return obj;
    }

    @Override // freemarker.template.ObjectWrapperAndUnwrapper
    public Object tryUnwrapTo(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
        return tryUnwrapTo(model, targetClass, 0);
    }

    Object tryUnwrapTo(TemplateModel model, Class<?> targetClass, int typeFlags) throws TemplateModelException {
        Object res = tryUnwrapTo(model, targetClass, typeFlags, null);
        if ((typeFlags & 1) != 0 && (res instanceof Number)) {
            return OverloadedNumberUtil.addFallbackType((Number) res, typeFlags);
        }
        return res;
    }

    /* JADX WARN: Code restructure failed: missing block: B:147:0x0260, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x0291, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x02ce, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x0330, code lost:
    
        return java.lang.Boolean.valueOf(((freemarker.template.TemplateBooleanModel) r7).getAsBoolean());
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x035f, code lost:
    
        return new freemarker.ext.beans.HashAdapter((freemarker.template.TemplateHashModel) r7, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x038e, code lost:
    
        return new freemarker.ext.beans.SequenceAdapter((freemarker.template.TemplateSequenceModel) r7, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:235:0x03bd, code lost:
    
        return new freemarker.ext.beans.SetAdapter((freemarker.template.TemplateCollectionModel) r7, r6);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.Object tryUnwrapTo(freemarker.template.TemplateModel r7, java.lang.Class<?> r8, int r9, java.util.Map<java.lang.Object, java.lang.Object> r10) throws freemarker.template.TemplateModelException {
        /*
            Method dump skipped, instructions count: 1014
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.ext.beans.BeansWrapper.tryUnwrapTo(freemarker.template.TemplateModel, java.lang.Class, int, java.util.Map):java.lang.Object");
    }

    Object unwrapSequenceToArray(TemplateSequenceModel seq, Class<?> arrayClass, boolean tryOnly, Map<Object, Object> recursionStops) throws TemplateModelException, NegativeArraySizeException {
        if (recursionStops != null) {
            Object retval = recursionStops.get(seq);
            if (retval != null) {
                return retval;
            }
        } else {
            recursionStops = new IdentityHashMap();
        }
        Class<?> componentType = arrayClass.getComponentType();
        int size = seq.size();
        Object array = Array.newInstance(componentType, size);
        recursionStops.put(seq, array);
        for (int i = 0; i < size; i++) {
            try {
                TemplateModel seqItem = seq.get(i);
                Object val = tryUnwrapTo(seqItem, componentType, 0, recursionStops);
                if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                    if (tryOnly) {
                        Object obj = ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
                        recursionStops.remove(seq);
                        return obj;
                    }
                    throw new _TemplateModelException("Failed to convert ", new _DelayedFTLTypeDescription(seq), " object to ", new _DelayedShortClassName(array.getClass()), ": Problematic sequence item at index ", Integer.valueOf(i), " with value type: ", new _DelayedFTLTypeDescription(seqItem));
                }
                Array.set(array, i, val);
            } catch (Throwable th) {
                recursionStops.remove(seq);
                throw th;
            }
        }
        recursionStops.remove(seq);
        return array;
    }

    Object listToArray(List<?> list, Class<?> arrayClass, Map<Object, Object> i) throws TemplateModelException, NegativeArraySizeException {
        Object listItem;
        if (list instanceof SequenceAdapter) {
            return unwrapSequenceToArray(((SequenceAdapter) list).getTemplateSequenceModel(), arrayClass, false, i);
        }
        if (i != null) {
            Object retval = i.get(list);
            if (retval != null) {
                return retval;
            }
        } else {
            i = new IdentityHashMap();
        }
        Class<?> componentType = arrayClass.getComponentType();
        Object array = Array.newInstance(componentType, list.size());
        i.put(list, array);
        try {
            boolean isComponentTypeExamined = false;
            boolean isComponentTypeNumerical = false;
            boolean isComponentTypeList = false;
            int i2 = 0;
            Iterator<?> it = list.iterator();
            while (it.hasNext()) {
                listItem = it.next();
                if (listItem != null && !componentType.isInstance(listItem)) {
                    if (!isComponentTypeExamined) {
                        isComponentTypeNumerical = ClassUtil.isNumerical(componentType);
                        isComponentTypeList = List.class.isAssignableFrom(componentType);
                        isComponentTypeExamined = true;
                    }
                    if (isComponentTypeNumerical && (listItem instanceof Number)) {
                        listItem = forceUnwrappedNumberToType((Number) listItem, componentType, true);
                    } else if (componentType == String.class && (listItem instanceof Character)) {
                        listItem = String.valueOf(((Character) listItem).charValue());
                    } else if ((componentType == Character.class || componentType == Character.TYPE) && (listItem instanceof String)) {
                        String listItemStr = (String) listItem;
                        if (listItemStr.length() == 1) {
                            listItem = Character.valueOf(listItemStr.charAt(0));
                        }
                    } else if (componentType.isArray()) {
                        if (listItem instanceof List) {
                            listItem = listToArray((List) listItem, componentType, i);
                        } else if (listItem instanceof TemplateSequenceModel) {
                            listItem = unwrapSequenceToArray((TemplateSequenceModel) listItem, componentType, false, i);
                        }
                    } else if (isComponentTypeList && listItem.getClass().isArray()) {
                        listItem = arrayToList(listItem);
                    }
                }
                Array.set(array, i, listItem);
                i2 = i + 1;
            }
            return array;
        } catch (IllegalArgumentException e) {
            StringBuilder sbAppend = new StringBuilder().append("Failed to convert ").append(ClassUtil.getShortClassNameOfObject(list)).append(" object to ").append(ClassUtil.getShortClassNameOfObject(array)).append(": Problematic List item at index ");
            throw new TemplateModelException(sbAppend.append(list).append(" with value type: ").append(ClassUtil.getShortClassNameOfObject(listItem)).toString(), (Exception) e);
        } finally {
            i.remove(list);
        }
    }

    List<?> arrayToList(Object array) throws TemplateModelException {
        if (!(array instanceof Object[])) {
            return Array.getLength(array) == 0 ? Collections.EMPTY_LIST : new PrimtiveArrayBackedReadOnlyList(array);
        }
        Object[] objArray = (Object[]) array;
        return objArray.length == 0 ? Collections.EMPTY_LIST : new NonPrimitiveArrayBackedReadOnlyList(objArray);
    }

    static Number forceUnwrappedNumberToType(Number n, Class<?> targetType, boolean bugfixed) {
        if (targetType == n.getClass()) {
            return n;
        }
        if (targetType == Integer.TYPE || targetType == Integer.class) {
            return n instanceof Integer ? (Integer) n : Integer.valueOf(n.intValue());
        }
        if (targetType == Long.TYPE || targetType == Long.class) {
            return n instanceof Long ? (Long) n : Long.valueOf(n.longValue());
        }
        if (targetType == Double.TYPE || targetType == Double.class) {
            return n instanceof Double ? (Double) n : Double.valueOf(n.doubleValue());
        }
        if (targetType == BigDecimal.class) {
            if (n instanceof BigDecimal) {
                return n;
            }
            if (n instanceof BigInteger) {
                return new BigDecimal((BigInteger) n);
            }
            if (n instanceof Long) {
                return BigDecimal.valueOf(n.longValue());
            }
            return new BigDecimal(n.doubleValue());
        }
        if (targetType == Float.TYPE || targetType == Float.class) {
            return n instanceof Float ? (Float) n : Float.valueOf(n.floatValue());
        }
        if (targetType == Byte.TYPE || targetType == Byte.class) {
            return n instanceof Byte ? (Byte) n : Byte.valueOf(n.byteValue());
        }
        if (targetType == Short.TYPE || targetType == Short.class) {
            return n instanceof Short ? (Short) n : Short.valueOf(n.shortValue());
        }
        if (targetType == BigInteger.class) {
            if (n instanceof BigInteger) {
                return n;
            }
            if (bugfixed) {
                if (n instanceof OverloadedNumberUtil.IntegerBigDecimal) {
                    return ((OverloadedNumberUtil.IntegerBigDecimal) n).bigIntegerValue();
                }
                if (n instanceof BigDecimal) {
                    return ((BigDecimal) n).toBigInteger();
                }
                return BigInteger.valueOf(n.longValue());
            }
            return new BigInteger(n.toString());
        }
        Number oriN = n instanceof OverloadedNumberUtil.NumberWithFallbackType ? ((OverloadedNumberUtil.NumberWithFallbackType) n).getSourceNumber() : n;
        if (targetType.isInstance(oriN)) {
            return oriN;
        }
        return null;
    }

    protected TemplateModel invokeMethod(Object object, Method method, Object[] args) throws IllegalAccessException, TemplateModelException, IllegalArgumentException, InvocationTargetException {
        Object retval = method.invoke(object, args);
        return method.getReturnType() == Void.TYPE ? TemplateModel.NOTHING : getOuterIdentity().wrap(retval);
    }

    protected TemplateModel readField(Object object, Field field) throws IllegalAccessException, TemplateModelException {
        return getOuterIdentity().wrap(field.get(object));
    }

    public TemplateHashModel getStaticModels() {
        return this.staticModels;
    }

    public TemplateHashModel getEnumModels() {
        if (this.enumModels == null) {
            throw new UnsupportedOperationException("Enums not supported before J2SE 5.");
        }
        return this.enumModels;
    }

    ModelCache getModelCache() {
        return this.modelCache;
    }

    public Object newInstance(Class<?> clazz, List arguments) throws TemplateModelException {
        try {
            Object ctors = this.classIntrospector.get(clazz).get(ClassIntrospector.CONSTRUCTORS_KEY);
            if (ctors == null) {
                throw new TemplateModelException("Class " + clazz.getName() + " has no exposed constructors.");
            }
            if (ctors instanceof SimpleMethod) {
                SimpleMethod sm = (SimpleMethod) ctors;
                Constructor<?> ctor = (Constructor) sm.getMember();
                Object[] objargs = sm.unwrapArguments(arguments, this);
                try {
                    return ctor.newInstance(objargs);
                } catch (Exception e) {
                    if (e instanceof TemplateModelException) {
                        throw ((TemplateModelException) e);
                    }
                    throw _MethodUtil.newInvocationTemplateModelException((Object) null, ctor, e);
                }
            }
            if (ctors instanceof OverloadedMethods) {
                MemberAndArguments mma = ((OverloadedMethods) ctors).getMemberAndArguments(arguments, this);
                try {
                    return mma.invokeConstructor(this);
                } catch (Exception e2) {
                    if (e2 instanceof TemplateModelException) {
                        throw ((TemplateModelException) e2);
                    }
                    throw _MethodUtil.newInvocationTemplateModelException((Object) null, mma.getCallableMemberDescriptor(), e2);
                }
            }
            throw new BugException();
        } catch (TemplateModelException e3) {
            throw e3;
        } catch (Exception e4) {
            throw new TemplateModelException("Error while creating new instance of class " + clazz.getName() + "; see cause exception", e4);
        }
    }

    public void removeFromClassIntrospectionCache(Class<?> clazz) {
        this.classIntrospector.remove(clazz);
    }

    @Deprecated
    public void clearClassIntrospecitonCache() {
        this.classIntrospector.clearCache();
    }

    public void clearClassIntrospectionCache() {
        this.classIntrospector.clearCache();
    }

    ClassIntrospector getClassIntrospector() {
        return this.classIntrospector;
    }

    @Deprecated
    protected void finetuneMethodAppearance(Class<?> clazz, Method m, MethodAppearanceDecision decision) {
    }

    public static void coerceBigDecimals(AccessibleObject callable, Object[] args) {
        Class<?>[] formalTypes = null;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof BigDecimal) {
                if (formalTypes == null) {
                    if (callable instanceof Method) {
                        formalTypes = ((Method) callable).getParameterTypes();
                    } else if (callable instanceof Constructor) {
                        formalTypes = ((Constructor) callable).getParameterTypes();
                    } else {
                        throw new IllegalArgumentException("Expected method or  constructor; callable is " + callable.getClass().getName());
                    }
                }
                args[i] = coerceBigDecimal((BigDecimal) arg, formalTypes[i]);
            }
        }
    }

    public static void coerceBigDecimals(Class<?>[] formalTypes, Object[] args) {
        int typeLen = formalTypes.length;
        int argsLen = args.length;
        int min = Math.min(typeLen, argsLen);
        for (int i = 0; i < min; i++) {
            Object arg = args[i];
            if (arg instanceof BigDecimal) {
                args[i] = coerceBigDecimal((BigDecimal) arg, formalTypes[i]);
            }
        }
        if (argsLen > typeLen) {
            Class<?> varArgType = formalTypes[typeLen - 1];
            for (int i2 = typeLen; i2 < argsLen; i2++) {
                Object arg2 = args[i2];
                if (arg2 instanceof BigDecimal) {
                    args[i2] = coerceBigDecimal((BigDecimal) arg2, varArgType);
                }
            }
        }
    }

    public static Object coerceBigDecimal(BigDecimal bd, Class<?> formalType) {
        if (formalType == Integer.TYPE || formalType == Integer.class) {
            return Integer.valueOf(bd.intValue());
        }
        if (formalType == Double.TYPE || formalType == Double.class) {
            return Double.valueOf(bd.doubleValue());
        }
        if (formalType == Long.TYPE || formalType == Long.class) {
            return Long.valueOf(bd.longValue());
        }
        if (formalType == Float.TYPE || formalType == Float.class) {
            return Float.valueOf(bd.floatValue());
        }
        if (formalType == Short.TYPE || formalType == Short.class) {
            return Short.valueOf(bd.shortValue());
        }
        if (formalType == Byte.TYPE || formalType == Byte.class) {
            return Byte.valueOf(bd.byteValue());
        }
        if (BigInteger.class.isAssignableFrom(formalType)) {
            return bd.toBigInteger();
        }
        return bd;
    }

    public String toString() {
        String propsStr = toPropertiesString();
        return ClassUtil.getShortClassNameOfObject(this) + "@" + System.identityHashCode(this) + "(" + this.incompatibleImprovements + ", " + (propsStr.length() != 0 ? propsStr + ", ..." : "") + ")";
    }

    protected String toPropertiesString() {
        return "simpleMapWrapper=" + this.simpleMapWrapper + ", exposureLevel=" + this.classIntrospector.getExposureLevel() + ", exposeFields=" + this.classIntrospector.getExposeFields() + ", preferIndexedReadMethod=" + this.preferIndexedReadMethod + ", treatDefaultMethodsAsBeanMembers=" + this.classIntrospector.getTreatDefaultMethodsAsBeanMembers() + ", sharedClassIntrospCache=" + (this.classIntrospector.isShared() ? "@" + System.identityHashCode(this.classIntrospector) : "none");
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BeansWrapper$MethodAppearanceDecision.class */
    public static final class MethodAppearanceDecision {
        private PropertyDescriptor exposeAsProperty;
        private boolean methodInsteadOfPropertyValueBeforeCall;
        private boolean replaceExistingProperty;
        private String exposeMethodAs;
        private boolean methodShadowsProperty;

        void setDefaults(Method m, ZeroArgumentNonVoidMethodPolicy appliedZeroArgumentNonVoidMethodPolicy) {
            if (appliedZeroArgumentNonVoidMethodPolicy != null && appliedZeroArgumentNonVoidMethodPolicy != ZeroArgumentNonVoidMethodPolicy.METHOD_ONLY) {
                try {
                    this.exposeAsProperty = new PropertyDescriptor(m.getName(), m, (Method) null);
                    this.methodInsteadOfPropertyValueBeforeCall = appliedZeroArgumentNonVoidMethodPolicy == ZeroArgumentNonVoidMethodPolicy.BOTH_METHOD_AND_PROPERTY_UNLESS_BEAN_PROPERTY_READ_METHOD;
                } catch (IntrospectionException e) {
                    throw new BugException("Failed to create PropertyDescriptor for " + m, e);
                }
            } else {
                this.exposeAsProperty = null;
                this.methodInsteadOfPropertyValueBeforeCall = false;
            }
            this.exposeMethodAs = appliedZeroArgumentNonVoidMethodPolicy != ZeroArgumentNonVoidMethodPolicy.PROPERTY_ONLY_UNLESS_BEAN_PROPERTY_READ_METHOD ? m.getName() : null;
            this.methodShadowsProperty = true;
            this.replaceExistingProperty = false;
        }

        public PropertyDescriptor getExposeAsProperty() {
            return this.exposeAsProperty;
        }

        public void setExposeAsProperty(PropertyDescriptor exposeAsProperty) {
            this.exposeAsProperty = exposeAsProperty;
        }

        public boolean getReplaceExistingProperty() {
            return this.replaceExistingProperty;
        }

        public void setReplaceExistingProperty(boolean overrideExistingProperty) {
            this.replaceExistingProperty = overrideExistingProperty;
        }

        public String getExposeMethodAs() {
            return this.exposeMethodAs;
        }

        public void setExposeMethodAs(String exposeAsMethod) {
            this.exposeMethodAs = exposeAsMethod;
        }

        public boolean getMethodShadowsProperty() {
            return this.methodShadowsProperty;
        }

        public void setMethodShadowsProperty(boolean shadowEarlierProperty) {
            this.methodShadowsProperty = shadowEarlierProperty;
        }

        public boolean isMethodInsteadOfPropertyValueBeforeCall() {
            return this.methodInsteadOfPropertyValueBeforeCall;
        }

        public void setMethodInsteadOfPropertyValueBeforeCall(boolean methodInsteadOfPropertyValueBeforeCall) {
            this.methodInsteadOfPropertyValueBeforeCall = methodInsteadOfPropertyValueBeforeCall;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BeansWrapper$MethodAppearanceDecisionInput.class */
    public static final class MethodAppearanceDecisionInput {
        private Method method;
        private Class<?> containingClass;

        void setMethod(Method method) {
            this.method = method;
        }

        void setContainingClass(Class<?> containingClass) {
            this.containingClass = containingClass;
        }

        public Method getMethod() {
            return this.method;
        }

        public Class getContainingClass() {
            return this.containingClass;
        }
    }
}
