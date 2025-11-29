package org.springframework.boot.autoconfigure.security.servlet;

import org.springframework.security.web.util.matcher.RequestMatcher;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/security/servlet/RequestMatcherProvider.class */
public interface RequestMatcherProvider {
    RequestMatcher getRequestMatcher(String pattern);
}
