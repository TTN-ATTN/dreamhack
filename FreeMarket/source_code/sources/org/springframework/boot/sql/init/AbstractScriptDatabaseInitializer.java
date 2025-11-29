package org.springframework.boot.sql.init;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.CollectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/sql/init/AbstractScriptDatabaseInitializer.class */
public abstract class AbstractScriptDatabaseInitializer implements ResourceLoaderAware, InitializingBean {
    private static final String OPTIONAL_LOCATION_PREFIX = "optional:";
    private final DatabaseInitializationSettings settings;
    private volatile ResourceLoader resourceLoader;

    protected abstract void runScripts(List<Resource> resources, boolean continueOnError, String separator, Charset encoding);

    protected AbstractScriptDatabaseInitializer(DatabaseInitializationSettings settings) {
        this.settings = settings;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        initializeDatabase();
    }

    public boolean initializeDatabase() {
        ScriptLocationResolver locationResolver = new ScriptLocationResolver(this.resourceLoader);
        boolean initialized = applySchemaScripts(locationResolver);
        return applyDataScripts(locationResolver) || initialized;
    }

    private boolean isEnabled() {
        if (this.settings.getMode() == DatabaseInitializationMode.NEVER) {
            return false;
        }
        return this.settings.getMode() == DatabaseInitializationMode.ALWAYS || isEmbeddedDatabase();
    }

    protected boolean isEmbeddedDatabase() {
        throw new IllegalStateException("Database initialization mode is '" + this.settings.getMode() + "' and database type is unknown");
    }

    private boolean applySchemaScripts(ScriptLocationResolver locationResolver) {
        return applyScripts(this.settings.getSchemaLocations(), "schema", locationResolver);
    }

    private boolean applyDataScripts(ScriptLocationResolver locationResolver) {
        return applyScripts(this.settings.getDataLocations(), "data", locationResolver);
    }

    private boolean applyScripts(List<String> locations, String type, ScriptLocationResolver locationResolver) {
        List<Resource> scripts = getScripts(locations, type, locationResolver);
        if (!scripts.isEmpty() && isEnabled()) {
            runScripts(scripts);
            return true;
        }
        return false;
    }

    private List<Resource> getScripts(List<String> locations, String type, ScriptLocationResolver locationResolver) {
        if (CollectionUtils.isEmpty(locations)) {
            return Collections.emptyList();
        }
        List<Resource> resources = new ArrayList<>();
        for (String location : locations) {
            boolean optional = location.startsWith("optional:");
            if (optional) {
                location = location.substring("optional:".length());
            }
            for (Resource resource : doGetResources(location, locationResolver)) {
                if (resource.exists()) {
                    resources.add(resource);
                } else if (!optional) {
                    throw new IllegalStateException("No " + type + " scripts found at location '" + location + "'");
                }
            }
        }
        return resources;
    }

    private List<Resource> doGetResources(String location, ScriptLocationResolver locationResolver) {
        try {
            return locationResolver.resolve(location);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load resources from " + location, ex);
        }
    }

    private void runScripts(List<Resource> resources) {
        runScripts(resources, this.settings.isContinueOnError(), this.settings.getSeparator(), this.settings.getEncoding());
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/sql/init/AbstractScriptDatabaseInitializer$ScriptLocationResolver.class */
    private static class ScriptLocationResolver {
        private final ResourcePatternResolver resourcePatternResolver;

        ScriptLocationResolver(ResourceLoader resourceLoader) {
            this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public List<Resource> resolve(String location) throws IOException {
            List<Resource> resources = new ArrayList<>(Arrays.asList(this.resourcePatternResolver.getResources(location)));
            resources.sort((r1, r2) -> {
                try {
                    return r1.getURL().toString().compareTo(r2.getURL().toString());
                } catch (IOException e) {
                    return 0;
                }
            });
            return resources;
        }
    }
}
