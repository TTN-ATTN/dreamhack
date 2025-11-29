package org.apache.tomcat.util.buf;

import java.util.Locale;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/buf/EncodedSolidusHandling.class */
public enum EncodedSolidusHandling {
    DECODE("decode"),
    REJECT("reject"),
    PASS_THROUGH("passthrough");

    private static final StringManager sm = StringManager.getManager((Class<?>) EncodedSolidusHandling.class);
    private final String value;

    EncodedSolidusHandling(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static EncodedSolidusHandling fromString(String from) {
        String trimmedLower = from.trim().toLowerCase(Locale.ENGLISH);
        for (EncodedSolidusHandling value : values()) {
            if (value.getValue().equals(trimmedLower)) {
                return value;
            }
        }
        throw new IllegalStateException(sm.getString("encodedSolidusHandling.invalid", from));
    }
}
