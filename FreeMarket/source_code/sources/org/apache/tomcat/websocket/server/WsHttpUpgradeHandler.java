package org.apache.tomcat.websocket.server;

import java.io.EOFException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.WebConnection;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsSession;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/server/WsHttpUpgradeHandler.class */
public class WsHttpUpgradeHandler implements InternalHttpUpgradeHandler {
    private static final StringManager sm = StringManager.getManager((Class<?>) WsHttpUpgradeHandler.class);
    private SocketWrapperBase<?> socketWrapper;
    private Endpoint ep;
    private ServerEndpointConfig serverEndpointConfig;
    private WsServerContainer webSocketContainer;
    private WsHandshakeRequest handshakeRequest;
    private List<Extension> negotiatedExtensions;
    private String subProtocol;
    private Transformation transformation;
    private Map<String, String> pathParameters;
    private boolean secure;
    private WebConnection connection;
    private WsRemoteEndpointImplServer wsRemoteEndpointServer;
    private WsFrameServer wsFrame;
    private WsSession wsSession;
    private final Log log = LogFactory.getLog((Class<?>) WsHttpUpgradeHandler.class);
    private UpgradeInfo upgradeInfo = new UpgradeInfo();
    private final ClassLoader applicationClassLoader = Thread.currentThread().getContextClassLoader();

    @Override // org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public void setSocketWrapper(SocketWrapperBase<?> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    public void preInit(ServerEndpointConfig serverEndpointConfig, WsServerContainer wsc, WsHandshakeRequest handshakeRequest, List<Extension> negotiatedExtensionsPhase2, String subProtocol, Transformation transformation, Map<String, String> pathParameters, boolean secure) {
        this.serverEndpointConfig = serverEndpointConfig;
        this.webSocketContainer = wsc;
        this.handshakeRequest = handshakeRequest;
        this.negotiatedExtensions = negotiatedExtensionsPhase2;
        this.subProtocol = subProtocol;
        this.transformation = transformation;
        this.pathParameters = pathParameters;
        this.secure = secure;
    }

    @Override // javax.servlet.http.HttpUpgradeHandler
    public void init(WebConnection connection) {
        this.connection = connection;
        if (this.serverEndpointConfig == null) {
            throw new IllegalStateException(sm.getString("wsHttpUpgradeHandler.noPreInit"));
        }
        String httpSessionId = null;
        Object session = this.handshakeRequest.getHttpSession();
        if (session != null) {
            httpSessionId = ((HttpSession) session).getId();
        }
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            try {
                this.wsRemoteEndpointServer = new WsRemoteEndpointImplServer(this.socketWrapper, this.upgradeInfo, this.webSocketContainer, connection);
                this.wsSession = new WsSession(this.wsRemoteEndpointServer, this.webSocketContainer, this.handshakeRequest.getRequestURI(), this.handshakeRequest.getParameterMap(), this.handshakeRequest.getQueryString(), this.handshakeRequest.getUserPrincipal(), httpSessionId, this.negotiatedExtensions, this.subProtocol, this.pathParameters, this.secure, this.serverEndpointConfig);
                this.ep = this.wsSession.getLocal();
                this.wsFrame = new WsFrameServer(this.socketWrapper, this.upgradeInfo, this.wsSession, this.transformation, this.applicationClassLoader);
                this.wsRemoteEndpointServer.setTransformation(this.wsFrame.getTransformation());
                this.ep.onOpen(this.wsSession, this.serverEndpointConfig);
                this.webSocketContainer.registerSession(this.serverEndpointConfig.getPath(), this.wsSession);
                t.setContextClassLoader(cl);
            } catch (DeploymentException e) {
                throw new IllegalArgumentException(e);
            }
        } catch (Throwable th) {
            t.setContextClassLoader(cl);
            throw th;
        }
    }

    @Override // org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public UpgradeInfo getUpgradeInfo() {
        return this.upgradeInfo;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00f0  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00f4  */
    @Override // org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState upgradeDispatch(org.apache.tomcat.util.net.SocketEvent r8) {
        /*
            r7 = this;
            int[] r0 = org.apache.tomcat.websocket.server.WsHttpUpgradeHandler.AnonymousClass1.$SwitchMap$org$apache$tomcat$util$net$SocketEvent
            r1 = r8
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                case 1: goto L34;
                case 2: goto L66;
                case 3: goto L71;
                case 4: goto Lad;
                case 5: goto Le2;
                case 6: goto Le2;
                case 7: goto Le2;
                default: goto Le6;
            }
        L34:
            r0 = r7
            org.apache.tomcat.websocket.server.WsFrameServer r0 = r0.wsFrame     // Catch: org.apache.tomcat.websocket.WsIOException -> L3c java.io.IOException -> L48
            org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState r0 = r0.notifyDataAvailable()     // Catch: org.apache.tomcat.websocket.WsIOException -> L3c java.io.IOException -> L48
            return r0
        L3c:
            r9 = move-exception
            r0 = r7
            r1 = r9
            javax.websocket.CloseReason r1 = r1.getCloseReason()
            r0.close(r1)
            goto L62
        L48:
            r9 = move-exception
            r0 = r7
            r1 = r9
            r0.onError(r1)
            javax.websocket.CloseReason r0 = new javax.websocket.CloseReason
            r1 = r0
            javax.websocket.CloseReason$CloseCodes r2 = javax.websocket.CloseReason.CloseCodes.CLOSED_ABNORMALLY
            r3 = r9
            java.lang.String r3 = r3.getMessage()
            r1.<init>(r2, r3)
            r10 = r0
            r0 = r7
            r1 = r10
            r0.close(r1)
        L62:
            org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState r0 = org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState.CLOSED
            return r0
        L66:
            r0 = r7
            org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer r0 = r0.wsRemoteEndpointServer
            r1 = 0
            r0.onWritePossible(r1)
            goto Le6
        L71:
            javax.websocket.CloseReason r0 = new javax.websocket.CloseReason
            r1 = r0
            javax.websocket.CloseReason$CloseCodes r2 = javax.websocket.CloseReason.CloseCodes.GOING_AWAY
            org.apache.tomcat.util.res.StringManager r3 = org.apache.tomcat.websocket.server.WsHttpUpgradeHandler.sm
            java.lang.String r4 = "wsHttpUpgradeHandler.serverStop"
            java.lang.String r3 = r3.getString(r4)
            r1.<init>(r2, r3)
            r9 = r0
            r0 = r7
            org.apache.tomcat.websocket.WsSession r0 = r0.wsSession     // Catch: java.io.IOException -> L8f
            r1 = r9
            r0.close(r1)     // Catch: java.io.IOException -> L8f
            goto Le6
        L8f:
            r10 = move-exception
            r0 = r7
            r1 = r10
            r0.onError(r1)
            javax.websocket.CloseReason r0 = new javax.websocket.CloseReason
            r1 = r0
            javax.websocket.CloseReason$CloseCodes r2 = javax.websocket.CloseReason.CloseCodes.CLOSED_ABNORMALLY
            r3 = r10
            java.lang.String r3 = r3.getMessage()
            r1.<init>(r2, r3)
            r9 = r0
            r0 = r7
            r1 = r9
            r0.close(r1)
            org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState r0 = org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState.CLOSED
            return r0
        Lad:
            r0 = r7
            org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer r0 = r0.wsRemoteEndpointServer
            r1 = r7
            org.apache.tomcat.util.net.SocketWrapperBase<?> r1 = r1.socketWrapper
            java.io.IOException r1 = r1.getError()
            r2 = 0
            r0.clearHandler(r1, r2)
            org.apache.tomcat.util.res.StringManager r0 = org.apache.tomcat.websocket.server.WsHttpUpgradeHandler.sm
            java.lang.String r1 = "wsHttpUpgradeHandler.closeOnError"
            java.lang.String r0 = r0.getString(r1)
            r10 = r0
            r0 = r7
            org.apache.tomcat.websocket.WsSession r0 = r0.wsSession
            javax.websocket.CloseReason r1 = new javax.websocket.CloseReason
            r2 = r1
            javax.websocket.CloseReason$CloseCodes r3 = javax.websocket.CloseReason.CloseCodes.GOING_AWAY
            r4 = r10
            r2.<init>(r3, r4)
            javax.websocket.CloseReason r2 = new javax.websocket.CloseReason
            r3 = r2
            javax.websocket.CloseReason$CloseCodes r4 = javax.websocket.CloseReason.CloseCodes.CLOSED_ABNORMALLY
            r5 = r10
            r3.<init>(r4, r5)
            r0.doClose(r1, r2)
        Le2:
            org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState r0 = org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState.CLOSED
            return r0
        Le6:
            r0 = r7
            org.apache.tomcat.websocket.WsSession r0 = r0.wsSession
            boolean r0 = r0.isClosed()
            if (r0 == 0) goto Lf4
            org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState r0 = org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState.CLOSED
            return r0
        Lf4:
            org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState r0 = org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState.UPGRADED
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.websocket.server.WsHttpUpgradeHandler.upgradeDispatch(org.apache.tomcat.util.net.SocketEvent):org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState");
    }

    @Override // org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public void timeoutAsync(long now) {
    }

    @Override // org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public void pause() {
    }

    @Override // javax.servlet.http.HttpUpgradeHandler
    public void destroy() {
        WebConnection connection = this.connection;
        if (connection != null) {
            this.connection = null;
            try {
                connection.close();
            } catch (Exception e) {
                this.log.error(sm.getString("wsHttpUpgradeHandler.destroyFailed"), e);
            }
        }
    }

    private void onError(Throwable throwable) {
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.ep.onError(this.wsSession, throwable);
            t.setContextClassLoader(cl);
        } catch (Throwable th) {
            t.setContextClassLoader(cl);
            throw th;
        }
    }

    private void close(CloseReason cr) {
        this.wsRemoteEndpointServer.clearHandler(new EOFException(), true);
        this.wsSession.onClose(cr);
    }

    @Override // org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public void setSslSupport(SSLSupport sslSupport) {
    }
}
