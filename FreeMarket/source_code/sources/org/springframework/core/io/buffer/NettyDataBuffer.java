package org.springframework.core.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.IntPredicate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/buffer/NettyDataBuffer.class */
public class NettyDataBuffer implements PooledDataBuffer {
    private final ByteBuf byteBuf;
    private final NettyDataBufferFactory dataBufferFactory;

    NettyDataBuffer(ByteBuf byteBuf, NettyDataBufferFactory dataBufferFactory) {
        Assert.notNull(byteBuf, "ByteBuf must not be null");
        Assert.notNull(dataBufferFactory, "NettyDataBufferFactory must not be null");
        this.byteBuf = byteBuf;
        this.dataBufferFactory = dataBufferFactory;
    }

    public ByteBuf getNativeBuffer() {
        return this.byteBuf;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBufferFactory factory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int indexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.byteBuf.writerIndex()) {
            return -1;
        }
        int length = this.byteBuf.writerIndex() - fromIndex;
        IntPredicate intPredicateNegate = predicate.negate();
        intPredicateNegate.getClass();
        return this.byteBuf.forEachByte(fromIndex, length, (v1) -> {
            return r3.test(v1);
        });
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        if (fromIndex < 0) {
            return -1;
        }
        int fromIndex2 = Math.min(fromIndex, this.byteBuf.writerIndex() - 1);
        IntPredicate intPredicateNegate = predicate.negate();
        intPredicateNegate.getClass();
        return this.byteBuf.forEachByteDesc(0, fromIndex2 + 1, (v1) -> {
            return r3.test(v1);
        });
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readableByteCount() {
        return this.byteBuf.readableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writableByteCount() {
        return this.byteBuf.writableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readPosition() {
        return this.byteBuf.readerIndex();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer readPosition(int readPosition) {
        this.byteBuf.readerIndex(readPosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writePosition() {
        return this.byteBuf.writerIndex();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer writePosition(int writePosition) {
        this.byteBuf.writerIndex(writePosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte getByte(int index) {
        return this.byteBuf.getByte(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int capacity() {
        return this.byteBuf.capacity();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer capacity(int capacity) {
        this.byteBuf.capacity(capacity);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer ensureCapacity(int capacity) {
        this.byteBuf.ensureWritable(capacity);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte read() {
        return this.byteBuf.readByte();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer read(byte[] destination) {
        this.byteBuf.readBytes(destination);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer read(byte[] destination, int offset, int length) {
        this.byteBuf.readBytes(destination, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte b) {
        this.byteBuf.writeByte(b);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte[] source) {
        this.byteBuf.writeBytes(source);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte[] source, int offset, int length) {
        this.byteBuf.writeBytes(source, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(DataBuffer... buffers) {
        if (!ObjectUtils.isEmpty((Object[]) buffers)) {
            if (hasNettyDataBuffers(buffers)) {
                ByteBuf[] nativeBuffers = new ByteBuf[buffers.length];
                for (int i = 0; i < buffers.length; i++) {
                    nativeBuffers[i] = ((NettyDataBuffer) buffers[i]).getNativeBuffer();
                }
                write(nativeBuffers);
            } else {
                ByteBuffer[] byteBuffers = new ByteBuffer[buffers.length];
                for (int i2 = 0; i2 < buffers.length; i2++) {
                    byteBuffers[i2] = buffers[i2].asByteBuffer();
                }
                write(byteBuffers);
            }
        }
        return this;
    }

    private static boolean hasNettyDataBuffers(DataBuffer[] buffers) {
        for (DataBuffer buffer : buffers) {
            if (!(buffer instanceof NettyDataBuffer)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(ByteBuffer... buffers) {
        if (!ObjectUtils.isEmpty((Object[]) buffers)) {
            for (ByteBuffer buffer : buffers) {
                this.byteBuf.writeBytes(buffer);
            }
        }
        return this;
    }

    public NettyDataBuffer write(ByteBuf... byteBufs) {
        if (!ObjectUtils.isEmpty((Object[]) byteBufs)) {
            for (ByteBuf byteBuf : byteBufs) {
                this.byteBuf.writeBytes(byteBuf);
            }
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(CharSequence charSequence, Charset charset) {
        Assert.notNull(charSequence, "CharSequence must not be null");
        Assert.notNull(charset, "Charset must not be null");
        if (StandardCharsets.UTF_8.equals(charset)) {
            ByteBufUtil.writeUtf8(this.byteBuf, charSequence);
        } else if (StandardCharsets.US_ASCII.equals(charset)) {
            ByteBufUtil.writeAscii(this.byteBuf, charSequence);
        } else {
            return super.write(charSequence, charset);
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer slice(int index, int length) {
        ByteBuf slice = this.byteBuf.slice(index, length);
        return new NettyDataBuffer(slice, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer retainedSlice(int index, int length) {
        ByteBuf slice = this.byteBuf.retainedSlice(index, length);
        return new NettyDataBuffer(slice, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public ByteBuffer asByteBuffer() {
        return this.byteBuf.nioBuffer();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public ByteBuffer asByteBuffer(int index, int length) {
        return this.byteBuf.nioBuffer(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream() {
        return new ByteBufInputStream(this.byteBuf);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream(boolean releaseOnClose) {
        return new ByteBufInputStream(this.byteBuf, releaseOnClose);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public OutputStream asOutputStream() {
        return new ByteBufOutputStream(this.byteBuf);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        return this.byteBuf.toString(charset);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(int index, int length, Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        return this.byteBuf.toString(index, length, charset);
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public boolean isAllocated() {
        return this.byteBuf.refCnt() > 0;
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public PooledDataBuffer retain() {
        return new NettyDataBuffer(this.byteBuf.retain(), this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public PooledDataBuffer touch(Object hint) {
        this.byteBuf.touch(hint);
        return this;
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public boolean release() {
        return this.byteBuf.release();
    }

    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof NettyDataBuffer) && this.byteBuf.equals(((NettyDataBuffer) other).byteBuf));
    }

    public int hashCode() {
        return this.byteBuf.hashCode();
    }

    public String toString() {
        return this.byteBuf.toString();
    }
}
