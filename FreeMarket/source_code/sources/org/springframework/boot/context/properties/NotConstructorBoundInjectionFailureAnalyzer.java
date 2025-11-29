package org.springframework.boot.context.properties;

import java.lang.reflect.Constructor;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.context.properties.ConfigurationPropertiesBean;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/NotConstructorBoundInjectionFailureAnalyzer.class */
class NotConstructorBoundInjectionFailureAnalyzer extends AbstractInjectionFailureAnalyzer<NoSuchBeanDefinitionException> implements Ordered {
    NotConstructorBoundInjectionFailureAnalyzer() {
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoSuchBeanDefinitionException cause, String description) {
        InjectionPoint injectionPoint = findInjectionPoint(rootFailure);
        if (isConstructorBindingConfigurationProperties(injectionPoint)) {
            String simpleName = injectionPoint.getMember().getDeclaringClass().getSimpleName();
            String action = String.format("Update your configuration so that " + simpleName + " is defined via @" + ConfigurationPropertiesScan.class.getSimpleName() + " or @" + EnableConfigurationProperties.class.getSimpleName() + ".", simpleName);
            return new FailureAnalysis(simpleName + " is annotated with @" + ConstructorBinding.class.getSimpleName() + " but it is defined as a regular bean which caused dependency injection to fail.", action, cause);
        }
        return null;
    }

    private boolean isConstructorBindingConfigurationProperties(InjectionPoint injectionPoint) {
        if (injectionPoint != null && (injectionPoint.getMember() instanceof Constructor)) {
            Constructor<?> constructor = (Constructor) injectionPoint.getMember();
            Class<?> declaringClass = constructor.getDeclaringClass();
            MergedAnnotation<ConfigurationProperties> configurationProperties = MergedAnnotations.from(declaringClass).get(ConfigurationProperties.class);
            return configurationProperties.isPresent() && ConfigurationPropertiesBean.BindMethod.forType(constructor.getDeclaringClass()) == ConfigurationPropertiesBean.BindMethod.VALUE_OBJECT;
        }
        return false;
    }

    private InjectionPoint findInjectionPoint(Throwable failure) {
        UnsatisfiedDependencyException unsatisfiedDependencyException = (UnsatisfiedDependencyException) findCause(failure, UnsatisfiedDependencyException.class);
        if (unsatisfiedDependencyException == null) {
            return null;
        }
        return unsatisfiedDependencyException.getInjectionPoint();
    }
}
