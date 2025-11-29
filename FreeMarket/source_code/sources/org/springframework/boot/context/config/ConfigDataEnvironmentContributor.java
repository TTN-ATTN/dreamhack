package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributor.class */
class ConfigDataEnvironmentContributor implements Iterable<ConfigDataEnvironmentContributor> {
    private static final ConfigData.Options EMPTY_LOCATION_OPTIONS = ConfigData.Options.of(ConfigData.Option.IGNORE_IMPORTS);
    private final ConfigDataLocation location;
    private final ConfigDataResource resource;
    private final boolean fromProfileSpecificImport;
    private final PropertySource<?> propertySource;
    private final ConfigurationPropertySource configurationPropertySource;
    private final ConfigDataProperties properties;
    private final ConfigData.Options configDataOptions;
    private final Map<ImportPhase, List<ConfigDataEnvironmentContributor>> children;
    private final Kind kind;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributor$Kind.class */
    enum Kind {
        ROOT,
        INITIAL_IMPORT,
        EXISTING,
        UNBOUND_IMPORT,
        BOUND_IMPORT,
        EMPTY_LOCATION
    }

    ConfigDataEnvironmentContributor(Kind kind, ConfigDataLocation location, ConfigDataResource resource, boolean fromProfileSpecificImport, PropertySource<?> propertySource, ConfigurationPropertySource configurationPropertySource, ConfigDataProperties properties, ConfigData.Options configDataOptions, Map<ImportPhase, List<ConfigDataEnvironmentContributor>> children) {
        this.kind = kind;
        this.location = location;
        this.resource = resource;
        this.fromProfileSpecificImport = fromProfileSpecificImport;
        this.properties = properties;
        this.propertySource = propertySource;
        this.configurationPropertySource = configurationPropertySource;
        this.configDataOptions = configDataOptions != null ? configDataOptions : ConfigData.Options.NONE;
        this.children = children != null ? children : Collections.emptyMap();
    }

    Kind getKind() {
        return this.kind;
    }

    ConfigDataLocation getLocation() {
        return this.location;
    }

    boolean isActive(ConfigDataActivationContext activationContext) {
        if (this.kind == Kind.UNBOUND_IMPORT) {
            return false;
        }
        return this.properties == null || this.properties.isActive(activationContext);
    }

    ConfigDataResource getResource() {
        return this.resource;
    }

    boolean isFromProfileSpecificImport() {
        return this.fromProfileSpecificImport;
    }

    PropertySource<?> getPropertySource() {
        return this.propertySource;
    }

    ConfigurationPropertySource getConfigurationPropertySource() {
        return this.configurationPropertySource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasConfigDataOption(ConfigData.Option option) {
        return this.configDataOptions.contains(option);
    }

    ConfigDataEnvironmentContributor withoutConfigDataOption(ConfigData.Option option) {
        return new ConfigDataEnvironmentContributor(this.kind, this.location, this.resource, this.fromProfileSpecificImport, this.propertySource, this.configurationPropertySource, this.properties, this.configDataOptions.without(option), this.children);
    }

    List<ConfigDataLocation> getImports() {
        return this.properties != null ? this.properties.getImports() : Collections.emptyList();
    }

    boolean hasUnprocessedImports(ImportPhase importPhase) {
        return (getImports().isEmpty() || this.children.containsKey(importPhase)) ? false : true;
    }

    List<ConfigDataEnvironmentContributor> getChildren(ImportPhase importPhase) {
        return this.children.getOrDefault(importPhase, Collections.emptyList());
    }

    Stream<ConfigDataEnvironmentContributor> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override // java.lang.Iterable
    public Iterator<ConfigDataEnvironmentContributor> iterator() {
        return new ContributorIterator();
    }

    ConfigDataEnvironmentContributor withBoundProperties(Iterable<ConfigDataEnvironmentContributor> contributors, ConfigDataActivationContext activationContext) {
        Iterable<ConfigurationPropertySource> sources = Collections.singleton(getConfigurationPropertySource());
        PlaceholdersResolver placeholdersResolver = new ConfigDataEnvironmentContributorPlaceholdersResolver(contributors, activationContext, this, true);
        Binder binder = new Binder(sources, placeholdersResolver, null, null, null);
        UseLegacyConfigProcessingException.throwIfRequested(binder);
        ConfigDataProperties properties = ConfigDataProperties.get(binder);
        if (properties != null && this.configDataOptions.contains(ConfigData.Option.IGNORE_IMPORTS)) {
            properties = properties.withoutImports();
        }
        return new ConfigDataEnvironmentContributor(Kind.BOUND_IMPORT, this.location, this.resource, this.fromProfileSpecificImport, this.propertySource, this.configurationPropertySource, properties, this.configDataOptions, null);
    }

    ConfigDataEnvironmentContributor withChildren(ImportPhase importPhase, List<ConfigDataEnvironmentContributor> children) {
        Map<ImportPhase, List<ConfigDataEnvironmentContributor>> updatedChildren = new LinkedHashMap<>(this.children);
        updatedChildren.put(importPhase, children);
        if (importPhase == ImportPhase.AFTER_PROFILE_ACTIVATION) {
            moveProfileSpecific(updatedChildren);
        }
        return new ConfigDataEnvironmentContributor(this.kind, this.location, this.resource, this.fromProfileSpecificImport, this.propertySource, this.configurationPropertySource, this.properties, this.configDataOptions, updatedChildren);
    }

    private void moveProfileSpecific(Map<ImportPhase, List<ConfigDataEnvironmentContributor>> children) {
        List<ConfigDataEnvironmentContributor> before = children.get(ImportPhase.BEFORE_PROFILE_ACTIVATION);
        if (!hasAnyProfileSpecificChildren(before)) {
            return;
        }
        List<ConfigDataEnvironmentContributor> updatedBefore = new ArrayList<>(before.size());
        List<ConfigDataEnvironmentContributor> updatedAfter = new ArrayList<>();
        for (ConfigDataEnvironmentContributor contributor : before) {
            updatedBefore.add(moveProfileSpecificChildren(contributor, updatedAfter));
        }
        updatedAfter.addAll(children.getOrDefault(ImportPhase.AFTER_PROFILE_ACTIVATION, Collections.emptyList()));
        children.put(ImportPhase.BEFORE_PROFILE_ACTIVATION, updatedBefore);
        children.put(ImportPhase.AFTER_PROFILE_ACTIVATION, updatedAfter);
    }

    private ConfigDataEnvironmentContributor moveProfileSpecificChildren(ConfigDataEnvironmentContributor contributor, List<ConfigDataEnvironmentContributor> removed) {
        for (ImportPhase importPhase : ImportPhase.values()) {
            List<ConfigDataEnvironmentContributor> children = contributor.getChildren(importPhase);
            List<ConfigDataEnvironmentContributor> updatedChildren = new ArrayList<>(children.size());
            for (ConfigDataEnvironmentContributor child : children) {
                if (child.hasConfigDataOption(ConfigData.Option.PROFILE_SPECIFIC)) {
                    removed.add(child.withoutConfigDataOption(ConfigData.Option.PROFILE_SPECIFIC));
                } else {
                    updatedChildren.add(child);
                }
            }
            contributor = contributor.withChildren(importPhase, updatedChildren);
        }
        return contributor;
    }

    private boolean hasAnyProfileSpecificChildren(List<ConfigDataEnvironmentContributor> contributors) {
        if (CollectionUtils.isEmpty(contributors)) {
            return false;
        }
        for (ConfigDataEnvironmentContributor contributor : contributors) {
            for (ImportPhase importPhase : ImportPhase.values()) {
                if (contributor.getChildren(importPhase).stream().anyMatch(child -> {
                    return child.hasConfigDataOption(ConfigData.Option.PROFILE_SPECIFIC);
                })) {
                    return true;
                }
            }
        }
        return false;
    }

    ConfigDataEnvironmentContributor withReplacement(ConfigDataEnvironmentContributor existing, ConfigDataEnvironmentContributor replacement) {
        if (this == existing) {
            return replacement;
        }
        Map<ImportPhase, List<ConfigDataEnvironmentContributor>> updatedChildren = new LinkedHashMap<>(this.children.size());
        this.children.forEach((importPhase, contributors) -> {
            List<ConfigDataEnvironmentContributor> updatedContributors = new ArrayList<>(contributors.size());
            Iterator it = contributors.iterator();
            while (it.hasNext()) {
                ConfigDataEnvironmentContributor contributor = (ConfigDataEnvironmentContributor) it.next();
                updatedContributors.add(contributor.withReplacement(existing, replacement));
            }
            updatedChildren.put(importPhase, Collections.unmodifiableList(updatedContributors));
        });
        return new ConfigDataEnvironmentContributor(this.kind, this.location, this.resource, this.fromProfileSpecificImport, this.propertySource, this.configurationPropertySource, this.properties, this.configDataOptions, updatedChildren);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        buildToString("", builder);
        return builder.toString();
    }

    private void buildToString(String prefix, StringBuilder builder) {
        builder.append(prefix);
        builder.append(this.kind);
        builder.append(" ");
        builder.append(this.location);
        builder.append(" ");
        builder.append(this.resource);
        builder.append(" ");
        builder.append(this.configDataOptions);
        builder.append("\n");
        for (ConfigDataEnvironmentContributor child : this.children.getOrDefault(ImportPhase.BEFORE_PROFILE_ACTIVATION, Collections.emptyList())) {
            child.buildToString(prefix + "    ", builder);
        }
        for (ConfigDataEnvironmentContributor child2 : this.children.getOrDefault(ImportPhase.AFTER_PROFILE_ACTIVATION, Collections.emptyList())) {
            child2.buildToString(prefix + "    ", builder);
        }
    }

    static ConfigDataEnvironmentContributor of(List<ConfigDataEnvironmentContributor> contributors) {
        Map<ImportPhase, List<ConfigDataEnvironmentContributor>> children = new LinkedHashMap<>();
        children.put(ImportPhase.BEFORE_PROFILE_ACTIVATION, Collections.unmodifiableList(contributors));
        return new ConfigDataEnvironmentContributor(Kind.ROOT, null, null, false, null, null, null, null, children);
    }

    static ConfigDataEnvironmentContributor ofInitialImport(ConfigDataLocation initialImport) {
        List<ConfigDataLocation> imports = Collections.singletonList(initialImport);
        ConfigDataProperties properties = new ConfigDataProperties(imports, null);
        return new ConfigDataEnvironmentContributor(Kind.INITIAL_IMPORT, null, null, false, null, null, properties, null, null);
    }

    static ConfigDataEnvironmentContributor ofExisting(PropertySource<?> propertySource) {
        return new ConfigDataEnvironmentContributor(Kind.EXISTING, null, null, false, propertySource, ConfigurationPropertySource.from(propertySource), null, null, null);
    }

    static ConfigDataEnvironmentContributor ofUnboundImport(ConfigDataLocation location, ConfigDataResource resource, boolean profileSpecific, ConfigData configData, int propertySourceIndex) {
        PropertySource<?> propertySource = configData.getPropertySources().get(propertySourceIndex);
        ConfigData.Options options = configData.getOptions(propertySource);
        ConfigurationPropertySource configurationPropertySource = ConfigurationPropertySource.from(propertySource);
        return new ConfigDataEnvironmentContributor(Kind.UNBOUND_IMPORT, location, resource, profileSpecific, propertySource, configurationPropertySource, null, options, null);
    }

    static ConfigDataEnvironmentContributor ofEmptyLocation(ConfigDataLocation location, boolean profileSpecific) {
        return new ConfigDataEnvironmentContributor(Kind.EMPTY_LOCATION, location, null, profileSpecific, null, null, null, EMPTY_LOCATION_OPTIONS, null);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributor$ImportPhase.class */
    enum ImportPhase {
        BEFORE_PROFILE_ACTIVATION,
        AFTER_PROFILE_ACTIVATION;

        static ImportPhase get(ConfigDataActivationContext activationContext) {
            if (activationContext != null && activationContext.getProfiles() != null) {
                return AFTER_PROFILE_ACTIVATION;
            }
            return BEFORE_PROFILE_ACTIVATION;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributor$ContributorIterator.class */
    private final class ContributorIterator implements Iterator<ConfigDataEnvironmentContributor> {
        private ImportPhase phase;
        private Iterator<ConfigDataEnvironmentContributor> children;
        private Iterator<ConfigDataEnvironmentContributor> current;
        private ConfigDataEnvironmentContributor next;

        private ContributorIterator() {
            this.phase = ImportPhase.AFTER_PROFILE_ACTIVATION;
            this.children = ConfigDataEnvironmentContributor.this.getChildren(this.phase).iterator();
            this.current = Collections.emptyIterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return fetchIfNecessary() != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ConfigDataEnvironmentContributor next() {
            ConfigDataEnvironmentContributor next = fetchIfNecessary();
            if (next == null) {
                throw new NoSuchElementException();
            }
            this.next = null;
            return next;
        }

        private ConfigDataEnvironmentContributor fetchIfNecessary() {
            if (this.next != null) {
                return this.next;
            }
            if (this.current.hasNext()) {
                this.next = this.current.next();
                return this.next;
            }
            if (this.children.hasNext()) {
                this.current = this.children.next().iterator();
                return fetchIfNecessary();
            }
            if (this.phase == ImportPhase.AFTER_PROFILE_ACTIVATION) {
                this.phase = ImportPhase.BEFORE_PROFILE_ACTIVATION;
                this.children = ConfigDataEnvironmentContributor.this.getChildren(this.phase).iterator();
                return fetchIfNecessary();
            }
            if (this.phase == ImportPhase.BEFORE_PROFILE_ACTIVATION) {
                this.phase = null;
                this.next = ConfigDataEnvironmentContributor.this;
                return this.next;
            }
            return null;
        }
    }
}
