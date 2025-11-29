package org.springframework.boot.web.embedded.tomcat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.catalina.Container;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/tomcat/GracefulShutdown.class */
final class GracefulShutdown {
    private static final Log logger = LogFactory.getLog((Class<?>) GracefulShutdown.class);
    private final Tomcat tomcat;
    private volatile boolean aborted = false;

    GracefulShutdown(Tomcat tomcat) {
        this.tomcat = tomcat;
    }

    void shutDownGracefully(GracefulShutdownCallback callback) {
        logger.info("Commencing graceful shutdown. Waiting for active requests to complete");
        new Thread(() -> {
            doShutdown(callback);
        }, "tomcat-shutdown").start();
    }

    private void doShutdown(GracefulShutdownCallback callback) throws InterruptedException {
        List<Connector> connectors = getConnectors();
        connectors.forEach(this::close);
        try {
            for (Container host : this.tomcat.getEngine().findChildren()) {
                for (Container context : host.findChildren()) {
                    while (isActive(context)) {
                        if (this.aborted) {
                            logger.info("Graceful shutdown aborted with one or more requests still active");
                            callback.shutdownComplete(GracefulShutdownResult.REQUESTS_ACTIVE);
                            return;
                        }
                        Thread.sleep(50L);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("Graceful shutdown complete");
        callback.shutdownComplete(GracefulShutdownResult.IDLE);
    }

    private List<Connector> getConnectors() {
        List<Connector> connectors = new ArrayList<>();
        for (Service service : this.tomcat.getServer().findServices()) {
            Collections.addAll(connectors, service.findConnectors());
        }
        return connectors;
    }

    private void close(Connector connector) {
        connector.pause();
        connector.getProtocolHandler().closeServerSocketGraceful();
    }

    private boolean isActive(Container context) {
        try {
            if (((StandardContext) context).getInProgressAsyncCount() > 0) {
                return true;
            }
            for (Container wrapper : context.findChildren()) {
                if (((StandardWrapper) wrapper).getCountAllocated() > 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void abort() {
        this.aborted = true;
    }
}
