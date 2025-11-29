package org.springframework.boot.convert;

import java.time.Period;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/NumberToPeriodConverter.class */
final class NumberToPeriodConverter implements GenericConverter {
    private final StringToPeriodConverter delegate = new StringToPeriodConverter();

    NumberToPeriodConverter() {
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Number.class, Period.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.delegate.convert(source != null ? source.toString() : null, TypeDescriptor.valueOf(String.class), targetType);
    }
}
