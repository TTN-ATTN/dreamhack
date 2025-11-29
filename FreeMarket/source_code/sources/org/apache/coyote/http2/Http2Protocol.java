package org.apache.coyote.http2;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Adapter;
import org.apache.coyote.CompressionConfig;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.Response;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.util.SocketUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/Http2Protocol.class */
public class Http2Protocol implements UpgradeProtocol {
    static final long DEFAULT_READ_TIMEOUT = 5000;
    static final long DEFAULT_WRITE_TIMEOUT = 5000;
    static final long DEFAULT_KEEP_ALIVE_TIMEOUT = 20000;
    static final long DEFAULT_STREAM_READ_TIMEOUT = 20000;
    static final long DEFAULT_STREAM_WRITE_TIMEOUT = 20000;
    static final long DEFAULT_MAX_CONCURRENT_STREAMS = 100;
    static final int DEFAULT_MAX_CONCURRENT_STREAM_EXECUTION = 20;
    static final int DEFAULT_OVERHEAD_COUNT_FACTOR = 10;
    static final int DEFAULT_OVERHEAD_REDUCTION_FACTOR = -20;
    static final int DEFAULT_OVERHEAD_CONTINUATION_THRESHOLD = 1024;
    static final int DEFAULT_OVERHEAD_DATA_THRESHOLD = 1024;
    static final int DEFAULT_OVERHEAD_WINDOW_UPDATE_THRESHOLD = 1024;
    private static final String HTTP_UPGRADE_NAME = "h2c";
    private long readTimeout = 5000;
    private long writeTimeout = 5000;
    private long keepAliveTimeout = org.apache.tomcat.websocket.Constants.DEFAULT_BLOCKING_SEND_TIMEOUT;
    private long streamReadTimeout = org.apache.tomcat.websocket.Constants.DEFAULT_BLOCKING_SEND_TIMEOUT;
    private long streamWriteTimeout = org.apache.tomcat.websocket.Constants.DEFAULT_BLOCKING_SEND_TIMEOUT;
    private long maxConcurrentStreams = DEFAULT_MAX_CONCURRENT_STREAMS;
    private int maxConcurrentStreamExecution = 20;
    private int initialWindowSize = SocketUtils.PORT_RANGE_MAX;
    private Set<String> allowedTrailerHeaders = ConcurrentHashMap.newKeySet();
    private int maxHeaderCount = 100;
    private int maxHeaderSize = 8192;
    private int maxTrailerCount = 100;
    private int maxTrailerSize = 8192;
    private int overheadCountFactor = 10;
    private int overheadContinuationThreshold = 1024;
    private int overheadDataThreshold = 1024;
    private int overheadWindowUpdateThreshold = 1024;
    private boolean initiatePingDisabled = false;
    private boolean useSendfile = true;
    private final CompressionConfig compressionConfig = new CompressionConfig();
    private AbstractHttp11Protocol<?> http11Protocol = null;
    private RequestGroupInfo global = new RequestGroupInfo();
    private static final Log log = LogFactory.getLog((Class<?>) Http2Protocol.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Http2Protocol.class);
    private static final String ALPN_NAME = "h2";
    private static final byte[] ALPN_IDENTIFIER = ALPN_NAME.getBytes(StandardCharsets.UTF_8);

    @Override // org.apache.coyote.UpgradeProtocol
    public String getHttpUpgradeName(boolean isSSLEnabled) {
        if (isSSLEnabled) {
            return null;
        }
        return HTTP_UPGRADE_NAME;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public byte[] getAlpnIdentifier() {
        return ALPN_IDENTIFIER;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public String getAlpnName() {
        return ALPN_NAME;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public Processor getProcessor(SocketWrapperBase<?> socketWrapper, Adapter adapter) {
        String upgradeProtocol = getUpgradeProtocolName();
        UpgradeProcessorInternal processor = new UpgradeProcessorInternal(socketWrapper, new UpgradeToken(getInternalUpgradeHandler(socketWrapper, adapter, null), null, null, upgradeProtocol), null);
        return processor;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public InternalHttpUpgradeHandler getInternalUpgradeHandler(SocketWrapperBase<?> socketWrapper, Adapter adapter, Request coyoteRequest) {
        return socketWrapper.hasAsyncIO() ? new Http2AsyncUpgradeHandler(this, adapter, coyoteRequest) : new Http2UpgradeHandler(this, adapter, coyoteRequest);
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public boolean accept(Request request) {
        boolean found;
        Enumeration<String> settings = request.getMimeHeaders().values("HTTP2-Settings");
        int count = 0;
        while (settings.hasMoreElements()) {
            count++;
            settings.nextElement();
        }
        if (count != 1) {
            return false;
        }
        Enumeration<String> connection = request.getMimeHeaders().values("Connection");
        boolean zContains = false;
        while (true) {
            found = zContains;
            if (!connection.hasMoreElements() || found) {
                break;
            }
            zContains = connection.nextElement().contains("HTTP2-Settings");
        }
        return found;
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public long getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public long getStreamReadTimeout() {
        return this.streamReadTimeout;
    }

    public void setStreamReadTimeout(long streamReadTimeout) {
        this.streamReadTimeout = streamReadTimeout;
    }

    public long getStreamWriteTimeout() {
        return this.streamWriteTimeout;
    }

    public void setStreamWriteTimeout(long streamWriteTimeout) {
        this.streamWriteTimeout = streamWriteTimeout;
    }

    public long getMaxConcurrentStreams() {
        return this.maxConcurrentStreams;
    }

    public void setMaxConcurrentStreams(long maxConcurrentStreams) {
        this.maxConcurrentStreams = maxConcurrentStreams;
    }

    public int getMaxConcurrentStreamExecution() {
        return this.maxConcurrentStreamExecution;
    }

    public void setMaxConcurrentStreamExecution(int maxConcurrentStreamExecution) {
        this.maxConcurrentStreamExecution = maxConcurrentStreamExecution;
    }

    public int getInitialWindowSize() {
        return this.initialWindowSize;
    }

    public void setInitialWindowSize(int initialWindowSize) {
        this.initialWindowSize = initialWindowSize;
    }

    public boolean getUseSendfile() {
        return this.useSendfile;
    }

    public void setUseSendfile(boolean useSendfile) {
        this.useSendfile = useSendfile;
    }

    public void setAllowedTrailerHeaders(String commaSeparatedHeaders) {
        Set<String> toRemove = new HashSet<>();
        toRemove.addAll(this.allowedTrailerHeaders);
        if (commaSeparatedHeaders != null) {
            String[] headers = commaSeparatedHeaders.split(",");
            for (String header : headers) {
                String trimmedHeader = header.trim().toLowerCase(Locale.ENGLISH);
                if (toRemove.contains(trimmedHeader)) {
                    toRemove.remove(trimmedHeader);
                } else {
                    this.allowedTrailerHeaders.add(trimmedHeader);
                }
            }
            this.allowedTrailerHeaders.removeAll(toRemove);
        }
    }

    public String getAllowedTrailerHeaders() {
        List<String> copy = new ArrayList<>(this.allowedTrailerHeaders.size());
        copy.addAll(this.allowedTrailerHeaders);
        return StringUtils.join(copy);
    }

    boolean isTrailerHeaderAllowed(String headerName) {
        return this.allowedTrailerHeaders.contains(headerName);
    }

    public void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public int getMaxHeaderSize() {
        return this.maxHeaderSize;
    }

    public void setMaxTrailerCount(int maxTrailerCount) {
        this.maxTrailerCount = maxTrailerCount;
    }

    public int getMaxTrailerCount() {
        return this.maxTrailerCount;
    }

    public void setMaxTrailerSize(int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }

    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }

    public int getOverheadCountFactor() {
        return this.overheadCountFactor;
    }

    public void setOverheadCountFactor(int overheadCountFactor) {
        this.overheadCountFactor = overheadCountFactor;
    }

    public int getOverheadContinuationThreshold() {
        return this.overheadContinuationThreshold;
    }

    public void setOverheadContinuationThreshold(int overheadContinuationThreshold) {
        this.overheadContinuationThreshold = overheadContinuationThreshold;
    }

    public int getOverheadDataThreshold() {
        return this.overheadDataThreshold;
    }

    public void setOverheadDataThreshold(int overheadDataThreshold) {
        this.overheadDataThreshold = overheadDataThreshold;
    }

    public int getOverheadWindowUpdateThreshold() {
        return this.overheadWindowUpdateThreshold;
    }

    public void setOverheadWindowUpdateThreshold(int overheadWindowUpdateThreshold) {
        this.overheadWindowUpdateThreshold = overheadWindowUpdateThreshold;
    }

    public void setInitiatePingDisabled(boolean initiatePingDisabled) {
        this.initiatePingDisabled = initiatePingDisabled;
    }

    public boolean getInitiatePingDisabled() {
        return this.initiatePingDisabled;
    }

    public void setCompression(String compression) {
        this.compressionConfig.setCompression(compression);
    }

    public String getCompression() {
        return this.compressionConfig.getCompression();
    }

    protected int getCompressionLevel() {
        return this.compressionConfig.getCompressionLevel();
    }

    public String getNoCompressionUserAgents() {
        return this.compressionConfig.getNoCompressionUserAgents();
    }

    protected Pattern getNoCompressionUserAgentsPattern() {
        return this.compressionConfig.getNoCompressionUserAgentsPattern();
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        this.compressionConfig.setNoCompressionUserAgents(noCompressionUserAgents);
    }

    public String getCompressibleMimeType() {
        return this.compressionConfig.getCompressibleMimeType();
    }

    public void setCompressibleMimeType(String valueS) {
        this.compressionConfig.setCompressibleMimeType(valueS);
    }

    public String[] getCompressibleMimeTypes() {
        return this.compressionConfig.getCompressibleMimeTypes();
    }

    public int getCompressionMinSize() {
        return this.compressionConfig.getCompressionMinSize();
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionConfig.setCompressionMinSize(compressionMinSize);
    }

    @Deprecated
    public boolean getNoCompressionStrongETag() {
        return this.compressionConfig.getNoCompressionStrongETag();
    }

    @Deprecated
    public void setNoCompressionStrongETag(boolean noCompressionStrongETag) {
        this.compressionConfig.setNoCompressionStrongETag(noCompressionStrongETag);
    }

    public boolean useCompression(Request request, Response response) {
        return this.compressionConfig.useCompression(request, response);
    }

    public ContinueResponseTiming getContinueResponseTimingInternal() {
        return this.http11Protocol.getContinueResponseTimingInternal();
    }

    public AbstractProtocol<?> getHttp11Protocol() {
        return this.http11Protocol;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public void setHttp11Protocol(AbstractHttp11Protocol<?> http11Protocol) {
        this.http11Protocol = http11Protocol;
        try {
            ObjectName oname = this.http11Protocol.getONameForUpgrade(getUpgradeProtocolName());
            if (oname != null) {
                Registry.getRegistry(null, null).registerComponent(this.global, oname, (String) null);
            }
        } catch (Exception e) {
            log.warn(sm.getString("http2Protocol.jmxRegistration.fail"), e);
        }
    }

    public String getUpgradeProtocolName() {
        if (this.http11Protocol.isSSLEnabled()) {
            return ALPN_NAME;
        }
        return HTTP_UPGRADE_NAME;
    }

    public RequestGroupInfo getGlobal() {
        return this.global;
    }
}
