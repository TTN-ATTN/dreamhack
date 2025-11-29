package org.springframework.boot.convert;

import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/CharSequenceToObjectConverter.class */
class CharSequenceToObjectConverter implements ConditionalGenericConverter {
    private static final TypeDescriptor STRING = TypeDescriptor.valueOf(String.class);
    private static final TypeDescriptor BYTE_ARRAY = TypeDescriptor.valueOf(byte[].class);
    private static final Set<GenericConverter.ConvertiblePair> TYPES = Collections.singleton(new GenericConverter.ConvertiblePair(CharSequence.class, Object.class));
    private final ThreadLocal<Boolean> disable = new ThreadLocal<>();
    private final ConversionService conversionService;

    CharSequenceToObjectConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return TYPES;
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.getType() == String.class || this.disable.get() == Boolean.TRUE) {
            return false;
        }
        this.disable.set(Boolean.TRUE);
        try {
            boolean canDirectlyConvertCharSequence = this.conversionService.canConvert(sourceType, targetType);
            if (canDirectlyConvertCharSequence && !isStringConversionBetter(sourceType, targetType)) {
                return false;
            }
            boolean zCanConvert = this.conversionService.canConvert(STRING, targetType);
            this.disable.set(null);
            return zCanConvert;
        } finally {
            this.disable.set(null);
        }
    }

    private boolean isStringConversionBetter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (this.conversionService instanceof ApplicationConversionService) {
            ApplicationConversionService applicationConversionService = (ApplicationConversionService) this.conversionService;
            if (applicationConversionService.isConvertViaObjectSourceType(sourceType, targetType)) {
                return true;
            }
        }
        if ((targetType.isArray() || targetType.isCollection()) && !targetType.equals(BYTE_ARRAY)) {
            return true;
        }
        return false;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.convert(source.toString(), STRING, targetType);
    }
}
