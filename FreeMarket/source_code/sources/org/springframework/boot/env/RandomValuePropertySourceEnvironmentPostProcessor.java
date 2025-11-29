package org.springframework.boot.env;

import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/RandomValuePropertySourceEnvironmentPostProcessor.class */
public class RandomValuePropertySourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final int ORDER = -2147483647;
    private final Log logger;

    public RandomValuePropertySourceEnvironmentPostProcessor(Log logger) {
        this.logger = logger;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return ORDER;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        RandomValuePropertySource.addToEnvironment(environment, this.logger);
    }
}
