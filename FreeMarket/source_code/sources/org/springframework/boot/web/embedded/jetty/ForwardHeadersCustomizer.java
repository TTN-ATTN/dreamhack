package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/ForwardHeadersCustomizer.class */
class ForwardHeadersCustomizer implements JettyServerCustomizer {
    ForwardHeadersCustomizer() {
    }

    @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
    public void customize(Server server) {
        ForwardedRequestCustomizer customizer = new ForwardedRequestCustomizer();
        for (Connector connector : server.getConnectors()) {
            for (HttpConfiguration.ConnectionFactory connectionFactory : connector.getConnectionFactories()) {
                if (connectionFactory instanceof HttpConfiguration.ConnectionFactory) {
                    connectionFactory.getHttpConfiguration().addCustomizer(customizer);
                }
            }
        }
    }
}
