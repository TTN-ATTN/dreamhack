package org.springframework.core.annotation;

import java.lang.annotation.Annotation;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/MergedAnnotationSelector.class */
public interface MergedAnnotationSelector<A extends Annotation> {
    MergedAnnotation<A> select(MergedAnnotation<A> existing, MergedAnnotation<A> candidate);

    default boolean isBestCandidate(MergedAnnotation<A> annotation) {
        return false;
    }
}
