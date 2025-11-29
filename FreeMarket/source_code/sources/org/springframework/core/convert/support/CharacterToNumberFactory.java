package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/CharacterToNumberFactory.class */
final class CharacterToNumberFactory implements ConverterFactory<Character, Number> {
    CharacterToNumberFactory() {
    }

    @Override // org.springframework.core.convert.converter.ConverterFactory
    public <T extends Number> Converter<Character, T> getConverter(Class<T> targetType) {
        return new CharacterToNumber(targetType);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/CharacterToNumberFactory$CharacterToNumber.class */
    private static final class CharacterToNumber<T extends Number> implements Converter<Character, T> {
        private final Class<T> targetType;

        public CharacterToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override // org.springframework.core.convert.converter.Converter
        public T convert(Character ch2) {
            return (T) NumberUtils.convertNumberToTargetClass(Short.valueOf((short) ch2.charValue()), this.targetType);
        }
    }
}
