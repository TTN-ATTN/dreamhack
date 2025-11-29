package org.springframework.boot.autoconfigure.web.embedded;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import org.apache.coyote.http11.Constants;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.RequestLogWriter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/JettyWebServerFactoryCustomizer.class */
public class JettyWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public JettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableJettyWebServerFactory factory) {
        ServerProperties properties = this.serverProperties;
        ServerProperties.Jetty jettyProperties = properties.getJetty();
        factory.setUseForwardHeaders(getOrDeduceUseForwardHeaders());
        ServerProperties.Jetty.Threads threadProperties = jettyProperties.getThreads();
        factory.setThreadPool(determineThreadPool(jettyProperties.getThreads()));
        PropertyMapper propertyMapper = PropertyMapper.get();
        threadProperties.getClass();
        PropertyMapper.Source sourceWhenNonNull = propertyMapper.from(threadProperties::getAcceptors).whenNonNull();
        factory.getClass();
        sourceWhenNonNull.to((v1) -> {
            r1.setAcceptors(v1);
        });
        threadProperties.getClass();
        PropertyMapper.Source sourceWhenNonNull2 = propertyMapper.from(threadProperties::getSelectors).whenNonNull();
        factory.getClass();
        sourceWhenNonNull2.to((v1) -> {
            r1.setSelectors(v1);
        });
        properties.getClass();
        propertyMapper.from(properties::getMaxHttpHeaderSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).when(this::isPositive).to(maxHttpHeaderSize -> {
            factory.addServerCustomizers(new MaxHttpHeaderSizeCustomizer(maxHttpHeaderSize.intValue()));
        });
        jettyProperties.getClass();
        propertyMapper.from(jettyProperties::getMaxHttpFormPostSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(this::isPositive).to(maxHttpFormPostSize -> {
            customizeMaxHttpFormPostSize(factory, maxHttpFormPostSize.intValue());
        });
        jettyProperties.getClass();
        propertyMapper.from(jettyProperties::getConnectionIdleTimeout).whenNonNull().to(idleTimeout -> {
            customizeIdleTimeout(factory, idleTimeout);
        });
        jettyProperties.getClass();
        propertyMapper.from(jettyProperties::getAccesslog).when((v0) -> {
            return v0.isEnabled();
        }).to(accesslog -> {
            customizeAccessLog(factory, accesslog);
        });
    }

    private boolean isPositive(Integer value) {
        return value.intValue() > 0;
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.getForwardHeadersStrategy() == null) {
            CloudPlatform platform = CloudPlatform.getActive(this.environment);
            return platform != null && platform.isUsingForwardHeaders();
        }
        return this.serverProperties.getForwardHeadersStrategy().equals(ServerProperties.ForwardHeadersStrategy.NATIVE);
    }

    private void customizeIdleTimeout(ConfigurableJettyWebServerFactory factory, Duration connectionTimeout) {
        factory.addServerCustomizers(server -> {
            for (AbstractConnector abstractConnector : server.getConnectors()) {
                if (abstractConnector instanceof AbstractConnector) {
                    abstractConnector.setIdleTimeout(connectionTimeout.toMillis());
                }
            }
        });
    }

    private void customizeMaxHttpFormPostSize(ConfigurableJettyWebServerFactory factory, final int maxHttpFormPostSize) {
        factory.addServerCustomizers(new JettyServerCustomizer() { // from class: org.springframework.boot.autoconfigure.web.embedded.JettyWebServerFactoryCustomizer.1
            @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
            public void customize(Server server) {
                setHandlerMaxHttpFormPostSize(server.getHandlers());
            }

            private void setHandlerMaxHttpFormPostSize(Handler... handlers) {
                for (Handler handler : handlers) {
                    if (handler instanceof ContextHandler) {
                        ((ContextHandler) handler).setMaxFormContentSize(maxHttpFormPostSize);
                    } else if (handler instanceof HandlerWrapper) {
                        setHandlerMaxHttpFormPostSize(((HandlerWrapper) handler).getHandler());
                    } else if (handler instanceof HandlerCollection) {
                        setHandlerMaxHttpFormPostSize(((HandlerCollection) handler).getHandlers());
                    }
                }
            }
        });
    }

    private ThreadPool determineThreadPool(ServerProperties.Jetty.Threads properties) {
        BlockingQueue<Runnable> queue = determineBlockingQueue(properties.getMaxQueueCapacity());
        int maxThreadCount = properties.getMax().intValue() > 0 ? properties.getMax().intValue() : 200;
        int minThreadCount = properties.getMin().intValue() > 0 ? properties.getMin().intValue() : 8;
        int threadIdleTimeout = properties.getIdleTimeout() != null ? (int) properties.getIdleTimeout().toMillis() : Constants.DEFAULT_CONNECTION_TIMEOUT;
        return new QueuedThreadPool(maxThreadCount, minThreadCount, threadIdleTimeout, queue);
    }

    private BlockingQueue<Runnable> determineBlockingQueue(Integer maxQueueCapacity) {
        if (maxQueueCapacity == null) {
            return null;
        }
        if (maxQueueCapacity.intValue() == 0) {
            return new SynchronousQueue();
        }
        return new BlockingArrayQueue(maxQueueCapacity.intValue());
    }

    private void customizeAccessLog(ConfigurableJettyWebServerFactory factory, ServerProperties.Jetty.Accesslog properties) {
        factory.addServerCustomizers(server -> {
            RequestLogWriter logWriter = new RequestLogWriter();
            String format = getLogFormat(properties);
            CustomRequestLog log = new CustomRequestLog(logWriter, format);
            if (!CollectionUtils.isEmpty(properties.getIgnorePaths())) {
                log.setIgnorePaths((String[]) properties.getIgnorePaths().toArray(new String[0]));
            }
            if (properties.getFilename() != null) {
                logWriter.setFilename(properties.getFilename());
            }
            if (properties.getFileDateFormat() != null) {
                logWriter.setFilenameDateFormat(properties.getFileDateFormat());
            }
            logWriter.setRetainDays(properties.getRetentionPeriod());
            logWriter.setAppend(properties.isAppend());
            server.setRequestLog(log);
        });
    }

    private String getLogFormat(ServerProperties.Jetty.Accesslog properties) {
        if (properties.getCustomFormat() != null) {
            return properties.getCustomFormat();
        }
        if (ServerProperties.Jetty.Accesslog.FORMAT.EXTENDED_NCSA.equals(properties.getFormat())) {
            return "%{client}a - %u %t \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"";
        }
        return "%{client}a - %u %t \"%r\" %s %O";
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/JettyWebServerFactoryCustomizer$MaxHttpHeaderSizeCustomizer.class */
    private static class MaxHttpHeaderSizeCustomizer implements JettyServerCustomizer {
        private final int maxHttpHeaderSize;

        MaxHttpHeaderSizeCustomizer(int maxHttpHeaderSize) {
            this.maxHttpHeaderSize = maxHttpHeaderSize;
        }

        @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
        public void customize(Server server) {
            Arrays.stream(server.getConnectors()).forEach(this::customize);
        }

        private void customize(Connector connector) {
            connector.getConnectionFactories().forEach(this::customize);
        }

        private void customize(ConnectionFactory factory) {
            if (factory instanceof HttpConfiguration.ConnectionFactory) {
                ((HttpConfiguration.ConnectionFactory) factory).getHttpConfiguration().setRequestHeaderSize(this.maxHttpHeaderSize);
            }
        }
    }
}
