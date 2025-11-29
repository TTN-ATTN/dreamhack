package org.springframework.validation;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/validation/MessageCodeFormatter.class */
public interface MessageCodeFormatter {
    String format(String errorCode, @Nullable String objectName, @Nullable String field);
}
