package org.springframework.boot.logging.log4j2;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Order(0)
@Plugin(name = "SpringBootConfigurationFactory", category = "ConfigurationFactory")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/log4j2/SpringBootConfigurationFactory.class */
public class SpringBootConfigurationFactory extends ConfigurationFactory {
    private static final String[] TYPES = {".springboot"};

    protected String[] getSupportedTypes() {
        return TYPES;
    }

    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        if (source == null || source == ConfigurationSource.NULL_SOURCE) {
            return null;
        }
        return new DefaultConfiguration();
    }
}
