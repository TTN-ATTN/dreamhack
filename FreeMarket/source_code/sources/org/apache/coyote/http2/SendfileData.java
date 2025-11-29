package org.apache.coyote.http2;

import java.nio.MappedByteBuffer;
import java.nio.file.Path;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/SendfileData.class */
class SendfileData {
    Path path;
    Stream stream;
    MappedByteBuffer mappedBuffer;
    long left;
    int streamReservation;
    int connectionReservation;
    long pos;
    long end;

    SendfileData() {
    }
}
