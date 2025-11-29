package org.apache.logging.log4j.util;

import java.util.Locale;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/EnglishEnums.class */
public final class EnglishEnums {
    private EnglishEnums() {
    }

    public static <T extends Enum<T>> T valueOf(Class<T> cls, String str) {
        return (T) valueOf(cls, str, null);
    }

    public static <T extends Enum<T>> T valueOf(Class<T> cls, String str, T t) {
        return str == null ? t : (T) Enum.valueOf(cls, str.toUpperCase(Locale.ENGLISH));
    }
}
