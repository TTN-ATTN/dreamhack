package org.springframework.boot.convert;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/LenientObjectToEnumConverterFactory.class */
abstract class LenientObjectToEnumConverterFactory<T> implements ConverterFactory<T, Enum<?>> {
    private static Map<String, List<String>> ALIASES;

    LenientObjectToEnumConverterFactory() {
    }

    static {
        MultiValueMap<String, String> aliases = new LinkedMultiValueMap<>();
        aliases.add("true", CustomBooleanEditor.VALUE_ON);
        aliases.add("false", CustomBooleanEditor.VALUE_OFF);
        ALIASES = Collections.unmodifiableMap(aliases);
    }

    @Override // org.springframework.core.convert.converter.ConverterFactory
    public <E extends Enum<?>> Converter<T, E> getConverter(Class<E> targetType) {
        Class<E> cls;
        Class<E> superclass = targetType;
        while (true) {
            cls = superclass;
            if (cls == null || cls.isEnum()) {
                break;
            }
            superclass = cls.getSuperclass();
        }
        Assert.notNull(cls, (Supplier<String>) () -> {
            return "The target type " + targetType.getName() + " does not refer to an enum";
        });
        return new LenientToEnumConverter(cls);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/LenientObjectToEnumConverterFactory$LenientToEnumConverter.class */
    private class LenientToEnumConverter<E extends Enum> implements Converter<T, E> {
        private final Class<E> enumType;

        @Override // org.springframework.core.convert.converter.Converter
        public /* bridge */ /* synthetic */ Object convert(Object source) {
            return convert((LenientToEnumConverter<E>) source);
        }

        LenientToEnumConverter(Class<E> enumType) {
            this.enumType = enumType;
        }

        @Override // org.springframework.core.convert.converter.Converter
        public E convert(T t) {
            String strTrim = t.toString().trim();
            if (strTrim.isEmpty()) {
                return null;
            }
            try {
                return (E) Enum.valueOf(this.enumType, strTrim);
            } catch (Exception e) {
                return (E) findEnum(strTrim);
            }
        }

        private E findEnum(String value) {
            String name = getCanonicalName(value);
            List<String> aliases = (List) LenientObjectToEnumConverterFactory.ALIASES.getOrDefault(name, Collections.emptyList());
            for (E e : EnumSet.allOf(this.enumType)) {
                String candidateName = getCanonicalName(e.name());
                if (name.equals(candidateName) || aliases.contains(candidateName)) {
                    return e;
                }
            }
            throw new IllegalArgumentException("No enum constant " + this.enumType.getCanonicalName() + "." + value);
        }

        private String getCanonicalName(String name) {
            StringBuilder canonicalName = new StringBuilder(name.length());
            name.chars().filter(Character::isLetterOrDigit).map(Character::toLowerCase).forEach(c -> {
                canonicalName.append((char) c);
            });
            return canonicalName.toString();
        }
    }
}
