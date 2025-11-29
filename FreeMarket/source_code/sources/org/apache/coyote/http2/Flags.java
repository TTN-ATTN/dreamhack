package org.apache.coyote.http2;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Flags.class */
class Flags {
    private Flags() {
    }

    static boolean isEndOfStream(int flags) {
        return (flags & 1) != 0;
    }

    static boolean isAck(int flags) {
        return (flags & 1) != 0;
    }

    static boolean isEndOfHeaders(int flags) {
        return (flags & 4) != 0;
    }

    static boolean hasPadding(int flags) {
        return (flags & 8) != 0;
    }

    static boolean hasPriority(int flags) {
        return (flags & 32) != 0;
    }
}
