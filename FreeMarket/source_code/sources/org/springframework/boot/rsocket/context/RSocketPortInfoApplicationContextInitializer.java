package org.springframework.boot.rsocket.context;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/context/RSocketPortInfoApplicationContextInitializer.class */
public class RSocketPortInfoApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(new Listener(applicationContext));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/context/RSocketPortInfoApplicationContextInitializer$Listener.class */
    private static class Listener implements ApplicationListener<RSocketServerInitializedEvent> {
        private static final String PROPERTY_NAME = "local.rsocket.server.port";
        private final ConfigurableApplicationContext applicationContext;

        Listener(ConfigurableApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(RSocketServerInitializedEvent event) {
            if (event.getServer().address() != null) {
                setPortProperty(this.applicationContext, event.getServer().address().getPort());
            }
        }

        private void setPortProperty(ApplicationContext context, int port) {
            if (context instanceof ConfigurableApplicationContext) {
                setPortProperty(((ConfigurableApplicationContext) context).getEnvironment(), port);
            }
            if (context.getParent() != null) {
                setPortProperty(context.getParent(), port);
            }
        }

        private void setPortProperty(ConfigurableEnvironment environment, int port) {
            MutablePropertySources sources = environment.getPropertySources();
            PropertySource<?> source = sources.get("server.ports");
            if (source == null) {
                source = new MapPropertySource("server.ports", new HashMap());
                sources.addFirst(source);
            }
            setPortProperty(port, source);
        }

        private void setPortProperty(int port, PropertySource<?> source) {
            ((Map) source.getSource()).put(PROPERTY_NAME, Integer.valueOf(port));
        }
    }
}
