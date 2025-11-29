package org.apache.catalina;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import org.ietf.jgss.GSSCredential;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/TomcatPrincipal.class */
public interface TomcatPrincipal extends Principal {
    Principal getUserPrincipal();

    GSSCredential getGssCredential();

    void logout() throws Exception;

    default Object getAttribute(String name) {
        return null;
    }

    default Enumeration<String> getAttributeNames() {
        return Collections.emptyEnumeration();
    }
}
