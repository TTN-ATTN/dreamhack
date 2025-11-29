package org.apache.tomcat.util.net.jsse;

import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/jsse/JSSEImplementation.class */
public class JSSEImplementation extends SSLImplementation {
    public JSSEImplementation() {
        JSSESupport.init();
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    @Deprecated
    public SSLSupport getSSLSupport(SSLSession session) {
        return getSSLSupport(session, null);
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLSupport getSSLSupport(SSLSession session, Map<String, List<String>> additionalAttributes) {
        return new JSSESupport(session, additionalAttributes);
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) {
        return new JSSEUtil(certificate);
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public boolean isAlpnSupported() {
        return JreCompat.isAlpnSupported();
    }
}
