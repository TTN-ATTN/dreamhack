package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/ObjectToStringConverter.class */
final class ObjectToStringConverter implements Converter<Object, String> {
    ObjectToStringConverter() {
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.convert.converter.Converter
    public String convert(Object source) {
        return source.toString();
    }
}
