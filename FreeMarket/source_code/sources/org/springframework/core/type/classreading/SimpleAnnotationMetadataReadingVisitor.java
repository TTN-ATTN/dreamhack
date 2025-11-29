package org.springframework.core.type.classreading;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/classreading/SimpleAnnotationMetadataReadingVisitor.class */
final class SimpleAnnotationMetadataReadingVisitor extends ClassVisitor {

    @Nullable
    private final ClassLoader classLoader;
    private String className;
    private int access;

    @Nullable
    private String superClassName;
    private String[] interfaceNames;

    @Nullable
    private String enclosingClassName;
    private boolean independentInnerClass;
    private Set<String> memberClassNames;
    private List<MergedAnnotation<?>> annotations;
    private List<SimpleMethodMetadata> annotatedMethods;

    @Nullable
    private SimpleAnnotationMetadata metadata;

    @Nullable
    private Source source;

    SimpleAnnotationMetadataReadingVisitor(@Nullable ClassLoader classLoader) {
        super(17432576);
        this.className = "";
        this.interfaceNames = new String[0];
        this.memberClassNames = new LinkedHashSet(4);
        this.annotations = new ArrayList();
        this.annotatedMethods = new ArrayList();
        this.classLoader = classLoader;
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visit(int version, int access, String name, String signature, @Nullable String supername, String[] interfaces) {
        this.className = toClassName(name);
        this.access = access;
        if (supername != null && !isInterface(access)) {
            this.superClassName = toClassName(supername);
        }
        this.interfaceNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            this.interfaceNames[i] = toClassName(interfaces[i]);
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitOuterClass(String owner, String name, String desc) {
        this.enclosingClassName = toClassName(owner);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitInnerClass(String name, @Nullable String outerName, String innerName, int access) {
        if (outerName != null) {
            String className = toClassName(name);
            String outerClassName = toClassName(outerName);
            if (this.className.equals(className)) {
                this.enclosingClassName = outerClassName;
                this.independentInnerClass = (access & 8) != 0;
            } else if (this.className.equals(outerClassName)) {
                this.memberClassNames.add(className);
            }
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    @Nullable
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        ClassLoader classLoader = this.classLoader;
        Source source = getSource();
        List<MergedAnnotation<?>> list = this.annotations;
        list.getClass();
        return MergedAnnotationReadingVisitor.get(classLoader, source, descriptor, visible, (v1) -> {
            r4.add(v1);
        });
    }

    @Override // org.springframework.asm.ClassVisitor
    @Nullable
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (isBridge(access)) {
            return null;
        }
        ClassLoader classLoader = this.classLoader;
        String str = this.className;
        List<SimpleMethodMetadata> list = this.annotatedMethods;
        list.getClass();
        return new SimpleMethodMetadataReadingVisitor(classLoader, str, access, name, descriptor, (v1) -> {
            r7.add(v1);
        });
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitEnd() {
        String[] memberClassNames = StringUtils.toStringArray(this.memberClassNames);
        MethodMetadata[] annotatedMethods = (MethodMetadata[]) this.annotatedMethods.toArray(new MethodMetadata[0]);
        MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
        this.metadata = new SimpleAnnotationMetadata(this.className, this.access, this.enclosingClassName, this.superClassName, this.independentInnerClass, this.interfaceNames, memberClassNames, annotatedMethods, annotations);
    }

    public SimpleAnnotationMetadata getMetadata() {
        Assert.state(this.metadata != null, "AnnotationMetadata not initialized");
        return this.metadata;
    }

    private Source getSource() {
        Source source = this.source;
        if (source == null) {
            source = new Source(this.className);
            this.source = source;
        }
        return source;
    }

    private String toClassName(String name) {
        return ClassUtils.convertResourcePathToClassName(name);
    }

    private boolean isBridge(int access) {
        return (access & 64) != 0;
    }

    private boolean isInterface(int access) {
        return (access & 512) != 0;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/classreading/SimpleAnnotationMetadataReadingVisitor$Source.class */
    private static final class Source {
        private final String className;

        Source(String className) {
            this.className = className;
        }

        public int hashCode() {
            return this.className.hashCode();
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.className.equals(((Source) obj).className);
        }

        public String toString() {
            return this.className;
        }
    }
}
