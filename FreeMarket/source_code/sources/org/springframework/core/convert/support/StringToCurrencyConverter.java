package org.springframework.core.convert.support;

import java.util.Currency;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/StringToCurrencyConverter.class */
class StringToCurrencyConverter implements Converter<String, Currency> {
    StringToCurrencyConverter() {
    }

    @Override // org.springframework.core.convert.converter.Converter
    public Currency convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Currency.getInstance(source);
    }
}
