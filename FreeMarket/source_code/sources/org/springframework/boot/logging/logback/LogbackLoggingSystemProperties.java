package org.springframework.boot.logging.logback;

import ch.qos.logback.core.util.FileSize;
import java.nio.charset.Charset;
import java.util.function.BiConsumer;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingSystemProperties;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.unit.DataSize;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/logback/LogbackLoggingSystemProperties.class */
public class LogbackLoggingSystemProperties extends LoggingSystemProperties {
    public static final String ROLLINGPOLICY_FILE_NAME_PATTERN = "LOGBACK_ROLLINGPOLICY_FILE_NAME_PATTERN";
    public static final String ROLLINGPOLICY_CLEAN_HISTORY_ON_START = "LOGBACK_ROLLINGPOLICY_CLEAN_HISTORY_ON_START";
    public static final String ROLLINGPOLICY_MAX_FILE_SIZE = "LOGBACK_ROLLINGPOLICY_MAX_FILE_SIZE";
    public static final String ROLLINGPOLICY_TOTAL_SIZE_CAP = "LOGBACK_ROLLINGPOLICY_TOTAL_SIZE_CAP";
    public static final String ROLLINGPOLICY_MAX_HISTORY = "LOGBACK_ROLLINGPOLICY_MAX_HISTORY";

    public LogbackLoggingSystemProperties(Environment environment) {
        super(environment);
    }

    public LogbackLoggingSystemProperties(Environment environment, BiConsumer<String, String> setter) {
        super(environment, setter);
    }

    @Override // org.springframework.boot.logging.LoggingSystemProperties
    protected Charset getDefaultCharset() {
        return Charset.defaultCharset();
    }

    @Override // org.springframework.boot.logging.LoggingSystemProperties
    protected void apply(LogFile logFile, PropertyResolver resolver) {
        super.apply(logFile, resolver);
        applyRollingPolicy(resolver, ROLLINGPOLICY_FILE_NAME_PATTERN, "logging.logback.rollingpolicy.file-name-pattern", "logging.pattern.rolling-file-name");
        applyRollingPolicy(resolver, ROLLINGPOLICY_CLEAN_HISTORY_ON_START, "logging.logback.rollingpolicy.clean-history-on-start", "logging.file.clean-history-on-start");
        applyRollingPolicy(resolver, ROLLINGPOLICY_MAX_FILE_SIZE, "logging.logback.rollingpolicy.max-file-size", "logging.file.max-size", DataSize.class);
        applyRollingPolicy(resolver, ROLLINGPOLICY_TOTAL_SIZE_CAP, "logging.logback.rollingpolicy.total-size-cap", "logging.file.total-size-cap", DataSize.class);
        applyRollingPolicy(resolver, ROLLINGPOLICY_MAX_HISTORY, "logging.logback.rollingpolicy.max-history", "logging.file.max-history");
    }

    private void applyRollingPolicy(PropertyResolver resolver, String systemPropertyName, String propertyName, String deprecatedPropertyName) {
        applyRollingPolicy(resolver, systemPropertyName, propertyName, deprecatedPropertyName, String.class);
    }

    private <T> void applyRollingPolicy(PropertyResolver resolver, String systemPropertyName, String propertyName, String deprecatedPropertyName, Class<T> type) {
        Object property = getProperty(resolver, propertyName, type);
        if (property == null) {
            property = getProperty(resolver, deprecatedPropertyName, type);
        }
        if (property != null) {
            String stringValue = String.valueOf(property instanceof DataSize ? Long.valueOf(((DataSize) property).toBytes()) : property);
            setSystemProperty(systemPropertyName, stringValue);
        }
    }

    private <T> T getProperty(PropertyResolver propertyResolver, String str, Class<T> cls) {
        try {
            return (T) propertyResolver.getProperty(str, cls);
        } catch (ConversionFailedException | ConverterNotFoundException e) {
            if (cls != DataSize.class) {
                throw e;
            }
            return (T) DataSize.ofBytes(FileSize.valueOf(propertyResolver.getProperty(str)).getSize());
        }
    }
}
