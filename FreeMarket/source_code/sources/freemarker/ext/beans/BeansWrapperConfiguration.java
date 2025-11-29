package freemarker.ext.beans;

import freemarker.template.ObjectWrapper;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BeansWrapperConfiguration.class */
public abstract class BeansWrapperConfiguration implements Cloneable {
    private final Version incompatibleImprovements;
    private ClassIntrospectorBuilder classIntrospectorBuilder;
    private boolean simpleMapWrapper;
    private boolean preferIndexedReadMethod;
    private int defaultDateType;
    private ObjectWrapper outerIdentity;
    private boolean strict;
    private boolean useModelCache;

    protected BeansWrapperConfiguration(Version incompatibleImprovements, boolean isIncompImprsAlreadyNormalized) {
        this.simpleMapWrapper = false;
        this.defaultDateType = 0;
        this.outerIdentity = null;
        this.strict = false;
        this.useModelCache = false;
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        if (!isIncompImprsAlreadyNormalized) {
            _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.beans", "BeansWrapper");
        }
        Version incompatibleImprovements2 = isIncompImprsAlreadyNormalized ? incompatibleImprovements : BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
        this.incompatibleImprovements = incompatibleImprovements2;
        this.preferIndexedReadMethod = incompatibleImprovements2.intValue() < _VersionInts.V_2_3_27;
        this.classIntrospectorBuilder = new ClassIntrospectorBuilder(incompatibleImprovements2);
    }

    protected BeansWrapperConfiguration(Version incompatibleImprovements) {
        this(incompatibleImprovements, false);
    }

    public int hashCode() {
        int result = (31 * 1) + this.incompatibleImprovements.hashCode();
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.simpleMapWrapper ? 1231 : 1237))) + (this.preferIndexedReadMethod ? 1231 : 1237))) + this.defaultDateType)) + (this.outerIdentity != null ? this.outerIdentity.hashCode() : 0))) + (this.strict ? 1231 : 1237))) + (this.useModelCache ? 1231 : 1237))) + this.classIntrospectorBuilder.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BeansWrapperConfiguration other = (BeansWrapperConfiguration) obj;
        return this.incompatibleImprovements.equals(other.incompatibleImprovements) && this.simpleMapWrapper == other.simpleMapWrapper && this.preferIndexedReadMethod == other.preferIndexedReadMethod && this.defaultDateType == other.defaultDateType && this.outerIdentity == other.outerIdentity && this.strict == other.strict && this.useModelCache == other.useModelCache && this.classIntrospectorBuilder.equals(other.classIntrospectorBuilder);
    }

    protected Object clone(boolean deepCloneKey) {
        try {
            BeansWrapperConfiguration clone = (BeansWrapperConfiguration) super.clone();
            if (deepCloneKey) {
                clone.classIntrospectorBuilder = (ClassIntrospectorBuilder) this.classIntrospectorBuilder.clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone BeansWrapperConfiguration", e);
        }
    }

    public boolean isSimpleMapWrapper() {
        return this.simpleMapWrapper;
    }

    public void setSimpleMapWrapper(boolean simpleMapWrapper) {
        this.simpleMapWrapper = simpleMapWrapper;
    }

    public boolean getPreferIndexedReadMethod() {
        return this.preferIndexedReadMethod;
    }

    public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
        this.preferIndexedReadMethod = preferIndexedReadMethod;
    }

    public int getDefaultDateType() {
        return this.defaultDateType;
    }

    public void setDefaultDateType(int defaultDateType) {
        this.defaultDateType = defaultDateType;
    }

    public ObjectWrapper getOuterIdentity() {
        return this.outerIdentity;
    }

    public void setOuterIdentity(ObjectWrapper outerIdentity) {
        this.outerIdentity = outerIdentity;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean getUseModelCache() {
        return this.useModelCache;
    }

    public void setUseModelCache(boolean useModelCache) {
        this.useModelCache = useModelCache;
    }

    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    public int getExposureLevel() {
        return this.classIntrospectorBuilder.getExposureLevel();
    }

    public void setExposureLevel(int exposureLevel) {
        this.classIntrospectorBuilder.setExposureLevel(exposureLevel);
    }

    public boolean getExposeFields() {
        return this.classIntrospectorBuilder.getExposeFields();
    }

    public void setExposeFields(boolean exposeFields) {
        this.classIntrospectorBuilder.setExposeFields(exposeFields);
    }

    public MemberAccessPolicy getMemberAccessPolicy() {
        return this.classIntrospectorBuilder.getMemberAccessPolicy();
    }

    public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
        this.classIntrospectorBuilder.setMemberAccessPolicy(memberAccessPolicy);
    }

    public boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.classIntrospectorBuilder.getTreatDefaultMethodsAsBeanMembers();
    }

    public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
        this.classIntrospectorBuilder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
    }

    public ZeroArgumentNonVoidMethodPolicy getDefaultZeroArgumentNonVoidMethodPolicy() {
        return this.classIntrospectorBuilder.getDefaultZeroArgumentNonVoidMethodPolicy();
    }

    public void setDefaultZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy defaultZeroArgumentNonVoidMethodPolicy) {
        this.classIntrospectorBuilder.setDefaultZeroArgumentNonVoidMethodPolicy(defaultZeroArgumentNonVoidMethodPolicy);
    }

    public ZeroArgumentNonVoidMethodPolicy getRecordZeroArgumentNonVoidMethodPolicy() {
        return this.classIntrospectorBuilder.getRecordZeroArgumentNonVoidMethodPolicy();
    }

    public void setRecordZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy recordZeroArgumentNonVoidMethodPolicy) {
        this.classIntrospectorBuilder.setRecordZeroArgumentNonVoidMethodPolicy(recordZeroArgumentNonVoidMethodPolicy);
    }

    public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.classIntrospectorBuilder.getMethodAppearanceFineTuner();
    }

    public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
        this.classIntrospectorBuilder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
    }

    MethodSorter getMethodSorter() {
        return this.classIntrospectorBuilder.getMethodSorter();
    }

    void setMethodSorter(MethodSorter methodSorter) {
        this.classIntrospectorBuilder.setMethodSorter(methodSorter);
    }

    ClassIntrospectorBuilder getClassIntrospectorBuilder() {
        return this.classIntrospectorBuilder;
    }
}
