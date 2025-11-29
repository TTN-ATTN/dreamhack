package freemarker.ext.beans;

import freemarker.core._JavaVersions;
import freemarker.template.Configuration;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.NullArgumentException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ClassIntrospectorBuilder.class */
final class ClassIntrospectorBuilder implements Cloneable {
    private static final Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> INSTANCE_CACHE = new HashMap();
    private static final ReferenceQueue<ClassIntrospector> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue<>();
    private final Version incompatibleImprovements;
    private int exposureLevel;
    private boolean exposeFields;
    private MemberAccessPolicy memberAccessPolicy;
    private boolean treatDefaultMethodsAsBeanMembers;
    private ZeroArgumentNonVoidMethodPolicy defaultZeroArgumentNonVoidMethodPolicy;
    private ZeroArgumentNonVoidMethodPolicy recordZeroArgumentNonVoidMethodPolicy;
    private MethodAppearanceFineTuner methodAppearanceFineTuner;
    private MethodSorter methodSorter;

    ClassIntrospectorBuilder(ClassIntrospector ci) {
        this.exposureLevel = 1;
        this.incompatibleImprovements = ci.incompatibleImprovements;
        this.exposureLevel = ci.exposureLevel;
        this.exposeFields = ci.exposeFields;
        this.memberAccessPolicy = ci.memberAccessPolicy;
        this.treatDefaultMethodsAsBeanMembers = ci.treatDefaultMethodsAsBeanMembers;
        this.defaultZeroArgumentNonVoidMethodPolicy = ci.defaultZeroArgumentNonVoidMethodPolicy;
        this.recordZeroArgumentNonVoidMethodPolicy = ci.recordZeroArgumentNonVoidMethodPolicy;
        this.methodAppearanceFineTuner = ci.methodAppearanceFineTuner;
        this.methodSorter = ci.methodSorter;
    }

    ClassIntrospectorBuilder(Version incompatibleImprovements) {
        this.exposureLevel = 1;
        this.incompatibleImprovements = normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
        this.treatDefaultMethodsAsBeanMembers = incompatibleImprovements.intValue() >= _VersionInts.V_2_3_26;
        this.defaultZeroArgumentNonVoidMethodPolicy = ZeroArgumentNonVoidMethodPolicy.METHOD_ONLY;
        this.recordZeroArgumentNonVoidMethodPolicy = (incompatibleImprovements.intValue() < _VersionInts.V_2_3_33 || _JavaVersions.JAVA_16 == null) ? this.defaultZeroArgumentNonVoidMethodPolicy : ZeroArgumentNonVoidMethodPolicy.BOTH_METHOD_AND_PROPERTY_UNLESS_BEAN_PROPERTY_READ_METHOD;
        this.memberAccessPolicy = DefaultMemberAccessPolicy.getInstance(this.incompatibleImprovements);
    }

    private static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        return incompatibleImprovements.intValue() >= _VersionInts.V_2_3_33 ? Configuration.VERSION_2_3_33 : incompatibleImprovements.intValue() >= _VersionInts.V_2_3_30 ? Configuration.VERSION_2_3_30 : incompatibleImprovements.intValue() >= _VersionInts.V_2_3_21 ? Configuration.VERSION_2_3_21 : Configuration.VERSION_2_3_0;
    }

    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone ClassIntrospectorBuilder", e);
        }
    }

    public int hashCode() {
        int result = (31 * 1) + this.incompatibleImprovements.hashCode();
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.exposeFields ? 1231 : 1237))) + (this.treatDefaultMethodsAsBeanMembers ? 1231 : 1237))) + this.defaultZeroArgumentNonVoidMethodPolicy.hashCode())) + this.recordZeroArgumentNonVoidMethodPolicy.hashCode())) + this.exposureLevel)) + this.memberAccessPolicy.hashCode())) + System.identityHashCode(this.methodAppearanceFineTuner))) + System.identityHashCode(this.methodSorter);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ClassIntrospectorBuilder other = (ClassIntrospectorBuilder) obj;
        return this.incompatibleImprovements.equals(other.incompatibleImprovements) && this.exposeFields == other.exposeFields && this.treatDefaultMethodsAsBeanMembers == other.treatDefaultMethodsAsBeanMembers && this.defaultZeroArgumentNonVoidMethodPolicy == other.defaultZeroArgumentNonVoidMethodPolicy && this.recordZeroArgumentNonVoidMethodPolicy == other.recordZeroArgumentNonVoidMethodPolicy && this.exposureLevel == other.exposureLevel && this.memberAccessPolicy.equals(other.memberAccessPolicy) && this.methodAppearanceFineTuner == other.methodAppearanceFineTuner && this.methodSorter == other.methodSorter;
    }

    public int getExposureLevel() {
        return this.exposureLevel;
    }

    public void setExposureLevel(int exposureLevel) {
        if (exposureLevel < 0 || exposureLevel > 3) {
            throw new IllegalArgumentException("Illegal exposure level: " + exposureLevel);
        }
        this.exposureLevel = exposureLevel;
    }

    public boolean getExposeFields() {
        return this.exposeFields;
    }

    public void setExposeFields(boolean exposeFields) {
        this.exposeFields = exposeFields;
    }

    public boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.treatDefaultMethodsAsBeanMembers;
    }

    public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
        this.treatDefaultMethodsAsBeanMembers = treatDefaultMethodsAsBeanMembers;
    }

    public ZeroArgumentNonVoidMethodPolicy getDefaultZeroArgumentNonVoidMethodPolicy() {
        return this.defaultZeroArgumentNonVoidMethodPolicy;
    }

    public void setDefaultZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy defaultZeroArgumentNonVoidMethodPolicy) {
        NullArgumentException.check(defaultZeroArgumentNonVoidMethodPolicy);
        this.defaultZeroArgumentNonVoidMethodPolicy = defaultZeroArgumentNonVoidMethodPolicy;
    }

    public ZeroArgumentNonVoidMethodPolicy getRecordZeroArgumentNonVoidMethodPolicy() {
        return this.recordZeroArgumentNonVoidMethodPolicy;
    }

    public void setRecordZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy recordZeroArgumentNonVoidMethodPolicy) {
        NullArgumentException.check(recordZeroArgumentNonVoidMethodPolicy);
        this.recordZeroArgumentNonVoidMethodPolicy = recordZeroArgumentNonVoidMethodPolicy;
    }

    public MemberAccessPolicy getMemberAccessPolicy() {
        return this.memberAccessPolicy;
    }

    public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
        NullArgumentException.check(memberAccessPolicy);
        this.memberAccessPolicy = memberAccessPolicy;
    }

    public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.methodAppearanceFineTuner;
    }

    public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
        this.methodAppearanceFineTuner = methodAppearanceFineTuner;
    }

    public MethodSorter getMethodSorter() {
        return this.methodSorter;
    }

    public void setMethodSorter(MethodSorter methodSorter) {
        this.methodSorter = methodSorter;
    }

    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    private static void removeClearedReferencesFromInstanceCache() {
        while (true) {
            Reference<? extends ClassIntrospector> clearedRef = INSTANCE_CACHE_REF_QUEUE.poll();
            if (clearedRef != null) {
                synchronized (INSTANCE_CACHE) {
                    Iterator<Reference<ClassIntrospector>> it = INSTANCE_CACHE.values().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (it.next() == clearedRef) {
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

    static void clearInstanceCache() {
        synchronized (INSTANCE_CACHE) {
            INSTANCE_CACHE.clear();
        }
    }

    static Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> getInstanceCache() {
        return INSTANCE_CACHE;
    }

    ClassIntrospector build() {
        ClassIntrospector instance;
        if ((this.methodAppearanceFineTuner == null || (this.methodAppearanceFineTuner instanceof SingletonCustomizer)) && (this.methodSorter == null || (this.methodSorter instanceof SingletonCustomizer))) {
            synchronized (INSTANCE_CACHE) {
                Reference<ClassIntrospector> instanceRef = INSTANCE_CACHE.get(this);
                instance = instanceRef != null ? instanceRef.get() : null;
                if (instance == null) {
                    ClassIntrospectorBuilder thisClone = (ClassIntrospectorBuilder) clone();
                    instance = new ClassIntrospector(thisClone, new Object(), true, true);
                    INSTANCE_CACHE.put(thisClone, new WeakReference(instance, INSTANCE_CACHE_REF_QUEUE));
                }
            }
            removeClearedReferencesFromInstanceCache();
            return instance;
        }
        return new ClassIntrospector(this, new Object(), true, false);
    }
}
