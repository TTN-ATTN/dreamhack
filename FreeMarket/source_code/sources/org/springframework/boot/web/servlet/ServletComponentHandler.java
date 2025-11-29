package org.springframework.boot.web.servlet;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/ServletComponentHandler.class */
abstract class ServletComponentHandler {
    private final Class<? extends Annotation> annotationType;
    private final TypeFilter typeFilter;

    protected abstract void doHandle(Map<String, Object> attributes, AnnotatedBeanDefinition beanDefinition, BeanDefinitionRegistry registry);

    protected ServletComponentHandler(Class<? extends Annotation> annotationType) {
        this.typeFilter = new AnnotationTypeFilter(annotationType);
        this.annotationType = annotationType;
    }

    TypeFilter getTypeFilter() {
        return this.typeFilter;
    }

    protected String[] extractUrlPatterns(Map<String, Object> attributes) {
        String[] value = (String[]) attributes.get("value");
        String[] urlPatterns = (String[]) attributes.get("urlPatterns");
        if (urlPatterns.length > 0) {
            Assert.state(value.length == 0, "The urlPatterns and value attributes are mutually exclusive.");
            return urlPatterns;
        }
        return value;
    }

    protected final Map<String, String> extractInitParameters(Map<String, Object> attributes) {
        Map<String, String> initParameters = new HashMap<>();
        for (AnnotationAttributes initParam : (AnnotationAttributes[]) attributes.get("initParams")) {
            String name = (String) initParam.get("name");
            String value = (String) initParam.get("value");
            initParameters.put(name, value);
        }
        return initParameters;
    }

    void handle(AnnotatedBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = beanDefinition.getMetadata().getAnnotationAttributes(this.annotationType.getName());
        if (attributes != null) {
            doHandle(attributes, beanDefinition, registry);
        }
    }
}
