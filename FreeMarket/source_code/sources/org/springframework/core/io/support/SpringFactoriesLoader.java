package org.springframework.core.io.support;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/support/SpringFactoriesLoader.class */
public final class SpringFactoriesLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    private static final Log logger = LogFactory.getLog((Class<?>) SpringFactoriesLoader.class);
    static final Map<ClassLoader, Map<String, List<String>>> cache = new ConcurrentReferenceHashMap();

    private SpringFactoriesLoader() {
    }

    public static <T> List<T> loadFactories(Class<T> factoryType, @Nullable ClassLoader classLoader) {
        Assert.notNull(factoryType, "'factoryType' must not be null");
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
        }
        List<String> factoryImplementationNames = loadFactoryNames(factoryType, classLoaderToUse);
        if (logger.isTraceEnabled()) {
            logger.trace("Loaded [" + factoryType.getName() + "] names: " + factoryImplementationNames);
        }
        ArrayList arrayList = new ArrayList(factoryImplementationNames.size());
        for (String factoryImplementationName : factoryImplementationNames) {
            arrayList.add(instantiateFactory(factoryImplementationName, factoryType, classLoaderToUse));
        }
        AnnotationAwareOrderComparator.sort(arrayList);
        return arrayList;
    }

    public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
        }
        String factoryTypeName = factoryType.getName();
        return loadSpringFactories(classLoaderToUse).getOrDefault(factoryTypeName, Collections.emptyList());
    }

    private static Map<String, List<String>> loadSpringFactories(ClassLoader classLoader) throws IOException {
        Map<String, List<String>> result = cache.get(classLoader);
        if (result != null) {
            return result;
        }
        Map<String, List<String>> result2 = new HashMap<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                UrlResource resource = new UrlResource(url);
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String factoryTypeName = ((String) entry.getKey()).trim();
                    String[] factoryImplementationNames = StringUtils.commaDelimitedListToStringArray((String) entry.getValue());
                    for (String factoryImplementationName : factoryImplementationNames) {
                        result2.computeIfAbsent(factoryTypeName, key -> {
                            return new ArrayList();
                        }).add(factoryImplementationName.trim());
                    }
                }
            }
            result2.replaceAll((factoryType, implementations) -> {
                return (List) implementations.stream().distinct().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
            });
            cache.put(classLoader, result2);
            return result2;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load factories from location [META-INF/spring.factories]", ex);
        }
    }

    private static <T> T instantiateFactory(String str, Class<T> cls, ClassLoader classLoader) {
        try {
            Class<?> clsForName = ClassUtils.forName(str, classLoader);
            if (!cls.isAssignableFrom(clsForName)) {
                throw new IllegalArgumentException("Class [" + str + "] is not assignable to factory type [" + cls.getName() + "]");
            }
            return (T) ReflectionUtils.accessibleConstructor(clsForName, new Class[0]).newInstance(new Object[0]);
        } catch (Throwable th) {
            throw new IllegalArgumentException("Unable to instantiate factory class [" + str + "] for factory type [" + cls.getName() + "]", th);
        }
    }
}
