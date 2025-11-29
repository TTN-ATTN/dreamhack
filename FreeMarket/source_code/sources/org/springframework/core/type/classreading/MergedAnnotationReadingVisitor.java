package org.springframework.core.type.classreading;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationFilter;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/classreading/MergedAnnotationReadingVisitor.class */
class MergedAnnotationReadingVisitor<A extends Annotation> extends AnnotationVisitor {

    @Nullable
    private final ClassLoader classLoader;

    @Nullable
    private final Object source;
    private final Class<A> annotationType;
    private final Consumer<MergedAnnotation<A>> consumer;
    private final Map<String, Object> attributes;

    public MergedAnnotationReadingVisitor(@Nullable ClassLoader classLoader, @Nullable Object source, Class<A> annotationType, Consumer<MergedAnnotation<A>> consumer) {
        super(17432576);
        this.attributes = new LinkedHashMap(4);
        this.classLoader = classLoader;
        this.source = source;
        this.annotationType = annotationType;
        this.consumer = consumer;
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visit(String name, Object value) {
        if (value instanceof Type) {
            value = ((Type) value).getClassName();
        }
        this.attributes.put(name, value);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visitEnum(String name, String descriptor, String value) {
        visitEnum(descriptor, value, enumValue -> {
            this.attributes.put(name, enumValue);
        });
    }

    @Override // org.springframework.asm.AnnotationVisitor
    @Nullable
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        return visitAnnotation(descriptor, annotation -> {
            this.attributes.put(name, annotation);
        });
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public AnnotationVisitor visitArray(String name) {
        return new ArrayVisitor(value -> {
            this.attributes.put(name, value);
        });
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visitEnd() {
        MergedAnnotation<A> annotation = MergedAnnotation.of(this.classLoader, this.source, this.annotationType, this.attributes);
        this.consumer.accept(annotation);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <E extends Enum<E>> void visitEnum(String descriptor, String value, Consumer<E> consumer) {
        String className = Type.getType(descriptor).getClassName();
        consumer.accept(Enum.valueOf(ClassUtils.resolveClassName(className, this.classLoader), value));
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public <T extends Annotation> AnnotationVisitor visitAnnotation(String descriptor, Consumer<MergedAnnotation<T>> consumer) throws IllegalArgumentException {
        String className = Type.getType(descriptor).getClassName();
        if (AnnotationFilter.PLAIN.matches(className)) {
            return null;
        }
        return new MergedAnnotationReadingVisitor(this.classLoader, this.source, ClassUtils.resolveClassName(className, this.classLoader), consumer);
    }

    @Nullable
    static <A extends Annotation> AnnotationVisitor get(@Nullable ClassLoader classLoader, @Nullable Object source, String descriptor, boolean visible, Consumer<MergedAnnotation<A>> consumer) {
        if (!visible) {
            return null;
        }
        String typeName = Type.getType(descriptor).getClassName();
        if (AnnotationFilter.PLAIN.matches(typeName)) {
            return null;
        }
        try {
            return new MergedAnnotationReadingVisitor(classLoader, source, ClassUtils.forName(typeName, classLoader), consumer);
        } catch (ClassNotFoundException | LinkageError e) {
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/classreading/MergedAnnotationReadingVisitor$ArrayVisitor.class */
    private class ArrayVisitor extends AnnotationVisitor {
        private final List<Object> elements;
        private final Consumer<Object[]> consumer;

        ArrayVisitor(Consumer<Object[]> consumer) {
            super(17432576);
            this.elements = new ArrayList();
            this.consumer = consumer;
        }

        @Override // org.springframework.asm.AnnotationVisitor
        public void visit(String name, Object value) {
            if (value instanceof Type) {
                value = ((Type) value).getClassName();
            }
            this.elements.add(value);
        }

        @Override // org.springframework.asm.AnnotationVisitor
        public void visitEnum(String name, String descriptor, String value) {
            MergedAnnotationReadingVisitor mergedAnnotationReadingVisitor = MergedAnnotationReadingVisitor.this;
            List<Object> list = this.elements;
            list.getClass();
            mergedAnnotationReadingVisitor.visitEnum(descriptor, value, (v1) -> {
                r3.add(v1);
            });
        }

        @Override // org.springframework.asm.AnnotationVisitor
        @Nullable
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            MergedAnnotationReadingVisitor mergedAnnotationReadingVisitor = MergedAnnotationReadingVisitor.this;
            List<Object> list = this.elements;
            list.getClass();
            return mergedAnnotationReadingVisitor.visitAnnotation(descriptor, (v1) -> {
                r2.add(v1);
            });
        }

        @Override // org.springframework.asm.AnnotationVisitor
        public void visitEnd() {
            Class<?> componentType = getComponentType();
            Object[] array = (Object[]) Array.newInstance(componentType, this.elements.size());
            this.consumer.accept(this.elements.toArray(array));
        }

        private Class<?> getComponentType() {
            if (this.elements.isEmpty()) {
                return Object.class;
            }
            Object firstElement = this.elements.get(0);
            if (firstElement instanceof Enum) {
                return ((Enum) firstElement).getDeclaringClass();
            }
            return firstElement.getClass();
        }
    }
}
