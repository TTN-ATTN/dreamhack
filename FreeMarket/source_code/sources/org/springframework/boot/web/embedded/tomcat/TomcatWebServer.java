package org.springframework.boot.web.embedded.tomcat;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.NamingException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/tomcat/TomcatWebServer.class */
public class TomcatWebServer implements WebServer {
    private static final Log logger = LogFactory.getLog((Class<?>) TomcatWebServer.class);
    private static final AtomicInteger containerCounter = new AtomicInteger(-1);
    private final Object monitor;
    private final Map<Service, Connector[]> serviceConnectors;
    private final Tomcat tomcat;
    private final boolean autoStart;
    private final GracefulShutdown gracefulShutdown;
    private volatile boolean started;

    public TomcatWebServer(Tomcat tomcat) {
        this(tomcat, true);
    }

    public TomcatWebServer(Tomcat tomcat, boolean autoStart) {
        this(tomcat, autoStart, Shutdown.IMMEDIATE);
    }

    public TomcatWebServer(Tomcat tomcat, boolean autoStart, Shutdown shutdown) throws WebServerException {
        this.monitor = new Object();
        this.serviceConnectors = new HashMap();
        Assert.notNull(tomcat, "Tomcat Server must not be null");
        this.tomcat = tomcat;
        this.autoStart = autoStart;
        this.gracefulShutdown = shutdown == Shutdown.GRACEFUL ? new GracefulShutdown(tomcat) : null;
        initialize();
    }

    private void initialize() throws WebServerException {
        logger.info("Tomcat initialized with port(s): " + getPortsDescription(false));
        synchronized (this.monitor) {
            try {
                addInstanceIdToEngineName();
                Context context = findContext();
                context.addLifecycleListener(event -> {
                    if (context.equals(event.getSource()) && Lifecycle.START_EVENT.equals(event.getType())) {
                        removeServiceConnectors();
                    }
                });
                this.tomcat.start();
                rethrowDeferredStartupExceptions();
                try {
                    ContextBindings.bindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
                } catch (NamingException e) {
                }
                startDaemonAwaitThread();
            } catch (Exception ex) {
                stopSilently();
                destroySilently();
                throw new WebServerException("Unable to start embedded Tomcat", ex);
            }
        }
    }

    private Context findContext() {
        for (Container child : this.tomcat.getHost().findChildren()) {
            if (child instanceof Context) {
                return (Context) child;
            }
        }
        throw new IllegalStateException("The host does not contain a Context");
    }

    private void addInstanceIdToEngineName() {
        int instanceId = containerCounter.incrementAndGet();
        if (instanceId > 0) {
            Engine engine = this.tomcat.getEngine();
            engine.setName(engine.getName() + "-" + instanceId);
        }
    }

    private void removeServiceConnectors() {
        for (Service service : this.tomcat.getServer().findServices()) {
            Connector[] connectors = (Connector[]) service.findConnectors().clone();
            this.serviceConnectors.put(service, connectors);
            for (Connector connector : connectors) {
                service.removeConnector(connector);
            }
        }
    }

    private void rethrowDeferredStartupExceptions() throws Exception {
        TomcatStarter tomcatStarter;
        Exception exception;
        Container[] children = this.tomcat.getHost().findChildren();
        for (Container container : children) {
            if ((container instanceof TomcatEmbeddedContext) && (tomcatStarter = ((TomcatEmbeddedContext) container).getStarter()) != null && (exception = tomcatStarter.getStartUpException()) != null) {
                throw exception;
            }
            if (!LifecycleState.STARTED.equals(container.getState())) {
                throw new IllegalStateException(container + " failed to start");
            }
        }
    }

    private void startDaemonAwaitThread() {
        Thread awaitThread = new Thread("container-" + containerCounter.get()) { // from class: org.springframework.boot.web.embedded.tomcat.TomcatWebServer.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                TomcatWebServer.this.tomcat.getServer().await();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        synchronized (this.monitor) {
            try {
                if (this.started) {
                    return;
                }
                try {
                    addPreviouslyRemovedConnectors();
                    Connector connector = this.tomcat.getConnector();
                    if (connector != null && this.autoStart) {
                        performDeferredLoadOnStartup();
                    }
                    checkThatConnectorsHaveStarted();
                    this.started = true;
                    logger.info("Tomcat started on port(s): " + getPortsDescription(true) + " with context path '" + getContextPath() + "'");
                    Context context = findContext();
                    ContextBindings.unbindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
                } catch (ConnectorStartFailedException ex) {
                    stopSilently();
                    throw ex;
                } catch (Exception ex2) {
                    PortInUseException.throwIfPortBindingException(ex2, () -> {
                        return this.tomcat.getConnector().getPort();
                    });
                    throw new WebServerException("Unable to start embedded Tomcat server", ex2);
                }
            } catch (Throwable th) {
                Context context2 = findContext();
                ContextBindings.unbindClassLoader(context2, context2.getNamingToken(), getClass().getClassLoader());
                throw th;
            }
        }
    }

    private void checkThatConnectorsHaveStarted() {
        checkConnectorHasStarted(this.tomcat.getConnector());
        for (Connector connector : this.tomcat.getService().findConnectors()) {
            checkConnectorHasStarted(connector);
        }
    }

    private void checkConnectorHasStarted(Connector connector) {
        if (LifecycleState.FAILED.equals(connector.getState())) {
            throw new ConnectorStartFailedException(connector.getPort());
        }
    }

    private void stopSilently() throws IOException {
        try {
            stopTomcat();
        } catch (LifecycleException e) {
        }
    }

    private void destroySilently() throws IOException {
        try {
            this.tomcat.destroy();
        } catch (LifecycleException e) {
        }
    }

    private void stopTomcat() throws LifecycleException, IOException {
        if (Thread.currentThread().getContextClassLoader() instanceof TomcatEmbeddedWebappClassLoader) {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        }
        this.tomcat.stop();
    }

    private void addPreviouslyRemovedConnectors() {
        Service[] services = this.tomcat.getServer().findServices();
        for (Service service : services) {
            Connector[] connectors = this.serviceConnectors.get(service);
            if (connectors != null) {
                for (Connector connector : connectors) {
                    service.addConnector(connector);
                    if (!this.autoStart) {
                        stopProtocolHandler(connector);
                    }
                }
                this.serviceConnectors.remove(service);
            }
        }
    }

    private void stopProtocolHandler(Connector connector) {
        try {
            connector.getProtocolHandler().stop();
        } catch (Exception ex) {
            logger.error("Cannot pause connector: ", ex);
        }
    }

    private void performDeferredLoadOnStartup() {
        try {
            for (Container child : this.tomcat.getHost().findChildren()) {
                if (child instanceof TomcatEmbeddedContext) {
                    ((TomcatEmbeddedContext) child).deferredLoadOnStartup();
                }
            }
        } catch (Exception ex) {
            if (ex instanceof WebServerException) {
                throw ((WebServerException) ex);
            }
            throw new WebServerException("Unable to start embedded Tomcat connectors", ex);
        }
    }

    Map<Service, Connector[]> getServiceConnectors() {
        return this.serviceConnectors;
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.springframework.boot.web.server.WebServer
    public void stop() throws WebServerException {
        synchronized (this.monitor) {
            boolean wasStarted = this.started;
            try {
                try {
                    this.started = false;
                    try {
                        if (this.gracefulShutdown != null) {
                            this.gracefulShutdown.abort();
                        }
                        stopTomcat();
                        this.tomcat.destroy();
                    } catch (LifecycleException e) {
                    }
                    if (wasStarted) {
                        containerCounter.decrementAndGet();
                    }
                } catch (Exception ex) {
                    throw new WebServerException("Unable to stop embedded Tomcat", ex);
                }
            } catch (Throwable th) {
                if (wasStarted) {
                    containerCounter.decrementAndGet();
                }
                throw th;
            }
        }
    }

    private String getPortsDescription(boolean localPort) {
        StringBuilder ports = new StringBuilder();
        for (Connector connector : this.tomcat.getService().findConnectors()) {
            if (ports.length() != 0) {
                ports.append(' ');
            }
            int port = localPort ? connector.getLocalPort() : connector.getPort();
            ports.append(port).append(" (").append(connector.getScheme()).append(')');
        }
        return ports.toString();
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        Connector connector = this.tomcat.getConnector();
        if (connector != null) {
            return connector.getLocalPort();
        }
        return -1;
    }

    private String getContextPath() {
        Stream stream = Arrays.stream(this.tomcat.getHost().findChildren());
        Class<TomcatEmbeddedContext> cls = TomcatEmbeddedContext.class;
        TomcatEmbeddedContext.class.getClass();
        Stream streamFilter = stream.filter((v1) -> {
            return r1.isInstance(v1);
        });
        Class<TomcatEmbeddedContext> cls2 = TomcatEmbeddedContext.class;
        TomcatEmbeddedContext.class.getClass();
        return (String) streamFilter.map((v1) -> {
            return r1.cast(v1);
        }).map((v0) -> {
            return v0.getPath();
        }).collect(Collectors.joining(" "));
    }

    public Tomcat getTomcat() {
        return this.tomcat;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void shutDownGracefully(GracefulShutdownCallback callback) {
        if (this.gracefulShutdown == null) {
            callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
        } else {
            this.gracefulShutdown.shutDownGracefully(callback);
        }
    }
}
