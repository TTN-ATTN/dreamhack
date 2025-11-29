package org.apache.coyote.http2;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.ErrorState;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.Response;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/StreamProcessor.class */
class StreamProcessor extends AbstractProcessor {
    private static final Log log = LogFactory.getLog((Class<?>) StreamProcessor.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) StreamProcessor.class);
    private static final Set<String> H2_PSEUDO_HEADERS_REQUEST = new HashSet();
    private final Http2UpgradeHandler handler;
    private final Stream stream;
    private SendfileData sendfileData;
    private SendfileState sendfileState;

    static {
        H2_PSEUDO_HEADERS_REQUEST.add(":method");
        H2_PSEUDO_HEADERS_REQUEST.add(":scheme");
        H2_PSEUDO_HEADERS_REQUEST.add(":authority");
        H2_PSEUDO_HEADERS_REQUEST.add(":path");
    }

    StreamProcessor(Http2UpgradeHandler handler, Stream stream, Adapter adapter, SocketWrapperBase<?> socketWrapper) {
        super(adapter, stream.getCoyoteRequest(), stream.getCoyoteResponse());
        this.sendfileData = null;
        this.sendfileState = null;
        this.handler = handler;
        this.stream = stream;
        setSocketWrapper(socketWrapper);
    }

    /* JADX WARN: Finally extract failed */
    final void process(SocketEvent event) {
        try {
            synchronized (this) {
                AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.CLOSED;
                try {
                    try {
                        AbstractEndpoint.Handler.SocketState state2 = process(this.socketWrapper, event);
                        if (state2 == AbstractEndpoint.Handler.SocketState.LONG) {
                            this.handler.getProtocol().getHttp11Protocol().addWaitingProcessor(this);
                        } else if (state2 == AbstractEndpoint.Handler.SocketState.CLOSED) {
                            this.handler.getProtocol().getHttp11Protocol().removeWaitingProcessor(this);
                            if (!this.stream.isInputFinished() && getErrorState().isIoAllowed()) {
                                this.stream.close(new StreamException(sm.getString("streamProcessor.cancel", this.stream.getConnectionId(), this.stream.getIdAsString()), Http2Error.NO_ERROR, this.stream.getIdAsInt()));
                            } else if (!getErrorState().isConnectionIoAllowed()) {
                                ConnectionException ce = new ConnectionException(sm.getString("streamProcessor.error.connection", this.stream.getConnectionId(), this.stream.getIdAsString()), Http2Error.INTERNAL_ERROR);
                                this.stream.close(ce);
                            } else if (!getErrorState().isIoAllowed()) {
                                StreamException se = this.stream.getResetException();
                                if (se == null) {
                                    se = new StreamException(sm.getString("streamProcessor.error.stream", this.stream.getConnectionId(), this.stream.getIdAsString()), Http2Error.INTERNAL_ERROR, this.stream.getIdAsInt());
                                }
                                this.stream.close(se);
                            } else if (!this.stream.isActive()) {
                                this.stream.recycle();
                            }
                        }
                        if (state2 == AbstractEndpoint.Handler.SocketState.CLOSED) {
                            recycle();
                        }
                    } catch (Throwable th) {
                        if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                            recycle();
                        }
                        throw th;
                    }
                } catch (Exception e) {
                    String msg = sm.getString("streamProcessor.error.connection", this.stream.getConnectionId(), this.stream.getIdAsString());
                    if (log.isDebugEnabled()) {
                        log.debug(msg, e);
                    }
                    ConnectionException ce2 = new ConnectionException(msg, Http2Error.INTERNAL_ERROR, e);
                    this.stream.close(ce2);
                    AbstractEndpoint.Handler.SocketState state3 = AbstractEndpoint.Handler.SocketState.CLOSED;
                    if (state3 == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        recycle();
                    }
                }
            }
        } finally {
            this.handler.executeQueuedStream();
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void prepareResponse() throws IOException {
        this.response.setCommitted(true);
        if (this.handler.hasAsyncIO() && this.handler.getProtocol().getUseSendfile()) {
            prepareSendfile();
        }
        prepareHeaders(this.request, this.response, this.sendfileData == null, this.handler.getProtocol(), this.stream);
        this.stream.writeHeaders();
    }

    private void prepareSendfile() {
        String fileName = (String) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.filename");
        if (fileName != null) {
            this.sendfileData = new SendfileData();
            this.sendfileData.path = new File(fileName).toPath();
            this.sendfileData.pos = ((Long) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.start")).longValue();
            this.sendfileData.end = ((Long) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.end")).longValue();
            this.sendfileData.left = this.sendfileData.end - this.sendfileData.pos;
            this.sendfileData.stream = this.stream;
        }
    }

    static void prepareHeaders(Request coyoteRequest, Response coyoteResponse, boolean noSendfile, Http2Protocol protocol, Stream stream) {
        MimeHeaders headers = coyoteResponse.getMimeHeaders();
        int statusCode = coyoteResponse.getStatus();
        headers.addValue(":status").setString(Integer.toString(statusCode));
        if (noSendfile && protocol != null && protocol.useCompression(coyoteRequest, coyoteResponse)) {
            stream.addOutputFilter(new GzipOutputFilter());
        }
        if (statusCode >= 200 && statusCode != 204 && statusCode != 205 && statusCode != 304) {
            String contentType = coyoteResponse.getContentType();
            if (contentType != null) {
                headers.setValue("content-type").setString(contentType);
            }
            String contentLanguage = coyoteResponse.getContentLanguage();
            if (contentLanguage != null) {
                headers.setValue("content-language").setString(contentLanguage);
            }
            long contentLength = coyoteResponse.getContentLengthLong();
            if (contentLength != -1 && headers.getValue("content-length") == null) {
                headers.addValue("content-length").setLong(contentLength);
            }
        } else {
            if (stream != null) {
                stream.configureVoidOutputFilter();
            }
            if (statusCode == 205) {
                coyoteResponse.setContentLength(0L);
            } else {
                coyoteResponse.setContentLength(-1L);
            }
        }
        if (statusCode >= 200 && headers.getValue("date") == null) {
            headers.addValue("date").setString(FastHttpDateFormat.getCurrentDate());
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void finishResponse() throws IOException {
        this.sendfileState = this.handler.processSendfile(this.sendfileData);
        if (this.sendfileState != SendfileState.PENDING) {
            this.stream.getOutputBuffer().end();
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void ack(ContinueResponseTiming continueResponseTiming) {
        if ((continueResponseTiming == ContinueResponseTiming.ALWAYS || continueResponseTiming == this.handler.getProtocol().getContinueResponseTimingInternal()) && !this.response.isCommitted() && this.request.hasExpectation()) {
            try {
                this.stream.writeAck();
            } catch (IOException ioe) {
                setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void flush() throws IOException {
        this.stream.getOutputBuffer().flush();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final int available(boolean doRead) {
        return this.stream.getInputBuffer().available();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setRequestBody(ByteChunk body) {
        this.stream.getInputBuffer().insertReplayedBody(body);
        try {
            this.stream.receivedEndOfStream();
        } catch (ConnectionException e) {
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setSwallowResponse() {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void disableSwallowRequest() {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected void processSocketEvent(SocketEvent event, boolean dispatch) {
        if (dispatch) {
            this.handler.processStreamOnContainerThread(this, event);
        } else {
            process(event);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isReadyForRead() {
        return this.stream.getInputBuffer().isReadyForRead();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isRequestBodyFullyRead() {
        return this.stream.getInputBuffer().isRequestBodyFullyRead();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void registerReadInterest() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isReadyForWrite() {
        return this.stream.isReadyForWrite();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void executeDispatches() {
        Iterator<DispatchType> dispatches = getIteratorAndClearDispatches();
        while (dispatches != null && dispatches.hasNext()) {
            DispatchType dispatchType = dispatches.next();
            processSocketEvent(dispatchType.getSocketStatus(), true);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isPushSupported() {
        return this.stream.isPushSupported();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void doPush(Request pushTarget) throws PrivilegedActionException {
        try {
            this.stream.push(pushTarget);
        } catch (IOException ioe) {
            setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            this.response.setErrorException(ioe);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsReady() {
        return this.stream.isTrailerFieldsReady();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsSupported() {
        return this.stream.isTrailerFieldsSupported();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected Object getConnectionID() {
        return this.stream.getConnectionId();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected Object getStreamID() {
        return this.stream.getIdAsString().toString();
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public final void recycle() {
        RequestGroupInfo global = this.handler.getProtocol().getGlobal();
        if (global != null) {
            global.removeRequestProcessor(this.request.getRequestProcessor());
        }
        setSocketWrapper(null);
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    protected final Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.Processor
    public final void pause() {
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socket) throws IOException {
        try {
            if (validateRequest()) {
                this.adapter.service(this.request, this.response);
            } else {
                this.response.setStatus(400);
                this.adapter.log(this.request, this.response, 0L);
                setErrorState(ErrorState.CLOSE_CLEAN, null);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("streamProcessor.service.error"), e);
            }
            this.response.setStatus(500);
            setErrorState(ErrorState.CLOSE_NOW, e);
        }
        if (this.sendfileState == SendfileState.PENDING) {
            return AbstractEndpoint.Handler.SocketState.SENDFILE;
        }
        if (getErrorState().isError()) {
            action(ActionCode.CLOSE, null);
            this.request.updateCounters();
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        action(ActionCode.CLOSE, null);
        this.request.updateCounters();
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    private boolean validateRequest() {
        HttpParser httpParser = new HttpParser(((AbstractHttp11Protocol) this.handler.getProtocol().getHttp11Protocol()).getRelaxedPathChars(), ((AbstractHttp11Protocol) this.handler.getProtocol().getHttp11Protocol()).getRelaxedQueryChars());
        String method = this.request.method().toString();
        if (!HttpParser.isToken(method)) {
            return false;
        }
        String scheme = this.request.scheme().toString();
        if (!HttpParser.isScheme(scheme)) {
            return false;
        }
        ByteChunk bc = this.request.requestURI().getByteChunk();
        for (int i = bc.getStart(); i < bc.getEnd(); i++) {
            if (httpParser.isNotRequestTargetRelaxed(bc.getBuffer()[i])) {
                return false;
            }
        }
        String qs = this.request.queryString().toString();
        if (qs != null) {
            for (char c : qs.toCharArray()) {
                if (!httpParser.isQueryRelaxed(c)) {
                    return false;
                }
            }
        }
        MimeHeaders headers = this.request.getMimeHeaders();
        Enumeration<String> names = headers.names();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (H2_PSEUDO_HEADERS_REQUEST.contains(name)) {
                if (1 == 0) {
                    return false;
                }
            } else if (!HttpParser.isToken(name)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean flushBufferedWrite() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("streamProcessor.flushBufferedWrite.entry", this.stream.getConnectionId(), this.stream.getIdAsString()));
        }
        if (this.stream.flush(false)) {
            if (this.stream.isReadyForWrite()) {
                throw new IllegalStateException();
            }
            return true;
        }
        return false;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final AbstractEndpoint.Handler.SocketState dispatchEndRequest() throws IOException {
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
}
