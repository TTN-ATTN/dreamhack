package org.springframework.boot.logging.log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.util.NameUtil;
import org.apache.logging.log4j.message.Message;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.logging.AbstractLoggingSystem;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.boot.logging.Slf4JLoggingSystem;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/log4j2/Log4J2LoggingSystem.class */
public class Log4J2LoggingSystem extends Slf4JLoggingSystem {
    private static final String FILE_PROTOCOL = "file";
    private static final AbstractLoggingSystem.LogLevels<Level> LEVELS = new AbstractLoggingSystem.LogLevels<>();
    private static final Filter FILTER;

    static {
        LEVELS.map(LogLevel.TRACE, Level.TRACE);
        LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.map(LogLevel.INFO, Level.INFO);
        LEVELS.map(LogLevel.WARN, Level.WARN);
        LEVELS.map(LogLevel.ERROR, Level.ERROR);
        LEVELS.map(LogLevel.FATAL, Level.FATAL);
        LEVELS.map(LogLevel.OFF, Level.OFF);
        FILTER = new AbstractFilter() { // from class: org.springframework.boot.logging.log4j2.Log4J2LoggingSystem.1
            public Filter.Result filter(LogEvent event) {
                return Filter.Result.DENY;
            }

            public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
                return Filter.Result.DENY;
            }

            public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
                return Filter.Result.DENY;
            }

            public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
                return Filter.Result.DENY;
            }
        };
    }

    public Log4J2LoggingSystem(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected String[] getStandardConfigLocations() {
        return getCurrentlySupportedConfigLocations();
    }

    private String[] getCurrentlySupportedConfigLocations() {
        List<String> supportedConfigLocations = new ArrayList<>();
        addTestFiles(supportedConfigLocations);
        supportedConfigLocations.add("log4j2.properties");
        if (isClassAvailable("com.fasterxml.jackson.dataformat.yaml.YAMLParser")) {
            Collections.addAll(supportedConfigLocations, "log4j2.yaml", "log4j2.yml");
        }
        if (isClassAvailable("com.fasterxml.jackson.databind.ObjectMapper")) {
            Collections.addAll(supportedConfigLocations, "log4j2.json", "log4j2.jsn");
        }
        supportedConfigLocations.add("log4j2.xml");
        return StringUtils.toStringArray(supportedConfigLocations);
    }

    private void addTestFiles(List<String> supportedConfigLocations) {
        supportedConfigLocations.add("log4j2-test.properties");
        if (isClassAvailable("com.fasterxml.jackson.dataformat.yaml.YAMLParser")) {
            Collections.addAll(supportedConfigLocations, "log4j2-test.yaml", "log4j2-test.yml");
        }
        if (isClassAvailable("com.fasterxml.jackson.databind.ObjectMapper")) {
            Collections.addAll(supportedConfigLocations, "log4j2-test.json", "log4j2-test.jsn");
        }
        supportedConfigLocations.add("log4j2-test.xml");
    }

    protected boolean isClassAvailable(String className) {
        return ClassUtils.isPresent(className, getClassLoader());
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void beforeInitialize() {
        LoggerContext loggerContext = getLoggerContext();
        if (isAlreadyInitialized(loggerContext)) {
            return;
        }
        super.beforeInitialize();
        loggerContext.getConfiguration().addFilter(FILTER);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
        LoggerContext loggerContext = getLoggerContext();
        if (isAlreadyInitialized(loggerContext)) {
            return;
        }
        loggerContext.getConfiguration().removeFilter(FILTER);
        super.initialize(initializationContext, configLocation, logFile);
        markAsInitialized(loggerContext);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void loadDefaults(LoggingInitializationContext initializationContext, LogFile logFile) {
        if (logFile != null) {
            loadConfiguration(getPackagedConfigFile("log4j2-file.xml"), logFile, getOverrides(initializationContext));
        } else {
            loadConfiguration(getPackagedConfigFile("log4j2.xml"), logFile, getOverrides(initializationContext));
        }
    }

    private List<String> getOverrides(LoggingInitializationContext initializationContext) {
        BindResult<List<String>> overrides = Binder.get(initializationContext.getEnvironment()).bind("logging.log4j2.config.override", Bindable.listOf(String.class));
        return overrides.orElse(Collections.emptyList());
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.AbstractLoggingSystem
    protected void loadConfiguration(LoggingInitializationContext initializationContext, String location, LogFile logFile) {
        super.loadConfiguration(initializationContext, location, logFile);
        loadConfiguration(location, logFile, getOverrides(initializationContext));
    }

    @Deprecated
    protected void loadConfiguration(String location, LogFile logFile) {
        loadConfiguration(location, logFile, Collections.emptyList());
    }

    protected void loadConfiguration(String location, LogFile logFile, List<String> overrides) {
        Assert.notNull(location, "Location must not be null");
        try {
            List<Configuration> configurations = new ArrayList<>();
            LoggerContext context = getLoggerContext();
            configurations.add(load(location, context));
            for (String override : overrides) {
                configurations.add(load(override, context));
            }
            context.start(configurations.size() > 1 ? createComposite(configurations) : (Configuration) configurations.iterator().next());
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize Log4J2 logging from " + location, ex);
        }
    }

    private Configuration load(String location, LoggerContext context) throws IOException {
        URL url = ResourceUtils.getURL(location);
        ConfigurationSource source = getConfigurationSource(url);
        return ConfigurationFactory.getInstance().getConfiguration(context, source);
    }

    private ConfigurationSource getConfigurationSource(URL url) throws IOException {
        InputStream stream = url.openStream();
        if ("file".equals(url.getProtocol())) {
            return new ConfigurationSource(stream, ResourceUtils.getFile(url));
        }
        return new ConfigurationSource(stream, url);
    }

    private CompositeConfiguration createComposite(List<Configuration> configurations) {
        Stream<Configuration> stream = configurations.stream();
        Class<AbstractConfiguration> cls = AbstractConfiguration.class;
        AbstractConfiguration.class.getClass();
        return new CompositeConfiguration((List) stream.map((v1) -> {
            return r3.cast(v1);
        }).collect(Collectors.toList()));
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void reinitialize(LoggingInitializationContext initializationContext) {
        List<String> overrides = getOverrides(initializationContext);
        if (!CollectionUtils.isEmpty(overrides)) {
            reinitializeWithOverrides(overrides);
        } else {
            LoggerContext context = getLoggerContext();
            context.reconfigure();
        }
    }

    private void reinitializeWithOverrides(List<String> overrides) {
        LoggerContext context = getLoggerContext();
        AbstractConfiguration configuration = context.getConfiguration();
        List<AbstractConfiguration> configurations = new ArrayList<>();
        configurations.add(configuration);
        for (String override : overrides) {
            try {
                configurations.add(load(override, context));
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load overriding configuration from '" + override + "'", ex);
            }
        }
        CompositeConfiguration composite = new CompositeConfiguration(configurations);
        context.reconfigure(composite);
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Set<LogLevel> getSupportedLogLevels() {
        return LEVELS.getSupported();
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void setLogLevel(String loggerName, LogLevel logLevel) {
        setLogLevel(loggerName, LEVELS.convertSystemToNative(logLevel));
    }

    private void setLogLevel(String loggerName, Level level) {
        LoggerConfig logger = getLogger(loggerName);
        if (level == null) {
            clearLogLevel(loggerName, logger);
        } else {
            setLogLevel(loggerName, logger, level);
        }
        getLoggerContext().updateLoggers();
    }

    private void clearLogLevel(String loggerName, LoggerConfig logger) {
        if (logger instanceof LevelSetLoggerConfig) {
            getLoggerContext().getConfiguration().removeLogger(loggerName);
        } else {
            logger.setLevel((Level) null);
        }
    }

    private void setLogLevel(String loggerName, LoggerConfig logger, Level level) {
        if (logger == null) {
            getLoggerContext().getConfiguration().addLogger(loggerName, new LevelSetLoggerConfig(loggerName, level, true));
        } else {
            logger.setLevel(level);
        }
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public List<LoggerConfiguration> getLoggerConfigurations() {
        List<LoggerConfiguration> result = new ArrayList<>();
        getAllLoggers().forEach((name, loggerConfig) -> {
            result.add(convertLoggerConfig(name, loggerConfig));
        });
        result.sort(CONFIGURATION_COMPARATOR);
        return result;
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public LoggerConfiguration getLoggerConfiguration(String loggerName) {
        LoggerConfig loggerConfig = getAllLoggers().get(loggerName);
        if (loggerConfig != null) {
            return convertLoggerConfig(loggerName, loggerConfig);
        }
        return null;
    }

    private Map<String, LoggerConfig> getAllLoggers() {
        Map<String, LoggerConfig> loggers = new LinkedHashMap<>();
        for (Logger logger : getLoggerContext().getLoggers()) {
            addLogger(loggers, logger.getName());
        }
        getLoggerContext().getConfiguration().getLoggers().keySet().forEach(name -> {
            addLogger(loggers, name);
        });
        return loggers;
    }

    private void addLogger(Map<String, LoggerConfig> loggers, String name) {
        Configuration configuration = getLoggerContext().getConfiguration();
        while (name != null) {
            configuration.getClass();
            loggers.computeIfAbsent(name, configuration::getLoggerConfig);
            name = getSubName(name);
        }
    }

    private String getSubName(String name) {
        if (!StringUtils.hasLength(name)) {
            return null;
        }
        int nested = name.lastIndexOf(36);
        return nested != -1 ? name.substring(0, nested) : NameUtil.getSubName(name);
    }

    private LoggerConfiguration convertLoggerConfig(String name, LoggerConfig loggerConfig) {
        if (loggerConfig == null) {
            return null;
        }
        LogLevel level = LEVELS.convertNativeToSystem(loggerConfig.getLevel());
        if (!StringUtils.hasLength(name) || "".equals(name)) {
            name = "ROOT";
        }
        boolean isLoggerConfigured = loggerConfig.getName().equals(name);
        LogLevel configuredLevel = isLoggerConfigured ? level : null;
        return new LoggerConfiguration(name, configuredLevel, level);
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Runnable getShutdownHandler() {
        return () -> {
            getLoggerContext().stop();
        };
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void cleanUp() {
        super.cleanUp();
        LoggerContext loggerContext = getLoggerContext();
        markAsUninitialized(loggerContext);
        loggerContext.getConfiguration().removeFilter(FILTER);
    }

    private LoggerConfig getLogger(String name) {
        boolean isRootLogger = !StringUtils.hasLength(name) || "ROOT".equals(name);
        return findLogger(isRootLogger ? "" : name);
    }

    private LoggerConfig findLogger(String name) {
        AbstractConfiguration configuration = getLoggerContext().getConfiguration();
        if (configuration instanceof AbstractConfiguration) {
            return configuration.getLogger(name);
        }
        return (LoggerConfig) configuration.getLoggers().get(name);
    }

    private LoggerContext getLoggerContext() {
        return LogManager.getContext(false);
    }

    private boolean isAlreadyInitialized(LoggerContext loggerContext) {
        return LoggingSystem.class.getName().equals(loggerContext.getExternalContext());
    }

    private void markAsInitialized(LoggerContext loggerContext) {
        loggerContext.setExternalContext(LoggingSystem.class.getName());
    }

    private void markAsUninitialized(LoggerContext loggerContext) {
        loggerContext.setExternalContext((Object) null);
    }

    @Order(Integer.MAX_VALUE)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/log4j2/Log4J2LoggingSystem$Factory.class */
    public static class Factory implements LoggingSystemFactory {
        private static final boolean PRESENT = ClassUtils.isPresent("org.apache.logging.log4j.core.impl.Log4jContextFactory", Factory.class.getClassLoader());

        @Override // org.springframework.boot.logging.LoggingSystemFactory
        public LoggingSystem getLoggingSystem(ClassLoader classLoader) {
            if (PRESENT) {
                return new Log4J2LoggingSystem(classLoader);
            }
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/log4j2/Log4J2LoggingSystem$LevelSetLoggerConfig.class */
    private static class LevelSetLoggerConfig extends LoggerConfig {
        LevelSetLoggerConfig(String name, Level level, boolean additive) {
            super(name, level, additive);
        }
    }
}
