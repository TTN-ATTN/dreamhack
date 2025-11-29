package com.fasterxml.jackson.annotation;

import java.lang.annotation.Annotation;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-annotations-2.13.5.jar:com/fasterxml/jackson/annotation/JacksonAnnotationValue.class */
public interface JacksonAnnotationValue<A extends Annotation> {
    Class<A> valueFor();
}
