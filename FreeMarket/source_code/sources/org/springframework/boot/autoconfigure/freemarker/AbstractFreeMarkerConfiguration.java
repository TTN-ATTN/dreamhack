package org.springframework.boot.autoconfigure.freemarker;

import java.util.Properties;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/freemarker/AbstractFreeMarkerConfiguration.class */
abstract class AbstractFreeMarkerConfiguration {
    private final FreeMarkerProperties properties;

    protected AbstractFreeMarkerConfiguration(FreeMarkerProperties properties) {
        this.properties = properties;
    }

    protected final FreeMarkerProperties getProperties() {
        return this.properties;
    }

    protected void applyProperties(FreeMarkerConfigurationFactory factory) {
        factory.setTemplateLoaderPaths(this.properties.getTemplateLoaderPath());
        factory.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
        factory.setDefaultEncoding(this.properties.getCharsetName());
        Properties settings = new Properties();
        settings.put("recognize_standard_file_extensions", "true");
        settings.putAll(this.properties.getSettings());
        factory.setFreemarkerSettings(settings);
    }
}
