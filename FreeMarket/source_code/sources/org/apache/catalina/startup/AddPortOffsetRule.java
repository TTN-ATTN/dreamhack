package org.apache.catalina.startup;

import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/AddPortOffsetRule.class */
public class AddPortOffsetRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Connector conn = (Connector) this.digester.peek();
        Server server = (Server) this.digester.peek(2);
        int portOffset = server.getPortOffset();
        conn.setPortOffset(portOffset);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(this.digester.toVariableName(conn)).append(".setPortOffset(");
            code.append(this.digester.toVariableName(server)).append(".getPortOffset());");
            code.append(System.lineSeparator());
        }
    }
}
