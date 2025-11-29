package org.springframework.boot.diagnostics;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/diagnostics/FailureAnalysisReporter.class */
public interface FailureAnalysisReporter {
    void report(FailureAnalysis analysis);
}
