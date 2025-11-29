package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/ConfigurableJettyWebServerFactory.class */
public interface ConfigurableJettyWebServerFactory extends ConfigurableWebServerFactory {
    void setAcceptors(int acceptors);

    void setThreadPool(ThreadPool threadPool);

    void setSelectors(int selectors);

    void setUseForwardHeaders(boolean useForwardHeaders);

    void addServerCustomizers(JettyServerCustomizer... customizers);
}
