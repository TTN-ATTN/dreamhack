package org.springframework.core.convert.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.springframework.core.convert.converter.Converter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/StringToPropertiesConverter.class */
final class StringToPropertiesConverter implements Converter<String, Properties> {
    StringToPropertiesConverter() {
    }

    @Override // org.springframework.core.convert.converter.Converter
    public Properties convert(String source) throws IOException {
        try {
            Properties props = new Properties();
            props.load(new ByteArrayInputStream(source.getBytes(StandardCharsets.ISO_8859_1)));
            return props;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse [" + source + "] into Properties", ex);
        }
    }
}
