package org.springframework.format;

import java.util.Locale;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/format/Printer.class */
public interface Printer<T> {
    String print(T object, Locale locale);
}
