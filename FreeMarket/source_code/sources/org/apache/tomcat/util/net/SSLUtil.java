package org.apache.tomcat.util.net;

import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/SSLUtil.class */
public interface SSLUtil {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/SSLUtil$ProtocolInfo.class */
    public interface ProtocolInfo {
        String getNegotiatedProtocol();
    }

    SSLContext createSSLContext(List<String> list) throws Exception;

    KeyManager[] getKeyManagers() throws Exception;

    TrustManager[] getTrustManagers() throws Exception;

    void configureSessionContext(SSLSessionContext sSLSessionContext);

    String[] getEnabledProtocols() throws IllegalArgumentException;

    String[] getEnabledCiphers() throws IllegalArgumentException;
}
