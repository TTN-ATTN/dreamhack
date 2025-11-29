package org.springframework.boot.loader.jar;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/Bytes.class */
final class Bytes {
    private Bytes() {
    }

    static long littleEndianValue(byte[] bytes, int offset, int length) {
        long value = 0;
        for (int i = length - 1; i >= 0; i--) {
            value = (value << 8) | (bytes[offset + i] & 255);
        }
        return value;
    }
}
