package org.apache.logging.log4j.util;

import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/EnvironmentPropertySource.class */
public class EnvironmentPropertySource implements PropertySource {
    private static final String PREFIX = "LOG4J_";
    private static final int DEFAULT_PRIORITY = -100;

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return -100;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(final BiConsumer<String, String> action) {
        try {
            Map<String, String> getenv = System.getenv();
            for (Map.Entry<String, String> entry : getenv.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(PREFIX)) {
                    action.accept(key.substring(PREFIX.length()), entry.getValue());
                }
            }
        } catch (SecurityException e) {
            LowLevelLogUtil.logException("The system environment variables are not available to Log4j due to security restrictions: " + e, e);
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
        StringBuilder sb = new StringBuilder("LOG4J");
        for (CharSequence token : tokens) {
            sb.append('_');
            for (int i = 0; i < token.length(); i++) {
                sb.append(Character.toUpperCase(token.charAt(i)));
            }
        }
        return sb.toString();
    }
}
