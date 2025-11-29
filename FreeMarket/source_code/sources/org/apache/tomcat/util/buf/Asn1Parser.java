package org.apache.tomcat.util.buf;

import java.math.BigInteger;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/buf/Asn1Parser.class */
public class Asn1Parser {
    private static final StringManager sm = StringManager.getManager((Class<?>) Asn1Parser.class);
    private final byte[] source;
    private int pos = 0;

    public Asn1Parser(byte[] source) {
        this.source = source;
    }

    public boolean eof() {
        return this.pos == this.source.length;
    }

    public int peekTag() {
        return this.source[this.pos] & 255;
    }

    public void parseTag(int tag) {
        int value = next();
        if (value != tag) {
            throw new IllegalArgumentException(sm.getString("asn1Parser.tagMismatch", Integer.valueOf(tag), Integer.valueOf(value)));
        }
    }

    public void parseFullLength() {
        int len = parseLength();
        if (len + this.pos != this.source.length) {
            throw new IllegalArgumentException(sm.getString("asn1Parser.lengthInvalid", Integer.valueOf(len), Integer.valueOf(this.source.length - this.pos)));
        }
    }

    public int parseLength() {
        int len = next();
        if (len > 127) {
            int bytes = len - 128;
            len = 0;
            for (int i = 0; i < bytes; i++) {
                len = (len << 8) + next();
            }
        }
        return len;
    }

    public BigInteger parseInt() {
        parseTag(2);
        int len = parseLength();
        byte[] val = new byte[len];
        System.arraycopy(this.source, this.pos, val, 0, len);
        this.pos += len;
        return new BigInteger(val);
    }

    public void parseBytes(byte[] dest) {
        System.arraycopy(this.source, this.pos, dest, 0, dest.length);
        this.pos += dest.length;
    }

    private int next() {
        byte[] bArr = this.source;
        int i = this.pos;
        this.pos = i + 1;
        return bArr[i] & 255;
    }
}
