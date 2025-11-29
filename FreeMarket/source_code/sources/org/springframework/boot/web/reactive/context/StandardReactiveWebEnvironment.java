package org.springframework.boot.web.reactive.context;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/StandardReactiveWebEnvironment.class */
public class StandardReactiveWebEnvironment extends StandardEnvironment implements ConfigurableReactiveWebEnvironment {
    public StandardReactiveWebEnvironment() {
    }

    protected StandardReactiveWebEnvironment(MutablePropertySources propertySources) {
        super(propertySources);
    }
}
