package org.springframework.boot.context.properties.bind;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.springframework.beans.BeanUtils;
import org.springframework.core.KotlinDetector;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/DefaultBindConstructorProvider.class */
class DefaultBindConstructorProvider implements BindConstructorProvider {
    DefaultBindConstructorProvider() {
    }

    @Override // org.springframework.boot.context.properties.bind.BindConstructorProvider
    public Constructor<?> getBindConstructor(Bindable<?> bindable, boolean isNestedConstructorBinding) throws SecurityException {
        Class<?> type = bindable.getType().resolve();
        if (bindable.getValue() != null || type == null) {
            return null;
        }
        if (KotlinDetector.isKotlinPresent() && KotlinDetector.isKotlinType(type)) {
            return getDeducedKotlinConstructor(type);
        }
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        if (constructors.length == 1 && constructors[0].getParameterCount() > 0) {
            return constructors[0];
        }
        Constructor<?> constructor = null;
        for (Constructor<?> candidate : constructors) {
            if (!Modifier.isPrivate(candidate.getModifiers())) {
                if (constructor != null) {
                    return null;
                }
                constructor = candidate;
            }
        }
        if (constructor != null && constructor.getParameterCount() > 0) {
            return constructor;
        }
        return null;
    }

    private Constructor<?> getDeducedKotlinConstructor(Class<?> type) {
        Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(type);
        if (primaryConstructor != null && primaryConstructor.getParameterCount() > 0) {
            return primaryConstructor;
        }
        return null;
    }
}
