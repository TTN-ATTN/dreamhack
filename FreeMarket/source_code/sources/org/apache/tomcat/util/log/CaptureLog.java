package org.apache.tomcat.util.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/log/CaptureLog.class */
class CaptureLog {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintStream ps = new PrintStream(this.baos);

    protected CaptureLog() {
    }

    protected PrintStream getStream() {
        return this.ps;
    }

    protected void reset() {
        this.baos.reset();
    }

    protected String getCapture() {
        return this.baos.toString();
    }
}
