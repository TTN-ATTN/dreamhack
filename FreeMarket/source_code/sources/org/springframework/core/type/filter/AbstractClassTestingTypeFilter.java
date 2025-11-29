package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/filter/AbstractClassTestingTypeFilter.class */
public abstract class AbstractClassTestingTypeFilter implements TypeFilter {
    protected abstract boolean match(ClassMetadata metadata);

    @Override // org.springframework.core.type.filter.TypeFilter
    public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return match(metadataReader.getClassMetadata());
    }
}
