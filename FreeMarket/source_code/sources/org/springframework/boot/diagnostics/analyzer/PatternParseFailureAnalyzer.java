package org.springframework.boot.diagnostics.analyzer;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.web.util.pattern.PatternParseException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/diagnostics/analyzer/PatternParseFailureAnalyzer.class */
class PatternParseFailureAnalyzer extends AbstractFailureAnalyzer<PatternParseException> {
    PatternParseFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, PatternParseException cause) {
        return new FailureAnalysis("Invalid mapping pattern detected: " + cause.toDetailedString(), "Fix this pattern in your application or switch to the legacy parser implementation with 'spring.mvc.pathmatch.matching-strategy=ant_path_matcher'.", cause);
    }
}
