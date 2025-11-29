package org.springframework.boot.env;

import java.io.IOException;
import java.util.List;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/PropertySourceLoader.class */
public interface PropertySourceLoader {
    String[] getFileExtensions();

    List<PropertySource<?>> load(String name, Resource resource) throws IOException;
}
