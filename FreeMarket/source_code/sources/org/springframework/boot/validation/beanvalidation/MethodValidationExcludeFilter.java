package org.springframework.boot.validation.beanvalidation;

import java.lang.annotation.Annotation;
import org.springframework.core.annotation.MergedAnnotations;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/validation/beanvalidation/MethodValidationExcludeFilter.class */
public interface MethodValidationExcludeFilter {
    boolean isExcluded(Class<?> type);

    static MethodValidationExcludeFilter byAnnotation(Class<? extends Annotation> annotationType) {
        return byAnnotation(annotationType, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS);
    }

    static MethodValidationExcludeFilter byAnnotation(Class<? extends Annotation> annotationType, MergedAnnotations.SearchStrategy searchStrategy) {
        return type -> {
            return MergedAnnotations.from(type, searchStrategy).isPresent(annotationType);
        };
    }
}
