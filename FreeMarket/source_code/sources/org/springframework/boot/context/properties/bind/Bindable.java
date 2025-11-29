package org.springframework.boot.context.properties.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.core.ResolvableType;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/Bindable.class */
public final class Bindable<T> {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    private static final EnumSet<BindRestriction> NO_BIND_RESTRICTIONS = EnumSet.noneOf(BindRestriction.class);
    private final ResolvableType type;
    private final ResolvableType boxedType;
    private final Supplier<T> value;
    private final Annotation[] annotations;
    private final EnumSet<BindRestriction> bindRestrictions;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/Bindable$BindRestriction.class */
    public enum BindRestriction {
        NO_DIRECT_PROPERTY
    }

    private Bindable(ResolvableType type, ResolvableType boxedType, Supplier<T> value, Annotation[] annotations, EnumSet<BindRestriction> bindRestrictions) {
        this.type = type;
        this.boxedType = boxedType;
        this.value = value;
        this.annotations = annotations;
        this.bindRestrictions = bindRestrictions;
    }

    public ResolvableType getType() {
        return this.type;
    }

    public ResolvableType getBoxedType() {
        return this.boxedType;
    }

    public Supplier<T> getValue() {
        return this.value;
    }

    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public <A extends Annotation> A getAnnotation(Class<A> cls) {
        for (Annotation annotation : this.annotations) {
            A a = (A) annotation;
            if (cls.isInstance(a)) {
                return a;
            }
        }
        return null;
    }

    public boolean hasBindRestriction(BindRestriction bindRestriction) {
        return this.bindRestrictions.contains(bindRestriction);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bindable<?> other = (Bindable) obj;
        boolean result = 1 != 0 && nullSafeEquals(this.type.resolve(), other.type.resolve());
        boolean result2 = result && nullSafeEquals(this.annotations, other.annotations);
        boolean result3 = result2 && nullSafeEquals(this.bindRestrictions, other.bindRestrictions);
        return result3;
    }

    public int hashCode() {
        int result = (31 * 1) + ObjectUtils.nullSafeHashCode(this.type);
        return (31 * ((31 * result) + ObjectUtils.nullSafeHashCode((Object[]) this.annotations))) + ObjectUtils.nullSafeHashCode(this.bindRestrictions);
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("type", this.type);
        creator.append("value", this.value != null ? "provided" : "none");
        creator.append("annotations", this.annotations);
        return creator.toString();
    }

    private boolean nullSafeEquals(Object o1, Object o2) {
        return ObjectUtils.nullSafeEquals(o1, o2);
    }

    public Bindable<T> withAnnotations(Annotation... annotations) {
        return new Bindable<>(this.type, this.boxedType, this.value, annotations != null ? annotations : NO_ANNOTATIONS, NO_BIND_RESTRICTIONS);
    }

    public Bindable<T> withExistingValue(T existingValue) {
        Assert.isTrue(existingValue == null || this.type.isArray() || this.boxedType.resolve().isInstance(existingValue), (Supplier<String>) () -> {
            return "ExistingValue must be an instance of " + this.type;
        });
        Supplier<T> value = existingValue != null ? () -> {
            return existingValue;
        } : null;
        return new Bindable<>(this.type, this.boxedType, value, this.annotations, this.bindRestrictions);
    }

    public Bindable<T> withSuppliedValue(Supplier<T> suppliedValue) {
        return new Bindable<>(this.type, this.boxedType, suppliedValue, this.annotations, this.bindRestrictions);
    }

    public Bindable<T> withBindRestrictions(BindRestriction... additionalRestrictions) {
        EnumSet<BindRestriction> bindRestrictions = EnumSet.copyOf((EnumSet) this.bindRestrictions);
        bindRestrictions.addAll(Arrays.asList(additionalRestrictions));
        return new Bindable<>(this.type, this.boxedType, this.value, this.annotations, bindRestrictions);
    }

    public static <T> Bindable<T> ofInstance(T instance) {
        Assert.notNull(instance, "Instance must not be null");
        return of(instance.getClass()).withExistingValue(instance);
    }

    public static <T> Bindable<T> of(Class<T> type) {
        Assert.notNull(type, "Type must not be null");
        return of(ResolvableType.forClass(type));
    }

    public static <E> Bindable<List<E>> listOf(Class<E> elementType) {
        return of(ResolvableType.forClassWithGenerics((Class<?>) List.class, (Class<?>[]) new Class[]{elementType}));
    }

    public static <E> Bindable<Set<E>> setOf(Class<E> elementType) {
        return of(ResolvableType.forClassWithGenerics((Class<?>) Set.class, (Class<?>[]) new Class[]{elementType}));
    }

    public static <K, V> Bindable<Map<K, V>> mapOf(Class<K> keyType, Class<V> valueType) {
        return of(ResolvableType.forClassWithGenerics((Class<?>) Map.class, (Class<?>[]) new Class[]{keyType, valueType}));
    }

    public static <T> Bindable<T> of(ResolvableType type) throws NegativeArraySizeException {
        Assert.notNull(type, "Type must not be null");
        ResolvableType boxedType = box(type);
        return new Bindable<>(type, boxedType, null, NO_ANNOTATIONS, NO_BIND_RESTRICTIONS);
    }

    private static ResolvableType box(ResolvableType type) throws NegativeArraySizeException {
        Class<?> resolved = type.resolve();
        if (resolved != null && resolved.isPrimitive()) {
            Object array = Array.newInstance(resolved, 1);
            Class<?> wrapperType = Array.get(array, 0).getClass();
            return ResolvableType.forClass(wrapperType);
        }
        if (resolved != null && resolved.isArray()) {
            return ResolvableType.forArrayComponent(box(type.getComponentType()));
        }
        return type;
    }
}
