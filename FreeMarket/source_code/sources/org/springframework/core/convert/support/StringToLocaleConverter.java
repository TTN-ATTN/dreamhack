package org.springframework.core.convert.support;

import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/StringToLocaleConverter.class */
final class StringToLocaleConverter implements Converter<String, Locale> {
    StringToLocaleConverter() {
    }

    @Override // org.springframework.core.convert.converter.Converter
    @Nullable
    public Locale convert(String source) {
        return StringUtils.parseLocale(source);
    }
}
