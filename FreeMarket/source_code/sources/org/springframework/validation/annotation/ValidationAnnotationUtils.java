package org.springframework.validation.annotation;

import java.lang.annotation.Annotation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/validation/annotation/ValidationAnnotationUtils.class */
public abstract class ValidationAnnotationUtils {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    @Nullable
    public static Object[] determineValidationHints(Annotation ann) {
        Class<? extends Annotation> annotationType = ann.annotationType();
        String annotationName = annotationType.getName();
        if ("javax.validation.Valid".equals(annotationName)) {
            return EMPTY_OBJECT_ARRAY;
        }
        Validated validatedAnn = (Validated) AnnotationUtils.getAnnotation(ann, Validated.class);
        if (validatedAnn != null) {
            Object hints = validatedAnn.value();
            return convertValidationHints(hints);
        }
        if (annotationType.getSimpleName().startsWith("Valid")) {
            Object hints2 = AnnotationUtils.getValue(ann);
            return convertValidationHints(hints2);
        }
        return null;
    }

    private static Object[] convertValidationHints(@Nullable Object hints) {
        if (hints == null) {
            return EMPTY_OBJECT_ARRAY;
        }
        return hints instanceof Object[] ? (Object[]) hints : new Object[]{hints};
    }
}
