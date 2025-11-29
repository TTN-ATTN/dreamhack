package org.apache.coyote;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/UpgradeProtocol.class */
public interface UpgradeProtocol {
    String getHttpUpgradeName(boolean z);

    byte[] getAlpnIdentifier();

    String getAlpnName();

    Processor getProcessor(SocketWrapperBase<?> socketWrapperBase, Adapter adapter);

    InternalHttpUpgradeHandler getInternalUpgradeHandler(SocketWrapperBase<?> socketWrapperBase, Adapter adapter, Request request);

    boolean accept(Request request);

    default void setHttp11Protocol(AbstractHttp11Protocol<?> protocol) {
    }

    @Deprecated
    default void setHttp11Protocol(AbstractProtocol<?> protocol) {
        if (protocol instanceof AbstractHttp11Protocol) {
            setHttp11Protocol((AbstractHttp11Protocol<?>) protocol);
        }
    }
}
