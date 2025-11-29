package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.introspect.Annotated;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/introspect/AnnotatedAndMetadata.class */
public class AnnotatedAndMetadata<A extends Annotated, M> {
    public final A annotated;
    public final M metadata;

    public AnnotatedAndMetadata(A ann, M md) {
        this.annotated = ann;
        this.metadata = md;
    }

    public static <A extends Annotated, M> AnnotatedAndMetadata<A, M> of(A ann, M md) {
        return new AnnotatedAndMetadata<>(ann, md);
    }
}
