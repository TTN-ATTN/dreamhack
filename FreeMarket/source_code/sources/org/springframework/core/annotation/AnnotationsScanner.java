package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/AnnotationsScanner.class */
abstract class AnnotationsScanner {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    private static final Method[] NO_METHODS = new Method[0];
    private static final Map<AnnotatedElement, Annotation[]> declaredAnnotationCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<?>, Method[]> baseTypeMethodsCache = new ConcurrentReferenceHashMap(256);

    private AnnotationsScanner() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    static <C, R> R scan(C c, AnnotatedElement annotatedElement, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> annotationsProcessor) {
        return (R) annotationsProcessor.finish(process(c, annotatedElement, searchStrategy, annotationsProcessor));
    }

    @Nullable
    private static <C, R> R process(C c, AnnotatedElement annotatedElement, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> annotationsProcessor) {
        if (annotatedElement instanceof Class) {
            return (R) processClass(c, (Class) annotatedElement, searchStrategy, annotationsProcessor);
        }
        if (annotatedElement instanceof Method) {
            return (R) processMethod(c, (Method) annotatedElement, searchStrategy, annotationsProcessor);
        }
        return (R) processElement(c, annotatedElement, annotationsProcessor);
    }

    @Nullable
    private static <C, R> R processClass(C c, Class<?> cls, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> annotationsProcessor) {
        switch (searchStrategy) {
            case DIRECT:
                return (R) processElement(c, cls, annotationsProcessor);
            case INHERITED_ANNOTATIONS:
                return (R) processClassInheritedAnnotations(c, cls, searchStrategy, annotationsProcessor);
            case SUPERCLASS:
                return (R) processClassHierarchy(c, cls, annotationsProcessor, false, false);
            case TYPE_HIERARCHY:
                return (R) processClassHierarchy(c, cls, annotationsProcessor, true, false);
            case TYPE_HIERARCHY_AND_ENCLOSING_CLASSES:
                return (R) processClassHierarchy(c, cls, annotationsProcessor, true, true);
            default:
                throw new IllegalStateException("Unsupported search strategy " + searchStrategy);
        }
    }

    @Nullable
    private static <C, R> R processClassInheritedAnnotations(C c, Class<?> cls, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> annotationsProcessor) {
        try {
            if (isWithoutHierarchy(cls, searchStrategy)) {
                return (R) processElement(c, cls, annotationsProcessor);
            }
            Annotation[] annotations = null;
            int length = Integer.MAX_VALUE;
            int i = 0;
            while (cls != null && cls != Object.class && length > 0 && !hasPlainJavaAnnotationsOnly(cls)) {
                R rDoWithAggregate = annotationsProcessor.doWithAggregate(c, i);
                if (rDoWithAggregate != null) {
                    return rDoWithAggregate;
                }
                Annotation[] declaredAnnotations = getDeclaredAnnotations(cls, true);
                if (annotations == null && declaredAnnotations.length > 0) {
                    annotations = cls.getAnnotations();
                    length = annotations.length;
                }
                for (int i2 = 0; i2 < declaredAnnotations.length; i2++) {
                    if (declaredAnnotations[i2] != null) {
                        boolean z = false;
                        int i3 = 0;
                        while (true) {
                            if (i3 >= annotations.length) {
                                break;
                            }
                            if (annotations[i3] == null || declaredAnnotations[i2].annotationType() != annotations[i3].annotationType()) {
                                i3++;
                            } else {
                                z = true;
                                annotations[i3] = null;
                                length--;
                                break;
                            }
                        }
                        if (!z) {
                            declaredAnnotations[i2] = null;
                        }
                    }
                }
                R rDoWithAnnotations = annotationsProcessor.doWithAnnotations(c, i, cls, declaredAnnotations);
                if (rDoWithAnnotations != null) {
                    return rDoWithAnnotations;
                }
                cls = cls.getSuperclass();
                i++;
            }
            return null;
        } catch (Throwable th) {
            AnnotationUtils.handleIntrospectionFailure(cls, th);
            return null;
        }
    }

    @Nullable
    private static <C, R> R processClassHierarchy(C c, Class<?> cls, AnnotationsProcessor<C, R> annotationsProcessor, boolean z, boolean z2) {
        return (R) processClassHierarchy(c, new int[]{0}, cls, annotationsProcessor, z, z2);
    }

    @Nullable
    private static <C, R> R processClassHierarchy(C c, int[] iArr, Class<?> cls, AnnotationsProcessor<C, R> annotationsProcessor, boolean z, boolean z2) {
        R r;
        try {
            R rDoWithAggregate = annotationsProcessor.doWithAggregate(c, iArr[0]);
            if (rDoWithAggregate != null) {
                return rDoWithAggregate;
            }
            if (hasPlainJavaAnnotationsOnly(cls)) {
                return null;
            }
            R rDoWithAnnotations = annotationsProcessor.doWithAnnotations(c, iArr[0], cls, getDeclaredAnnotations(cls, false));
            if (rDoWithAnnotations != null) {
                return rDoWithAnnotations;
            }
            iArr[0] = iArr[0] + 1;
            if (z) {
                for (Class<?> cls2 : cls.getInterfaces()) {
                    R r2 = (R) processClassHierarchy(c, iArr, cls2, annotationsProcessor, true, z2);
                    if (r2 != null) {
                        return r2;
                    }
                }
            }
            Class<? super Object> superclass = cls.getSuperclass();
            if (superclass != Object.class && superclass != null && (r = (R) processClassHierarchy(c, iArr, superclass, annotationsProcessor, z, z2)) != null) {
                return r;
            }
            if (z2) {
                try {
                    Class<?> enclosingClass = cls.getEnclosingClass();
                    if (enclosingClass != null) {
                        R r3 = (R) processClassHierarchy(c, iArr, enclosingClass, annotationsProcessor, z, true);
                        if (r3 != null) {
                            return r3;
                        }
                    }
                } catch (Throwable th) {
                    AnnotationUtils.handleIntrospectionFailure(cls, th);
                }
            }
            return null;
        } catch (Throwable th2) {
            AnnotationUtils.handleIntrospectionFailure(cls, th2);
            return null;
        }
    }

    @Nullable
    private static <C, R> R processMethod(C c, Method method, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> annotationsProcessor) {
        switch (searchStrategy) {
            case DIRECT:
            case INHERITED_ANNOTATIONS:
                return (R) processMethodInheritedAnnotations(c, method, annotationsProcessor);
            case SUPERCLASS:
                return (R) processMethodHierarchy(c, new int[]{0}, method.getDeclaringClass(), annotationsProcessor, method, false);
            case TYPE_HIERARCHY:
            case TYPE_HIERARCHY_AND_ENCLOSING_CLASSES:
                return (R) processMethodHierarchy(c, new int[]{0}, method.getDeclaringClass(), annotationsProcessor, method, true);
            default:
                throw new IllegalStateException("Unsupported search strategy " + searchStrategy);
        }
    }

    @Nullable
    private static <C, R> R processMethodInheritedAnnotations(C c, Method method, AnnotationsProcessor<C, R> annotationsProcessor) {
        try {
            R rDoWithAggregate = annotationsProcessor.doWithAggregate(c, 0);
            return rDoWithAggregate != null ? rDoWithAggregate : (R) processMethodAnnotations(c, 0, method, annotationsProcessor);
        } catch (Throwable th) {
            AnnotationUtils.handleIntrospectionFailure(method, th);
            return null;
        }
    }

    @Nullable
    private static <C, R> R processMethodHierarchy(C c, int[] iArr, Class<?> cls, AnnotationsProcessor<C, R> annotationsProcessor, Method method, boolean z) {
        try {
            R rDoWithAggregate = annotationsProcessor.doWithAggregate(c, iArr[0]);
            if (rDoWithAggregate != null) {
                return rDoWithAggregate;
            }
            if (hasPlainJavaAnnotationsOnly(cls)) {
                return null;
            }
            boolean z2 = false;
            if (cls == method.getDeclaringClass()) {
                R r = (R) processMethodAnnotations(c, iArr[0], method, annotationsProcessor);
                z2 = true;
                if (r != null) {
                    return r;
                }
            } else {
                for (Method method2 : getBaseTypeMethods(c, cls)) {
                    if (method2 != null && isOverride(method, method2)) {
                        R r2 = (R) processMethodAnnotations(c, iArr[0], method2, annotationsProcessor);
                        z2 = true;
                        if (r2 != null) {
                            return r2;
                        }
                    }
                }
            }
            if (Modifier.isPrivate(method.getModifiers())) {
                return null;
            }
            if (z2) {
                iArr[0] = iArr[0] + 1;
            }
            if (z) {
                for (Class<?> cls2 : cls.getInterfaces()) {
                    R r3 = (R) processMethodHierarchy(c, iArr, cls2, annotationsProcessor, method, true);
                    if (r3 != null) {
                        return r3;
                    }
                }
            }
            Class<? super Object> superclass = cls.getSuperclass();
            if (superclass != Object.class && superclass != null) {
                R r4 = (R) processMethodHierarchy(c, iArr, superclass, annotationsProcessor, method, z);
                if (r4 != null) {
                    return r4;
                }
                return null;
            }
            return null;
        } catch (Throwable th) {
            AnnotationUtils.handleIntrospectionFailure(method, th);
            return null;
        }
    }

    private static <C> Method[] getBaseTypeMethods(C context, Class<?> baseType) {
        if (baseType == Object.class || hasPlainJavaAnnotationsOnly(baseType)) {
            return NO_METHODS;
        }
        Method[] methods = baseTypeMethodsCache.get(baseType);
        if (methods == null) {
            boolean isInterface = baseType.isInterface();
            methods = isInterface ? baseType.getMethods() : ReflectionUtils.getDeclaredMethods(baseType);
            int cleared = 0;
            for (int i = 0; i < methods.length; i++) {
                if ((!isInterface && Modifier.isPrivate(methods[i].getModifiers())) || hasPlainJavaAnnotationsOnly(methods[i]) || getDeclaredAnnotations(methods[i], false).length == 0) {
                    methods[i] = null;
                    cleared++;
                }
            }
            if (cleared == methods.length) {
                methods = NO_METHODS;
            }
            baseTypeMethodsCache.put(baseType, methods);
        }
        return methods;
    }

    private static boolean isOverride(Method rootMethod, Method candidateMethod) {
        return !Modifier.isPrivate(candidateMethod.getModifiers()) && candidateMethod.getName().equals(rootMethod.getName()) && hasSameParameterTypes(rootMethod, candidateMethod);
    }

    private static boolean hasSameParameterTypes(Method rootMethod, Method candidateMethod) {
        if (candidateMethod.getParameterCount() != rootMethod.getParameterCount()) {
            return false;
        }
        Class<?>[] rootParameterTypes = rootMethod.getParameterTypes();
        Class<?>[] candidateParameterTypes = candidateMethod.getParameterTypes();
        if (Arrays.equals(candidateParameterTypes, rootParameterTypes)) {
            return true;
        }
        return hasSameGenericTypeParameters(rootMethod, candidateMethod, rootParameterTypes);
    }

    private static boolean hasSameGenericTypeParameters(Method rootMethod, Method candidateMethod, Class<?>[] rootParameterTypes) {
        Class<?> sourceDeclaringClass = rootMethod.getDeclaringClass();
        Class<?> candidateDeclaringClass = candidateMethod.getDeclaringClass();
        if (!candidateDeclaringClass.isAssignableFrom(sourceDeclaringClass)) {
            return false;
        }
        for (int i = 0; i < rootParameterTypes.length; i++) {
            Class<?> resolvedParameterType = ResolvableType.forMethodParameter(candidateMethod, i, sourceDeclaringClass).resolve();
            if (rootParameterTypes[i] != resolvedParameterType) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static <C, R> R processMethodAnnotations(C context, int aggregateIndex, Method source, AnnotationsProcessor<C, R> processor) throws IllegalArgumentException {
        Annotation[] annotations = getDeclaredAnnotations(source, false);
        R result = processor.doWithAnnotations(context, aggregateIndex, source, annotations);
        if (result != null) {
            return result;
        }
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(source);
        if (bridgedMethod != source) {
            Annotation[] bridgedAnnotations = getDeclaredAnnotations(bridgedMethod, true);
            for (int i = 0; i < bridgedAnnotations.length; i++) {
                if (ObjectUtils.containsElement(annotations, bridgedAnnotations[i])) {
                    bridgedAnnotations[i] = null;
                }
            }
            return processor.doWithAnnotations(context, aggregateIndex, source, bridgedAnnotations);
        }
        return null;
    }

    @Nullable
    private static <C, R> R processElement(C context, AnnotatedElement source, AnnotationsProcessor<C, R> processor) {
        try {
            R result = processor.doWithAggregate(context, 0);
            return result != null ? result : processor.doWithAnnotations(context, 0, source, getDeclaredAnnotations(source, false));
        } catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(source, ex);
            return null;
        }
    }

    @Nullable
    static <A extends Annotation> A getDeclaredAnnotation(AnnotatedElement annotatedElement, Class<A> cls) {
        for (Annotation annotation : getDeclaredAnnotations(annotatedElement, false)) {
            A a = (A) annotation;
            if (a != null && cls == a.annotationType()) {
                return a;
            }
        }
        return null;
    }

    static Annotation[] getDeclaredAnnotations(AnnotatedElement source, boolean defensive) {
        boolean cached = false;
        Annotation[] annotations = declaredAnnotationCache.get(source);
        if (annotations != null) {
            cached = true;
        } else {
            annotations = source.getDeclaredAnnotations();
            if (annotations.length != 0) {
                boolean allIgnored = true;
                for (int i = 0; i < annotations.length; i++) {
                    Annotation annotation = annotations[i];
                    if (isIgnorable(annotation.annotationType()) || !AttributeMethods.forAnnotationType(annotation.annotationType()).isValid(annotation)) {
                        annotations[i] = null;
                    } else {
                        allIgnored = false;
                    }
                }
                annotations = allIgnored ? NO_ANNOTATIONS : annotations;
                if ((source instanceof Class) || (source instanceof Member)) {
                    declaredAnnotationCache.put(source, annotations);
                    cached = true;
                }
            }
        }
        if (!defensive || annotations.length == 0 || !cached) {
            return annotations;
        }
        return (Annotation[]) annotations.clone();
    }

    private static boolean isIgnorable(Class<?> annotationType) {
        return AnnotationFilter.PLAIN.matches(annotationType);
    }

    static boolean isKnownEmpty(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy) {
        if (hasPlainJavaAnnotationsOnly(source)) {
            return true;
        }
        if (searchStrategy == MergedAnnotations.SearchStrategy.DIRECT || isWithoutHierarchy(source, searchStrategy)) {
            return !((source instanceof Method) && ((Method) source).isBridge()) && getDeclaredAnnotations(source, false).length == 0;
        }
        return false;
    }

    static boolean hasPlainJavaAnnotationsOnly(@Nullable Object annotatedElement) {
        if (annotatedElement instanceof Class) {
            return hasPlainJavaAnnotationsOnly((Class<?>) annotatedElement);
        }
        if (annotatedElement instanceof Member) {
            return hasPlainJavaAnnotationsOnly(((Member) annotatedElement).getDeclaringClass());
        }
        return false;
    }

    static boolean hasPlainJavaAnnotationsOnly(Class<?> type) {
        return type.getName().startsWith("java.") || type == Ordered.class;
    }

    private static boolean isWithoutHierarchy(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy) {
        if (source == Object.class) {
            return true;
        }
        if (source instanceof Class) {
            Class<?> sourceClass = (Class) source;
            boolean noSuperTypes = sourceClass.getSuperclass() == Object.class && sourceClass.getInterfaces().length == 0;
            return searchStrategy == MergedAnnotations.SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES ? noSuperTypes && sourceClass.getEnclosingClass() == null : noSuperTypes;
        }
        if (source instanceof Method) {
            Method sourceMethod = (Method) source;
            return Modifier.isPrivate(sourceMethod.getModifiers()) || isWithoutHierarchy(sourceMethod.getDeclaringClass(), searchStrategy);
        }
        return true;
    }

    static void clearCache() {
        declaredAnnotationCache.clear();
        baseTypeMethodsCache.clear();
    }
}
