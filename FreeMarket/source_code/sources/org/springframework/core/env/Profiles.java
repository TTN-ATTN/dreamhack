package org.springframework.core.env;

import java.util.function.Predicate;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/Profiles.class */
public interface Profiles {
    boolean matches(Predicate<String> activeProfiles);

    static Profiles of(String... profiles) {
        return ProfilesParser.parse(profiles);
    }
}
