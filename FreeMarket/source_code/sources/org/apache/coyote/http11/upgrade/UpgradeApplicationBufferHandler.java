package org.apache.coyote.http11.upgrade;

import java.nio.ByteBuffer;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http11/upgrade/UpgradeApplicationBufferHandler.class */
public class UpgradeApplicationBufferHandler implements ApplicationBufferHandler {
    private ByteBuffer byteBuffer;

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void expand(int size) {
    }
}
