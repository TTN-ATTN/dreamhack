package org.springframework.format;

import java.text.ParseException;
import java.util.Locale;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/format/Parser.class */
public interface Parser<T> {
    T parse(String text, Locale locale) throws ParseException;
}
