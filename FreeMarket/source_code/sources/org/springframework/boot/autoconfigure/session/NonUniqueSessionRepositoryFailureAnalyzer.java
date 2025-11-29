package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/NonUniqueSessionRepositoryFailureAnalyzer.class */
class NonUniqueSessionRepositoryFailureAnalyzer extends AbstractFailureAnalyzer<NonUniqueSessionRepositoryException> {
    NonUniqueSessionRepositoryFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NonUniqueSessionRepositoryException cause) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("Multiple Spring Session store implementations are available on the classpath:%n", new Object[0]));
        for (Class<?> candidate : cause.getAvailableCandidates()) {
            message.append(String.format("    - %s%n", candidate.getName()));
        }
        return new FailureAnalysis(message.toString(), String.format("Consider any of the following:%n", new Object[0]) + String.format("    - Define the 'spring.session.store-type' property to the store you want to use%n", new Object[0]) + String.format("    - Review your classpath and remove the unwanted store implementation(s)%n", new Object[0]), cause);
    }
}
