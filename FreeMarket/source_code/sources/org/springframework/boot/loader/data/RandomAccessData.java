package org.springframework.boot.loader.data;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/data/RandomAccessData.class */
public interface RandomAccessData {
    InputStream getInputStream() throws IOException;

    RandomAccessData getSubsection(long offset, long length);

    byte[] read() throws IOException;

    byte[] read(long offset, long length) throws IOException;

    long getSize();
}
