package org.springframework.boot.autoconfigure.web.embedded;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.catalina.valves.RemoteIpValve;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/TomcatWebServerFactoryCustomizer.class */
public class TomcatWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public TomcatWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties properties = this.serverProperties;
        ServerProperties.Tomcat tomcatProperties = properties.getTomcat();
        PropertyMapper propertyMapper = PropertyMapper.get();
        tomcatProperties.getClass();
        PropertyMapper.Source sourceWhenNonNull = propertyMapper.from(tomcatProperties::getBasedir).whenNonNull();
        factory.getClass();
        sourceWhenNonNull.to(factory::setBaseDirectory);
        tomcatProperties.getClass();
        PropertyMapper.Source sourceAs = propertyMapper.from(tomcatProperties::getBackgroundProcessorDelay).whenNonNull().as((v0) -> {
            return v0.getSeconds();
        }).as((v0) -> {
            return v0.intValue();
        });
        factory.getClass();
        sourceAs.to((v1) -> {
            r1.setBackgroundProcessorDelay(v1);
        });
        customizeRemoteIpValve(factory);
        ServerProperties.Tomcat.Threads threadProperties = tomcatProperties.getThreads();
        threadProperties.getClass();
        propertyMapper.from(threadProperties::getMax).when((v1) -> {
            return isPositive(v1);
        }).to(maxThreads -> {
            customizeMaxThreads(factory, threadProperties.getMax());
        });
        threadProperties.getClass();
        propertyMapper.from(threadProperties::getMinSpare).when((v1) -> {
            return isPositive(v1);
        }).to(minSpareThreads -> {
            customizeMinThreads(factory, minSpareThreads.intValue());
        });
        propertyMapper.from((PropertyMapper) this.serverProperties.getMaxHttpHeaderSize()).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(maxHttpHeaderSize -> {
            customizeMaxHttpHeaderSize(factory, maxHttpHeaderSize.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getMaxSwallowSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).to(maxSwallowSize -> {
            customizeMaxSwallowSize(factory, maxSwallowSize.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getMaxHttpFormPostSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(maxHttpFormPostSize -> {
            return maxHttpFormPostSize.intValue() != 0;
        }).to(maxHttpFormPostSize2 -> {
            customizeMaxHttpFormPostSize(factory, maxHttpFormPostSize2.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getAccesslog).when((v0) -> {
            return v0.isEnabled();
        }).to(enabled -> {
            customizeAccessLog(factory);
        });
        tomcatProperties.getClass();
        PropertyMapper.Source sourceWhenNonNull2 = propertyMapper.from(tomcatProperties::getUriEncoding).whenNonNull();
        factory.getClass();
        sourceWhenNonNull2.to(factory::setUriEncoding);
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getConnectionTimeout).whenNonNull().to(connectionTimeout -> {
            customizeConnectionTimeout(factory, connectionTimeout);
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getMaxConnections).when((v1) -> {
            return isPositive(v1);
        }).to(maxConnections -> {
            customizeMaxConnections(factory, maxConnections.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getAcceptCount).when((v1) -> {
            return isPositive(v1);
        }).to(acceptCount -> {
            customizeAcceptCount(factory, acceptCount.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getProcessorCache).to(processorCache -> {
            customizeProcessorCache(factory, processorCache.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getKeepAliveTimeout).whenNonNull().to(keepAliveTimeout -> {
            customizeKeepAliveTimeout(factory, keepAliveTimeout);
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getMaxKeepAliveRequests).to(maxKeepAliveRequests -> {
            customizeMaxKeepAliveRequests(factory, maxKeepAliveRequests.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getRelaxedPathChars).as(this::joinCharacters).whenHasText().to(relaxedChars -> {
            customizeRelaxedPathChars(factory, relaxedChars);
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::getRelaxedQueryChars).as(this::joinCharacters).whenHasText().to(relaxedChars2 -> {
            customizeRelaxedQueryChars(factory, relaxedChars2);
        });
        tomcatProperties.getClass();
        propertyMapper.from(tomcatProperties::isRejectIllegalHeader).to(rejectIllegalHeader -> {
            customizeRejectIllegalHeader(factory, rejectIllegalHeader.booleanValue());
        });
        customizeStaticResources(factory);
        customizeErrorReportValve(properties.getError(), factory);
    }

    private boolean isPositive(int value) {
        return value > 0;
    }

    private void customizeAcceptCount(ConfigurableTomcatWebServerFactory factory, int acceptCount) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol = (AbstractProtocol) handler;
                protocol.setAcceptCount(acceptCount);
            }
        });
    }

    private void customizeProcessorCache(ConfigurableTomcatWebServerFactory factory, int processorCache) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                ((AbstractProtocol) handler).setProcessorCache(processorCache);
            }
        });
    }

    private void customizeKeepAliveTimeout(ConfigurableTomcatWebServerFactory factory, Duration keepAliveTimeout) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            for (UpgradeProtocol upgradeProtocol : handler.findUpgradeProtocols()) {
                if (upgradeProtocol instanceof Http2Protocol) {
                    ((Http2Protocol) upgradeProtocol).setKeepAliveTimeout(keepAliveTimeout.toMillis());
                }
            }
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol = (AbstractProtocol) handler;
                protocol.setKeepAliveTimeout((int) keepAliveTimeout.toMillis());
            }
        });
    }

    private void customizeMaxKeepAliveRequests(ConfigurableTomcatWebServerFactory factory, int maxKeepAliveRequests) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol) handler;
                protocol.setMaxKeepAliveRequests(maxKeepAliveRequests);
            }
        });
    }

    private void customizeMaxConnections(ConfigurableTomcatWebServerFactory factory, int maxConnections) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol = (AbstractProtocol) handler;
                protocol.setMaxConnections(maxConnections);
            }
        });
    }

    private void customizeConnectionTimeout(ConfigurableTomcatWebServerFactory factory, Duration connectionTimeout) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol = (AbstractProtocol) handler;
                protocol.setConnectionTimeout((int) connectionTimeout.toMillis());
            }
        });
    }

    private void customizeRelaxedPathChars(ConfigurableTomcatWebServerFactory factory, String relaxedChars) {
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("relaxedPathChars", relaxedChars);
        });
    }

    private void customizeRelaxedQueryChars(ConfigurableTomcatWebServerFactory factory, String relaxedChars) {
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("relaxedQueryChars", relaxedChars);
        });
    }

    private void customizeRejectIllegalHeader(ConfigurableTomcatWebServerFactory factory, boolean rejectIllegalHeader) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol) handler;
                protocol.setRejectIllegalHeader(rejectIllegalHeader);
            }
        });
    }

    private String joinCharacters(List<Character> content) {
        return (String) content.stream().map((v0) -> {
            return String.valueOf(v0);
        }).collect(Collectors.joining());
    }

    private void customizeRemoteIpValve(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat.Remoteip remoteIpProperties = this.serverProperties.getTomcat().getRemoteip();
        String protocolHeader = remoteIpProperties.getProtocolHeader();
        String remoteIpHeader = remoteIpProperties.getRemoteIpHeader();
        if (StringUtils.hasText(protocolHeader) || StringUtils.hasText(remoteIpHeader) || getOrDeduceUseForwardHeaders()) {
            RemoteIpValve valve = new RemoteIpValve();
            valve.setProtocolHeader(StringUtils.hasLength(protocolHeader) ? protocolHeader : "X-Forwarded-Proto");
            if (StringUtils.hasLength(remoteIpHeader)) {
                valve.setRemoteIpHeader(remoteIpHeader);
            }
            valve.setInternalProxies(remoteIpProperties.getInternalProxies());
            try {
                valve.setHostHeader(remoteIpProperties.getHostHeader());
            } catch (NoSuchMethodError e) {
            }
            valve.setPortHeader(remoteIpProperties.getPortHeader());
            valve.setProtocolHeaderHttpsValue(remoteIpProperties.getProtocolHeaderHttpsValue());
            factory.addEngineValves(valve);
        }
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.getForwardHeadersStrategy() == null) {
            CloudPlatform platform = CloudPlatform.getActive(this.environment);
            return platform != null && platform.isUsingForwardHeaders();
        }
        return this.serverProperties.getForwardHeadersStrategy().equals(ServerProperties.ForwardHeadersStrategy.NATIVE);
    }

    private void customizeMaxThreads(ConfigurableTomcatWebServerFactory factory, int maxThreads) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol protocol = (AbstractProtocol) handler;
                protocol.setMaxThreads(maxThreads);
            }
        });
    }

    private void customizeMinThreads(ConfigurableTomcatWebServerFactory factory, int minSpareThreads) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol protocol = (AbstractProtocol) handler;
                protocol.setMinSpareThreads(minSpareThreads);
            }
        });
    }

    private void customizeMaxHttpHeaderSize(ConfigurableTomcatWebServerFactory factory, int maxHttpHeaderSize) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol protocol = (AbstractHttp11Protocol) handler;
                protocol.setMaxHttpHeaderSize(maxHttpHeaderSize);
                for (UpgradeProtocol upgradeProtocol : protocol.findUpgradeProtocols()) {
                    if (upgradeProtocol instanceof Http2Protocol) {
                        ((Http2Protocol) upgradeProtocol).setMaxHeaderSize(maxHttpHeaderSize);
                    }
                }
            }
        });
    }

    private void customizeMaxSwallowSize(ConfigurableTomcatWebServerFactory factory, int maxSwallowSize) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol) handler;
                protocol.setMaxSwallowSize(maxSwallowSize);
            }
        });
    }

    private void customizeMaxHttpFormPostSize(ConfigurableTomcatWebServerFactory factory, int maxHttpFormPostSize) {
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(maxHttpFormPostSize);
        });
    }

    private void customizeAccessLog(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat tomcatProperties = this.serverProperties.getTomcat();
        AccessLogValve valve = new AccessLogValve();
        PropertyMapper map = PropertyMapper.get();
        ServerProperties.Tomcat.Accesslog accessLogConfig = tomcatProperties.getAccesslog();
        PropertyMapper.Source sourceFrom = map.from((PropertyMapper) accessLogConfig.getConditionIf());
        valve.getClass();
        sourceFrom.to(valve::setConditionIf);
        PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) accessLogConfig.getConditionUnless());
        valve.getClass();
        sourceFrom2.to(valve::setConditionUnless);
        PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) accessLogConfig.getPattern());
        valve.getClass();
        sourceFrom3.to(valve::setPattern);
        PropertyMapper.Source sourceFrom4 = map.from((PropertyMapper) accessLogConfig.getDirectory());
        valve.getClass();
        sourceFrom4.to(valve::setDirectory);
        PropertyMapper.Source sourceFrom5 = map.from((PropertyMapper) accessLogConfig.getPrefix());
        valve.getClass();
        sourceFrom5.to(valve::setPrefix);
        PropertyMapper.Source sourceFrom6 = map.from((PropertyMapper) accessLogConfig.getSuffix());
        valve.getClass();
        sourceFrom6.to(valve::setSuffix);
        PropertyMapper.Source sourceWhenHasText = map.from((PropertyMapper) accessLogConfig.getEncoding()).whenHasText();
        valve.getClass();
        sourceWhenHasText.to(valve::setEncoding);
        PropertyMapper.Source sourceWhenHasText2 = map.from((PropertyMapper) accessLogConfig.getLocale()).whenHasText();
        valve.getClass();
        sourceWhenHasText2.to(valve::setLocale);
        PropertyMapper.Source sourceFrom7 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isCheckExists()));
        valve.getClass();
        sourceFrom7.to((v1) -> {
            r1.setCheckExists(v1);
        });
        PropertyMapper.Source sourceFrom8 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isRotate()));
        valve.getClass();
        sourceFrom8.to((v1) -> {
            r1.setRotatable(v1);
        });
        PropertyMapper.Source sourceFrom9 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isRenameOnRotate()));
        valve.getClass();
        sourceFrom9.to((v1) -> {
            r1.setRenameOnRotate(v1);
        });
        PropertyMapper.Source sourceFrom10 = map.from((PropertyMapper) Integer.valueOf(accessLogConfig.getMaxDays()));
        valve.getClass();
        sourceFrom10.to((v1) -> {
            r1.setMaxDays(v1);
        });
        PropertyMapper.Source sourceFrom11 = map.from((PropertyMapper) accessLogConfig.getFileDateFormat());
        valve.getClass();
        sourceFrom11.to(valve::setFileDateFormat);
        PropertyMapper.Source sourceFrom12 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isIpv6Canonical()));
        valve.getClass();
        sourceFrom12.to((v1) -> {
            r1.setIpv6Canonical(v1);
        });
        PropertyMapper.Source sourceFrom13 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isRequestAttributesEnabled()));
        valve.getClass();
        sourceFrom13.to((v1) -> {
            r1.setRequestAttributesEnabled(v1);
        });
        PropertyMapper.Source sourceFrom14 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isBuffered()));
        valve.getClass();
        sourceFrom14.to((v1) -> {
            r1.setBuffered(v1);
        });
        factory.addEngineValves(valve);
    }

    private void customizeStaticResources(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat.Resource resource = this.serverProperties.getTomcat().getResource();
        factory.addContextCustomizers(context -> {
            context.addLifecycleListener(event -> {
                if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
                    context.getResources().setCachingAllowed(resource.isAllowCaching());
                    if (resource.getCacheTtl() != null) {
                        long ttl = resource.getCacheTtl().toMillis();
                        context.getResources().setCacheTtl(ttl);
                    }
                }
            });
        });
    }

    private void customizeErrorReportValve(ErrorProperties error, ConfigurableTomcatWebServerFactory factory) {
        if (error.getIncludeStacktrace() == ErrorProperties.IncludeAttribute.NEVER) {
            factory.addContextCustomizers(context -> {
                ErrorReportValve valve = new ErrorReportValve();
                valve.setShowServerInfo(false);
                valve.setShowReport(false);
                context.getParent().getPipeline().addValve(valve);
            });
        }
    }
}
