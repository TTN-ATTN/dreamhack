package org.apache.coyote.http2;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.WebConnection;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.Request;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.HpackEncoder;
import org.apache.coyote.http2.Http2Parser;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.util.SocketUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Http2UpgradeHandler.class */
class Http2UpgradeHandler extends AbstractStream implements InternalHttpUpgradeHandler, Http2Parser.Input, Http2Parser.Output {
    protected static final int FLAG_END_OF_STREAM = 1;
    protected static final int FLAG_END_OF_HEADERS = 4;
    private static final String HTTP2_SETTINGS_HEADER = "HTTP2-Settings";
    private final Object priorityTreeLock;
    protected final String connectionId;
    protected final Http2Protocol protocol;
    private final Adapter adapter;
    protected volatile SocketWrapperBase<?> socketWrapper;
    private volatile SSLSupport sslSupport;
    private volatile Http2Parser parser;
    private AtomicReference<ConnectionState> connectionState;
    private volatile long pausedNanoTime;
    private final ConnectionSettingsRemote remoteSettings;
    protected final ConnectionSettingsLocal localSettings;
    private HpackDecoder hpackDecoder;
    private HpackEncoder hpackEncoder;
    private final ConcurrentNavigableMap<Integer, AbstractNonZeroStream> streams;
    protected final AtomicInteger activeRemoteStreamCount;
    private volatile int maxActiveRemoteStreamId;
    private volatile int maxProcessedStreamId;
    private final AtomicInteger nextLocalStreamId;
    private final PingManager pingManager;
    private volatile int newStreamsSinceLastPrune;
    private final Set<AbstractStream> backLogStreams;
    private long backLogSize;
    private volatile long connectionTimeout;
    private AtomicInteger streamConcurrency;
    private Queue<StreamRunnable> queuedRunnable;
    private final AtomicLong overheadCount;
    private volatile int lastNonFinalDataPayload;
    private volatile int lastWindowUpdate;
    protected final UserDataHelper userDataHelper;
    protected static final Log log = LogFactory.getLog((Class<?>) Http2UpgradeHandler.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) Http2UpgradeHandler.class);
    private static final AtomicInteger connectionIdGenerator = new AtomicInteger(0);
    private static final Integer STREAM_ID_ZERO = 0;
    protected static final byte[] PING = {0, 0, 8, 6, 0, 0, 0, 0, 0};
    protected static final byte[] PING_ACK = {0, 0, 8, 6, 1, 0, 0, 0, 0};
    protected static final byte[] SETTINGS_ACK = {0, 0, 0, 4, 1, 0, 0, 0, 0};
    protected static final byte[] GOAWAY = {7, 0, 0, 0, 0, 0};
    private static final HeaderSink HEADER_SINK = new HeaderSink();

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Http2UpgradeHandler$HeaderFrameBuffers.class */
    protected interface HeaderFrameBuffers {
        void startFrame();

        void endFrame() throws IOException;

        void endHeaders() throws IOException;

        byte[] getHeader();

        ByteBuffer getPayload();

        void expandPayload();
    }

    Http2UpgradeHandler(Http2Protocol protocol, Adapter adapter, Request coyoteRequest) {
        super(STREAM_ID_ZERO);
        this.priorityTreeLock = new Object();
        this.connectionState = new AtomicReference<>(ConnectionState.NEW);
        this.pausedNanoTime = Long.MAX_VALUE;
        this.streams = new ConcurrentSkipListMap();
        this.activeRemoteStreamCount = new AtomicInteger(0);
        this.maxActiveRemoteStreamId = -1;
        this.nextLocalStreamId = new AtomicInteger(2);
        this.pingManager = getPingManager();
        this.newStreamsSinceLastPrune = 0;
        this.backLogStreams = ConcurrentHashMap.newKeySet();
        this.backLogSize = 0L;
        this.connectionTimeout = -1L;
        this.streamConcurrency = null;
        this.queuedRunnable = null;
        this.userDataHelper = new UserDataHelper(log);
        this.protocol = protocol;
        this.adapter = adapter;
        this.connectionId = Integer.toString(connectionIdGenerator.getAndIncrement());
        this.overheadCount = new AtomicLong((-10) * protocol.getOverheadCountFactor());
        this.lastNonFinalDataPayload = protocol.getOverheadDataThreshold() * 2;
        this.lastWindowUpdate = protocol.getOverheadWindowUpdateThreshold() * 2;
        this.remoteSettings = new ConnectionSettingsRemote(this.connectionId);
        this.localSettings = new ConnectionSettingsLocal(this.connectionId);
        this.localSettings.set(Setting.MAX_CONCURRENT_STREAMS, protocol.getMaxConcurrentStreams());
        this.localSettings.set(Setting.INITIAL_WINDOW_SIZE, protocol.getInitialWindowSize());
        this.pingManager.initiateDisabled = protocol.getInitiatePingDisabled();
        if (coyoteRequest != null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.upgrade", this.connectionId));
            }
            Stream stream = new Stream(1, this, coyoteRequest);
            this.streams.put(1, stream);
            this.maxActiveRemoteStreamId = 1;
            this.activeRemoteStreamCount.set(1);
            this.maxProcessedStreamId = 1;
        }
    }

    protected PingManager getPingManager() {
        return new PingManager();
    }

    public void init(WebConnection webConnection) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.init", this.connectionId, this.connectionState.get()));
        }
        if (!this.connectionState.compareAndSet(ConnectionState.NEW, ConnectionState.CONNECTED)) {
            return;
        }
        if (this.protocol.getMaxConcurrentStreamExecution() < this.localSettings.getMaxConcurrentStreams()) {
            this.streamConcurrency = new AtomicInteger(0);
            this.queuedRunnable = new ConcurrentLinkedQueue();
        }
        this.parser = getParser(this.connectionId);
        Stream stream = null;
        this.socketWrapper.setReadTimeout(this.protocol.getReadTimeout());
        this.socketWrapper.setWriteTimeout(this.protocol.getWriteTimeout());
        if (webConnection != null) {
            try {
                stream = getStream(1, true);
                String base64Settings = stream.getCoyoteRequest().getHeader(HTTP2_SETTINGS_HEADER);
                byte[] settings = Base64.decodeBase64URLSafe(base64Settings);
                FrameType.SETTINGS.check(0, settings.length);
                for (int i = 0; i < settings.length % 6; i++) {
                    int id = ByteUtil.getTwoBytes(settings, i * 6);
                    long value = ByteUtil.getFourBytes(settings, (i * 6) + 2);
                    Setting key = Setting.valueOf(id);
                    if (key == Setting.UNKNOWN) {
                        log.warn(sm.getString("connectionSettings.unknown", this.connectionId, Integer.toString(id), Long.toString(value)));
                    }
                    this.remoteSettings.set(key, value);
                }
            } catch (Http2Exception e) {
                throw new ProtocolException(sm.getString("upgradeHandler.upgrade.fail", this.connectionId));
            }
        }
        writeSettings();
        try {
            this.parser.readConnectionPreface(webConnection, stream);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.prefaceReceived", this.connectionId));
            }
            this.socketWrapper.setReadTimeout(-1L);
            this.socketWrapper.setWriteTimeout(-1L);
            processConnection(webConnection, stream);
        } catch (Http2Exception e2) {
            String msg = sm.getString("upgradeHandler.invalidPreface", this.connectionId);
            if (log.isDebugEnabled()) {
                log.debug(msg, e2);
            }
            throw new ProtocolException(msg);
        }
    }

    protected void processConnection(WebConnection webConnection, Stream stream) {
        try {
            this.pingManager.sendPing(true);
            if (webConnection != null) {
                processStreamOnContainerThread(stream);
            }
        } catch (IOException ioe) {
            throw new ProtocolException(sm.getString("upgradeHandler.pingFailed", this.connectionId), ioe);
        }
    }

    protected Http2Parser getParser(String connectionId) {
        return new Http2Parser(connectionId, this, this);
    }

    protected void processStreamOnContainerThread(Stream stream) {
        StreamProcessor streamProcessor = new StreamProcessor(this, stream, this.adapter, this.socketWrapper);
        streamProcessor.setSslSupport(this.sslSupport);
        processStreamOnContainerThread(streamProcessor, SocketEvent.OPEN_READ);
    }

    void processStreamOnContainerThread(StreamProcessor streamProcessor, SocketEvent event) {
        StreamRunnable streamRunnable = new StreamRunnable(streamProcessor, event);
        if (this.streamConcurrency == null) {
            this.socketWrapper.execute(streamRunnable);
        } else if (getStreamConcurrency() < this.protocol.getMaxConcurrentStreamExecution()) {
            increaseStreamConcurrency();
            this.socketWrapper.execute(streamRunnable);
        } else {
            this.queuedRunnable.offer(streamRunnable);
        }
    }

    public void setSocketWrapper(SocketWrapperBase<?> wrapper) {
        this.socketWrapper = wrapper;
    }

    public void setSslSupport(SSLSupport sslSupport) {
        this.sslSupport = sslSupport;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:81:0x02cc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState upgradeDispatch(org.apache.tomcat.util.net.SocketEvent r10) throws org.apache.coyote.http2.ConnectionException {
        /*
            Method dump skipped, instructions count: 749
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.coyote.http2.Http2UpgradeHandler.upgradeDispatch(org.apache.tomcat.util.net.SocketEvent):org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState");
    }

    protected void setConnectionTimeoutForStreamCount(int streamCount) {
        if (streamCount == 0) {
            long keepAliveTimeout = this.protocol.getKeepAliveTimeout();
            if (keepAliveTimeout == -1) {
                setConnectionTimeout(-1L);
                return;
            } else {
                setConnectionTimeout(System.currentTimeMillis() + keepAliveTimeout);
                return;
            }
        }
        setConnectionTimeout(-1L);
    }

    private void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void timeoutAsync(long now) {
        long connectionTimeout = this.connectionTimeout;
        if (now == -1 || (connectionTimeout > -1 && now > connectionTimeout)) {
            this.socketWrapper.processSocket(SocketEvent.TIMEOUT, true);
        }
    }

    ConnectionSettingsRemote getRemoteSettings() {
        return this.remoteSettings;
    }

    ConnectionSettingsLocal getLocalSettings() {
        return this.localSettings;
    }

    Http2Protocol getProtocol() {
        return this.protocol;
    }

    public void pause() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.pause.entry", this.connectionId));
        }
        if (this.connectionState.compareAndSet(ConnectionState.CONNECTED, ConnectionState.PAUSING)) {
            this.pausedNanoTime = System.nanoTime();
            try {
                writeGoAwayFrame(Integer.MAX_VALUE, Http2Error.NO_ERROR.getCode(), null);
            } catch (IOException e) {
            }
        }
    }

    public void destroy() {
    }

    void checkPauseState() throws IOException {
        if (this.connectionState.get() == ConnectionState.PAUSING && this.pausedNanoTime + this.pingManager.getRoundTripTimeNano() < System.nanoTime()) {
            this.connectionState.compareAndSet(ConnectionState.PAUSING, ConnectionState.PAUSED);
            writeGoAwayFrame(this.maxProcessedStreamId, Http2Error.NO_ERROR.getCode(), null);
        }
    }

    private int increaseStreamConcurrency() {
        return this.streamConcurrency.incrementAndGet();
    }

    private int decreaseStreamConcurrency() {
        return this.streamConcurrency.decrementAndGet();
    }

    private int getStreamConcurrency() {
        return this.streamConcurrency.get();
    }

    void executeQueuedStream() {
        StreamRunnable streamRunnable;
        if (this.streamConcurrency == null) {
            return;
        }
        decreaseStreamConcurrency();
        if (getStreamConcurrency() < this.protocol.getMaxConcurrentStreamExecution() && (streamRunnable = this.queuedRunnable.poll()) != null) {
            increaseStreamConcurrency();
            this.socketWrapper.execute(streamRunnable);
        }
    }

    void sendStreamReset(StreamStateMachine state, StreamException se) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.rst.debug", this.connectionId, Integer.toString(se.getStreamId()), se.getError(), se.getMessage()));
        }
        byte[] rstFrame = new byte[13];
        ByteUtil.setThreeBytes(rstFrame, 0, 4);
        rstFrame[3] = FrameType.RST.getIdByte();
        ByteUtil.set31Bits(rstFrame, 5, se.getStreamId());
        ByteUtil.setFourBytes(rstFrame, 9, se.getError().getCode());
        this.socketWrapper.getLock().lock();
        if (state != null) {
            try {
                boolean active = state.isActive();
                state.sendReset();
                if (active) {
                    this.activeRemoteStreamCount.decrementAndGet();
                }
            } catch (Throwable th) {
                this.socketWrapper.getLock().unlock();
                throw th;
            }
        }
        this.socketWrapper.write(true, rstFrame, 0, rstFrame.length);
        this.socketWrapper.flush(true);
        this.socketWrapper.getLock().unlock();
    }

    void closeConnection(Http2Exception ce) {
        long code;
        byte[] msg;
        if (ce == null) {
            code = Http2Error.NO_ERROR.getCode();
            msg = null;
        } else {
            code = ce.getError().getCode();
            msg = ce.getMessage().getBytes(StandardCharsets.UTF_8);
        }
        try {
            writeGoAwayFrame(this.maxProcessedStreamId, code, msg);
        } catch (IOException e) {
        }
        close();
    }

    protected void writeSettings() {
        try {
            byte[] settings = this.localSettings.getSettingsFrameForPending();
            this.socketWrapper.write(true, settings, 0, settings.length);
            byte[] windowUpdateFrame = createWindowUpdateForSettings();
            if (windowUpdateFrame.length > 0) {
                this.socketWrapper.write(true, windowUpdateFrame, 0, windowUpdateFrame.length);
            }
            this.socketWrapper.flush(true);
        } catch (IOException ioe) {
            String msg = sm.getString("upgradeHandler.sendPrefaceFail", this.connectionId);
            if (log.isDebugEnabled()) {
                log.debug(msg);
            }
            throw new ProtocolException(msg, ioe);
        }
    }

    protected byte[] createWindowUpdateForSettings() {
        byte[] windowUpdateFrame;
        int increment = this.protocol.getInitialWindowSize() - SocketUtils.PORT_RANGE_MAX;
        if (increment > 0) {
            windowUpdateFrame = new byte[13];
            ByteUtil.setThreeBytes(windowUpdateFrame, 0, 4);
            windowUpdateFrame[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(windowUpdateFrame, 9, increment);
        } else {
            windowUpdateFrame = new byte[0];
        }
        return windowUpdateFrame;
    }

    protected void writeGoAwayFrame(int maxStreamId, long errorCode, byte[] debugMsg) throws IOException {
        byte[] fixedPayload = new byte[8];
        ByteUtil.set31Bits(fixedPayload, 0, maxStreamId);
        ByteUtil.setFourBytes(fixedPayload, 4, errorCode);
        int len = 8;
        if (debugMsg != null) {
            len = 8 + debugMsg.length;
        }
        byte[] payloadLength = new byte[3];
        ByteUtil.setThreeBytes(payloadLength, 0, len);
        this.socketWrapper.getLock().lock();
        try {
            this.socketWrapper.write(true, payloadLength, 0, payloadLength.length);
            this.socketWrapper.write(true, GOAWAY, 0, GOAWAY.length);
            this.socketWrapper.write(true, fixedPayload, 0, 8);
            if (debugMsg != null) {
                this.socketWrapper.write(true, debugMsg, 0, debugMsg.length);
            }
            this.socketWrapper.flush(true);
            this.socketWrapper.getLock().unlock();
        } catch (Throwable th) {
            this.socketWrapper.getLock().unlock();
            throw th;
        }
    }

    void writeHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        this.socketWrapper.getLock().lock();
        try {
            doWriteHeaders(stream, pushedStreamId, mimeHeaders, endOfStream, payloadSize);
            this.socketWrapper.getLock().unlock();
            stream.sentHeaders();
            if (endOfStream) {
                sentEndOfStream(stream);
            }
        } catch (Throwable th) {
            this.socketWrapper.getLock().unlock();
            throw th;
        }
    }

    protected HeaderFrameBuffers doWriteHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        if (log.isDebugEnabled()) {
            if (pushedStreamId == 0) {
                log.debug(sm.getString("upgradeHandler.writeHeaders", this.connectionId, stream.getIdAsString(), Boolean.valueOf(endOfStream)));
            } else {
                log.debug(sm.getString("upgradeHandler.writePushHeaders", this.connectionId, stream.getIdAsString(), Integer.valueOf(pushedStreamId), Boolean.valueOf(endOfStream)));
            }
        }
        if (!stream.canWrite()) {
            return null;
        }
        HeaderFrameBuffers headerFrameBuffers = getHeaderFrameBuffers(payloadSize);
        byte[] pushedStreamIdBytes = null;
        if (pushedStreamId > 0) {
            pushedStreamIdBytes = new byte[4];
            ByteUtil.set31Bits(pushedStreamIdBytes, 0, pushedStreamId);
        }
        boolean first = true;
        HpackEncoder.State state = null;
        while (state != HpackEncoder.State.COMPLETE) {
            headerFrameBuffers.startFrame();
            if (first && pushedStreamIdBytes != null) {
                headerFrameBuffers.getPayload().put(pushedStreamIdBytes);
            }
            state = getHpackEncoder().encode(mimeHeaders, headerFrameBuffers.getPayload());
            headerFrameBuffers.getPayload().flip();
            if (state == HpackEncoder.State.COMPLETE || headerFrameBuffers.getPayload().limit() > 0) {
                ByteUtil.setThreeBytes(headerFrameBuffers.getHeader(), 0, headerFrameBuffers.getPayload().limit());
                if (first) {
                    first = false;
                    if (pushedStreamIdBytes == null) {
                        headerFrameBuffers.getHeader()[3] = FrameType.HEADERS.getIdByte();
                    } else {
                        headerFrameBuffers.getHeader()[3] = FrameType.PUSH_PROMISE.getIdByte();
                    }
                    if (endOfStream) {
                        headerFrameBuffers.getHeader()[4] = 1;
                    }
                } else {
                    headerFrameBuffers.getHeader()[3] = FrameType.CONTINUATION.getIdByte();
                }
                if (state == HpackEncoder.State.COMPLETE) {
                    byte[] header = headerFrameBuffers.getHeader();
                    header[4] = (byte) (header[4] + 4);
                }
                if (log.isDebugEnabled()) {
                    log.debug(headerFrameBuffers.getPayload().limit() + " bytes");
                }
                ByteUtil.set31Bits(headerFrameBuffers.getHeader(), 5, stream.getIdAsInt());
                headerFrameBuffers.endFrame();
            } else if (state == HpackEncoder.State.UNDERFLOW) {
                headerFrameBuffers.expandPayload();
            }
        }
        headerFrameBuffers.endHeaders();
        return headerFrameBuffers;
    }

    protected HeaderFrameBuffers getHeaderFrameBuffers(int initialPayloadSize) {
        return new DefaultHeaderFrameBuffers(initialPayloadSize);
    }

    protected HpackEncoder getHpackEncoder() {
        if (this.hpackEncoder == null) {
            this.hpackEncoder = new HpackEncoder();
        }
        this.hpackEncoder.setMaxTableSize(this.remoteSettings.getHeaderTableSize());
        return this.hpackEncoder;
    }

    void writeBody(Stream stream, ByteBuffer data, int len, boolean finished) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.writeBody", this.connectionId, stream.getIdAsString(), Integer.toString(len), Boolean.valueOf(finished)));
        }
        reduceOverheadCount(FrameType.DATA);
        boolean writable = stream.canWrite();
        byte[] header = new byte[9];
        ByteUtil.setThreeBytes(header, 0, len);
        header[3] = FrameType.DATA.getIdByte();
        if (finished) {
            header[4] = 1;
            sentEndOfStream(stream);
        }
        if (writable) {
            ByteUtil.set31Bits(header, 5, stream.getIdAsInt());
            this.socketWrapper.getLock().lock();
            try {
                try {
                    this.socketWrapper.write(true, header, 0, header.length);
                    int orgLimit = data.limit();
                    data.limit(data.position() + len);
                    this.socketWrapper.write(true, data);
                    data.limit(orgLimit);
                    this.socketWrapper.flush(true);
                    this.socketWrapper.getLock().unlock();
                } catch (IOException ioe) {
                    handleAppInitiatedIOException(ioe);
                    this.socketWrapper.getLock().unlock();
                }
            } catch (Throwable th) {
                this.socketWrapper.getLock().unlock();
                throw th;
            }
        }
    }

    protected void sentEndOfStream(Stream stream) {
        stream.sentEndOfStream();
        if (!stream.isActive()) {
            setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
        }
    }

    protected void handleAppInitiatedIOException(IOException ioe) throws IOException {
        close();
        throw ioe;
    }

    void writeWindowUpdate(AbstractNonZeroStream stream, int increment, boolean applicationInitiated) throws IOException {
        int streamIncrement;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.windowUpdateConnection", getConnectionId(), Integer.valueOf(increment)));
        }
        this.socketWrapper.getLock().lock();
        try {
            byte[] frame = new byte[13];
            ByteUtil.setThreeBytes(frame, 0, 4);
            frame[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(frame, 9, increment);
            this.socketWrapper.write(true, frame, 0, frame.length);
            boolean needFlush = true;
            if ((stream instanceof Stream) && ((Stream) stream).canWrite() && (streamIncrement = ((Stream) stream).getWindowUpdateSizeToWrite(increment)) > 0) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeHandler.windowUpdateStream", getConnectionId(), getIdAsString(), Integer.valueOf(streamIncrement)));
                }
                ByteUtil.set31Bits(frame, 5, stream.getIdAsInt());
                ByteUtil.set31Bits(frame, 9, streamIncrement);
                try {
                    this.socketWrapper.write(true, frame, 0, frame.length);
                    this.socketWrapper.flush(true);
                    needFlush = false;
                } catch (IOException ioe) {
                    if (applicationInitiated) {
                        handleAppInitiatedIOException(ioe);
                    } else {
                        throw ioe;
                    }
                }
            }
            if (needFlush) {
                this.socketWrapper.flush(true);
            }
        } finally {
            this.socketWrapper.getLock().unlock();
        }
    }

    protected void processWrites() throws IOException {
        this.socketWrapper.getLock().lock();
        try {
            if (this.socketWrapper.flush(false)) {
                this.socketWrapper.registerWriteInterest();
            } else {
                this.pingManager.sendPing(false);
            }
        } finally {
            this.socketWrapper.getLock().unlock();
        }
    }

    int reserveWindowSize(Stream stream, int reservation, boolean block) throws IOException {
        String msg;
        Http2Error error;
        int allocation = 0;
        synchronized (stream) {
            synchronized (this) {
                if (!stream.canWrite()) {
                    stream.doStreamCancel(sm.getString("upgradeHandler.stream.notWritable", stream.getConnectionId(), stream.getIdAsString(), stream.state.getCurrentStateName()), Http2Error.STREAM_CLOSED);
                }
                long windowSize = getWindowSize();
                if (stream.getConnectionAllocationMade() > 0) {
                    allocation = stream.getConnectionAllocationMade();
                    stream.setConnectionAllocationMade(0);
                } else if (windowSize < 1) {
                    if (stream.getConnectionAllocationMade() == 0) {
                        stream.setConnectionAllocationRequested(reservation);
                        this.backLogSize += reservation;
                        this.backLogStreams.add(stream);
                        for (AbstractStream parent = stream.getParentStream(); parent != null && this.backLogStreams.add(parent); parent = parent.getParentStream()) {
                        }
                    }
                } else if (windowSize < reservation) {
                    allocation = (int) windowSize;
                    decrementWindowSize(allocation);
                } else {
                    allocation = reservation;
                    decrementWindowSize(allocation);
                }
            }
            if (allocation == 0) {
                if (block) {
                    try {
                        long writeTimeout = this.protocol.getWriteTimeout();
                        stream.waitForConnectionAllocation(writeTimeout);
                        if (stream.getConnectionAllocationMade() == 0) {
                            if (stream.isActive()) {
                                if (log.isDebugEnabled()) {
                                    log.debug(sm.getString("upgradeHandler.noAllocation", this.connectionId, stream.getIdAsString()));
                                }
                                close();
                                msg = sm.getString("stream.writeTimeout");
                                error = Http2Error.ENHANCE_YOUR_CALM;
                            } else {
                                msg = sm.getString("stream.clientCancel");
                                error = Http2Error.STREAM_CLOSED;
                            }
                            stream.doStreamCancel(msg, error);
                        } else {
                            allocation = stream.getConnectionAllocationMade();
                            stream.setConnectionAllocationMade(0);
                        }
                    } catch (InterruptedException e) {
                        throw new IOException(sm.getString("upgradeHandler.windowSizeReservationInterrupted", this.connectionId, stream.getIdAsString(), Integer.toString(reservation)), e);
                    }
                } else {
                    stream.waitForConnectionAllocationNonBlocking();
                    return 0;
                }
            }
            return allocation;
        }
    }

    @Override // org.apache.coyote.http2.AbstractStream
    protected void incrementWindowSize(int increment) throws Http2Exception {
        Set<AbstractStream> streamsToNotify = null;
        synchronized (this) {
            long windowSize = getWindowSize();
            if (windowSize < 1 && windowSize + increment > 0) {
                streamsToNotify = releaseBackLog((int) (windowSize + increment));
            } else {
                super.incrementWindowSize(increment);
            }
        }
        if (streamsToNotify != null) {
            for (AbstractStream stream : streamsToNotify) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeHandler.releaseBacklog", this.connectionId, stream.getIdAsString()));
                }
                if (this != stream) {
                    ((Stream) stream).notifyConnection();
                }
            }
        }
    }

    protected SendfileState processSendfile(SendfileData sendfileData) {
        return SendfileState.DONE;
    }

    private synchronized Set<AbstractStream> releaseBackLog(int increment) throws Http2Exception {
        Set<AbstractStream> result = new HashSet<>();
        if (this.backLogSize < increment) {
            for (AbstractStream stream : this.backLogStreams) {
                if (stream.getConnectionAllocationRequested() > 0) {
                    stream.setConnectionAllocationMade(stream.getConnectionAllocationRequested());
                    stream.setConnectionAllocationRequested(0);
                    result.add(stream);
                }
            }
            int remaining = (int) (increment - this.backLogSize);
            this.backLogSize = 0L;
            super.incrementWindowSize(remaining);
            this.backLogStreams.clear();
        } else {
            allocate(this, increment);
            Iterator<AbstractStream> streamIter = this.backLogStreams.iterator();
            while (streamIter.hasNext()) {
                AbstractStream stream2 = streamIter.next();
                if (stream2.getConnectionAllocationMade() > 0) {
                    this.backLogSize -= stream2.getConnectionAllocationMade();
                    this.backLogSize -= stream2.getConnectionAllocationRequested();
                    stream2.setConnectionAllocationRequested(0);
                    result.add(stream2);
                    streamIter.remove();
                }
            }
        }
        return result;
    }

    private synchronized int allocate(AbstractStream stream, int allocation) {
        int allocated;
        int allocatedThisTime;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.allocate.debug", getConnectionId(), stream.getIdAsString(), Integer.toString(allocation)));
        }
        int leftToAllocate = allocation;
        if (stream.getConnectionAllocationRequested() > 0) {
            if (allocation >= stream.getConnectionAllocationRequested()) {
                allocatedThisTime = stream.getConnectionAllocationRequested();
            } else {
                allocatedThisTime = allocation;
            }
            stream.setConnectionAllocationRequested(stream.getConnectionAllocationRequested() - allocatedThisTime);
            stream.setConnectionAllocationMade(stream.getConnectionAllocationMade() + allocatedThisTime);
            leftToAllocate -= allocatedThisTime;
        }
        if (leftToAllocate == 0) {
            return 0;
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.allocate.left", getConnectionId(), stream.getIdAsString(), Integer.toString(leftToAllocate)));
        }
        Set<AbstractStream> recipients = new HashSet<>(stream.getChildStreams());
        recipients.retainAll(this.backLogStreams);
        while (leftToAllocate > 0) {
            if (recipients.size() == 0) {
                if (stream.getConnectionAllocationMade() == 0) {
                    this.backLogStreams.remove(stream);
                }
                if (stream.getIdAsInt() == 0) {
                    throw new IllegalStateException();
                }
                return leftToAllocate;
            }
            int totalWeight = 0;
            for (AbstractStream recipient : recipients) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeHandler.allocate.recipient", getConnectionId(), stream.getIdAsString(), recipient.getIdAsString(), Integer.toString(recipient.getWeight())));
                }
                totalWeight += recipient.getWeight();
            }
            Iterator<AbstractStream> iter = recipients.iterator();
            int i = 0;
            while (true) {
                allocated = i;
                if (iter.hasNext()) {
                    AbstractStream recipient2 = iter.next();
                    int share = (leftToAllocate * recipient2.getWeight()) / totalWeight;
                    if (share == 0) {
                        share = 1;
                    }
                    int remainder = allocate(recipient2, share);
                    if (remainder > 0) {
                        iter.remove();
                    }
                    i = allocated + (share - remainder);
                }
            }
            leftToAllocate -= allocated;
        }
        return 0;
    }

    private Stream getStream(int streamId) {
        Integer key = Integer.valueOf(streamId);
        AbstractStream result = (AbstractStream) this.streams.get(key);
        if (result instanceof Stream) {
            return (Stream) result;
        }
        return null;
    }

    private Stream getStream(int streamId, boolean unknownIsError) throws ConnectionException {
        Stream result = getStream(streamId);
        if (result == null && unknownIsError) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.closed", Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR);
        }
        return result;
    }

    private AbstractNonZeroStream getAbstractNonZeroStream(int streamId) {
        Integer key = Integer.valueOf(streamId);
        return (AbstractNonZeroStream) this.streams.get(key);
    }

    private AbstractNonZeroStream getAbstractNonZeroStream(int streamId, boolean unknownIsError) throws ConnectionException {
        AbstractNonZeroStream result = getAbstractNonZeroStream(streamId);
        if (result == null && unknownIsError) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.closed", Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR);
        }
        return result;
    }

    private Stream createRemoteStream(int streamId) throws ConnectionException {
        Integer key = Integer.valueOf(streamId);
        if (streamId % 2 != 1) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.even", key), Http2Error.PROTOCOL_ERROR);
        }
        pruneClosedStreams(streamId);
        Stream result = new Stream(key, this);
        this.streams.put(key, result);
        return result;
    }

    private Stream createLocalStream(Request request) {
        int streamId = this.nextLocalStreamId.getAndAdd(2);
        Integer key = Integer.valueOf(streamId);
        Stream result = new Stream(key, this, request);
        this.streams.put(key, result);
        return result;
    }

    private void close() {
        ConnectionState previous = this.connectionState.getAndSet(ConnectionState.CLOSED);
        if (previous == ConnectionState.CLOSED) {
            return;
        }
        for (AbstractNonZeroStream stream : this.streams.values()) {
            if (stream instanceof Stream) {
                ((Stream) stream).receiveReset(Http2Error.CANCEL.getCode());
            }
        }
        try {
            this.socketWrapper.close();
        } catch (Exception e) {
            log.debug(sm.getString("upgradeHandler.socketCloseFailed"), e);
        }
    }

    private void pruneClosedStreams(int streamId) {
        if (this.newStreamsSinceLastPrune < 9) {
            this.newStreamsSinceLastPrune++;
            return;
        }
        this.newStreamsSinceLastPrune = 0;
        long max = this.localSettings.getMaxConcurrentStreams() * 5;
        if (max > 2147483647L) {
            max = 2147483647L;
        }
        int size = this.streams.size();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.pruneStart", this.connectionId, Long.toString(max), Integer.toString(size)));
        }
        int toClose = size - ((int) max);
        if (toClose < 1) {
            return;
        }
        TreeSet<Integer> candidatesStepTwo = new TreeSet<>();
        TreeSet<Integer> candidatesStepThree = new TreeSet<>();
        synchronized (this.priorityTreeLock) {
            for (AbstractNonZeroStream stream : this.streams.values()) {
                if (!(stream instanceof Stream) || !((Stream) stream).isActive()) {
                    if (stream.isClosedFinal()) {
                        candidatesStepThree.add(stream.getIdentifier());
                    } else if (stream.getChildStreams().size() == 0) {
                        AbstractStream parent = stream.getParentStream();
                        this.streams.remove(stream.getIdentifier());
                        stream.detachFromParent();
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("upgradeHandler.pruned", this.connectionId, stream.getIdAsString()));
                        }
                        toClose--;
                        if (toClose < 1) {
                            return;
                        }
                        while (toClose > 0 && parent.getIdAsInt() > 0 && parent.getIdAsInt() < stream.getIdAsInt() && parent.getChildStreams().isEmpty()) {
                            stream = (AbstractNonZeroStream) parent;
                            parent = stream.getParentStream();
                            this.streams.remove(stream.getIdentifier());
                            stream.detachFromParent();
                            if (log.isDebugEnabled()) {
                                log.debug(sm.getString("upgradeHandler.pruned", this.connectionId, stream.getIdAsString()));
                            }
                            toClose--;
                            if (toClose < 1) {
                                return;
                            } else {
                                candidatesStepTwo.remove(stream.getIdentifier());
                            }
                        }
                    } else {
                        candidatesStepTwo.add(stream.getIdentifier());
                    }
                }
            }
            Iterator<Integer> it = candidatesStepTwo.iterator();
            while (it.hasNext()) {
                Integer streamIdToRemove = it.next();
                removeStreamFromPriorityTree(streamIdToRemove);
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeHandler.pruned", this.connectionId, streamIdToRemove));
                }
                toClose--;
                if (toClose < 1) {
                    return;
                }
            }
            while (toClose > 0 && candidatesStepThree.size() > 0) {
                Integer streamIdToRemove2 = candidatesStepThree.pollLast();
                removeStreamFromPriorityTree(streamIdToRemove2);
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeHandler.prunedPriority", this.connectionId, streamIdToRemove2));
                }
                toClose--;
                if (toClose < 1) {
                    return;
                }
            }
            if (toClose > 0) {
                log.warn(sm.getString("upgradeHandler.pruneIncomplete", this.connectionId, Integer.toString(streamId), Integer.toString(toClose)));
            }
        }
    }

    private void removeStreamFromPriorityTree(Integer streamIdToRemove) {
        synchronized (this.priorityTreeLock) {
            AbstractNonZeroStream streamToRemove = (AbstractNonZeroStream) this.streams.remove(streamIdToRemove);
            Set<AbstractNonZeroStream> children = streamToRemove.getChildStreams();
            if (children.size() == 1) {
                children.iterator().next().rePrioritise(streamToRemove.getParentStream(), streamToRemove.getWeight());
            } else {
                int totalWeight = 0;
                for (AbstractNonZeroStream child : children) {
                    totalWeight += child.getWeight();
                }
                for (AbstractNonZeroStream child2 : children) {
                    children.iterator().next().rePrioritise(streamToRemove.getParentStream(), (streamToRemove.getWeight() * child2.getWeight()) / totalWeight);
                }
            }
            streamToRemove.detachFromParent();
            children.clear();
        }
    }

    void push(Request request, Stream associatedStream) throws IOException {
        if (this.localSettings.getMaxConcurrentStreams() < this.activeRemoteStreamCount.incrementAndGet()) {
            setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
            return;
        }
        this.socketWrapper.getLock().lock();
        try {
            Stream pushStream = createLocalStream(request);
            writeHeaders(associatedStream, pushStream.getIdAsInt(), request.getMimeHeaders(), false, 1024);
            this.socketWrapper.getLock().unlock();
            pushStream.sentPushPromise();
            processStreamOnContainerThread(pushStream);
        } catch (Throwable th) {
            this.socketWrapper.getLock().unlock();
            throw th;
        }
    }

    @Override // org.apache.coyote.http2.AbstractStream
    protected final String getConnectionId() {
        return this.connectionId;
    }

    @Override // org.apache.coyote.http2.AbstractStream
    protected final int getWeight() {
        return 0;
    }

    private void reduceOverheadCount(FrameType frameType) {
        updateOverheadCount(frameType, -20);
    }

    private void increaseOverheadCount(FrameType frameType) {
        updateOverheadCount(frameType, getProtocol().getOverheadCountFactor());
    }

    private void increaseOverheadCount(FrameType frameType, int increment) {
        updateOverheadCount(frameType, increment);
    }

    private void updateOverheadCount(FrameType frameType, int increment) {
        long newOverheadCount = this.overheadCount.addAndGet(increment);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.overheadChange", this.connectionId, getIdAsString(), frameType.name(), Long.valueOf(newOverheadCount)));
        }
    }

    public boolean fill(boolean block, byte[] data, int offset, int length) throws IOException {
        int len = length;
        int pos = offset;
        boolean nextReadBlock = block;
        while (len > 0) {
            if (nextReadBlock) {
                this.socketWrapper.setReadTimeout(this.protocol.getReadTimeout());
            } else {
                this.socketWrapper.setReadTimeout(-1L);
            }
            int thisRead = this.socketWrapper.read(nextReadBlock, data, pos, len);
            if (thisRead == 0) {
                if (nextReadBlock) {
                    throw new IllegalStateException();
                }
                return false;
            }
            if (thisRead == -1) {
                if (this.connectionState.get().isNewStreamAllowed()) {
                    throw new EOFException();
                }
                return false;
            }
            pos += thisRead;
            len -= thisRead;
            nextReadBlock = true;
        }
        return true;
    }

    public int getMaxFrameSize() {
        return this.localSettings.getMaxFrameSize();
    }

    public HpackDecoder getHpackDecoder() {
        if (this.hpackDecoder == null) {
            this.hpackDecoder = new HpackDecoder(this.localSettings.getHeaderTableSize());
        }
        return this.hpackDecoder;
    }

    public ByteBuffer startRequestBodyFrame(int streamId, int payloadSize, boolean endOfStream) throws Http2Exception {
        reduceOverheadCount(FrameType.DATA);
        if (!endOfStream) {
            int overheadThreshold = this.protocol.getOverheadDataThreshold();
            int average = (this.lastNonFinalDataPayload >> 1) + (payloadSize >> 1);
            this.lastNonFinalDataPayload = payloadSize;
            if (average == 0) {
                average = 1;
            }
            if (average < overheadThreshold) {
                increaseOverheadCount(FrameType.DATA, overheadThreshold / average);
            }
        }
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, true);
        abstractNonZeroStream.checkState(FrameType.DATA);
        abstractNonZeroStream.receivedData(payloadSize);
        ByteBuffer result = abstractNonZeroStream.getInputByteBuffer();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.startRequestBodyFrame.result", getConnectionId(), abstractNonZeroStream.getIdAsString(), result));
        }
        return result;
    }

    public void endRequestBodyFrame(int streamId, int dataLength) throws IOException, Http2Exception {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, true);
        if (abstractNonZeroStream instanceof Stream) {
            ((Stream) abstractNonZeroStream).getInputBuffer().onDataAvailable();
        } else {
            onSwallowedDataFramePayload(streamId, dataLength);
        }
    }

    public void receivedEndOfStream(int streamId) throws ConnectionException {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, this.connectionState.get().isNewStreamAllowed());
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream) abstractNonZeroStream;
            stream.receivedEndOfStream();
            if (!stream.isActive()) {
                setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
            }
        }
    }

    public void onSwallowedDataFramePayload(int streamId, int swallowedDataBytesCount) throws IOException {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId);
        writeWindowUpdate(abstractNonZeroStream, swallowedDataBytesCount, false);
    }

    public HpackDecoder.HeaderEmitter headersStart(int streamId, boolean headersEndStream) throws IOException, Http2Exception {
        checkPauseState();
        if (this.connectionState.get().isNewStreamAllowed()) {
            Stream stream = getStream(streamId, false);
            if (stream == null) {
                stream = createRemoteStream(streamId);
            }
            if (streamId < this.maxActiveRemoteStreamId) {
                throw new ConnectionException(sm.getString("upgradeHandler.stream.old", Integer.valueOf(streamId), Integer.valueOf(this.maxActiveRemoteStreamId)), Http2Error.PROTOCOL_ERROR);
            }
            stream.checkState(FrameType.HEADERS);
            stream.receivedStartOfHeaders(headersEndStream);
            closeIdleStreams(streamId);
            return stream;
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.noNewStreams", this.connectionId, Integer.toString(streamId)));
        }
        reduceOverheadCount(FrameType.HEADERS);
        return HEADER_SINK;
    }

    private void closeIdleStreams(int newMaxActiveRemoteStreamId) {
        ConcurrentNavigableMap<Integer, AbstractNonZeroStream> subMap = this.streams.subMap((boolean) Integer.valueOf(this.maxActiveRemoteStreamId), false, (boolean) Integer.valueOf(newMaxActiveRemoteStreamId), false);
        for (AbstractNonZeroStream stream : subMap.values()) {
            if (stream instanceof Stream) {
                ((Stream) stream).closeIfIdle();
            }
        }
        this.maxActiveRemoteStreamId = newMaxActiveRemoteStreamId;
    }

    public void reprioritise(int streamId, int parentStreamId, boolean exclusive, int weight) throws Http2Exception {
        if (streamId == parentStreamId) {
            throw new ConnectionException(sm.getString("upgradeHandler.dependency.invalid", getConnectionId(), Integer.valueOf(streamId)), Http2Error.PROTOCOL_ERROR);
        }
        increaseOverheadCount(FrameType.PRIORITY);
        synchronized (this.priorityTreeLock) {
            AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId);
            if (abstractNonZeroStream == null) {
                abstractNonZeroStream = createRemoteStream(streamId);
            }
            AbstractStream parentStream = getAbstractNonZeroStream(parentStreamId);
            if (parentStream == null) {
                parentStream = this;
            }
            abstractNonZeroStream.rePrioritise(parentStream, exclusive, weight);
        }
    }

    public void headersContinue(int payloadSize, boolean endOfHeaders) {
        int overheadThreshold;
        if (!endOfHeaders && payloadSize < (overheadThreshold = getProtocol().getOverheadContinuationThreshold())) {
            if (payloadSize == 0) {
                increaseOverheadCount(FrameType.HEADERS, overheadThreshold);
            } else {
                increaseOverheadCount(FrameType.HEADERS, overheadThreshold / payloadSize);
            }
        }
    }

    public void headersEnd(int streamId) throws Http2Exception {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, this.connectionState.get().isNewStreamAllowed());
        if (abstractNonZeroStream instanceof Stream) {
            setMaxProcessedStream(streamId);
            Stream stream = (Stream) abstractNonZeroStream;
            if (stream.isActive() && stream.receivedEndOfHeaders()) {
                if (this.localSettings.getMaxConcurrentStreams() < this.activeRemoteStreamCount.incrementAndGet()) {
                    setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
                    increaseOverheadCount(FrameType.HEADERS);
                    throw new StreamException(sm.getString("upgradeHandler.tooManyRemoteStreams", Long.toString(this.localSettings.getMaxConcurrentStreams())), Http2Error.REFUSED_STREAM, streamId);
                }
                reduceOverheadCount(FrameType.HEADERS);
                processStreamOnContainerThread(stream);
            }
        }
    }

    private void setMaxProcessedStream(int streamId) {
        if (this.maxProcessedStreamId < streamId) {
            this.maxProcessedStreamId = streamId;
        }
    }

    public void reset(int streamId, long errorCode) throws Http2Exception {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.reset.receive", getConnectionId(), Integer.toString(streamId), Long.toString(errorCode)));
        }
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, true);
        abstractNonZeroStream.checkState(FrameType.RST);
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream) abstractNonZeroStream;
            boolean active = stream.isActive();
            stream.receiveReset(errorCode);
            if (active) {
                this.activeRemoteStreamCount.decrementAndGet();
            }
        }
    }

    public void setting(Setting setting, long value) throws ConnectionException {
        increaseOverheadCount(FrameType.SETTINGS);
        if (setting == null) {
            return;
        }
        if (setting == Setting.INITIAL_WINDOW_SIZE) {
            long oldValue = this.remoteSettings.getInitialWindowSize();
            this.remoteSettings.set(setting, value);
            int diff = (int) (value - oldValue);
            for (AbstractNonZeroStream stream : this.streams.values()) {
                try {
                    stream.incrementWindowSize(diff);
                } catch (Http2Exception h2e) {
                    ((Stream) stream).close(new StreamException(sm.getString("upgradeHandler.windowSizeTooBig", this.connectionId, stream.getIdAsString()), h2e.getError(), stream.getIdAsInt()));
                }
            }
            return;
        }
        this.remoteSettings.set(setting, value);
    }

    public void settingsEnd(boolean ack) throws IOException {
        if (ack) {
            if (!this.localSettings.ack()) {
                log.warn(sm.getString("upgradeHandler.unexpectedAck", this.connectionId, getIdAsString()));
            }
        } else {
            this.socketWrapper.getLock().lock();
            try {
                this.socketWrapper.write(true, SETTINGS_ACK, 0, SETTINGS_ACK.length);
                this.socketWrapper.flush(true);
            } finally {
                this.socketWrapper.getLock().unlock();
            }
        }
    }

    public void pingReceive(byte[] payload, boolean ack) throws IOException {
        if (!ack) {
            increaseOverheadCount(FrameType.PING);
        }
        this.pingManager.receivePing(payload, ack);
    }

    public void goaway(int lastStreamId, long errorCode, String debugData) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.goaway.debug", this.connectionId, Integer.toString(lastStreamId), Long.toHexString(errorCode), debugData));
        }
        close();
    }

    public void incrementWindowSize(int streamId, int increment) throws Http2Exception {
        int average = (this.lastWindowUpdate >> 1) + (increment >> 1);
        int overheadThreshold = this.protocol.getOverheadWindowUpdateThreshold();
        this.lastWindowUpdate = increment;
        if (average == 0) {
            average = 1;
        }
        if (streamId == 0) {
            if (average < overheadThreshold) {
                increaseOverheadCount(FrameType.WINDOW_UPDATE, overheadThreshold / average);
            }
            incrementWindowSize(increment);
        } else {
            AbstractNonZeroStream stream = getAbstractNonZeroStream(streamId, true);
            if (average < overheadThreshold && increment < stream.getConnectionAllocationRequested()) {
                increaseOverheadCount(FrameType.WINDOW_UPDATE, overheadThreshold / average);
            }
            stream.checkState(FrameType.WINDOW_UPDATE);
            stream.incrementWindowSize(increment);
        }
    }

    public void onSwallowedUnknownFrame(int streamId, int frameTypeId, int flags, int size) throws IOException {
    }

    void replaceStream(AbstractNonZeroStream original, AbstractNonZeroStream replacement) {
        synchronized (this.priorityTreeLock) {
            AbstractNonZeroStream current = (AbstractNonZeroStream) this.streams.get(original.getIdentifier());
            if (current instanceof Stream) {
                this.streams.put(original.getIdentifier(), replacement);
                original.replaceStream(replacement);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Http2UpgradeHandler$PingManager.class */
    protected class PingManager {
        protected boolean initiateDisabled = false;
        protected final long pingIntervalNano = 10000000000L;
        protected int sequence = 0;
        protected long lastPingNanoTime = Long.MIN_VALUE;
        protected Queue<PingRecord> inflightPings = new ConcurrentLinkedQueue();
        protected Queue<Long> roundTripTimes = new ConcurrentLinkedQueue();

        protected PingManager() {
        }

        public void sendPing(boolean force) throws IOException {
            if (this.initiateDisabled) {
                return;
            }
            long now = System.nanoTime();
            if (force || now - this.lastPingNanoTime > 10000000000L) {
                this.lastPingNanoTime = now;
                byte[] payload = new byte[8];
                Http2UpgradeHandler.this.socketWrapper.getLock().lock();
                try {
                    int sentSequence = this.sequence + 1;
                    this.sequence = sentSequence;
                    PingRecord pingRecord = new PingRecord(sentSequence, now);
                    this.inflightPings.add(pingRecord);
                    ByteUtil.set31Bits(payload, 4, sentSequence);
                    Http2UpgradeHandler.this.socketWrapper.write(true, Http2UpgradeHandler.PING, 0, Http2UpgradeHandler.PING.length);
                    Http2UpgradeHandler.this.socketWrapper.write(true, payload, 0, payload.length);
                    Http2UpgradeHandler.this.socketWrapper.flush(true);
                    Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                } catch (Throwable th) {
                    Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                    throw th;
                }
            }
        }

        public void receivePing(byte[] payload, boolean ack) throws IOException {
            PingRecord pingRecord;
            if (ack) {
                int receivedSequence = ByteUtil.get31Bits(payload, 4);
                PingRecord pingRecordPoll = this.inflightPings.poll();
                while (true) {
                    pingRecord = pingRecordPoll;
                    if (pingRecord == null || pingRecord.getSequence() >= receivedSequence) {
                        break;
                    } else {
                        pingRecordPoll = this.inflightPings.poll();
                    }
                }
                if (pingRecord != null) {
                    long roundTripTime = System.nanoTime() - pingRecord.getSentNanoTime();
                    this.roundTripTimes.add(Long.valueOf(roundTripTime));
                    while (this.roundTripTimes.size() > 3) {
                        this.roundTripTimes.poll();
                    }
                    if (Http2UpgradeHandler.log.isDebugEnabled()) {
                        Http2UpgradeHandler.log.debug(Http2UpgradeHandler.sm.getString("pingManager.roundTripTime", Http2UpgradeHandler.this.connectionId, Long.valueOf(roundTripTime)));
                        return;
                    }
                    return;
                }
                return;
            }
            Http2UpgradeHandler.this.socketWrapper.getLock().lock();
            try {
                Http2UpgradeHandler.this.socketWrapper.write(true, Http2UpgradeHandler.PING_ACK, 0, Http2UpgradeHandler.PING_ACK.length);
                Http2UpgradeHandler.this.socketWrapper.write(true, payload, 0, payload.length);
                Http2UpgradeHandler.this.socketWrapper.flush(true);
                Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
            } catch (Throwable th) {
                Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                throw th;
            }
        }

        public long getRoundTripTimeNano() {
            return (long) this.roundTripTimes.stream().mapToLong((v0) -> {
                return v0.longValue();
            }).average().orElse(0.0d);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Http2UpgradeHandler$PingRecord.class */
    protected static class PingRecord {
        private final int sequence;
        private final long sentNanoTime;

        public PingRecord(int sequence, long sentNanoTime) {
            this.sequence = sequence;
            this.sentNanoTime = sentNanoTime;
        }

        public int getSequence() {
            return this.sequence;
        }

        public long getSentNanoTime() {
            return this.sentNanoTime;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Http2UpgradeHandler$ConnectionState.class */
    private enum ConnectionState {
        NEW(true),
        CONNECTED(true),
        PAUSING(true),
        PAUSED(false),
        CLOSED(false);

        private final boolean newStreamsAllowed;

        ConnectionState(boolean newStreamsAllowed) {
            this.newStreamsAllowed = newStreamsAllowed;
        }

        public boolean isNewStreamAllowed() {
            return this.newStreamsAllowed;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Http2UpgradeHandler$DefaultHeaderFrameBuffers.class */
    private class DefaultHeaderFrameBuffers implements HeaderFrameBuffers {
        private final byte[] header = new byte[9];
        private ByteBuffer payload;

        DefaultHeaderFrameBuffers(int initialPayloadSize) {
            this.payload = ByteBuffer.allocate(initialPayloadSize);
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void startFrame() {
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endFrame() throws IOException {
            try {
                Http2UpgradeHandler.this.socketWrapper.write(true, this.header, 0, this.header.length);
                Http2UpgradeHandler.this.socketWrapper.write(true, this.payload);
                Http2UpgradeHandler.this.socketWrapper.flush(true);
            } catch (IOException ioe) {
                Http2UpgradeHandler.this.handleAppInitiatedIOException(ioe);
            }
            this.payload.clear();
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endHeaders() {
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public byte[] getHeader() {
            return this.header;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public ByteBuffer getPayload() {
            return this.payload;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void expandPayload() {
            this.payload = ByteBuffer.allocate(this.payload.capacity() * 2);
        }
    }
}
