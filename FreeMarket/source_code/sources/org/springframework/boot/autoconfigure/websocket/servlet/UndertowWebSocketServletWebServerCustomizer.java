package org.springframework.boot.autoconfigure.websocket.servlet;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/UndertowWebSocketServletWebServerCustomizer.class */
public class UndertowWebSocketServletWebServerCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(UndertowServletWebServerFactory factory) {
        WebsocketDeploymentInfoCustomizer customizer = new WebsocketDeploymentInfoCustomizer();
        factory.addDeploymentInfoCustomizers(customizer);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/UndertowWebSocketServletWebServerCustomizer$WebsocketDeploymentInfoCustomizer.class */
    private static class WebsocketDeploymentInfoCustomizer implements UndertowDeploymentInfoCustomizer {
        private WebsocketDeploymentInfoCustomizer() {
        }

        @Override // org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
        public void customize(DeploymentInfo deploymentInfo) {
            WebSocketDeploymentInfo info = new WebSocketDeploymentInfo();
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", info);
        }
    }
}
