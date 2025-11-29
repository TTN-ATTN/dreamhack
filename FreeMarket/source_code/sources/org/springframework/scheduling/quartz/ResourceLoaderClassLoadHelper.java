package org.springframework.scheduling.quartz;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.spi.ClassLoadHelper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/ResourceLoaderClassLoadHelper.class */
public class ResourceLoaderClassLoadHelper implements ClassLoadHelper {
    protected static final Log logger = LogFactory.getLog((Class<?>) ResourceLoaderClassLoadHelper.class);

    @Nullable
    private ResourceLoader resourceLoader;

    public ResourceLoaderClassLoadHelper() {
    }

    public ResourceLoaderClassLoadHelper(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void initialize() {
        if (this.resourceLoader == null) {
            this.resourceLoader = SchedulerFactoryBean.getConfigTimeResourceLoader();
            if (this.resourceLoader == null) {
                this.resourceLoader = new DefaultResourceLoader();
            }
        }
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        return ClassUtils.forName(name, this.resourceLoader.getClassLoader());
    }

    public <T> Class<? extends T> loadClass(String str, Class<T> cls) throws ClassNotFoundException {
        return (Class<? extends T>) loadClass(str);
    }

    @Nullable
    public URL getResource(String name) {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        Resource resource = this.resourceLoader.getResource(name);
        if (resource.exists()) {
            try {
                return resource.getURL();
            } catch (IOException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not load " + resource);
                    return null;
                }
                return null;
            }
        }
        return getClassLoader().getResource(name);
    }

    @Nullable
    public InputStream getResourceAsStream(String name) {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        Resource resource = this.resourceLoader.getResource(name);
        if (resource.exists()) {
            try {
                return resource.getInputStream();
            } catch (IOException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not load " + resource);
                    return null;
                }
                return null;
            }
        }
        return getClassLoader().getResourceAsStream(name);
    }

    public ClassLoader getClassLoader() {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        ClassLoader classLoader = this.resourceLoader.getClassLoader();
        Assert.state(classLoader != null, "No ClassLoader");
        return classLoader;
    }
}
