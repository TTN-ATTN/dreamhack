package org.apache.coyote.http2;

import org.apache.tomcat.util.net.SocketEvent;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/StreamRunnable.class */
class StreamRunnable implements Runnable {
    private final StreamProcessor processor;
    private final SocketEvent event;

    StreamRunnable(StreamProcessor processor, SocketEvent event) {
        this.processor = processor;
        this.event = event;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.processor.process(this.event);
    }
}
