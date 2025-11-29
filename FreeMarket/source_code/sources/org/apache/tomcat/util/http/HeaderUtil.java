package org.apache.tomcat.util.http;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/HeaderUtil.class */
public class HeaderUtil {
    public static String toPrintableString(byte[] bytes, int offset, int len) {
        StringBuilder result = new StringBuilder();
        for (int i = offset; i < offset + len; i++) {
            char c = (char) (bytes[i] & 255);
            if (c < ' ' || c > '~') {
                result.append("0x");
                result.append(Character.forDigit((c >> 4) & 15, 16));
                result.append(Character.forDigit(c & 15, 16));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private HeaderUtil() {
    }
}
