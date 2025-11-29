package org.springframework.boot.logging;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/LoggingSystemProperties.class */
public class LoggingSystemProperties {
    public static final String PID_KEY = "PID";
    public static final String EXCEPTION_CONVERSION_WORD = "LOG_EXCEPTION_CONVERSION_WORD";
    public static final String LOG_FILE = "LOG_FILE";
    public static final String LOG_PATH = "LOG_PATH";
    public static final String CONSOLE_LOG_PATTERN = "CONSOLE_LOG_PATTERN";
    public static final String CONSOLE_LOG_CHARSET = "CONSOLE_LOG_CHARSET";
    public static final String FILE_LOG_PATTERN = "FILE_LOG_PATTERN";
    public static final String FILE_LOG_CHARSET = "FILE_LOG_CHARSET";
    public static final String LOG_LEVEL_PATTERN = "LOG_LEVEL_PATTERN";
    public static final String LOG_DATEFORMAT_PATTERN = "LOG_DATEFORMAT_PATTERN";
    private static final BiConsumer<String, String> systemPropertySetter = (name, value) -> {
        if (System.getProperty(name) == null && value != null) {
            System.setProperty(name, value);
        }
    };
    private final Environment environment;
    private final BiConsumer<String, String> setter;

    public LoggingSystemProperties(Environment environment) {
        this(environment, systemPropertySetter);
    }

    public LoggingSystemProperties(Environment environment, BiConsumer<String, String> setter) {
        Assert.notNull(environment, "Environment must not be null");
        Assert.notNull(setter, "Setter must not be null");
        this.environment = environment;
        this.setter = setter;
    }

    protected Charset getDefaultCharset() {
        return StandardCharsets.UTF_8;
    }

    public final void apply() {
        apply(null);
    }

    public final void apply(LogFile logFile) {
        PropertyResolver resolver = getPropertyResolver();
        apply(logFile, resolver);
    }

    protected void apply(LogFile logFile, PropertyResolver resolver) {
        setSystemProperty(resolver, EXCEPTION_CONVERSION_WORD, "logging.exception-conversion-word");
        setSystemProperty(PID_KEY, new ApplicationPid().toString());
        setSystemProperty(resolver, CONSOLE_LOG_PATTERN, "logging.pattern.console");
        setSystemProperty(resolver, CONSOLE_LOG_CHARSET, "logging.charset.console", getDefaultCharset().name());
        setSystemProperty(resolver, LOG_DATEFORMAT_PATTERN, "logging.pattern.dateformat");
        setSystemProperty(resolver, FILE_LOG_PATTERN, "logging.pattern.file");
        setSystemProperty(resolver, FILE_LOG_CHARSET, "logging.charset.file", getDefaultCharset().name());
        setSystemProperty(resolver, LOG_LEVEL_PATTERN, "logging.pattern.level");
        if (logFile != null) {
            logFile.applyToSystemProperties();
        }
    }

    private PropertyResolver getPropertyResolver() {
        if (this.environment instanceof ConfigurableEnvironment) {
            PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(((ConfigurableEnvironment) this.environment).getPropertySources());
            resolver.setConversionService(((ConfigurableEnvironment) this.environment).getConversionService());
            resolver.setIgnoreUnresolvableNestedPlaceholders(true);
            return resolver;
        }
        return this.environment;
    }

    protected final void setSystemProperty(PropertyResolver resolver, String systemPropertyName, String propertyName) {
        setSystemProperty(resolver, systemPropertyName, propertyName, null);
    }

    protected final void setSystemProperty(PropertyResolver resolver, String systemPropertyName, String propertyName, String defaultValue) {
        String value = resolver.getProperty(propertyName);
        setSystemProperty(systemPropertyName, value != null ? value : defaultValue);
    }

    protected final void setSystemProperty(String name, String value) {
        this.setter.accept(name, value);
    }
}
