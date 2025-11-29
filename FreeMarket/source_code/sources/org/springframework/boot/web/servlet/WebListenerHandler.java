package org.springframework.boot.web.servlet;

import java.util.Map;
import javax.servlet.annotation.WebListener;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/WebListenerHandler.class */
class WebListenerHandler extends ServletComponentHandler {
    WebListenerHandler() {
        super(WebListener.class);
    }

    @Override // org.springframework.boot.web.servlet.ServletComponentHandler
    protected void doHandle(Map<String, Object> attributes, AnnotatedBeanDefinition beanDefinition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) ServletComponentWebListenerRegistrar.class);
        builder.addConstructorArgValue(beanDefinition.getBeanClassName());
        registry.registerBeanDefinition(beanDefinition.getBeanClassName() + "Registrar", builder.getBeanDefinition());
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/WebListenerHandler$ServletComponentWebListenerRegistrar.class */
    static class ServletComponentWebListenerRegistrar implements WebListenerRegistrar {
        private final String listenerClassName;

        ServletComponentWebListenerRegistrar(String listenerClassName) {
            this.listenerClassName = listenerClassName;
        }

        @Override // org.springframework.boot.web.servlet.WebListenerRegistrar
        public void register(WebListenerRegistry registry) {
            registry.addWebListeners(this.listenerClassName);
        }
    }
}
