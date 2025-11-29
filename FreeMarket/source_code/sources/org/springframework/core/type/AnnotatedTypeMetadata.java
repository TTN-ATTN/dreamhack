package org.springframework.core.type;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationCollectors;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotationSelectors;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/AnnotatedTypeMetadata.class */
public interface AnnotatedTypeMetadata {
    MergedAnnotations getAnnotations();

    default boolean isAnnotated(String annotationName) {
        return getAnnotations().isPresent(annotationName);
    }

    @Nullable
    default Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Nullable
    default Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        MergedAnnotation<Annotation> annotation = getAnnotations().get(annotationName, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared());
        if (!annotation.isPresent()) {
            return null;
        }
        return annotation.asAnnotationAttributes(MergedAnnotation.Adapt.values(classValuesAsString, true));
    }

    @Nullable
    default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Nullable
    default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, true);
        return (MultiValueMap) getAnnotations().stream(annotationName).filter(MergedAnnotationPredicates.unique((v0) -> {
            return v0.getMetaTypes();
        })).map((v0) -> {
            return v0.withNonMergedAttributes();
        }).collect(MergedAnnotationCollectors.toMultiValueMap(map -> {
            if (map.isEmpty()) {
                return null;
            }
            return map;
        }, adaptations));
    }
}
