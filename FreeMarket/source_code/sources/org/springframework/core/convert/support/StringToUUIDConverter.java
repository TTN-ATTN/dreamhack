package org.springframework.core.convert.support;

import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/StringToUUIDConverter.class */
final class StringToUUIDConverter implements Converter<String, UUID> {
    StringToUUIDConverter() {
    }

    @Override // org.springframework.core.convert.converter.Converter
    @Nullable
    public UUID convert(String source) {
        if (StringUtils.hasText(source)) {
            return UUID.fromString(source.trim());
        }
        return null;
    }
}
