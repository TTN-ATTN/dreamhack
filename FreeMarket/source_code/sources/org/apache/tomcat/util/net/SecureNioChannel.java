package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.TLSClientHelloExtractor;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/SecureNioChannel.class */
public class SecureNioChannel extends NioChannel {
    private static final Log log = LogFactory.getLog((Class<?>) SecureNioChannel.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) SecureNioChannel.class);
    private static final int DEFAULT_NET_BUFFER_SIZE = 16921;
    private final NioEndpoint endpoint;
    protected ByteBuffer netInBuffer;
    protected ByteBuffer netOutBuffer;
    protected SSLEngine sslEngine;
    protected boolean sniComplete;
    protected boolean handshakeComplete;
    protected SSLEngineResult.HandshakeStatus handshakeStatus;
    protected boolean closed;
    protected boolean closing;
    private final Map<String, List<String>> additionalTlsAttributes;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/SecureNioChannel$OverflowState.class */
    private enum OverflowState {
        NONE,
        PROCESSING,
        DONE
    }

    public SecureNioChannel(SocketBufferHandler bufHandler, NioEndpoint endpoint) {
        super(bufHandler);
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.closed = false;
        this.closing = false;
        this.additionalTlsAttributes = new HashMap();
        if (endpoint.getSocketProperties().getDirectSslBuffer()) {
            this.netInBuffer = ByteBuffer.allocateDirect(DEFAULT_NET_BUFFER_SIZE);
            this.netOutBuffer = ByteBuffer.allocateDirect(DEFAULT_NET_BUFFER_SIZE);
        } else {
            this.netInBuffer = ByteBuffer.allocate(DEFAULT_NET_BUFFER_SIZE);
            this.netOutBuffer = ByteBuffer.allocate(DEFAULT_NET_BUFFER_SIZE);
        }
        this.endpoint = endpoint;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public void reset(SocketChannel channel, NioEndpoint.NioSocketWrapper socketWrapper) throws IOException {
        super.reset(channel, socketWrapper);
        this.sslEngine = null;
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.closed = false;
        this.closing = false;
        this.netInBuffer.clear();
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public void free() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        super.free();
        if (this.endpoint.getSocketProperties().getDirectSslBuffer()) {
            ByteBufferUtils.cleanDirectBuffer(this.netInBuffer);
            ByteBufferUtils.cleanDirectBuffer(this.netOutBuffer);
        }
    }

    protected boolean flush(ByteBuffer buf) throws IOException {
        int remaining = buf.remaining();
        return remaining <= 0 || this.sc.write(buf) >= remaining;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public int handshake(boolean read, boolean write) throws IOException {
        if (this.handshakeComplete) {
            return 0;
        }
        if (!this.sniComplete) {
            int sniResult = processSNI();
            if (sniResult == 0) {
                this.sniComplete = true;
            } else {
                return sniResult;
            }
        }
        if (!flush(this.netOutBuffer)) {
            return 4;
        }
        while (!this.handshakeComplete) {
            switch (AnonymousClass1.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[this.handshakeStatus.ordinal()]) {
                case 1:
                    throw new IOException(sm.getString("channel.nio.ssl.notHandshaking"));
                case 2:
                    if (this.endpoint.hasNegotiableProtocols()) {
                        if (this.sslEngine instanceof SSLUtil.ProtocolInfo) {
                            this.socketWrapper.setNegotiatedProtocol(((SSLUtil.ProtocolInfo) this.sslEngine).getNegotiatedProtocol());
                        } else if (JreCompat.isAlpnSupported()) {
                            this.socketWrapper.setNegotiatedProtocol(JreCompat.getInstance().getApplicationProtocol(this.sslEngine));
                        }
                    }
                    this.handshakeComplete = !this.netOutBuffer.hasRemaining();
                    return this.handshakeComplete ? 0 : 4;
                case 3:
                    try {
                        SSLEngineResult handshake = handshakeWrap(write);
                        if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                            if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                                this.handshakeStatus = tasks();
                            }
                            if (this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP || !flush(this.netOutBuffer)) {
                                return 4;
                            }
                        } else {
                            if (handshake.getStatus() == SSLEngineResult.Status.CLOSED) {
                                flush(this.netOutBuffer);
                                return -1;
                            }
                            throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", handshake.getStatus()));
                        }
                    } catch (SSLException e) {
                        handshakeWrap(write);
                        throw e;
                    }
                    break;
                case 4:
                    break;
                case 5:
                    this.handshakeStatus = tasks();
                    continue;
                default:
                    throw new IllegalStateException(sm.getString("channel.nio.ssl.invalidStatus", this.handshakeStatus));
            }
            SSLEngineResult handshake2 = handshakeUnwrap(read);
            if (handshake2.getStatus() == SSLEngineResult.Status.OK) {
                if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.handshakeStatus = tasks();
                }
            } else {
                if (handshake2.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    return 1;
                }
                throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", handshake2.getStatus()));
            }
        }
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0140  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x01b8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int processSNI() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 562
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.SecureNioChannel.processSNI():int");
    }

    /* renamed from: org.apache.tomcat.util.net.SecureNioChannel$1, reason: invalid class name */
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/SecureNioChannel$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus;

        static {
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.COMPLETE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NOT_PRESENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NEED_READ.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.UNDERFLOW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NON_SECURE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus = new int[SSLEngineResult.HandshakeStatus.values().length];
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.FINISHED.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_WRAP.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_UNWRAP.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_TASK.ordinal()] = 5;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    public void rehandshake(long timeout) throws IOException {
        if (this.netInBuffer.position() > 0 && this.netInBuffer.position() < this.netInBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netInputNotEmpty"));
        }
        if (this.netOutBuffer.position() > 0 && this.netOutBuffer.position() < this.netOutBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netOutputNotEmpty"));
        }
        if (!getBufHandler().isReadBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appInputNotEmpty"));
        }
        if (!getBufHandler().isWriteBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appOutputNotEmpty"));
        }
        this.handshakeComplete = false;
        boolean isReadable = false;
        boolean isWritable = false;
        boolean handshaking = true;
        Selector selector = null;
        SelectionKey key = null;
        try {
            try {
                this.sslEngine.beginHandshake();
                this.handshakeStatus = this.sslEngine.getHandshakeStatus();
                while (handshaking) {
                    int hsStatus = handshake(isReadable, isWritable);
                    switch (hsStatus) {
                        case -1:
                            throw new EOFException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
                        case 0:
                            handshaking = false;
                            break;
                        default:
                            long now = System.currentTimeMillis();
                            if (selector == null) {
                                selector = Selector.open();
                                key = getIOChannel().register(selector, hsStatus);
                            } else {
                                key.interestOps(hsStatus);
                            }
                            int keyCount = selector.select(timeout);
                            if (keyCount == 0 && System.currentTimeMillis() - now >= timeout) {
                                throw new SocketTimeoutException(sm.getString("channel.nio.ssl.timeoutDuringHandshake"));
                            }
                            isReadable = key.isReadable();
                            isWritable = key.isWritable();
                            break;
                    }
                }
                if (key != null) {
                    try {
                        key.cancel();
                    } catch (Exception e) {
                    }
                }
                if (selector != null) {
                    try {
                        selector.close();
                    } catch (Exception e2) {
                    }
                }
            } catch (IOException x) {
                closeSilently();
                throw x;
            } catch (Exception cx) {
                closeSilently();
                IOException x2 = new IOException(cx);
                throw x2;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    key.cancel();
                } catch (Exception e3) {
                }
            }
            if (0 != 0) {
                try {
                    selector.close();
                } catch (Exception e4) {
                }
            }
            throw th;
        }
    }

    protected SSLEngineResult.HandshakeStatus tasks() {
        while (true) {
            Runnable r = this.sslEngine.getDelegatedTask();
            if (r != null) {
                r.run();
            } else {
                return this.sslEngine.getHandshakeStatus();
            }
        }
    }

    protected SSLEngineResult handshakeWrap(boolean doWrite) throws IOException {
        this.netOutBuffer.clear();
        getBufHandler().configureWriteBufferForRead();
        SSLEngineResult result = this.sslEngine.wrap(getBufHandler().getWriteBuffer(), this.netOutBuffer);
        this.netOutBuffer.flip();
        this.handshakeStatus = result.getHandshakeStatus();
        if (doWrite) {
            flush(this.netOutBuffer);
        }
        return result;
    }

    protected SSLEngineResult handshakeUnwrap(boolean doread) throws IOException {
        SSLEngineResult result;
        boolean cont;
        if (this.netInBuffer.position() == this.netInBuffer.limit()) {
            this.netInBuffer.clear();
        }
        if (doread) {
            int read = this.sc.read(this.netInBuffer);
            if (read == -1) {
                throw new IOException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
            }
        }
        do {
            this.netInBuffer.flip();
            getBufHandler().configureReadBufferForWrite();
            result = this.sslEngine.unwrap(this.netInBuffer, getBufHandler().getReadBuffer());
            this.netInBuffer.compact();
            this.handshakeStatus = result.getHandshakeStatus();
            if (result.getStatus() == SSLEngineResult.Status.OK && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.handshakeStatus = tasks();
            }
            cont = result.getStatus() == SSLEngineResult.Status.OK && this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        } while (cont);
        return result;
    }

    public SSLSupport getSSLSupport() {
        if (this.sslEngine != null) {
            SSLSession session = this.sslEngine.getSession();
            return this.endpoint.getSslImplementation().getSSLSupport(session, this.additionalTlsAttributes);
        }
        return null;
    }

    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closing) {
            return;
        }
        this.closing = true;
        if (this.sslEngine == null) {
            this.netOutBuffer.clear();
            this.closed = true;
            return;
        }
        this.sslEngine.closeOutbound();
        if (!flush(this.netOutBuffer)) {
            throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
        }
        this.netOutBuffer.clear();
        SSLEngineResult handshake = this.sslEngine.wrap(getEmptyBuf(), this.netOutBuffer);
        if (handshake.getStatus() != SSLEngineResult.Status.CLOSED) {
            throw new IOException(sm.getString("channel.nio.ssl.invalidCloseState"));
        }
        this.netOutBuffer.flip();
        flush(this.netOutBuffer);
        this.closed = (this.netOutBuffer.hasRemaining() || handshake.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_WRAP) ? false : true;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public void close(boolean force) throws IOException {
        try {
            close();
            if (force || this.closed) {
                this.closed = true;
                this.sc.close();
            }
        } catch (Throwable th) {
            if (force || this.closed) {
                this.closed = true;
                this.sc.close();
            }
            throw th;
        }
    }

    private void closeSilently() {
        try {
            close(true);
        } catch (IOException ioe) {
            log.debug(sm.getString("channel.nio.ssl.closeSilentError"), ioe);
        }
    }

    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        if (this.closing || this.closed) {
            return -1;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        int netread = this.sc.read(this.netInBuffer);
        if (netread == -1) {
            return -1;
        }
        int read = 0;
        do {
            this.netInBuffer.flip();
            SSLEngineResult unwrap = this.sslEngine.unwrap(this.netInBuffer, dst);
            this.netInBuffer.compact();
            if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                read += unwrap.bytesProduced();
                if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    tasks();
                }
                if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    break;
                }
            } else if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                if (read > 0) {
                    break;
                }
                if (dst == getBufHandler().getReadBuffer()) {
                    getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                    dst = getBufHandler().getReadBuffer();
                } else if (getAppReadBufHandler() != null && dst == getAppReadBufHandler().getByteBuffer()) {
                    getAppReadBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                    dst = getAppReadBufHandler().getByteBuffer();
                } else {
                    throw new IOException(sm.getString("channel.nio.ssl.unwrapFailResize", unwrap.getStatus()));
                }
            } else if (unwrap.getStatus() != SSLEngineResult.Status.CLOSED || this.netInBuffer.position() != 0 || read <= 0) {
                throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", unwrap.getStatus()));
            }
        } while (this.netInBuffer.position() != 0);
        return read;
    }

    /* JADX WARN: Removed duplicated region for block: B:78:0x0241  */
    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.ScatteringByteChannel
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long read(java.nio.ByteBuffer[] r10, int r11, int r12) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 589
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.SecureNioChannel.read(java.nio.ByteBuffer[], int, int):long");
    }

    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        checkInterruptStatus();
        if (src == this.netOutBuffer) {
            int written = this.sc.write(src);
            return written;
        }
        if (this.closing || this.closed) {
            throw new IOException(sm.getString("channel.nio.ssl.closing"));
        }
        if (!flush(this.netOutBuffer) || !src.hasRemaining()) {
            return 0;
        }
        this.netOutBuffer.clear();
        SSLEngineResult result = this.sslEngine.wrap(src, this.netOutBuffer);
        int written2 = result.bytesConsumed();
        this.netOutBuffer.flip();
        if (result.getStatus() == SSLEngineResult.Status.OK) {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                tasks();
            }
            flush(this.netOutBuffer);
            return written2;
        }
        throw new IOException(sm.getString("channel.nio.ssl.wrapFail", result.getStatus()));
    }

    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.GatheringByteChannel
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        checkInterruptStatus();
        if (this.closing || this.closed) {
            throw new IOException(sm.getString("channel.nio.ssl.closing"));
        }
        if (!flush(this.netOutBuffer)) {
            return 0L;
        }
        this.netOutBuffer.clear();
        SSLEngineResult result = this.sslEngine.wrap(srcs, offset, length, this.netOutBuffer);
        int written = result.bytesConsumed();
        this.netOutBuffer.flip();
        if (result.getStatus() == SSLEngineResult.Status.OK) {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                tasks();
            }
            flush(this.netOutBuffer);
            return written;
        }
        throw new IOException(sm.getString("channel.nio.ssl.wrapFail", result.getStatus()));
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public int getOutboundRemaining() {
        return this.netOutBuffer.remaining();
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public boolean flushOutbound() throws IOException {
        int remaining = this.netOutBuffer.remaining();
        flush(this.netOutBuffer);
        int remaining2 = this.netOutBuffer.remaining();
        return remaining2 < remaining;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public boolean isClosing() {
        return this.closing;
    }

    public SSLEngine getSslEngine() {
        return this.sslEngine;
    }

    public ByteBuffer getEmptyBuf() {
        return emptyBuf;
    }
}
