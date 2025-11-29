package org.apache.tomcat.util.http;

import org.apache.tomcat.util.buf.MessageBytes;

/* compiled from: MimeHeaders.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/MimeHeaderField.class */
class MimeHeaderField {
    private final MessageBytes nameB = MessageBytes.newInstance();
    private final MessageBytes valueB = MessageBytes.newInstance();

    MimeHeaderField() {
    }

    public void recycle() {
        this.nameB.recycle();
        this.valueB.recycle();
    }

    public MessageBytes getName() {
        return this.nameB;
    }

    public MessageBytes getValue() {
        return this.valueB;
    }

    public String toString() {
        return this.nameB + ": " + this.valueB;
    }
}
