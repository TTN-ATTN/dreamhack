package org.springframework.core.type.classreading;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.asm.ClassReader;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/classreading/SimpleMetadataReader.class */
final class SimpleMetadataReader implements MetadataReader {
    private static final int PARSING_OPTIONS = 7;
    private final Resource resource;
    private final AnnotationMetadata annotationMetadata;

    SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
        SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(classLoader);
        getClassReader(resource).accept(visitor, 7);
        this.resource = resource;
        this.annotationMetadata = visitor.getMetadata();
    }

    private static ClassReader getClassReader(Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        Throwable th = null;
        try {
            try {
                ClassReader classReader = new ClassReader(is);
                if (is != null) {
                    if (0 != 0) {
                        try {
                            is.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        is.close();
                    }
                }
                return classReader;
            } catch (IllegalArgumentException ex) {
                throw new NestedIOException("ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: " + resource, ex);
            }
        } catch (Throwable th3) {
            if (is != null) {
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    is.close();
                }
            }
            throw th3;
        }
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public Resource getResource() {
        return this.resource;
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public ClassMetadata getClassMetadata() {
        return this.annotationMetadata;
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }
}
