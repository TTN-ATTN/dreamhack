package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.Marker;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.boot.logging.AbstractLoggingSystem;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.boot.logging.LoggingSystemProperties;
import org.springframework.boot.logging.Slf4JLoggingSystem;
import org.springframework.core.SpringProperties;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/logback/LogbackLoggingSystem.class */
public class LogbackLoggingSystem extends Slf4JLoggingSystem {
    private static final boolean XML_ENABLED;
    private static final String CONFIGURATION_FILE_PROPERTY = "logback.configurationFile";
    private static final AbstractLoggingSystem.LogLevels<Level> LEVELS;
    private static final TurboFilter FILTER;

    static {
        XML_ENABLED = !SpringProperties.getFlag("spring.xml.ignore");
        LEVELS = new AbstractLoggingSystem.LogLevels<>();
        LEVELS.map(LogLevel.TRACE, Level.TRACE);
        LEVELS.map(LogLevel.TRACE, Level.ALL);
        LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.map(LogLevel.INFO, Level.INFO);
        LEVELS.map(LogLevel.WARN, Level.WARN);
        LEVELS.map(LogLevel.ERROR, Level.ERROR);
        LEVELS.map(LogLevel.FATAL, Level.ERROR);
        LEVELS.map(LogLevel.OFF, Level.OFF);
        FILTER = new TurboFilter() { // from class: org.springframework.boot.logging.logback.LogbackLoggingSystem.1
            @Override // ch.qos.logback.classic.turbo.TurboFilter
            public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
                return FilterReply.DENY;
            }
        };
    }

    public LogbackLoggingSystem(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public LoggingSystemProperties getSystemProperties(ConfigurableEnvironment environment) {
        return new LogbackLoggingSystemProperties(environment);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected String[] getStandardConfigLocations() {
        return new String[]{"logback-test.groovy", ContextInitializer.TEST_AUTOCONFIG_FILE, "logback.groovy", ContextInitializer.AUTOCONFIG_FILE};
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void beforeInitialize() {
        LoggerContext loggerContext = getLoggerContext();
        if (isAlreadyInitialized(loggerContext)) {
            return;
        }
        super.beforeInitialize();
        loggerContext.getTurboFilterList().add(FILTER);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
        LoggerContext loggerContext = getLoggerContext();
        if (isAlreadyInitialized(loggerContext)) {
            return;
        }
        super.initialize(initializationContext, configLocation, logFile);
        loggerContext.getTurboFilterList().remove(FILTER);
        markAsInitialized(loggerContext);
        if (StringUtils.hasText(System.getProperty("logback.configurationFile"))) {
            getLogger(LogbackLoggingSystem.class.getName()).warn("Ignoring 'logback.configurationFile' system property. Please use 'logging.config' instead.");
        }
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void loadDefaults(LoggingInitializationContext initializationContext, LogFile logFile) {
        LoggerContext context = getLoggerContext();
        stopAndReset(context);
        boolean debug = Boolean.getBoolean("logback.debug");
        if (debug) {
            StatusListenerConfigHelper.addOnConsoleListenerInstance(context, new OnConsoleStatusListener());
        }
        Environment environment = initializationContext.getEnvironment();
        context.getClass();
        new LogbackLoggingSystemProperties(environment, context::putProperty).apply(logFile);
        LogbackConfigurator configurator = debug ? new DebugLogbackConfigurator(context) : new LogbackConfigurator(context);
        new DefaultLogbackConfiguration(logFile).apply(configurator);
        context.setPackagingDataEnabled(true);
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.AbstractLoggingSystem
    protected void loadConfiguration(LoggingInitializationContext initializationContext, String location, LogFile logFile) {
        super.loadConfiguration(initializationContext, location, logFile);
        LoggerContext loggerContext = getLoggerContext();
        stopAndReset(loggerContext);
        try {
            configureByResourceUrl(initializationContext, loggerContext, ResourceUtils.getURL(location));
            List<Status> statuses = loggerContext.getStatusManager().getCopyOfStatusList();
            StringBuilder errors = new StringBuilder();
            for (Status status : statuses) {
                if (status.getLevel() == 2) {
                    errors.append(errors.length() > 0 ? String.format("%n", new Object[0]) : "");
                    errors.append(status.toString());
                }
            }
            if (errors.length() > 0) {
                throw new IllegalStateException(String.format("Logback configuration error detected: %n%s", errors));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize Logback logging from " + location, ex);
        }
    }

    private void configureByResourceUrl(LoggingInitializationContext initializationContext, LoggerContext loggerContext, URL url) throws JoranException {
        if (XML_ENABLED && url.toString().endsWith("xml")) {
            JoranConfigurator configurator = new SpringBootJoranConfigurator(initializationContext);
            configurator.setContext(loggerContext);
            configurator.doConfigure(url);
            return;
        }
        new ContextInitializer(loggerContext).configureByResource(url);
    }

    private void stopAndReset(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();
        if (isBridgeHandlerInstalled()) {
            addLevelChangePropagator(loggerContext);
        }
    }

    private boolean isBridgeHandlerInstalled() {
        if (!isBridgeHandlerAvailable()) {
            return false;
        }
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        return handlers.length == 1 && (handlers[0] instanceof SLF4JBridgeHandler);
    }

    private void addLevelChangePropagator(LoggerContext loggerContext) {
        LevelChangePropagator levelChangePropagator = new LevelChangePropagator();
        levelChangePropagator.setResetJUL(true);
        levelChangePropagator.setContext(loggerContext);
        loggerContext.addListener(levelChangePropagator);
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void cleanUp() {
        LoggerContext context = getLoggerContext();
        markAsUninitialized(context);
        super.cleanUp();
        context.getStatusManager().clear();
        context.getTurboFilterList().remove(FILTER);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void reinitialize(LoggingInitializationContext initializationContext) {
        getLoggerContext().reset();
        getLoggerContext().getStatusManager().clear();
        loadConfiguration(initializationContext, getSelfInitializationConfig(), null);
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public List<LoggerConfiguration> getLoggerConfigurations() {
        List<LoggerConfiguration> result = new ArrayList<>();
        for (Logger logger : getLoggerContext().getLoggerList()) {
            result.add(getLoggerConfiguration(logger));
        }
        result.sort(CONFIGURATION_COMPARATOR);
        return result;
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public LoggerConfiguration getLoggerConfiguration(String loggerName) {
        String name = getLoggerName(loggerName);
        LoggerContext loggerContext = getLoggerContext();
        return getLoggerConfiguration(loggerContext.exists(name));
    }

    private String getLoggerName(String name) {
        if (!StringUtils.hasLength(name) || "ROOT".equals(name)) {
            return "ROOT";
        }
        return name;
    }

    private LoggerConfiguration getLoggerConfiguration(Logger logger) {
        if (logger == null) {
            return null;
        }
        LogLevel level = LEVELS.convertNativeToSystem(logger.getLevel());
        LogLevel effectiveLevel = LEVELS.convertNativeToSystem(logger.getEffectiveLevel());
        String name = getLoggerName(logger.getName());
        return new LoggerConfiguration(name, level, effectiveLevel);
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Set<LogLevel> getSupportedLogLevels() {
        return LEVELS.getSupported();
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void setLogLevel(String loggerName, LogLevel level) {
        Logger logger = getLogger(loggerName);
        if (logger != null) {
            logger.setLevel(LEVELS.convertSystemToNative(level));
        }
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Runnable getShutdownHandler() {
        return () -> {
            getLoggerContext().stop();
        };
    }

    private Logger getLogger(String name) {
        LoggerContext factory = getLoggerContext();
        return factory.getLogger(getLoggerName(name));
    }

    private LoggerContext getLoggerContext() {
        ILoggerFactory factory = StaticLoggerBinder.getSingleton().getLoggerFactory();
        Assert.isInstanceOf((Class<?>) LoggerContext.class, factory, (Supplier<String>) () -> {
            return String.format("LoggerFactory is not a Logback LoggerContext but Logback is on the classpath. Either remove Logback or the competing implementation (%s loaded from %s). If you are using WebLogic you will need to add 'org.slf4j' to prefer-application-packages in WEB-INF/weblogic.xml", factory.getClass(), getLocation(factory));
        });
        return (LoggerContext) factory;
    }

    private Object getLocation(ILoggerFactory factory) {
        try {
            ProtectionDomain protectionDomain = factory.getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource != null) {
                return codeSource.getLocation();
            }
            return "unknown location";
        } catch (SecurityException e) {
            return "unknown location";
        }
    }

    private boolean isAlreadyInitialized(LoggerContext loggerContext) {
        return loggerContext.getObject(LoggingSystem.class.getName()) != null;
    }

    private void markAsInitialized(LoggerContext loggerContext) {
        loggerContext.putObject(LoggingSystem.class.getName(), new Object());
    }

    private void markAsUninitialized(LoggerContext loggerContext) {
        loggerContext.removeObject(LoggingSystem.class.getName());
    }

    @Order(Integer.MAX_VALUE)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/logback/LogbackLoggingSystem$Factory.class */
    public static class Factory implements LoggingSystemFactory {
        private static final boolean PRESENT = ClassUtils.isPresent("ch.qos.logback.classic.LoggerContext", Factory.class.getClassLoader());

        @Override // org.springframework.boot.logging.LoggingSystemFactory
        public LoggingSystem getLoggingSystem(ClassLoader classLoader) {
            if (PRESENT) {
                return new LogbackLoggingSystem(classLoader);
            }
            return null;
        }
    }
}
