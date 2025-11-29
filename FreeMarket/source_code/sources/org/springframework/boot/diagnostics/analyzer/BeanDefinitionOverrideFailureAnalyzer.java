package org.springframework.boot.diagnostics.analyzer;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/diagnostics/analyzer/BeanDefinitionOverrideFailureAnalyzer.class */
class BeanDefinitionOverrideFailureAnalyzer extends AbstractFailureAnalyzer<BeanDefinitionOverrideException> {
    private static final String ACTION = "Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true";

    BeanDefinitionOverrideFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, BeanDefinitionOverrideException cause) {
        return new FailureAnalysis(getDescription(cause), ACTION, cause);
    }

    private String getDescription(BeanDefinitionOverrideException ex) {
        StringWriter description = new StringWriter();
        PrintWriter printer = new PrintWriter(description);
        printer.printf("The bean '%s'", ex.getBeanName());
        if (ex.getBeanDefinition().getResourceDescription() != null) {
            printer.printf(", defined in %s,", ex.getBeanDefinition().getResourceDescription());
        }
        printer.printf(" could not be registered. A bean with that name has already been defined ", new Object[0]);
        if (ex.getExistingDefinition().getResourceDescription() != null) {
            printer.printf("in %s ", ex.getExistingDefinition().getResourceDescription());
        }
        printer.printf("and overriding is disabled.", new Object[0]);
        return description.toString();
    }
}
