package org.apache.tomcat;

import java.lang.instrument.ClassFileTransformer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/InstrumentableClassLoader.class */
public interface InstrumentableClassLoader {
    void addTransformer(ClassFileTransformer classFileTransformer);

    void removeTransformer(ClassFileTransformer classFileTransformer);

    ClassLoader copyWithoutTransformers();
}
