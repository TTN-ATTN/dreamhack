package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/openssl/OpenSSLConf.class */
public class OpenSSLConf implements Serializable {
    private static final long serialVersionUID = 1;
    private final List<OpenSSLConfCmd> commands = new ArrayList();

    public void addCmd(OpenSSLConfCmd cmd) {
        this.commands.add(cmd);
    }

    public List<OpenSSLConfCmd> getCommands() {
        return this.commands;
    }
}
