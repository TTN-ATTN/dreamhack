package org.springframework.core.convert.support;

import java.nio.charset.Charset;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/StringToCharsetConverter.class */
class StringToCharsetConverter implements Converter<String, Charset> {
    StringToCharsetConverter() {
    }

    @Override // org.springframework.core.convert.converter.Converter
    public Charset convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Charset.forName(source);
    }
}
