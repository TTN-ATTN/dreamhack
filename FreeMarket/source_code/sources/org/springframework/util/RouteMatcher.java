package org.springframework.util;

import java.util.Comparator;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/RouteMatcher.class */
public interface RouteMatcher {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/RouteMatcher$Route.class */
    public interface Route {
        String value();
    }

    Route parseRoute(String routeValue);

    boolean isPattern(String route);

    String combine(String pattern1, String pattern2);

    boolean match(String pattern, Route route);

    @Nullable
    Map<String, String> matchAndExtract(String pattern, Route route);

    Comparator<String> getPatternComparator(Route route);
}
