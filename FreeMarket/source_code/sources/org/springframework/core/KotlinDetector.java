package org.springframework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/KotlinDetector.class */
public abstract class KotlinDetector {

    @Nullable
    private static final Class<? extends Annotation> kotlinMetadata;
    private static final boolean kotlinReflectPresent;

    /* JADX WARN: Multi-variable type inference failed */
    static {
        Class clsForName;
        ClassLoader classLoader = KotlinDetector.class.getClassLoader();
        try {
            clsForName = ClassUtils.forName("kotlin.Metadata", classLoader);
        } catch (ClassNotFoundException e) {
            clsForName = null;
        }
        kotlinMetadata = clsForName;
        kotlinReflectPresent = ClassUtils.isPresent("kotlin.reflect.full.KClasses", classLoader);
    }

    public static boolean isKotlinPresent() {
        return kotlinMetadata != null;
    }

    public static boolean isKotlinReflectPresent() {
        return kotlinReflectPresent;
    }

    public static boolean isKotlinType(Class<?> clazz) {
        return (kotlinMetadata == null || clazz.getDeclaredAnnotation(kotlinMetadata) == null) ? false : true;
    }

    public static boolean isSuspendingFunction(Method method) {
        if (isKotlinType(method.getDeclaringClass())) {
            Class<?>[] types = method.getParameterTypes();
            if (types.length > 0 && "kotlin.coroutines.Continuation".equals(types[types.length - 1].getName())) {
                return true;
            }
            return false;
        }
        return false;
    }
}
