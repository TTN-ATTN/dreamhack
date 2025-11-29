package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/openssl/OpenSSLConfCmd.class */
public class OpenSSLConfCmd implements Serializable {
    private static final long serialVersionUID = 1;
    private String name = null;
    private String value = null;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
