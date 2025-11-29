package org.springframework.objenesis.instantiator.util;

import org.springframework.objenesis.ObjenesisException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/objenesis/instantiator/util/ClassUtils.class */
public final class ClassUtils {
    private ClassUtils() {
    }

    public static String classNameToInternalClassName(String className) {
        return className.replace('.', '/');
    }

    public static String classNameToResource(String className) {
        return classNameToInternalClassName(className) + org.springframework.util.ClassUtils.CLASS_FILE_SUFFIX;
    }

    public static <T> Class<T> getExistingClass(ClassLoader classLoader, String str) {
        try {
            return (Class<T>) Class.forName(str, true, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ObjenesisException(e);
        }
    }
}
