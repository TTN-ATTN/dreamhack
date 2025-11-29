package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.tomcat.util.buf.ByteBufferHolder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/WriteBuffer.class */
public class WriteBuffer {
    private final int bufferSize;
    private final LinkedBlockingDeque<ByteBufferHolder> buffers = new LinkedBlockingDeque<>();

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/WriteBuffer$Sink.class */
    public interface Sink {
        boolean writeFromBuffer(ByteBuffer byteBuffer, boolean z) throws IOException;
    }

    public WriteBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    void clear() {
        this.buffers.clear();
    }

    void add(byte[] buf, int offset, int length) {
        ByteBufferHolder holder = getByteBufferHolder(length);
        holder.getBuf().put(buf, offset, length);
    }

    public void add(ByteBuffer from) {
        ByteBufferHolder holder = getByteBufferHolder(from.remaining());
        holder.getBuf().put(from);
    }

    private ByteBufferHolder getByteBufferHolder(int capacity) {
        ByteBufferHolder holder = this.buffers.peekLast();
        if (holder == null || holder.isFlipped() || holder.getBuf().remaining() < capacity) {
            ByteBuffer buffer = ByteBuffer.allocate(Math.max(this.bufferSize, capacity));
            holder = new ByteBufferHolder(buffer, false);
            this.buffers.add(holder);
        }
        return holder;
    }

    public boolean isEmpty() {
        return this.buffers.isEmpty();
    }

    ByteBuffer[] toArray(ByteBuffer... prefixes) {
        List<ByteBuffer> result = new ArrayList<>();
        for (ByteBuffer prefix : prefixes) {
            if (prefix.hasRemaining()) {
                result.add(prefix);
            }
        }
        Iterator<ByteBufferHolder> it = this.buffers.iterator();
        while (it.hasNext()) {
            ByteBufferHolder buffer = it.next();
            buffer.flip();
            result.add(buffer.getBuf());
        }
        this.buffers.clear();
        return (ByteBuffer[]) result.toArray(new ByteBuffer[0]);
    }

    boolean write(SocketWrapperBase<?> socketWrapper, boolean blocking) throws IOException {
        Iterator<ByteBufferHolder> bufIter = this.buffers.iterator();
        boolean dataLeft = false;
        while (!dataLeft && bufIter.hasNext()) {
            ByteBufferHolder buffer = bufIter.next();
            buffer.flip();
            if (blocking) {
                socketWrapper.writeBlocking(buffer.getBuf());
            } else {
                socketWrapper.writeNonBlockingInternal(buffer.getBuf());
            }
            if (buffer.getBuf().remaining() == 0) {
                bufIter.remove();
            } else {
                dataLeft = true;
            }
        }
        return dataLeft;
    }

    public boolean write(Sink sink, boolean blocking) throws IOException {
        Iterator<ByteBufferHolder> bufIter = this.buffers.iterator();
        boolean dataLeft = false;
        while (!dataLeft && bufIter.hasNext()) {
            ByteBufferHolder buffer = bufIter.next();
            buffer.flip();
            dataLeft = sink.writeFromBuffer(buffer.getBuf(), blocking);
            if (!dataLeft) {
                bufIter.remove();
            }
        }
        return dataLeft;
    }
}
