package org.apache.catalina.valves;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/valves/RemoteAddrValve.class */
public final class RemoteAddrValve extends RequestFilterValve {
    private static final Log log = LogFactory.getLog((Class<?>) RemoteAddrValve.class);

    @Override // org.apache.catalina.valves.RequestFilterValve, org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws ServletException, IOException {
        String property;
        if (getUsePeerAddress()) {
            property = request.getPeerAddr();
        } else {
            property = request.getRequest().getRemoteAddr();
        }
        if (getAddConnectorPort()) {
            property = property + ";" + request.getConnector().getPortWithOffset();
        }
        process(property, request, response);
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    protected Log getLog() {
        return log;
    }
}
