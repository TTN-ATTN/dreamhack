package org.springframework.boot.context.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataEnvironmentPostProcessor.class */
public class ConfigDataEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final int ORDER = -2147483638;
    public static final String ON_LOCATION_NOT_FOUND_PROPERTY = "spring.config.on-not-found";
    private final DeferredLogFactory logFactory;
    private final Log logger;
    private final ConfigurableBootstrapContext bootstrapContext;
    private final ConfigDataEnvironmentUpdateListener environmentUpdateListener;

    public ConfigDataEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        this(logFactory, bootstrapContext, null);
    }

    public ConfigDataEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        this.logFactory = logFactory;
        this.logger = logFactory.getLog(getClass());
        this.bootstrapContext = bootstrapContext;
        this.environmentUpdateListener = environmentUpdateListener;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return -2147483638;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        postProcessEnvironment(environment, application.getResourceLoader(), application.getAdditionalProfiles());
    }

    void postProcessEnvironment(ConfigurableEnvironment environment, ResourceLoader resourceLoader, Collection<String> additionalProfiles) {
        try {
            this.logger.trace("Post-processing environment to add config data");
            resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
            getConfigDataEnvironment(environment, resourceLoader, additionalProfiles).processAndApply();
        } catch (UseLegacyConfigProcessingException ex) {
            this.logger.debug(LogMessage.format("Switching to legacy config file processing [%s]", ex.getConfigurationProperty()));
            configureAdditionalProfiles(environment, additionalProfiles);
            postProcessUsingLegacyApplicationListener(environment, resourceLoader);
        }
    }

    ConfigDataEnvironment getConfigDataEnvironment(ConfigurableEnvironment environment, ResourceLoader resourceLoader, Collection<String> additionalProfiles) {
        return new ConfigDataEnvironment(this.logFactory, this.bootstrapContext, environment, resourceLoader, additionalProfiles, this.environmentUpdateListener);
    }

    private void configureAdditionalProfiles(ConfigurableEnvironment environment, Collection<String> additionalProfiles) {
        if (!CollectionUtils.isEmpty(additionalProfiles)) {
            Set<String> profiles = new LinkedHashSet<>(additionalProfiles);
            profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
            environment.setActiveProfiles(StringUtils.toStringArray(profiles));
        }
    }

    private void postProcessUsingLegacyApplicationListener(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
        getLegacyListener().addPropertySources(environment, resourceLoader);
    }

    LegacyConfigFileApplicationListener getLegacyListener() {
        return new LegacyConfigFileApplicationListener(this.logFactory.getLog(ConfigFileApplicationListener.class));
    }

    public static void applyTo(ConfigurableEnvironment environment) {
        applyTo(environment, (ResourceLoader) null, (ConfigurableBootstrapContext) null, Collections.emptyList());
    }

    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader, ConfigurableBootstrapContext bootstrapContext, String... additionalProfiles) {
        applyTo(environment, resourceLoader, bootstrapContext, Arrays.asList(additionalProfiles));
    }

    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader, ConfigurableBootstrapContext bootstrapContext, Collection<String> additionalProfiles) {
        DeferredLogFactory logFactory = (v0) -> {
            return v0.get();
        };
        ConfigDataEnvironmentPostProcessor postProcessor = new ConfigDataEnvironmentPostProcessor(logFactory, bootstrapContext != null ? bootstrapContext : new DefaultBootstrapContext());
        postProcessor.postProcessEnvironment(environment, resourceLoader, additionalProfiles);
    }

    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader, ConfigurableBootstrapContext bootstrapContext, Collection<String> additionalProfiles, ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        DeferredLogFactory logFactory = (v0) -> {
            return v0.get();
        };
        ConfigDataEnvironmentPostProcessor postProcessor = new ConfigDataEnvironmentPostProcessor(logFactory, bootstrapContext != null ? bootstrapContext : new DefaultBootstrapContext(), environmentUpdateListener);
        postProcessor.postProcessEnvironment(environment, resourceLoader, additionalProfiles);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataEnvironmentPostProcessor$LegacyConfigFileApplicationListener.class */
    static class LegacyConfigFileApplicationListener extends ConfigFileApplicationListener {
        LegacyConfigFileApplicationListener(Log logger) {
            super(logger);
        }

        @Override // org.springframework.boot.context.config.ConfigFileApplicationListener
        public void addPropertySources(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
            super.addPropertySources(environment, resourceLoader);
        }
    }
}
