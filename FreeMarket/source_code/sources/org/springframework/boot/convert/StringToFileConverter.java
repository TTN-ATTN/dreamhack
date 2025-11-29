package org.springframework.boot.convert;

import java.io.File;
import java.io.IOException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/StringToFileConverter.class */
class StringToFileConverter implements Converter<String, File> {
    private static final ResourceLoader resourceLoader = new DefaultResourceLoader(null);

    StringToFileConverter() {
    }

    @Override // org.springframework.core.convert.converter.Converter
    public File convert(String source) {
        if (ResourceUtils.isUrl(source)) {
            return getFile(resourceLoader.getResource(source));
        }
        File file = new File(source);
        if (file.exists()) {
            return file;
        }
        Resource resource = resourceLoader.getResource(source);
        if (resource.exists()) {
            return getFile(resource);
        }
        return file;
    }

    private File getFile(Resource resource) {
        try {
            return resource.getFile();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not retrieve file for " + resource + ": " + ex.getMessage());
        }
    }
}
