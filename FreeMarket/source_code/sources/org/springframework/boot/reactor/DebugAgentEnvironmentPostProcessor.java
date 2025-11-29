package org.springframework.boot.reactor;

import java.lang.reflect.InvocationTargetException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/reactor/DebugAgentEnvironmentPostProcessor.class */
public class DebugAgentEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    private static final String REACTOR_DEBUGAGENT_CLASS = "reactor.tools.agent.ReactorDebugAgent";
    private static final String DEBUGAGENT_ENABLED_CONFIG_KEY = "spring.reactor.debug-agent.enabled";

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) throws IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {
        if (ClassUtils.isPresent(REACTOR_DEBUGAGENT_CLASS, null)) {
            Boolean agentEnabled = (Boolean) environment.getProperty(DEBUGAGENT_ENABLED_CONFIG_KEY, Boolean.class);
            if (agentEnabled != Boolean.FALSE) {
                try {
                    Class<?> debugAgent = Class.forName(REACTOR_DEBUGAGENT_CLASS);
                    debugAgent.getMethod("init", new Class[0]).invoke(null, new Object[0]);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to init Reactor's debug agent", ex);
                }
            }
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
