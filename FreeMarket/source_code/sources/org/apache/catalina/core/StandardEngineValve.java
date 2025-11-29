package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/core/StandardEngineValve.class */
final class StandardEngineValve extends ValveBase {
    StandardEngineValve() {
        super(true);
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws ServletException, IOException {
        Host host = request.getHost();
        if (host == null) {
            if (!response.isError()) {
                response.sendError(404);
            }
        } else {
            if (request.isAsyncSupported()) {
                request.setAsyncSupported(host.getPipeline().isAsyncSupported());
            }
            host.getPipeline().getFirst().invoke(request, response);
        }
    }
}
