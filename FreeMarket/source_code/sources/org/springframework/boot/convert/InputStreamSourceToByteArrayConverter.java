package org.springframework.boot.convert;

import java.io.IOException;
import org.springframework.boot.origin.Origin;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/InputStreamSourceToByteArrayConverter.class */
class InputStreamSourceToByteArrayConverter implements Converter<InputStreamSource, byte[]> {
    InputStreamSourceToByteArrayConverter() {
    }

    @Override // org.springframework.core.convert.converter.Converter
    public byte[] convert(InputStreamSource source) {
        try {
            return FileCopyUtils.copyToByteArray(source.getInputStream());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read from " + getName(source), ex);
        }
    }

    private String getName(InputStreamSource source) {
        Origin origin = Origin.from(source);
        if (origin != null) {
            return origin.toString();
        }
        if (source instanceof Resource) {
            return ((Resource) source).getDescription();
        }
        return "input stream source";
    }
}
