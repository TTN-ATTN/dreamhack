package org.springframework.boot.context.config;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataProperties.class */
class ConfigDataProperties {
    private static final ConfigurationPropertyName NAME = ConfigurationPropertyName.of("spring.config");
    private static final ConfigurationPropertyName LEGACY_PROFILES_NAME = ConfigurationPropertyName.of("spring.profiles");
    private static final Bindable<ConfigDataProperties> BINDABLE_PROPERTIES = Bindable.of(ConfigDataProperties.class);
    private static final Bindable<String[]> BINDABLE_STRING_ARRAY = Bindable.of(String[].class);
    private final List<ConfigDataLocation> imports;
    private final Activate activate;

    ConfigDataProperties(@Name(DefaultBeanDefinitionDocumentReader.IMPORT_ELEMENT) List<ConfigDataLocation> imports, Activate activate) {
        this.imports = imports != null ? imports : Collections.emptyList();
        this.activate = activate;
    }

    List<ConfigDataLocation> getImports() {
        return this.imports;
    }

    boolean isActive(ConfigDataActivationContext activationContext) {
        return this.activate == null || this.activate.isActive(activationContext);
    }

    ConfigDataProperties withoutImports() {
        return new ConfigDataProperties(null, this.activate);
    }

    ConfigDataProperties withLegacyProfiles(String[] legacyProfiles, ConfigurationProperty property) {
        if (this.activate != null && !ObjectUtils.isEmpty((Object[]) this.activate.onProfile)) {
            throw new InvalidConfigDataPropertyException(property, false, NAME.append("activate.on-profile"), null);
        }
        return new ConfigDataProperties(this.imports, new Activate(this.activate.onCloudPlatform, legacyProfiles));
    }

    static ConfigDataProperties get(Binder binder) {
        LegacyProfilesBindHandler legacyProfilesBindHandler = new LegacyProfilesBindHandler();
        String[] legacyProfiles = (String[]) binder.bind(LEGACY_PROFILES_NAME, BINDABLE_STRING_ARRAY, legacyProfilesBindHandler).orElse(null);
        ConfigDataProperties properties = (ConfigDataProperties) binder.bind(NAME, BINDABLE_PROPERTIES, new ConfigDataLocationBindHandler()).orElse(null);
        if (!ObjectUtils.isEmpty((Object[]) legacyProfiles)) {
            properties = properties != null ? properties.withLegacyProfiles(legacyProfiles, legacyProfilesBindHandler.getProperty()) : new ConfigDataProperties(null, new Activate(null, legacyProfiles));
        }
        return properties;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataProperties$LegacyProfilesBindHandler.class */
    private static class LegacyProfilesBindHandler implements BindHandler {
        private ConfigurationProperty property;

        private LegacyProfilesBindHandler() {
        }

        @Override // org.springframework.boot.context.properties.bind.BindHandler
        public Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
            this.property = context.getConfigurationProperty();
            return result;
        }

        ConfigurationProperty getProperty() {
            return this.property;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataProperties$Activate.class */
    static class Activate {
        private final CloudPlatform onCloudPlatform;
        private final String[] onProfile;

        Activate(CloudPlatform onCloudPlatform, String[] onProfile) {
            this.onProfile = onProfile;
            this.onCloudPlatform = onCloudPlatform;
        }

        boolean isActive(ConfigDataActivationContext activationContext) {
            if (activationContext == null) {
                return false;
            }
            boolean activate = 1 != 0 && isActive(activationContext.getCloudPlatform());
            boolean activate2 = activate && isActive(activationContext.getProfiles());
            return activate2;
        }

        private boolean isActive(CloudPlatform cloudPlatform) {
            return this.onCloudPlatform == null || this.onCloudPlatform == cloudPlatform;
        }

        private boolean isActive(Profiles profiles) {
            if (!ObjectUtils.isEmpty((Object[]) this.onProfile)) {
                if (profiles != null) {
                    profiles.getClass();
                    if (matchesActiveProfiles(profiles::isAccepted)) {
                    }
                }
                return false;
            }
            return true;
        }

        private boolean matchesActiveProfiles(Predicate<String> activeProfiles) {
            return org.springframework.core.env.Profiles.of(this.onProfile).matches(activeProfiles);
        }
    }
}
