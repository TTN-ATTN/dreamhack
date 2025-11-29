package org.springframework.core.io.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.function.IntPredicate;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/buffer/DataBuffer.class */
public interface DataBuffer {
    DataBufferFactory factory();

    int indexOf(IntPredicate predicate, int fromIndex);

    int lastIndexOf(IntPredicate predicate, int fromIndex);

    int readableByteCount();

    int writableByteCount();

    int capacity();

    DataBuffer capacity(int capacity);

    int readPosition();

    DataBuffer readPosition(int readPosition);

    int writePosition();

    DataBuffer writePosition(int writePosition);

    byte getByte(int index);

    byte read();

    DataBuffer read(byte[] destination);

    DataBuffer read(byte[] destination, int offset, int length);

    DataBuffer write(byte b);

    DataBuffer write(byte[] source);

    DataBuffer write(byte[] source, int offset, int length);

    DataBuffer write(DataBuffer... buffers);

    DataBuffer write(ByteBuffer... buffers);

    DataBuffer slice(int index, int length);

    ByteBuffer asByteBuffer();

    ByteBuffer asByteBuffer(int index, int length);

    InputStream asInputStream();

    InputStream asInputStream(boolean releaseOnClose);

    OutputStream asOutputStream();

    String toString(int index, int length, Charset charset);

    default DataBuffer ensureCapacity(int capacity) {
        return this;
    }

    default DataBuffer write(CharSequence charSequence, Charset charset) {
        Assert.notNull(charSequence, "CharSequence must not be null");
        Assert.notNull(charset, "Charset must not be null");
        if (charSequence.length() != 0) {
            CharsetEncoder charsetEncoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            CharBuffer inBuffer = CharBuffer.wrap(charSequence);
            int estimatedSize = (int) (inBuffer.remaining() * charsetEncoder.averageBytesPerChar());
            ByteBuffer outBuffer = ensureCapacity(estimatedSize).asByteBuffer(writePosition(), writableByteCount());
            while (true) {
                CoderResult cr = inBuffer.hasRemaining() ? charsetEncoder.encode(inBuffer, outBuffer, true) : CoderResult.UNDERFLOW;
                if (cr.isUnderflow()) {
                    cr = charsetEncoder.flush(outBuffer);
                }
                if (cr.isUnderflow()) {
                    break;
                }
                if (cr.isOverflow()) {
                    writePosition(writePosition() + outBuffer.position());
                    int maximumSize = (int) (inBuffer.remaining() * charsetEncoder.maxBytesPerChar());
                    ensureCapacity(maximumSize);
                    outBuffer = asByteBuffer(writePosition(), writableByteCount());
                }
            }
            writePosition(writePosition() + outBuffer.position());
        }
        return this;
    }

    default DataBuffer retainedSlice(int index, int length) {
        return DataBufferUtils.retain(slice(index, length));
    }

    default String toString(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        return toString(readPosition(), readableByteCount(), charset);
    }
}
