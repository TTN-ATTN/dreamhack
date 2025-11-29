package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/RepeatableContainers.class */
public abstract class RepeatableContainers {

    @Nullable
    private final RepeatableContainers parent;

    private RepeatableContainers(@Nullable RepeatableContainers parent) {
        this.parent = parent;
    }

    public RepeatableContainers and(Class<? extends Annotation> container, Class<? extends Annotation> repeatable) {
        return new ExplicitRepeatableContainer(this, repeatable, container);
    }

    @Nullable
    Annotation[] findRepeatedAnnotations(Annotation annotation) {
        if (this.parent == null) {
            return null;
        }
        return this.parent.findRepeatedAnnotations(annotation);
    }

    public boolean equals(@Nullable Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.parent, ((RepeatableContainers) other).parent);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.parent);
    }

    public static RepeatableContainers standardRepeatables() {
        return StandardRepeatableContainers.INSTANCE;
    }

    public static RepeatableContainers of(Class<? extends Annotation> repeatable, @Nullable Class<? extends Annotation> container) {
        return new ExplicitRepeatableContainer(null, repeatable, container);
    }

    public static RepeatableContainers none() {
        return NoRepeatableContainers.INSTANCE;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/RepeatableContainers$StandardRepeatableContainers.class */
    private static class StandardRepeatableContainers extends RepeatableContainers {
        private static final Map<Class<? extends Annotation>, Object> cache = new ConcurrentReferenceHashMap();
        private static final Object NONE = new Object();
        private static StandardRepeatableContainers INSTANCE = new StandardRepeatableContainers();

        StandardRepeatableContainers() {
            super();
        }

        @Override // org.springframework.core.annotation.RepeatableContainers
        @Nullable
        Annotation[] findRepeatedAnnotations(Annotation annotation) {
            Method method = getRepeatedAnnotationsMethod(annotation.annotationType());
            if (method != null) {
                return (Annotation[]) AnnotationUtils.invokeAnnotationMethod(method, annotation);
            }
            return super.findRepeatedAnnotations(annotation);
        }

        @Nullable
        private static Method getRepeatedAnnotationsMethod(Class<? extends Annotation> annotationType) {
            Object result = cache.computeIfAbsent(annotationType, StandardRepeatableContainers::computeRepeatedAnnotationsMethod);
            if (result != NONE) {
                return (Method) result;
            }
            return null;
        }

        private static Object computeRepeatedAnnotationsMethod(Class<? extends Annotation> annotationType) {
            AttributeMethods methods = AttributeMethods.forAnnotationType(annotationType);
            Method method = methods.get("value");
            if (method != null) {
                Class<?> returnType = method.getReturnType();
                if (returnType.isArray()) {
                    Class<?> componentType = returnType.getComponentType();
                    if (Annotation.class.isAssignableFrom(componentType) && componentType.isAnnotationPresent(Repeatable.class)) {
                        return method;
                    }
                }
            }
            return NONE;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/RepeatableContainers$ExplicitRepeatableContainer.class */
    private static class ExplicitRepeatableContainer extends RepeatableContainers {
        private final Class<? extends Annotation> repeatable;
        private final Class<? extends Annotation> container;
        private final Method valueMethod;

        ExplicitRepeatableContainer(@Nullable RepeatableContainers parent, Class<? extends Annotation> repeatable, @Nullable Class<? extends Annotation> container) {
            super();
            Assert.notNull(repeatable, "Repeatable must not be null");
            container = container == null ? deduceContainer(repeatable) : container;
            Method valueMethod = AttributeMethods.forAnnotationType(container).get("value");
            try {
                if (valueMethod == null) {
                    throw new NoSuchMethodException("No value method found");
                }
                Class<?> returnType = valueMethod.getReturnType();
                if (!returnType.isArray() || returnType.getComponentType() != repeatable) {
                    throw new AnnotationConfigurationException("Container type [" + container.getName() + "] must declare a 'value' attribute for an array of type [" + repeatable.getName() + "]");
                }
                this.repeatable = repeatable;
                this.container = container;
                this.valueMethod = valueMethod;
            } catch (AnnotationConfigurationException ex) {
                throw ex;
            } catch (Throwable ex2) {
                throw new AnnotationConfigurationException("Invalid declaration of container type [" + container.getName() + "] for repeatable annotation [" + repeatable.getName() + "]", ex2);
            }
        }

        private Class<? extends Annotation> deduceContainer(Class<? extends Annotation> repeatable) {
            Repeatable annotation = (Repeatable) repeatable.getAnnotation(Repeatable.class);
            Assert.notNull(annotation, (Supplier<String>) () -> {
                return "Annotation type must be a repeatable annotation: failed to resolve container type for " + repeatable.getName();
            });
            return annotation.value();
        }

        @Override // org.springframework.core.annotation.RepeatableContainers
        @Nullable
        Annotation[] findRepeatedAnnotations(Annotation annotation) {
            if (this.container.isAssignableFrom(annotation.annotationType())) {
                return (Annotation[]) AnnotationUtils.invokeAnnotationMethod(this.valueMethod, annotation);
            }
            return super.findRepeatedAnnotations(annotation);
        }

        @Override // org.springframework.core.annotation.RepeatableContainers
        public boolean equals(@Nullable Object other) {
            if (!super.equals(other)) {
                return false;
            }
            ExplicitRepeatableContainer otherErc = (ExplicitRepeatableContainer) other;
            return this.container.equals(otherErc.container) && this.repeatable.equals(otherErc.repeatable);
        }

        @Override // org.springframework.core.annotation.RepeatableContainers
        public int hashCode() {
            int hashCode = super.hashCode();
            return (31 * ((31 * hashCode) + this.container.hashCode())) + this.repeatable.hashCode();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/RepeatableContainers$NoRepeatableContainers.class */
    private static class NoRepeatableContainers extends RepeatableContainers {
        private static NoRepeatableContainers INSTANCE = new NoRepeatableContainers();

        NoRepeatableContainers() {
            super();
        }
    }
}
