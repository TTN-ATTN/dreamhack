package org.springframework.boot.context.config;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener.class */
public class ConfigFileApplicationListener implements EnvironmentPostProcessor, SmartApplicationListener, Ordered {
    private static final String DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/,file:./,file:./config/*/,file:./config/";
    private static final String DEFAULT_NAMES = "application";
    private static final Set<String> NO_SEARCH_NAMES = Collections.singleton(null);
    private static final Bindable<String[]> STRING_ARRAY = Bindable.of(String[].class);
    private static final Bindable<List<String>> STRING_LIST = Bindable.listOf(String.class);
    private static final Set<String> LOAD_FILTERED_PROPERTY;
    public static final String ACTIVE_PROFILES_PROPERTY = "spring.profiles.active";
    public static final String INCLUDE_PROFILES_PROPERTY = "spring.profiles.include";
    public static final String CONFIG_NAME_PROPERTY = "spring.config.name";
    public static final String CONFIG_LOCATION_PROPERTY = "spring.config.location";
    public static final String CONFIG_ADDITIONAL_LOCATION_PROPERTY = "spring.config.additional-location";
    public static final int DEFAULT_ORDER = -2147483638;
    private final Log logger;
    private static final Resource[] EMPTY_RESOURCES;
    private static final Comparator<File> FILE_COMPARATOR;
    private String searchLocations;
    private String names;
    private int order;

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentConsumer.class */
    private interface DocumentConsumer {
        void accept(Profile profile, Document document);
    }

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentFilter.class */
    private interface DocumentFilter {
        boolean match(Document document);
    }

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentFilterFactory.class */
    private interface DocumentFilterFactory {
        DocumentFilter getDocumentFilter(Profile profile);
    }

    static {
        Set<String> filteredProperties = new HashSet<>();
        filteredProperties.add("spring.profiles.active");
        filteredProperties.add("spring.profiles.include");
        LOAD_FILTERED_PROPERTY = Collections.unmodifiableSet(filteredProperties);
        EMPTY_RESOURCES = new Resource[0];
        FILE_COMPARATOR = Comparator.comparing((v0) -> {
            return v0.getAbsolutePath();
        });
    }

    public ConfigFileApplicationListener() {
        this(new DeferredLog());
    }

    ConfigFileApplicationListener(Log logger) {
        this.order = -2147483638;
        this.logger = logger;
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType) || ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        throw new IllegalStateException("ConfigFileApplicationListener [" + getClass().getName() + "] is deprecated and can only be used as an EnvironmentPostProcessor");
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        addPropertySources(environment, application.getResourceLoader());
    }

    protected void addPropertySources(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
        RandomValuePropertySource.addToEnvironment(environment);
        new Loader(environment, resourceLoader).load();
    }

    protected void addPostProcessors(ConfigurableApplicationContext context) {
        context.addBeanFactoryPostProcessor(new PropertySourceOrderingPostProcessor(context));
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.context.event.SmartApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setSearchLocations(String locations) {
        Assert.hasLength(locations, "Locations must not be empty");
        this.searchLocations = locations;
    }

    public void setSearchNames(String names) {
        Assert.hasLength(names, "Names must not be empty");
        this.names = names;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$PropertySourceOrderingPostProcessor.class */
    private static class PropertySourceOrderingPostProcessor implements BeanFactoryPostProcessor, Ordered {
        private final ConfigurableApplicationContext context;

        PropertySourceOrderingPostProcessor(ConfigurableApplicationContext context) {
            this.context = context;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MIN_VALUE;
        }

        @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            reorderSources(this.context.getEnvironment());
        }

        private void reorderSources(ConfigurableEnvironment environment) {
            DefaultPropertiesPropertySource.moveToEnd(environment);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$Loader.class */
    private class Loader {
        private final Log logger;
        private final ConfigurableEnvironment environment;
        private final PropertySourcesPlaceholdersResolver placeholdersResolver;
        private final ResourceLoader resourceLoader;
        private final List<PropertySourceLoader> propertySourceLoaders;
        private Deque<Profile> profiles;
        private List<Profile> processedProfiles;
        private boolean activatedProfiles;
        private Map<Profile, MutablePropertySources> loaded;
        private Map<DocumentsCacheKey, List<Document>> loadDocumentsCache = new HashMap();

        Loader(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
            this.logger = ConfigFileApplicationListener.this.logger;
            this.environment = environment;
            this.placeholdersResolver = new PropertySourcesPlaceholdersResolver(this.environment);
            this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader(null);
            this.propertySourceLoaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class, this.resourceLoader.getClassLoader());
        }

        void load() {
            FilteredPropertySource.apply(this.environment, DefaultPropertiesPropertySource.NAME, ConfigFileApplicationListener.LOAD_FILTERED_PROPERTY, this::loadWithFilteredProperties);
        }

        private void loadWithFilteredProperties(PropertySource<?> defaultProperties) {
            this.profiles = new LinkedList();
            this.processedProfiles = new LinkedList();
            this.activatedProfiles = false;
            this.loaded = new LinkedHashMap();
            initializeProfiles();
            while (!this.profiles.isEmpty()) {
                Profile profile = this.profiles.poll();
                if (isDefaultProfile(profile)) {
                    addProfileToEnvironment(profile.getName());
                }
                load(profile, this::getPositiveProfileFilter, addToLoaded((v0, v1) -> {
                    v0.addLast(v1);
                }, false));
                this.processedProfiles.add(profile);
            }
            load(null, this::getNegativeProfileFilter, addToLoaded((v0, v1) -> {
                v0.addFirst(v1);
            }, true));
            addLoadedPropertySources();
            applyActiveProfiles(defaultProperties);
        }

        private void initializeProfiles() {
            this.profiles.add(null);
            Binder binder = Binder.get(this.environment);
            Set<Profile> activatedViaProperty = getProfiles(binder, "spring.profiles.active");
            Set<Profile> includedViaProperty = getProfiles(binder, "spring.profiles.include");
            List<Profile> otherActiveProfiles = getOtherActiveProfiles(activatedViaProperty, includedViaProperty);
            this.profiles.addAll(otherActiveProfiles);
            this.profiles.addAll(includedViaProperty);
            addActiveProfiles(activatedViaProperty);
            if (this.profiles.size() == 1) {
                for (String defaultProfileName : getDefaultProfiles(binder)) {
                    Profile defaultProfile = new Profile(defaultProfileName, true);
                    this.profiles.add(defaultProfile);
                }
            }
        }

        private String[] getDefaultProfiles(Binder binder) {
            BindResult bindResultBind = binder.bind(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, ConfigFileApplicationListener.STRING_ARRAY);
            ConfigurableEnvironment configurableEnvironment = this.environment;
            configurableEnvironment.getClass();
            return (String[]) bindResultBind.orElseGet(configurableEnvironment::getDefaultProfiles);
        }

        private List<Profile> getOtherActiveProfiles(Set<Profile> activatedViaProperty, Set<Profile> includedViaProperty) {
            return (List) Arrays.stream(this.environment.getActiveProfiles()).map(Profile::new).filter(profile -> {
                return (activatedViaProperty.contains(profile) || includedViaProperty.contains(profile)) ? false : true;
            }).collect(Collectors.toList());
        }

        void addActiveProfiles(Set<Profile> profiles) {
            if (profiles.isEmpty()) {
                return;
            }
            if (this.activatedProfiles) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Profiles already activated, '" + profiles + "' will not be applied");
                }
            } else {
                this.profiles.addAll(profiles);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Activated activeProfiles " + StringUtils.collectionToCommaDelimitedString(profiles));
                }
                this.activatedProfiles = true;
                removeUnprocessedDefaultProfiles();
            }
        }

        private void removeUnprocessedDefaultProfiles() {
            this.profiles.removeIf(profile -> {
                return profile != null && profile.isDefaultProfile();
            });
        }

        private DocumentFilter getPositiveProfileFilter(Profile profile) {
            return document -> {
                if (profile == null) {
                    return ObjectUtils.isEmpty((Object[]) document.getProfiles());
                }
                return ObjectUtils.containsElement(document.getProfiles(), profile.getName()) && this.environment.acceptsProfiles(org.springframework.core.env.Profiles.of(document.getProfiles()));
            };
        }

        private DocumentFilter getNegativeProfileFilter(Profile profile) {
            return document -> {
                return profile == null && !ObjectUtils.isEmpty((Object[]) document.getProfiles()) && this.environment.acceptsProfiles(org.springframework.core.env.Profiles.of(document.getProfiles()));
            };
        }

        private DocumentConsumer addToLoaded(BiConsumer<MutablePropertySources, PropertySource<?>> addMethod, boolean checkForExisting) {
            return (profile, document) -> {
                if (checkForExisting) {
                    for (MutablePropertySources merged : this.loaded.values()) {
                        if (merged.contains(document.getPropertySource().getName())) {
                            return;
                        }
                    }
                }
                MutablePropertySources merged2 = this.loaded.computeIfAbsent(profile, k -> {
                    return new MutablePropertySources();
                });
                addMethod.accept(merged2, document.getPropertySource());
            };
        }

        private void load(Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
            getSearchLocations().forEach(location -> {
                String nonOptionalLocation = ConfigDataLocation.of(location).getValue();
                boolean isDirectory = location.endsWith("/");
                Set<String> names = isDirectory ? getSearchNames() : ConfigFileApplicationListener.NO_SEARCH_NAMES;
                names.forEach(name -> {
                    load(nonOptionalLocation, name, profile, filterFactory, consumer);
                });
            });
        }

        private void load(String location, String name, Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
            if (!StringUtils.hasText(name)) {
                for (PropertySourceLoader loader : this.propertySourceLoaders) {
                    if (canLoadFileExtension(loader, location)) {
                        load(loader, location, profile, filterFactory.getDocumentFilter(profile), consumer);
                        return;
                    }
                }
                throw new IllegalStateException("File extension of config file location '" + location + "' is not known to any PropertySourceLoader. If the location is meant to reference a directory, it must end in '/'");
            }
            Set<String> processed = new HashSet<>();
            for (PropertySourceLoader loader2 : this.propertySourceLoaders) {
                for (String fileExtension : loader2.getFileExtensions()) {
                    if (processed.add(fileExtension)) {
                        loadForFileExtension(loader2, location + name, "." + fileExtension, profile, filterFactory, consumer);
                    }
                }
            }
        }

        private boolean canLoadFileExtension(PropertySourceLoader loader, String name) {
            return Arrays.stream(loader.getFileExtensions()).anyMatch(fileExtension -> {
                return StringUtils.endsWithIgnoreCase(name, fileExtension);
            });
        }

        private void loadForFileExtension(PropertySourceLoader loader, String prefix, String fileExtension, Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
            DocumentFilter defaultFilter = filterFactory.getDocumentFilter(null);
            DocumentFilter profileFilter = filterFactory.getDocumentFilter(profile);
            if (profile != null) {
                String profileSpecificFile = prefix + "-" + profile + fileExtension;
                load(loader, profileSpecificFile, profile, defaultFilter, consumer);
                load(loader, profileSpecificFile, profile, profileFilter, consumer);
                for (Profile processedProfile : this.processedProfiles) {
                    if (processedProfile != null) {
                        String previouslyLoaded = prefix + "-" + processedProfile + fileExtension;
                        load(loader, previouslyLoaded, profile, profileFilter, consumer);
                    }
                }
            }
            load(loader, prefix + fileExtension, profile, profileFilter, consumer);
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x0030 A[Catch: Exception -> 0x01bd, TryCatch #0 {Exception -> 0x01bd, blocks: (B:8:0x0026, B:14:0x0056, B:16:0x0066, B:18:0x0072, B:20:0x008c, B:22:0x0096, B:24:0x009e, B:26:0x00a7, B:28:0x00b3, B:30:0x00cd, B:32:0x0100, B:34:0x010c, B:36:0x0126, B:37:0x0138, B:39:0x0142, B:41:0x015a, B:43:0x0179, B:45:0x0188, B:47:0x01a3, B:10:0x0030, B:12:0x003c), top: B:54:0x0026 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private void load(org.springframework.boot.env.PropertySourceLoader r7, java.lang.String r8, org.springframework.boot.context.config.ConfigFileApplicationListener.Profile r9, org.springframework.boot.context.config.ConfigFileApplicationListener.DocumentFilter r10, org.springframework.boot.context.config.ConfigFileApplicationListener.DocumentConsumer r11) {
            /*
                Method dump skipped, instructions count: 481
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.springframework.boot.context.config.ConfigFileApplicationListener.Loader.load(org.springframework.boot.env.PropertySourceLoader, java.lang.String, org.springframework.boot.context.config.ConfigFileApplicationListener$Profile, org.springframework.boot.context.config.ConfigFileApplicationListener$DocumentFilter, org.springframework.boot.context.config.ConfigFileApplicationListener$DocumentConsumer):void");
        }

        private boolean hasHiddenPathElement(Resource resource) throws IOException {
            String cleanPath = StringUtils.cleanPath(resource.getFile().getAbsolutePath());
            for (Path value : Paths.get(cleanPath, new String[0])) {
                if (value.toString().startsWith(CallerDataConverter.DEFAULT_RANGE_DELIMITER)) {
                    return true;
                }
            }
            return false;
        }

        private String getLocationName(String locationReference, Resource resource) {
            if (!locationReference.contains("*")) {
                return locationReference;
            }
            if (resource instanceof FileSystemResource) {
                return ((FileSystemResource) resource).getPath();
            }
            return resource.getDescription();
        }

        private Resource[] getResources(String locationReference) {
            try {
                if (isPatternLocation(locationReference)) {
                    return getResourcesFromPatternLocationReference(locationReference);
                }
                return new Resource[]{this.resourceLoader.getResource(locationReference)};
            } catch (Exception e) {
                return ConfigFileApplicationListener.EMPTY_RESOURCES;
            }
        }

        private boolean isPatternLocation(String location) {
            return location.contains("*");
        }

        private Resource[] getResourcesFromPatternLocationReference(String locationReference) throws IOException {
            String directoryPath = locationReference.substring(0, locationReference.indexOf(ResourceUtils.WAR_URL_SEPARATOR));
            Resource resource = this.resourceLoader.getResource(directoryPath);
            File[] files = resource.getFile().listFiles((v0) -> {
                return v0.isDirectory();
            });
            if (files == null) {
                return ConfigFileApplicationListener.EMPTY_RESOURCES;
            }
            String fileName = locationReference.substring(locationReference.lastIndexOf("/") + 1);
            Arrays.sort(files, ConfigFileApplicationListener.FILE_COMPARATOR);
            return (Resource[]) Arrays.stream(files).map(file -> {
                return file.listFiles((dir, name) -> {
                    return name.equals(fileName);
                });
            }).filter((v0) -> {
                return Objects.nonNull(v0);
            }).flatMap((v0) -> {
                return Arrays.stream(v0);
            }).map(FileSystemResource::new).toArray(x$0 -> {
                return new Resource[x$0];
            });
        }

        private void addIncludedProfiles(Set<Profile> includeProfiles) {
            LinkedList<Profile> existingProfiles = new LinkedList<>(this.profiles);
            this.profiles.clear();
            this.profiles.addAll(includeProfiles);
            this.profiles.removeAll(this.processedProfiles);
            this.profiles.addAll(existingProfiles);
        }

        private List<Document> loadDocuments(PropertySourceLoader loader, String name, Resource resource) throws IOException {
            DocumentsCacheKey cacheKey = new DocumentsCacheKey(loader, resource);
            List<Document> documents = this.loadDocumentsCache.get(cacheKey);
            if (documents == null) {
                List<PropertySource<?>> loaded = loader.load(name, resource);
                documents = asDocuments(loaded);
                this.loadDocumentsCache.put(cacheKey, documents);
            }
            return documents;
        }

        private List<Document> asDocuments(List<PropertySource<?>> loaded) {
            if (loaded == null) {
                return Collections.emptyList();
            }
            return (List) loaded.stream().map(propertySource -> {
                Binder binder = new Binder(ConfigurationPropertySources.from((PropertySource<?>) propertySource), this.placeholdersResolver);
                String[] profiles = (String[]) binder.bind("spring.profiles", ConfigFileApplicationListener.STRING_ARRAY).orElse(null);
                Set<Profile> activeProfiles = getProfiles(binder, "spring.profiles.active");
                Set<Profile> includeProfiles = getProfiles(binder, "spring.profiles.include");
                return new Document(propertySource, profiles, activeProfiles, includeProfiles);
            }).collect(Collectors.toList());
        }

        private StringBuilder getDescription(String prefix, String locationReference, Resource resource, Profile profile) {
            StringBuilder result = new StringBuilder(prefix);
            if (resource != null) {
                try {
                    String uri = resource.getURI().toASCIIString();
                    result.append("'");
                    result.append(uri);
                    result.append("' (");
                    result.append(locationReference);
                    result.append(")");
                } catch (IOException e) {
                    result.append(locationReference);
                }
            }
            if (profile != null) {
                result.append(" for profile ");
                result.append(profile);
            }
            return result;
        }

        private Set<Profile> getProfiles(Binder binder, String name) {
            return (Set) binder.bind(name, ConfigFileApplicationListener.STRING_ARRAY).map(this::asProfileSet).orElse(Collections.emptySet());
        }

        private Set<Profile> asProfileSet(String[] profileNames) {
            List<Profile> profiles = new ArrayList<>();
            for (String profileName : profileNames) {
                profiles.add(new Profile(profileName));
            }
            return new LinkedHashSet(profiles);
        }

        private void addProfileToEnvironment(String profile) {
            for (String activeProfile : this.environment.getActiveProfiles()) {
                if (activeProfile.equals(profile)) {
                    return;
                }
            }
            this.environment.addActiveProfile(profile);
        }

        private Set<String> getSearchLocations() {
            Set<String> locations = getSearchLocations(ConfigFileApplicationListener.CONFIG_ADDITIONAL_LOCATION_PROPERTY);
            if (this.environment.containsProperty(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY)) {
                locations.addAll(getSearchLocations(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY));
            } else {
                locations.addAll(asResolvedSet(ConfigFileApplicationListener.this.searchLocations, ConfigFileApplicationListener.DEFAULT_SEARCH_LOCATIONS));
            }
            return locations;
        }

        private Set<String> getSearchLocations(String propertyName) {
            Set<String> locations = new LinkedHashSet<>();
            if (this.environment.containsProperty(propertyName)) {
                for (String path : asResolvedSet(this.environment.getProperty(propertyName), null)) {
                    if (!path.contains(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)) {
                        path = StringUtils.cleanPath(path);
                        Assert.state(!path.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX), "Classpath wildcard patterns cannot be used as a search location");
                        validateWildcardLocation(path);
                        if (!ResourceUtils.isUrl(path)) {
                            path = ResourceUtils.FILE_URL_PREFIX + path;
                        }
                    }
                    locations.add(path);
                }
            }
            return locations;
        }

        private void validateWildcardLocation(String path) {
            if (path.contains("*")) {
                Assert.state(StringUtils.countOccurrencesOf(path, "*") == 1, (Supplier<String>) () -> {
                    return "Search location '" + path + "' cannot contain multiple wildcards";
                });
                String directoryPath = path.substring(0, path.lastIndexOf("/") + 1);
                Assert.state(directoryPath.endsWith(ResourceUtils.WAR_URL_SEPARATOR), (Supplier<String>) () -> {
                    return "Search location '" + path + "' must end with '*/'";
                });
            }
        }

        private Set<String> getSearchNames() {
            if (this.environment.containsProperty(ConfigFileApplicationListener.CONFIG_NAME_PROPERTY)) {
                String property = this.environment.getProperty(ConfigFileApplicationListener.CONFIG_NAME_PROPERTY);
                Set<String> names = asResolvedSet(property, null);
                names.forEach(this::assertValidConfigName);
                return names;
            }
            return asResolvedSet(ConfigFileApplicationListener.this.names, "application");
        }

        private Set<String> asResolvedSet(String value, String fallback) {
            List<String> list = Arrays.asList(StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(value != null ? this.environment.resolvePlaceholders(value) : fallback)));
            Collections.reverse(list);
            return new LinkedHashSet(list);
        }

        private void assertValidConfigName(String name) {
            Assert.state(!name.contains("*"), (Supplier<String>) () -> {
                return "Config name '" + name + "' cannot contain wildcards";
            });
        }

        private void addLoadedPropertySources() {
            MutablePropertySources destination = this.environment.getPropertySources();
            List<MutablePropertySources> loaded = new ArrayList<>(this.loaded.values());
            Collections.reverse(loaded);
            String lastAdded = null;
            Set<String> added = new HashSet<>();
            for (MutablePropertySources sources : loaded) {
                Iterator<PropertySource<?>> it = sources.iterator();
                while (it.hasNext()) {
                    PropertySource<?> source = it.next();
                    if (added.add(source.getName())) {
                        addLoadedPropertySource(destination, lastAdded, source);
                        lastAdded = source.getName();
                    }
                }
            }
        }

        private void addLoadedPropertySource(MutablePropertySources destination, String lastAdded, PropertySource<?> source) {
            if (lastAdded == null) {
                if (destination.contains(DefaultPropertiesPropertySource.NAME)) {
                    destination.addBefore(DefaultPropertiesPropertySource.NAME, source);
                    return;
                } else {
                    destination.addLast(source);
                    return;
                }
            }
            destination.addAfter(lastAdded, source);
        }

        private void applyActiveProfiles(PropertySource<?> defaultProperties) {
            List<String> activeProfiles = new ArrayList<>();
            if (defaultProperties != null) {
                Binder binder = new Binder(ConfigurationPropertySources.from(defaultProperties), new PropertySourcesPlaceholdersResolver(this.environment));
                activeProfiles.addAll(bindStringList(binder, "spring.profiles.include"));
                if (!this.activatedProfiles) {
                    activeProfiles.addAll(bindStringList(binder, "spring.profiles.active"));
                }
            }
            Stream<R> map = this.processedProfiles.stream().filter(this::isDefaultProfile).map((v0) -> {
                return v0.getName();
            });
            activeProfiles.getClass();
            map.forEach((v1) -> {
                r1.add(v1);
            });
            this.environment.setActiveProfiles((String[]) activeProfiles.toArray(new String[0]));
        }

        private boolean isDefaultProfile(Profile profile) {
            return (profile == null || profile.isDefaultProfile()) ? false : true;
        }

        private List<String> bindStringList(Binder binder, String property) {
            return (List) binder.bind(property, ConfigFileApplicationListener.STRING_LIST).orElse(Collections.emptyList());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$Profile.class */
    private static class Profile {
        private final String name;
        private final boolean defaultProfile;

        Profile(String name) {
            this(name, false);
        }

        Profile(String name, boolean defaultProfile) {
            Assert.notNull(name, "Name must not be null");
            this.name = name;
            this.defaultProfile = defaultProfile;
        }

        String getName() {
            return this.name;
        }

        boolean isDefaultProfile() {
            return this.defaultProfile;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            return ((Profile) obj).name.equals(this.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentsCacheKey.class */
    private static class DocumentsCacheKey {
        private final PropertySourceLoader loader;
        private final Resource resource;

        DocumentsCacheKey(PropertySourceLoader loader, Resource resource) {
            this.loader = loader;
            this.resource = resource;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DocumentsCacheKey other = (DocumentsCacheKey) obj;
            return this.loader.equals(other.loader) && this.resource.equals(other.resource);
        }

        public int hashCode() {
            return (this.loader.hashCode() * 31) + this.resource.hashCode();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$Document.class */
    private static class Document {
        private final PropertySource<?> propertySource;
        private String[] profiles;
        private final Set<Profile> activeProfiles;
        private final Set<Profile> includeProfiles;

        Document(PropertySource<?> propertySource, String[] profiles, Set<Profile> activeProfiles, Set<Profile> includeProfiles) {
            this.propertySource = propertySource;
            this.profiles = profiles;
            this.activeProfiles = activeProfiles;
            this.includeProfiles = includeProfiles;
        }

        PropertySource<?> getPropertySource() {
            return this.propertySource;
        }

        String[] getProfiles() {
            return this.profiles;
        }

        Set<Profile> getActiveProfiles() {
            return this.activeProfiles;
        }

        Set<Profile> getIncludeProfiles() {
            return this.includeProfiles;
        }

        public String toString() {
            return this.propertySource.toString();
        }
    }
}
