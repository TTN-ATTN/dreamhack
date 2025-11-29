package org.springframework.boot.context.properties;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.BindConstructorProvider;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.core.KotlinDetector;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindConstructorProvider.class */
class ConfigurationPropertiesBindConstructorProvider implements BindConstructorProvider {
    static final ConfigurationPropertiesBindConstructorProvider INSTANCE = new ConfigurationPropertiesBindConstructorProvider();

    ConfigurationPropertiesBindConstructorProvider() {
    }

    @Override // org.springframework.boot.context.properties.bind.BindConstructorProvider
    public Constructor<?> getBindConstructor(Bindable<?> bindable, boolean isNestedConstructorBinding) {
        return getBindConstructor(bindable.getType().resolve(), isNestedConstructorBinding);
    }

    Constructor<?> getBindConstructor(Class<?> type, boolean isNestedConstructorBinding) throws SecurityException {
        if (type == null) {
            return null;
        }
        Constructor<?> constructor = findConstructorBindingAnnotatedConstructor(type);
        if (constructor == null && (isConstructorBindingType(type) || isNestedConstructorBinding)) {
            constructor = deduceBindConstructor(type);
        }
        return constructor;
    }

    private Constructor<?> findConstructorBindingAnnotatedConstructor(Class<?> type) {
        Constructor<?> constructor;
        if (isKotlinType(type) && (constructor = BeanUtils.findPrimaryConstructor(type)) != null) {
            return findAnnotatedConstructor(type, constructor);
        }
        return findAnnotatedConstructor(type, type.getDeclaredConstructors());
    }

    private Constructor<?> findAnnotatedConstructor(Class<?> type, Constructor<?>... candidates) {
        Constructor<?> constructor = null;
        for (Constructor<?> candidate : candidates) {
            if (MergedAnnotations.from(candidate).isPresent(ConstructorBinding.class)) {
                Assert.state(candidate.getParameterCount() > 0, (Supplier<String>) () -> {
                    return type.getName() + " declares @ConstructorBinding on a no-args constructor";
                });
                Assert.state(constructor == null, (Supplier<String>) () -> {
                    return type.getName() + " has more than one @ConstructorBinding constructor";
                });
                constructor = candidate;
            }
        }
        return constructor;
    }

    private boolean isConstructorBindingType(Class<?> type) {
        return isImplicitConstructorBindingType(type) || isConstructorBindingAnnotatedType(type);
    }

    private boolean isImplicitConstructorBindingType(Class<?> type) {
        Class<?> superclass = type.getSuperclass();
        return superclass != null && "java.lang.Record".equals(superclass.getName());
    }

    private boolean isConstructorBindingAnnotatedType(Class<?> type) {
        return MergedAnnotations.from(type, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES).isPresent(ConstructorBinding.class);
    }

    private Constructor<?> deduceBindConstructor(Class<?> type) throws SecurityException {
        if (isKotlinType(type)) {
            return deducedKotlinBindConstructor(type);
        }
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        if (constructors.length == 1 && constructors[0].getParameterCount() > 0) {
            return constructors[0];
        }
        return null;
    }

    private Constructor<?> deducedKotlinBindConstructor(Class<?> type) {
        Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(type);
        if (primaryConstructor != null && primaryConstructor.getParameterCount() > 0) {
            return primaryConstructor;
        }
        return null;
    }

    private boolean isKotlinType(Class<?> type) {
        return KotlinDetector.isKotlinPresent() && KotlinDetector.isKotlinType(type);
    }
}
