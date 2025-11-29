package org.apache.tomcat.util.compat;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.tomcat.util.net.Constants;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/compat/TLS.class */
public class TLS {
    private static final boolean tlsv13Available;

    static {
        boolean ok = false;
        try {
            SSLContext.getInstance(Constants.SSL_PROTO_TLSv1_3);
            ok = true;
        } catch (NoSuchAlgorithmException e) {
        }
        tlsv13Available = ok;
    }

    public static boolean isTlsv13Available() {
        return tlsv13Available;
    }
}
