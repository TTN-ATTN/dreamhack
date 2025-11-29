package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/annotation/FullyQualifiedAnnotationBeanNameGenerator.class */
public class FullyQualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {
    public static final FullyQualifiedAnnotationBeanNameGenerator INSTANCE = new FullyQualifiedAnnotationBeanNameGenerator();

    @Override // org.springframework.context.annotation.AnnotationBeanNameGenerator
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        return beanClassName;
    }
}
