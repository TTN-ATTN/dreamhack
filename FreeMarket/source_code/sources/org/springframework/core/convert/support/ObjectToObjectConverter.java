package org.springframework.core.convert.support;

import ch.qos.logback.core.CoreConstants;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/ObjectToObjectConverter.class */
final class ObjectToObjectConverter implements ConditionalGenericConverter {
    private static final Map<Class<?>, Executable> conversionExecutableCache = new ConcurrentReferenceHashMap(32);

    ObjectToObjectConverter() {
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getType() != targetType.getType() && hasConversionMethodOrConstructor(targetType.getType(), sourceType.getType());
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws NoSuchMethodException, SecurityException {
        if (source == null) {
            return null;
        }
        Class<?> sourceClass = sourceType.getType();
        Class<?> targetClass = targetType.getType();
        Executable executable = getValidatedExecutable(targetClass, sourceClass);
        try {
            if (executable instanceof Method) {
                Method method = (Method) executable;
                ReflectionUtils.makeAccessible(method);
                if (!Modifier.isStatic(method.getModifiers())) {
                    return method.invoke(source, new Object[0]);
                }
                return method.invoke(null, source);
            }
            if (executable instanceof Constructor) {
                Constructor<?> ctor = (Constructor) executable;
                ReflectionUtils.makeAccessible(ctor);
                return ctor.newInstance(source);
            }
            throw new IllegalStateException(String.format("No to%3$s() method exists on %1$s, and no static valueOf/of/from(%1$s) method or %3$s(%1$s) constructor exists on %2$s.", sourceClass.getName(), targetClass.getName(), targetClass.getSimpleName()));
        } catch (InvocationTargetException ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
        } catch (Throwable ex2) {
            throw new ConversionFailedException(sourceType, targetType, source, ex2);
        }
    }

    static boolean hasConversionMethodOrConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return getValidatedExecutable(targetClass, sourceClass) != null;
    }

    @Nullable
    private static Executable getValidatedExecutable(Class<?> targetClass, Class<?> sourceClass) throws NoSuchMethodException, SecurityException {
        Executable executable = conversionExecutableCache.get(targetClass);
        if (isApplicable(executable, sourceClass)) {
            return executable;
        }
        Executable executable2 = determineToMethod(targetClass, sourceClass);
        if (executable2 == null) {
            executable2 = determineFactoryMethod(targetClass, sourceClass);
            if (executable2 == null) {
                executable2 = determineFactoryConstructor(targetClass, sourceClass);
                if (executable2 == null) {
                    return null;
                }
            }
        }
        conversionExecutableCache.put(targetClass, executable2);
        return executable2;
    }

    private static boolean isApplicable(Executable executable, Class<?> sourceClass) {
        if (executable instanceof Method) {
            Method method = (Method) executable;
            if (Modifier.isStatic(method.getModifiers())) {
                return method.getParameterTypes()[0] == sourceClass;
            }
            return ClassUtils.isAssignable(method.getDeclaringClass(), sourceClass);
        }
        if (executable instanceof Constructor) {
            Constructor<?> ctor = (Constructor) executable;
            return ctor.getParameterTypes()[0] == sourceClass;
        }
        return false;
    }

    @Nullable
    private static Method determineToMethod(Class<?> targetClass, Class<?> sourceClass) {
        Method method;
        if (String.class == targetClass || String.class == sourceClass || (method = ClassUtils.getMethodIfAvailable(sourceClass, "to" + targetClass.getSimpleName(), new Class[0])) == null || Modifier.isStatic(method.getModifiers()) || !ClassUtils.isAssignable(targetClass, method.getReturnType())) {
            return null;
        }
        return method;
    }

    @Nullable
    private static Method determineFactoryMethod(Class<?> targetClass, Class<?> sourceClass) throws NoSuchMethodException, SecurityException {
        if (String.class == targetClass) {
            return null;
        }
        Method method = ClassUtils.getStaticMethod(targetClass, CoreConstants.VALUE_OF, sourceClass);
        if (method == null) {
            method = ClassUtils.getStaticMethod(targetClass, "of", sourceClass);
            if (method == null) {
                method = ClassUtils.getStaticMethod(targetClass, "from", sourceClass);
            }
        }
        if (method == null || !areRelatedTypes(targetClass, method.getReturnType())) {
            return null;
        }
        return method;
    }

    private static boolean areRelatedTypes(Class<?> type1, Class<?> type2) {
        return ClassUtils.isAssignable(type1, type2) || ClassUtils.isAssignable(type2, type1);
    }

    @Nullable
    private static Constructor<?> determineFactoryConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return ClassUtils.getConstructorIfAvailable(targetClass, sourceClass);
    }
}
