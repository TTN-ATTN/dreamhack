package org.springframework.util;

import java.util.Comparator;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/PathMatcher.class */
public interface PathMatcher {
    boolean isPattern(String path);

    boolean match(String pattern, String path);

    boolean matchStart(String pattern, String path);

    String extractPathWithinPattern(String pattern, String path);

    Map<String, String> extractUriTemplateVariables(String pattern, String path);

    Comparator<String> getPatternComparator(String path);

    String combine(String pattern1, String pattern2);
}
