package org.apache.coyote.http2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.apache.coyote.http11.filters.VoidOutputFilter;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.WriteBuffer;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Stream.class */
class Stream extends AbstractNonZeroStream implements HpackDecoder.HeaderEmitter {
    private static final int HEADER_STATE_START = 0;
    private static final int HEADER_STATE_PSEUDO = 1;
    private static final int HEADER_STATE_REGULAR = 2;
    private static final int HEADER_STATE_TRAILER = 3;
    private static final MimeHeaders ACK_HEADERS;
    private volatile long contentLengthReceived;
    private final Http2UpgradeHandler handler;
    private final WindowAllocationManager allocationManager;
    private final Request coyoteRequest;
    private final Response coyoteResponse;
    private final StreamInputBuffer inputBuffer;
    private final StreamOutputBuffer streamOutputBuffer;
    private final Http2OutputBuffer http2OutputBuffer;
    private int headerState;
    private StreamException headerException;
    private volatile StringBuilder cookieHeader;
    private volatile boolean hostHeaderSeen;
    private Object pendingWindowUpdateForStreamLock;
    private int pendingWindowUpdateForStream;
    private static final Log log = LogFactory.getLog((Class<?>) Stream.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Stream.class);
    private static final Integer HTTP_UPGRADE_STREAM = 1;
    private static final Set<String> HTTP_CONNECTION_SPECIFIC_HEADERS = new HashSet();

    static {
        Response response = new Response();
        response.setStatus(100);
        StreamProcessor.prepareHeaders(null, response, true, null, null);
        ACK_HEADERS = response.getMimeHeaders();
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("connection");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("proxy-connection");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("keep-alive");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("transfer-encoding");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add(org.apache.tomcat.websocket.Constants.CONNECTION_HEADER_VALUE);
    }

    Stream(Integer identifier, Http2UpgradeHandler handler) {
        this(identifier, handler, null);
    }

    Stream(Integer identifier, Http2UpgradeHandler handler, Request coyoteRequest) {
        super(handler.getConnectionId(), identifier);
        this.contentLengthReceived = 0L;
        this.allocationManager = new WindowAllocationManager(this);
        this.coyoteResponse = new Response();
        this.streamOutputBuffer = new StreamOutputBuffer();
        this.http2OutputBuffer = new Http2OutputBuffer(this.coyoteResponse, this.streamOutputBuffer);
        this.headerState = 0;
        this.headerException = null;
        this.cookieHeader = null;
        this.hostHeaderSeen = false;
        this.pendingWindowUpdateForStreamLock = new Object();
        this.pendingWindowUpdateForStream = 0;
        this.handler = handler;
        handler.addChild(this);
        setWindowSize(handler.getRemoteSettings().getInitialWindowSize());
        if (coyoteRequest == null) {
            this.coyoteRequest = new Request();
            this.inputBuffer = new StandardStreamInputBuffer();
            this.coyoteRequest.setInputBuffer(this.inputBuffer);
        } else {
            this.coyoteRequest = coyoteRequest;
            this.inputBuffer = new SavedRequestStreamInputBuffer((SavedRequestInputFilter) coyoteRequest.getInputBuffer());
            this.state.receivedStartOfHeaders();
            if (HTTP_UPGRADE_STREAM.equals(identifier)) {
                try {
                    prepareRequest();
                } catch (IllegalArgumentException e) {
                    this.coyoteResponse.setStatus(400);
                    this.coyoteResponse.setError();
                }
            }
            this.state.receivedEndOfStream();
        }
        this.coyoteRequest.setSendfile(handler.hasAsyncIO() && handler.getProtocol().getUseSendfile());
        this.coyoteResponse.setOutputBuffer(this.http2OutputBuffer);
        this.coyoteRequest.setResponse(this.coyoteResponse);
        this.coyoteRequest.protocol().setString("HTTP/2.0");
        if (this.coyoteRequest.getStartTime() < 0) {
            this.coyoteRequest.setStartTime(System.currentTimeMillis());
        }
    }

    private void prepareRequest() {
        if (this.coyoteRequest.scheme().isNull()) {
            if (((AbstractHttp11Protocol) this.handler.getProtocol().getHttp11Protocol()).isSSLEnabled()) {
                this.coyoteRequest.scheme().setString("https");
            } else {
                this.coyoteRequest.scheme().setString("http");
            }
        }
        MessageBytes hostValueMB = this.coyoteRequest.getMimeHeaders().getUniqueValue("host");
        if (hostValueMB == null) {
            throw new IllegalArgumentException();
        }
        hostValueMB.toBytes();
        ByteChunk valueBC = hostValueMB.getByteChunk();
        byte[] valueB = valueBC.getBytes();
        int valueL = valueBC.getLength();
        int valueS = valueBC.getStart();
        int colonPos = Host.parse(hostValueMB);
        if (colonPos != -1) {
            int port = 0;
            for (int i = colonPos + 1; i < valueL; i++) {
                char c = (char) valueB[i + valueS];
                if (c < '0' || c > '9') {
                    throw new IllegalArgumentException();
                }
                port = ((port * 10) + c) - 48;
            }
            this.coyoteRequest.setServerPort(port);
            valueL = colonPos;
        }
        char[] hostNameC = new char[valueL];
        for (int i2 = 0; i2 < valueL; i2++) {
            hostNameC[i2] = (char) valueB[i2 + valueS];
        }
        this.coyoteRequest.serverName().setChars(hostNameC, 0, valueL);
    }

    final void receiveReset(long errorCode) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.reset.receive", getConnectionId(), getIdAsString(), Long.toString(errorCode)));
        }
        this.state.receivedReset();
        if (this.inputBuffer != null) {
            this.inputBuffer.receiveReset();
        }
        cancelAllocationRequests();
    }

    final void cancelAllocationRequests() {
        this.allocationManager.notifyAny();
    }

    @Override // org.apache.coyote.http2.AbstractStream
    final synchronized void incrementWindowSize(int windowSizeIncrement) throws Http2Exception {
        boolean notify = getWindowSize() < 1;
        super.incrementWindowSize(windowSizeIncrement);
        if (notify && getWindowSize() > 0) {
            this.allocationManager.notifyStream();
        }
    }

    final synchronized int reserveWindowSize(int reservation, boolean block) throws IOException {
        int allocation;
        long windowSize = getWindowSize();
        while (windowSize < 1) {
            if (!canWrite()) {
                throw new CloseNowException(sm.getString("stream.notWritable", getConnectionId(), getIdAsString()));
            }
            if (block) {
                try {
                    long writeTimeout = this.handler.getProtocol().getStreamWriteTimeout();
                    this.allocationManager.waitForStream(writeTimeout);
                    windowSize = getWindowSize();
                    if (windowSize == 0) {
                        doStreamCancel(sm.getString("stream.writeTimeout"), Http2Error.ENHANCE_YOUR_CALM);
                    }
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            } else {
                this.allocationManager.waitForStreamNonBlocking();
                return 0;
            }
        }
        if (windowSize < reservation) {
            allocation = (int) windowSize;
        } else {
            allocation = reservation;
        }
        decrementWindowSize(allocation);
        return allocation;
    }

    void doStreamCancel(String msg, Http2Error error) throws CloseNowException {
        StreamException se = new StreamException(msg, error, getIdAsInt());
        this.streamOutputBuffer.closed = true;
        this.coyoteResponse.setError();
        this.coyoteResponse.setErrorReported();
        this.streamOutputBuffer.reset = se;
        throw new CloseNowException(msg, se);
    }

    void waitForConnectionAllocation(long timeout) throws InterruptedException {
        this.allocationManager.waitForConnection(timeout);
    }

    void waitForConnectionAllocationNonBlocking() {
        this.allocationManager.waitForConnectionNonBlocking();
    }

    void notifyConnection() {
        this.allocationManager.notifyConnection();
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public final void emitHeader(String name, String value) throws HpackException {
        String uri;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.header.debug", getConnectionId(), getIdAsString(), name, value));
        }
        if (!name.toLowerCase(Locale.US).equals(name)) {
            throw new HpackException(sm.getString("stream.header.case", getConnectionId(), getIdAsString(), name));
        }
        if (HTTP_CONNECTION_SPECIFIC_HEADERS.contains(name)) {
            throw new HpackException(sm.getString("stream.header.connection", getConnectionId(), getIdAsString(), name));
        }
        if ("te".equals(name) && !"trailers".equals(value)) {
            throw new HpackException(sm.getString("stream.header.te", getConnectionId(), getIdAsString(), value));
        }
        if (this.headerException != null) {
            return;
        }
        if (name.length() == 0) {
            throw new HpackException(sm.getString("stream.header.empty", getConnectionId(), getIdAsString()));
        }
        boolean pseudoHeader = name.charAt(0) == ':';
        if (pseudoHeader && this.headerState != 1) {
            this.headerException = new StreamException(sm.getString("stream.header.unexpectedPseudoHeader", getConnectionId(), getIdAsString(), name), Http2Error.PROTOCOL_ERROR, getIdAsInt());
            return;
        }
        if (this.headerState == 1 && !pseudoHeader) {
            this.headerState = 2;
        }
        switch (name) {
            case ":method":
                if (this.coyoteRequest.method().isNull()) {
                    this.coyoteRequest.method().setString(value);
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":method"));
            case ":scheme":
                if (this.coyoteRequest.scheme().isNull()) {
                    this.coyoteRequest.scheme().setString(value);
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":scheme"));
            case ":path":
                if (!this.coyoteRequest.requestURI().isNull()) {
                    throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":path"));
                }
                if (value.length() == 0) {
                    throw new HpackException(sm.getString("stream.header.noPath", getConnectionId(), getIdAsString()));
                }
                int queryStart = value.indexOf(63);
                if (queryStart == -1) {
                    uri = value;
                } else {
                    uri = value.substring(0, queryStart);
                    String query = value.substring(queryStart + 1);
                    this.coyoteRequest.queryString().setString(query);
                }
                byte[] uriBytes = uri.getBytes(StandardCharsets.ISO_8859_1);
                this.coyoteRequest.requestURI().setBytes(uriBytes, 0, uriBytes.length);
                return;
            case ":authority":
                if (this.coyoteRequest.serverName().isNull()) {
                    parseAuthority(value, false);
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":authority"));
            case "cookie":
                if (this.cookieHeader == null) {
                    this.cookieHeader = new StringBuilder();
                } else {
                    this.cookieHeader.append("; ");
                }
                this.cookieHeader.append(value);
                return;
            case "host":
                if (this.coyoteRequest.serverName().isNull()) {
                    this.hostHeaderSeen = true;
                    parseAuthority(value, true);
                    return;
                } else {
                    if (!this.hostHeaderSeen) {
                        this.hostHeaderSeen = true;
                        compareAuthority(value);
                        return;
                    }
                    throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), "host"));
                }
            default:
                if (this.headerState != 3 || this.handler.getProtocol().isTrailerHeaderAllowed(name)) {
                    if ("expect".equals(name) && "100-continue".equals(value)) {
                        this.coyoteRequest.setExpectation(true);
                    }
                    if (pseudoHeader) {
                        this.headerException = new StreamException(sm.getString("stream.header.unknownPseudoHeader", getConnectionId(), getIdAsString(), name), Http2Error.PROTOCOL_ERROR, getIdAsInt());
                    }
                    if (this.headerState == 3) {
                        this.coyoteRequest.getTrailerFields().put(name, value);
                        return;
                    } else {
                        this.coyoteRequest.getMimeHeaders().addValue(name).setString(value);
                        return;
                    }
                }
                return;
        }
    }

    void configureVoidOutputFilter() {
        addOutputFilter(new VoidOutputFilter());
        this.streamOutputBuffer.closed = true;
    }

    private void parseAuthority(String value, boolean host) throws HpackException {
        try {
            int i = Host.parse(value);
            if (i > -1) {
                this.coyoteRequest.serverName().setString(value.substring(0, i));
                this.coyoteRequest.setServerPort(Integer.parseInt(value.substring(i + 1)));
            } else {
                this.coyoteRequest.serverName().setString(value);
            }
        } catch (IllegalArgumentException e) {
            StringManager stringManager = sm;
            Object[] objArr = new Object[4];
            objArr[0] = getConnectionId();
            objArr[1] = getIdAsString();
            objArr[2] = host ? "host" : ":authority";
            objArr[3] = value;
            throw new HpackException(stringManager.getString("stream.header.invalid", objArr));
        }
    }

    private void compareAuthority(String value) throws HpackException {
        try {
            int i = Host.parse(value);
            if (i != -1 || (value.equals(this.coyoteRequest.serverName().getString()) && this.coyoteRequest.getServerPort() == -1)) {
                if (i > -1) {
                    if (value.substring(0, i).equals(this.coyoteRequest.serverName().getString()) && Integer.parseInt(value.substring(i + 1)) == this.coyoteRequest.getServerPort()) {
                        return;
                    }
                } else {
                    return;
                }
            }
            throw new HpackException(sm.getString("stream.host.inconsistent", getConnectionId(), getIdAsString(), value, this.coyoteRequest.serverName().getString(), Integer.toString(this.coyoteRequest.getServerPort())));
        } catch (IllegalArgumentException e) {
            throw new HpackException(sm.getString("stream.header.invalid", getConnectionId(), getIdAsString(), "host", value));
        }
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void setHeaderException(StreamException streamException) {
        if (this.headerException == null) {
            this.headerException = streamException;
        }
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void validateHeaders() throws StreamException {
        if (this.headerException == null) {
        } else {
            throw this.headerException;
        }
    }

    final boolean receivedEndOfHeaders() throws ConnectionException {
        if (this.coyoteRequest.method().isNull() || this.coyoteRequest.scheme().isNull() || (!this.coyoteRequest.method().equalsIgnoreCase("CONNECT") && this.coyoteRequest.requestURI().isNull())) {
            throw new ConnectionException(sm.getString("stream.header.required", getConnectionId(), getIdAsString()), Http2Error.PROTOCOL_ERROR);
        }
        if (this.cookieHeader != null) {
            this.coyoteRequest.getMimeHeaders().addValue("cookie").setString(this.cookieHeader.toString());
        }
        return this.headerState == 2 || this.headerState == 1;
    }

    final void writeHeaders() throws IOException {
        boolean endOfStream = this.streamOutputBuffer.hasNoBody() && this.coyoteResponse.getTrailerFields() == null;
        this.handler.writeHeaders(this, 0, this.coyoteResponse.getMimeHeaders(), endOfStream, 1024);
    }

    final void addOutputFilter(OutputFilter filter) {
        this.http2OutputBuffer.addFilter(filter);
    }

    final void writeTrailers() throws IOException {
        Supplier<Map<String, String>> supplier = this.coyoteResponse.getTrailerFields();
        if (supplier == null) {
            return;
        }
        MimeHeaders mimeHeaders = this.coyoteResponse.getMimeHeaders();
        mimeHeaders.recycle();
        Map<String, String> headerMap = supplier.get();
        if (headerMap == null) {
            headerMap = Collections.emptyMap();
        }
        for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
            MessageBytes mb = mimeHeaders.addValue(headerEntry.getKey());
            mb.setString(headerEntry.getValue());
        }
        this.handler.writeHeaders(this, 0, mimeHeaders, true, 1024);
    }

    final void writeAck() throws IOException {
        this.handler.writeHeaders(this, 0, ACK_HEADERS, false, 64);
    }

    @Override // org.apache.coyote.http2.AbstractStream
    final String getConnectionId() {
        return this.handler.getConnectionId();
    }

    final Request getCoyoteRequest() {
        return this.coyoteRequest;
    }

    final Response getCoyoteResponse() {
        return this.coyoteResponse;
    }

    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    final ByteBuffer getInputByteBuffer() {
        if (this.inputBuffer == null) {
            return ZERO_LENGTH_BYTEBUFFER;
        }
        return this.inputBuffer.getInBuffer();
    }

    final void receivedStartOfHeaders(boolean headersEndStream) throws Http2Exception {
        if (this.headerState == 0) {
            this.headerState = 1;
            this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxHeaderCount());
            this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxHeaderSize());
        } else if (this.headerState == 1 || this.headerState == 2) {
            if (headersEndStream) {
                this.headerState = 3;
                this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxTrailerCount());
                this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxTrailerSize());
            } else {
                throw new ConnectionException(sm.getString("stream.trailerHeader.noEndOfStream", getConnectionId(), getIdAsString()), Http2Error.PROTOCOL_ERROR);
            }
        }
        this.state.receivedStartOfHeaders();
    }

    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    final void receivedData(int payloadSize) throws Http2Exception {
        this.contentLengthReceived += payloadSize;
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1 && this.contentLengthReceived > contentLengthHeader) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", getConnectionId(), getIdAsString(), Long.valueOf(contentLengthHeader), Long.valueOf(this.contentLengthReceived)), Http2Error.PROTOCOL_ERROR);
        }
    }

    final void receivedEndOfStream() throws ConnectionException {
        if (isContentLengthInconsistent()) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", getConnectionId(), getIdAsString(), Long.valueOf(this.coyoteRequest.getContentLengthLong()), Long.valueOf(this.contentLengthReceived)), Http2Error.PROTOCOL_ERROR);
        }
        this.state.receivedEndOfStream();
        if (this.inputBuffer != null) {
            this.inputBuffer.notifyEof();
        }
    }

    final boolean isContentLengthInconsistent() {
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1 && this.contentLengthReceived != contentLengthHeader) {
            return true;
        }
        return false;
    }

    final void sentHeaders() {
        this.state.sentHeaders();
    }

    final void sentEndOfStream() {
        this.streamOutputBuffer.endOfStreamSent = true;
        this.state.sentEndOfStream();
    }

    final boolean isReadyForWrite() {
        return this.streamOutputBuffer.isReady();
    }

    final boolean flush(boolean block) throws IOException {
        return this.streamOutputBuffer.flush(block);
    }

    final StreamInputBuffer getInputBuffer() {
        return this.inputBuffer;
    }

    final HttpOutputBuffer getOutputBuffer() {
        return this.http2OutputBuffer;
    }

    final void sentPushPromise() {
        this.state.sentPushPromise();
    }

    final boolean isActive() {
        return this.state.isActive();
    }

    final boolean canWrite() {
        return this.state.canWrite();
    }

    final void closeIfIdle() {
        this.state.closeIfIdle();
    }

    final boolean isInputFinished() {
        return !this.state.isFrameTypePermitted(FrameType.DATA);
    }

    final void close(Http2Exception http2Exception) {
        if (http2Exception instanceof StreamException) {
            try {
                StreamException se = (StreamException) http2Exception;
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("stream.reset.send", getConnectionId(), getIdAsString(), se.getError()));
                }
                this.handler.sendStreamReset(this.state, se);
                cancelAllocationRequests();
                if (this.inputBuffer != null) {
                    this.inputBuffer.swallowUnread();
                }
            } catch (IOException ioe) {
                ConnectionException ce = new ConnectionException(sm.getString("stream.reset.fail", getConnectionId(), getIdAsString()), Http2Error.PROTOCOL_ERROR, ioe);
                this.handler.closeConnection(ce);
            }
        } else {
            this.handler.closeConnection(http2Exception);
        }
        recycle();
    }

    final void recycle() {
        int remaining;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.recycle", getConnectionId(), getIdAsString()));
        }
        ByteBuffer inputByteBuffer = getInputByteBuffer();
        if (inputByteBuffer == null) {
            remaining = 0;
        } else {
            remaining = inputByteBuffer.remaining();
        }
        this.handler.replaceStream(this, new RecycledStream(getConnectionId(), getIdentifier(), this.state, remaining));
    }

    final boolean isPushSupported() {
        return this.handler.getRemoteSettings().getEnablePush();
    }

    final void push(Request request) throws PrivilegedActionException, IOException {
        if (!isPushSupported() || getIdAsInt() % 2 == 0) {
            return;
        }
        request.getMimeHeaders().addValue(":method").duplicate(request.method());
        request.getMimeHeaders().addValue(":scheme").duplicate(request.scheme());
        StringBuilder path = new StringBuilder(request.requestURI().toString());
        if (!request.queryString().isNull()) {
            path.append('?');
            path.append(request.queryString().toString());
        }
        request.getMimeHeaders().addValue(":path").setString(path.toString());
        if ((!request.scheme().equals("http") || request.getServerPort() != 80) && (!request.scheme().equals("https") || request.getServerPort() != 443)) {
            request.getMimeHeaders().addValue(":authority").setString(request.serverName().getString() + ":" + request.getServerPort());
        } else {
            request.getMimeHeaders().addValue(":authority").duplicate(request.serverName());
        }
        push(this.handler, request, this);
    }

    boolean isTrailerFieldsReady() {
        return !this.state.canRead();
    }

    boolean isTrailerFieldsSupported() {
        return !this.streamOutputBuffer.endOfStreamSent;
    }

    StreamException getResetException() {
        return this.streamOutputBuffer.reset;
    }

    int getWindowUpdateSizeToWrite(int increment) {
        int result;
        int threshold = this.handler.getProtocol().getOverheadWindowUpdateThreshold();
        synchronized (this.pendingWindowUpdateForStreamLock) {
            if (increment > threshold) {
                result = increment + this.pendingWindowUpdateForStream;
                this.pendingWindowUpdateForStream = 0;
            } else {
                this.pendingWindowUpdateForStream += increment;
                if (this.pendingWindowUpdateForStream > threshold) {
                    result = this.pendingWindowUpdateForStream;
                    this.pendingWindowUpdateForStream = 0;
                } else {
                    result = 0;
                }
            }
        }
        return result;
    }

    private static void push(Http2UpgradeHandler handler, Request request, Stream stream) throws PrivilegedActionException, IOException {
        if (org.apache.coyote.Constants.IS_SECURITY_ENABLED) {
            try {
                AccessController.doPrivileged(new PrivilegedPush(handler, request, stream));
                return;
            } catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new IOException(ex);
            }
        }
        handler.push(request, stream);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Stream$PrivilegedPush.class */
    private static class PrivilegedPush implements PrivilegedExceptionAction<Void> {
        private final Http2UpgradeHandler handler;
        private final Request request;
        private final Stream stream;

        PrivilegedPush(Http2UpgradeHandler handler, Request request, Stream stream) {
            this.handler = handler;
            this.request = request;
            this.stream = stream;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws IOException {
            this.handler.push(this.request, this.stream);
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Stream$StreamOutputBuffer.class */
    class StreamOutputBuffer implements HttpOutputBuffer, WriteBuffer.Sink {
        private boolean dataLeft;
        private final ByteBuffer buffer = ByteBuffer.allocate(8192);
        private final WriteBuffer writeBuffer = new WriteBuffer(32768);
        private volatile long written = 0;
        private int streamReservation = 0;
        private volatile boolean closed = false;
        private volatile StreamException reset = null;
        private volatile boolean endOfStreamSent = false;

        StreamOutputBuffer() {
        }

        @Override // org.apache.coyote.OutputBuffer
        public final synchronized int doWrite(ByteBuffer chunk) throws IOException {
            if (this.closed) {
                throw new IOException(Stream.sm.getString("stream.closed", Stream.this.getConnectionId(), Stream.this.getIdAsString()));
            }
            int result = chunk.remaining();
            if (this.writeBuffer.isEmpty()) {
                int chunkLimit = chunk.limit();
                while (true) {
                    if (chunk.remaining() <= 0) {
                        break;
                    }
                    int thisTime = Math.min(this.buffer.remaining(), chunk.remaining());
                    chunk.limit(chunk.position() + thisTime);
                    this.buffer.put(chunk);
                    chunk.limit(chunkLimit);
                    if (chunk.remaining() > 0 && !this.buffer.hasRemaining()) {
                        if (flush(true, Stream.this.coyoteResponse.getWriteListener() == null)) {
                            this.writeBuffer.add(chunk);
                            this.dataLeft = true;
                            break;
                        }
                    }
                }
            } else {
                this.writeBuffer.add(chunk);
            }
            this.written += result;
            return result;
        }

        final synchronized boolean flush(boolean block) throws IOException {
            boolean dataInBuffer = this.buffer.position() > 0;
            boolean flushed = false;
            if (dataInBuffer) {
                dataInBuffer = flush(false, block);
                flushed = true;
            }
            if (dataInBuffer) {
                this.dataLeft = true;
            } else if (this.writeBuffer.isEmpty()) {
                if (flushed) {
                    this.dataLeft = false;
                } else {
                    this.dataLeft = flush(false, block);
                }
            } else {
                this.dataLeft = this.writeBuffer.write(this, block);
            }
            return this.dataLeft;
        }

        private synchronized boolean flush(boolean writeInProgress, boolean block) throws IOException {
            if (Stream.log.isDebugEnabled()) {
                Stream.log.debug(Stream.sm.getString("stream.outputBuffer.flush.debug", Stream.this.getConnectionId(), Stream.this.getIdAsString(), Integer.toString(this.buffer.position()), Boolean.toString(writeInProgress), Boolean.toString(this.closed)));
            }
            if (this.buffer.position() == 0) {
                if (this.closed && !this.endOfStreamSent) {
                    Stream.this.handler.writeBody(Stream.this, this.buffer, 0, Stream.this.coyoteResponse.getTrailerFields() == null);
                    return false;
                }
                return false;
            }
            this.buffer.flip();
            int left = this.buffer.remaining();
            while (left > 0) {
                if (this.streamReservation == 0) {
                    this.streamReservation = Stream.this.reserveWindowSize(left, block);
                    if (this.streamReservation == 0) {
                        this.buffer.compact();
                        return true;
                    }
                }
                while (this.streamReservation > 0) {
                    int connectionReservation = Stream.this.handler.reserveWindowSize(Stream.this, this.streamReservation, block);
                    if (connectionReservation == 0) {
                        this.buffer.compact();
                        return true;
                    }
                    Stream.this.handler.writeBody(Stream.this, this.buffer, connectionReservation, !writeInProgress && this.closed && left == connectionReservation && Stream.this.coyoteResponse.getTrailerFields() == null);
                    this.streamReservation -= connectionReservation;
                    left -= connectionReservation;
                }
            }
            this.buffer.clear();
            return false;
        }

        final synchronized boolean isReady() {
            if (Stream.this.getWindowSize() <= 0 || !Stream.this.allocationManager.isWaitingForStream()) {
                if ((Stream.this.handler.getWindowSize() > 0 && Stream.this.allocationManager.isWaitingForConnection()) || this.dataLeft) {
                    return false;
                }
                return true;
            }
            return false;
        }

        @Override // org.apache.coyote.OutputBuffer
        public final long getBytesWritten() {
            return this.written;
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public final void end() throws IOException {
            if (this.reset != null) {
                throw new CloseNowException(this.reset);
            }
            if (!this.closed) {
                this.closed = true;
                flush(true);
                Stream.this.writeTrailers();
            }
        }

        final boolean hasNoBody() {
            return this.written == 0 && this.closed;
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public void flush() throws IOException {
            flush(Stream.this.getCoyoteResponse().getWriteListener() == null);
        }

        @Override // org.apache.tomcat.util.net.WriteBuffer.Sink
        public synchronized boolean writeFromBuffer(ByteBuffer src, boolean blocking) throws IOException {
            int chunkLimit = src.limit();
            while (src.remaining() > 0) {
                int thisTime = Math.min(this.buffer.remaining(), src.remaining());
                src.limit(src.position() + thisTime);
                this.buffer.put(src);
                src.limit(chunkLimit);
                if (flush(false, blocking)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Stream$StreamInputBuffer.class */
    abstract class StreamInputBuffer implements InputBuffer {
        abstract void receiveReset();

        abstract void swallowUnread() throws IOException;

        abstract void notifyEof();

        abstract ByteBuffer getInBuffer();

        abstract void onDataAvailable() throws IOException;

        abstract boolean isReadyForRead();

        abstract boolean isRequestBodyFullyRead();

        abstract void insertReplayedBody(ByteChunk byteChunk);

        StreamInputBuffer() {
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Stream$StandardStreamInputBuffer.class */
    class StandardStreamInputBuffer extends StreamInputBuffer {
        private byte[] outBuffer;
        private volatile ByteBuffer inBuffer;
        private volatile boolean readInterest;
        private volatile boolean closed;
        private boolean resetReceived;

        StandardStreamInputBuffer() {
            super();
        }

        @Override // org.apache.coyote.InputBuffer
        public final int doRead(ApplicationBufferHandler applicationBufferHandler) throws IOException {
            ensureBuffersExist();
            ByteBuffer tmpInBuffer = this.inBuffer;
            if (tmpInBuffer == null) {
                return -1;
            }
            synchronized (tmpInBuffer) {
                if (this.inBuffer == null) {
                    return -1;
                }
                boolean canRead = false;
                while (this.inBuffer.position() == 0) {
                    boolean z = Stream.this.isActive() && !Stream.this.isInputFinished();
                    canRead = z;
                    if (!z) {
                        break;
                    }
                    try {
                        if (Stream.log.isDebugEnabled()) {
                            Stream.log.debug(Stream.sm.getString("stream.inputBuffer.empty"));
                        }
                        long readTimeout = Stream.this.handler.getProtocol().getStreamReadTimeout();
                        if (readTimeout < 0) {
                            this.inBuffer.wait();
                        } else {
                            this.inBuffer.wait(readTimeout);
                        }
                        if (this.resetReceived) {
                            throw new IOException(Stream.sm.getString("stream.inputBuffer.reset"));
                        }
                        if (this.inBuffer.position() == 0 && Stream.this.isActive() && !Stream.this.isInputFinished()) {
                            String msg = Stream.sm.getString("stream.inputBuffer.readTimeout");
                            StreamException se = new StreamException(msg, Http2Error.ENHANCE_YOUR_CALM, Stream.this.getIdAsInt());
                            Stream.this.coyoteResponse.setError();
                            Stream.this.streamOutputBuffer.reset = se;
                            throw new CloseNowException(msg, se);
                        }
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                }
                if (this.inBuffer.position() > 0) {
                    this.inBuffer.flip();
                    int written = this.inBuffer.remaining();
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug(Stream.sm.getString("stream.inputBuffer.copy", Integer.toString(written)));
                    }
                    this.inBuffer.get(this.outBuffer, 0, written);
                    this.inBuffer.clear();
                    applicationBufferHandler.setByteBuffer(ByteBuffer.wrap(this.outBuffer, 0, written));
                    Stream.this.handler.writeWindowUpdate(Stream.this, written, true);
                    return written;
                }
                if (!canRead) {
                    return -1;
                }
                throw new IllegalStateException();
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final boolean isReadyForRead() {
            ensureBuffersExist();
            synchronized (this) {
                if (available() > 0) {
                    return true;
                }
                if (!isRequestBodyFullyRead()) {
                    this.readInterest = true;
                }
                return false;
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final synchronized boolean isRequestBodyFullyRead() {
            return (this.inBuffer == null || this.inBuffer.position() == 0) && Stream.this.isInputFinished();
        }

        @Override // org.apache.coyote.InputBuffer
        public final synchronized int available() {
            if (this.inBuffer == null) {
                return 0;
            }
            return this.inBuffer.position();
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final synchronized void onDataAvailable() throws IOException {
            if (this.closed) {
                swallowUnread();
                return;
            }
            if (this.readInterest) {
                if (Stream.log.isDebugEnabled()) {
                    Stream.log.debug(Stream.sm.getString("stream.inputBuffer.dispatch"));
                }
                this.readInterest = false;
                Stream.this.coyoteRequest.action(ActionCode.DISPATCH_READ, null);
                Stream.this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
                return;
            }
            if (Stream.log.isDebugEnabled()) {
                Stream.log.debug(Stream.sm.getString("stream.inputBuffer.signal"));
            }
            synchronized (this.inBuffer) {
                this.inBuffer.notifyAll();
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final ByteBuffer getInBuffer() {
            ensureBuffersExist();
            return this.inBuffer;
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final synchronized void insertReplayedBody(ByteChunk body) {
            this.inBuffer = ByteBuffer.wrap(body.getBytes(), body.getOffset(), body.getLength());
        }

        private void ensureBuffersExist() {
            if (this.inBuffer == null && !this.closed) {
                int size = Stream.this.handler.getLocalSettings().getInitialWindowSize();
                synchronized (this) {
                    if (this.inBuffer == null && !this.closed) {
                        this.inBuffer = ByteBuffer.allocate(size);
                        this.outBuffer = new byte[size];
                    }
                }
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void receiveReset() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.resetReceived = true;
                    this.inBuffer.notifyAll();
                }
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void notifyEof() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.inBuffer.notifyAll();
                }
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void swallowUnread() throws IOException {
            int unreadByteCount;
            synchronized (this) {
                this.closed = true;
            }
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    unreadByteCount = this.inBuffer.position();
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug(Stream.sm.getString("stream.inputBuffer.swallowUnread", Integer.valueOf(unreadByteCount)));
                    }
                    if (unreadByteCount > 0) {
                        this.inBuffer.position(0);
                        this.inBuffer.limit(this.inBuffer.limit() - unreadByteCount);
                    }
                }
                if (unreadByteCount > 0) {
                    Stream.this.handler.onSwallowedDataFramePayload(Stream.this.getIdAsInt(), unreadByteCount);
                }
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Stream$SavedRequestStreamInputBuffer.class */
    class SavedRequestStreamInputBuffer extends StreamInputBuffer {
        private final SavedRequestInputFilter inputFilter;

        SavedRequestStreamInputBuffer(SavedRequestInputFilter inputFilter) {
            super();
            this.inputFilter = inputFilter;
        }

        @Override // org.apache.coyote.InputBuffer
        public int doRead(ApplicationBufferHandler handler) throws IOException {
            return this.inputFilter.doRead(handler);
        }

        @Override // org.apache.coyote.InputBuffer
        public int available() {
            return this.inputFilter.available();
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void receiveReset() {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void swallowUnread() throws IOException {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void notifyEof() {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        ByteBuffer getInBuffer() {
            return null;
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void onDataAvailable() throws IOException {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        boolean isReadyForRead() {
            return true;
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        boolean isRequestBodyFullyRead() {
            return this.inputFilter.isFinished();
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void insertReplayedBody(ByteChunk body) {
        }
    }
}
