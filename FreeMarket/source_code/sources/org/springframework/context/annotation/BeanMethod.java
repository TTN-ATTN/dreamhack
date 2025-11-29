package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/annotation/BeanMethod.class */
final class BeanMethod extends ConfigurationMethod {
    BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        super(metadata, configurationClass);
    }

    @Override // org.springframework.context.annotation.ConfigurationMethod
    public void validate(ProblemReporter problemReporter) {
        if (!getMetadata().isStatic() && this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName()) && !getMetadata().isOverridable()) {
            problemReporter.error(new NonOverridableMethodError());
        }
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || ((obj instanceof BeanMethod) && this.metadata.equals(((BeanMethod) obj).metadata));
    }

    public int hashCode() {
        return this.metadata.hashCode();
    }

    @Override // org.springframework.context.annotation.ConfigurationMethod
    public String toString() {
        return "BeanMethod: " + this.metadata;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/annotation/BeanMethod$NonOverridableMethodError.class */
    private class NonOverridableMethodError extends Problem {
        NonOverridableMethodError() {
            super(String.format("@Bean method '%s' must not be private or final; change the method's modifiers to continue", BeanMethod.this.getMetadata().getMethodName()), BeanMethod.this.getResourceLocation());
        }
    }
}
