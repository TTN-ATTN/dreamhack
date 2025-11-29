package ch.qos.logback.core.util;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/util/ContentTypeUtil.class */
public class ContentTypeUtil {
    public static boolean isTextual(String contextType) {
        if (contextType == null) {
            return false;
        }
        return contextType.startsWith("text");
    }

    public static String getSubType(String contextType) {
        int index;
        int subTypeStartIndex;
        if (contextType != null && (index = contextType.indexOf(47)) != -1 && (subTypeStartIndex = index + 1) < contextType.length()) {
            return contextType.substring(subTypeStartIndex);
        }
        return null;
    }
}
