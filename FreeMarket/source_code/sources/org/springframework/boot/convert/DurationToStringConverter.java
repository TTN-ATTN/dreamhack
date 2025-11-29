package org.springframework.boot.convert;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/DurationToStringConverter.class */
final class DurationToStringConverter implements GenericConverter {
    DurationToStringConverter() {
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Duration.class, String.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        return convert((Duration) source, getDurationStyle(sourceType), getDurationUnit(sourceType));
    }

    private ChronoUnit getDurationUnit(TypeDescriptor sourceType) {
        DurationUnit annotation = (DurationUnit) sourceType.getAnnotation(DurationUnit.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private DurationStyle getDurationStyle(TypeDescriptor sourceType) {
        DurationFormat annotation = (DurationFormat) sourceType.getAnnotation(DurationFormat.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private String convert(Duration source, DurationStyle style, ChronoUnit unit) {
        return (style != null ? style : DurationStyle.ISO8601).print(source, unit);
    }
}
