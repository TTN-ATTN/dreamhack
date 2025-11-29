package org.springframework.boot;

import groovy.lang.Closure;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.SpringProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GroovyWebApplicationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/BeanDefinitionLoader.class */
class BeanDefinitionLoader {
    private static final boolean XML_ENABLED;
    private static final Pattern GROOVY_CLOSURE_PATTERN;
    private final Object[] sources;
    private final AnnotatedBeanDefinitionReader annotatedReader;
    private final AbstractBeanDefinitionReader xmlReader;
    private final BeanDefinitionReader groovyReader;
    private final ClassPathBeanDefinitionScanner scanner;
    private ResourceLoader resourceLoader;

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/BeanDefinitionLoader$GroovyBeanDefinitionSource.class */
    protected interface GroovyBeanDefinitionSource {
        Closure<?> getBeans();
    }

    static {
        XML_ENABLED = !SpringProperties.getFlag("spring.xml.ignore");
        GROOVY_CLOSURE_PATTERN = Pattern.compile(".*\\$_.*closure.*");
    }

    BeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notEmpty(sources, "Sources must not be empty");
        this.sources = sources;
        this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
        this.xmlReader = XML_ENABLED ? new XmlBeanDefinitionReader(registry) : null;
        this.groovyReader = isGroovyPresent() ? new GroovyBeanDefinitionReader(registry) : null;
        this.scanner = new ClassPathBeanDefinitionScanner(registry);
        this.scanner.addExcludeFilter(new ClassExcludeFilter(sources));
    }

    void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.annotatedReader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        if (this.xmlReader != null) {
            this.xmlReader.setBeanNameGenerator(beanNameGenerator);
        }
    }

    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.scanner.setResourceLoader(resourceLoader);
        if (this.xmlReader != null) {
            this.xmlReader.setResourceLoader(resourceLoader);
        }
    }

    void setEnvironment(ConfigurableEnvironment environment) {
        this.annotatedReader.setEnvironment(environment);
        this.scanner.setEnvironment(environment);
        if (this.xmlReader != null) {
            this.xmlReader.setEnvironment(environment);
        }
    }

    void load() {
        for (Object source : this.sources) {
            load(source);
        }
    }

    private void load(Object source) throws BeanDefinitionStoreException {
        Assert.notNull(source, "Source must not be null");
        if (source instanceof Class) {
            load((Class<?>) source);
            return;
        }
        if (source instanceof Resource) {
            load((Resource) source);
        } else if (source instanceof Package) {
            load((Package) source);
        } else {
            if (source instanceof CharSequence) {
                load((CharSequence) source);
                return;
            }
            throw new IllegalArgumentException("Invalid source type " + source.getClass());
        }
    }

    private void load(Class<?> source) throws BeanDefinitionStoreException {
        if (isGroovyPresent() && GroovyBeanDefinitionSource.class.isAssignableFrom(source)) {
            GroovyBeanDefinitionSource loader = (GroovyBeanDefinitionSource) BeanUtils.instantiateClass(source, GroovyBeanDefinitionSource.class);
            ((GroovyBeanDefinitionReader) this.groovyReader).beans(loader.getBeans());
        }
        if (isEligible(source)) {
            this.annotatedReader.register(source);
        }
    }

    private void load(Resource source) throws BeanDefinitionStoreException {
        if (source.getFilename().endsWith(GroovyWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
            if (this.groovyReader == null) {
                throw new BeanDefinitionStoreException("Cannot load Groovy beans without Groovy on classpath");
            }
            this.groovyReader.loadBeanDefinitions(source);
        } else {
            if (this.xmlReader == null) {
                throw new BeanDefinitionStoreException("Cannot load XML bean definitions when XML support is disabled");
            }
            this.xmlReader.loadBeanDefinitions(source);
        }
    }

    private void load(Package source) throws BeanDefinitionStoreException {
        this.scanner.scan(source.getName());
    }

    private void load(CharSequence source) throws BeanDefinitionStoreException {
        String resolvedSource = this.scanner.getEnvironment().resolvePlaceholders(source.toString());
        try {
            load(ClassUtils.forName(resolvedSource, null));
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            if (loadAsResources(resolvedSource)) {
                return;
            }
            Package packageResource = findPackage(resolvedSource);
            if (packageResource != null) {
                load(packageResource);
                return;
            }
            throw new IllegalArgumentException("Invalid source '" + resolvedSource + "'");
        }
    }

    private boolean loadAsResources(String resolvedSource) throws BeanDefinitionStoreException {
        boolean foundCandidate = false;
        Resource[] resources = findResources(resolvedSource);
        for (Resource resource : resources) {
            if (isLoadCandidate(resource)) {
                foundCandidate = true;
                load(resource);
            }
        }
        return foundCandidate;
    }

    private boolean isGroovyPresent() {
        return ClassUtils.isPresent("groovy.lang.MetaClass", null);
    }

    private Resource[] findResources(String source) {
        ResourceLoader loader = this.resourceLoader != null ? this.resourceLoader : new PathMatchingResourcePatternResolver();
        try {
            if (loader instanceof ResourcePatternResolver) {
                return ((ResourcePatternResolver) loader).getResources(source);
            }
            return new Resource[]{loader.getResource(source)};
        } catch (IOException e) {
            throw new IllegalStateException("Error reading source '" + source + "'");
        }
    }

    private boolean isLoadCandidate(Resource resource) {
        if (resource == null || !resource.exists()) {
            return false;
        }
        if (resource instanceof ClassPathResource) {
            String path = ((ClassPathResource) resource).getPath();
            if (path.indexOf(46) == -1) {
                try {
                    return Package.getPackage(path) == null;
                } catch (Exception e) {
                    return true;
                }
            }
            return true;
        }
        return true;
    }

    private Package findPackage(CharSequence source) {
        Package pkg = Package.getPackage(source.toString());
        if (pkg != null) {
            return pkg;
        }
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
            Resource[] resources = resolver.getResources(ClassUtils.convertClassNameToResourcePath(source.toString()) + "/*.class");
            if (0 < resources.length) {
                Resource resource = resources[0];
                String className = StringUtils.stripFilenameExtension(resource.getFilename());
                load(Class.forName(source.toString() + "." + className));
            }
        } catch (Exception e) {
        }
        return Package.getPackage(source.toString());
    }

    private boolean isEligible(Class<?> type) {
        return (type.isAnonymousClass() || isGroovyClosure(type) || hasNoConstructors(type)) ? false : true;
    }

    private boolean isGroovyClosure(Class<?> type) {
        return GROOVY_CLOSURE_PATTERN.matcher(type.getName()).matches();
    }

    private boolean hasNoConstructors(Class<?> type) throws SecurityException {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        return ObjectUtils.isEmpty((Object[]) constructors);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/BeanDefinitionLoader$ClassExcludeFilter.class */
    private static class ClassExcludeFilter extends AbstractTypeHierarchyTraversingFilter {
        private final Set<String> classNames;

        ClassExcludeFilter(Object... sources) {
            super(false, false);
            this.classNames = new HashSet();
            for (Object source : sources) {
                if (source instanceof Class) {
                    this.classNames.add(((Class) source).getName());
                }
            }
        }

        @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
        protected boolean matchClassName(String className) {
            return this.classNames.contains(className);
        }
    }
}
