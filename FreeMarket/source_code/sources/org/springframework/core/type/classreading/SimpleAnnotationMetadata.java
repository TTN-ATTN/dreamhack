package org.springframework.core.type.classreading;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/classreading/SimpleAnnotationMetadata.class */
final class SimpleAnnotationMetadata implements AnnotationMetadata {
    private final String className;
    private final int access;

    @Nullable
    private final String enclosingClassName;

    @Nullable
    private final String superClassName;
    private final boolean independentInnerClass;
    private final String[] interfaceNames;
    private final String[] memberClassNames;
    private final MethodMetadata[] annotatedMethods;
    private final MergedAnnotations annotations;

    @Nullable
    private Set<String> annotationTypes;

    SimpleAnnotationMetadata(String className, int access, @Nullable String enclosingClassName, @Nullable String superClassName, boolean independentInnerClass, String[] interfaceNames, String[] memberClassNames, MethodMetadata[] annotatedMethods, MergedAnnotations annotations) {
        this.className = className;
        this.access = access;
        this.enclosingClassName = enclosingClassName;
        this.superClassName = superClassName;
        this.independentInnerClass = independentInnerClass;
        this.interfaceNames = interfaceNames;
        this.memberClassNames = memberClassNames;
        this.annotatedMethods = annotatedMethods;
        this.annotations = annotations;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String getClassName() {
        return this.className;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isInterface() {
        return (this.access & 512) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAnnotation() {
        return (this.access & 8192) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAbstract() {
        return (this.access & 1024) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isFinal() {
        return (this.access & 16) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isIndependent() {
        return this.enclosingClassName == null || this.independentInnerClass;
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getSuperClassName() {
        return this.superClassName;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getInterfaceNames() {
        return (String[]) this.interfaceNames.clone();
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getMemberClassNames() {
        return (String[]) this.memberClassNames.clone();
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public MergedAnnotations getAnnotations() {
        return this.annotations;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = this.annotationTypes;
        if (annotationTypes == null) {
            annotationTypes = Collections.unmodifiableSet(super.getAnnotationTypes());
            this.annotationTypes = annotationTypes;
        }
        return annotationTypes;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        Set<MethodMetadata> annotatedMethods = null;
        for (MethodMetadata annotatedMethod : this.annotatedMethods) {
            if (annotatedMethod.isAnnotated(annotationName)) {
                if (annotatedMethods == null) {
                    annotatedMethods = new LinkedHashSet<>(4);
                }
                annotatedMethods.add(annotatedMethod);
            }
        }
        return annotatedMethods != null ? annotatedMethods : Collections.emptySet();
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || ((obj instanceof SimpleAnnotationMetadata) && this.className.equals(((SimpleAnnotationMetadata) obj).className));
    }

    public int hashCode() {
        return this.className.hashCode();
    }

    public String toString() {
        return this.className;
    }
}
