package com.fasterxml.jackson.core.async;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-core-2.13.5.jar:com/fasterxml/jackson/core/async/ByteArrayFeeder.class */
public interface ByteArrayFeeder extends NonBlockingInputFeeder {
    void feedInput(byte[] bArr, int i, int i2) throws IOException;
}
