package org.springframework.boot.autoconfigure.web.embedded;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/NettyWebServerFactoryCustomizer.class */
public class NettyWebServerFactoryCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public NettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.setUseForwardHeaders(getOrDeduceUseForwardHeaders());
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerProperties.Netty nettyProperties = this.serverProperties.getNetty();
        nettyProperties.getClass();
        propertyMapper.from(nettyProperties::getConnectionTimeout).whenNonNull().to(connectionTimeout -> {
            customizeConnectionTimeout(factory, connectionTimeout);
        });
        nettyProperties.getClass();
        propertyMapper.from(nettyProperties::getIdleTimeout).whenNonNull().to(idleTimeout -> {
            customizeIdleTimeout(factory, idleTimeout);
        });
        nettyProperties.getClass();
        propertyMapper.from(nettyProperties::getMaxKeepAliveRequests).to(maxKeepAliveRequests -> {
            customizeMaxKeepAliveRequests(factory, maxKeepAliveRequests.intValue());
        });
        customizeRequestDecoder(factory, propertyMapper);
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.getForwardHeadersStrategy() == null) {
            CloudPlatform platform = CloudPlatform.getActive(this.environment);
            return platform != null && platform.isUsingForwardHeaders();
        }
        return this.serverProperties.getForwardHeadersStrategy().equals(ServerProperties.ForwardHeadersStrategy.NATIVE);
    }

    private void customizeConnectionTimeout(NettyReactiveWebServerFactory factory, Duration connectionTimeout) {
        factory.addServerCustomizers(httpServer -> {
            return httpServer.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf((int) connectionTimeout.toMillis()));
        });
    }

    private void customizeRequestDecoder(NettyReactiveWebServerFactory factory, PropertyMapper propertyMapper) {
        factory.addServerCustomizers(httpServer -> {
            return httpServer.httpRequestDecoder(httpRequestDecoderSpec -> {
                propertyMapper.from((PropertyMapper) this.serverProperties.getMaxHttpHeaderSize()).whenNonNull().to(maxHttpRequestHeader -> {
                    httpRequestDecoderSpec.maxHeaderSize((int) maxHttpRequestHeader.toBytes());
                });
                ServerProperties.Netty nettyProperties = this.serverProperties.getNetty();
                propertyMapper.from((PropertyMapper) nettyProperties.getMaxChunkSize()).whenNonNull().to(maxChunkSize -> {
                    httpRequestDecoderSpec.maxChunkSize((int) maxChunkSize.toBytes());
                });
                propertyMapper.from((PropertyMapper) nettyProperties.getMaxInitialLineLength()).whenNonNull().to(maxInitialLineLength -> {
                    httpRequestDecoderSpec.maxInitialLineLength((int) maxInitialLineLength.toBytes());
                });
                propertyMapper.from((PropertyMapper) nettyProperties.getH2cMaxContentLength()).whenNonNull().to(h2cMaxContentLength -> {
                    httpRequestDecoderSpec.h2cMaxContentLength((int) h2cMaxContentLength.toBytes());
                });
                propertyMapper.from((PropertyMapper) nettyProperties.getInitialBufferSize()).whenNonNull().to(initialBufferSize -> {
                    httpRequestDecoderSpec.initialBufferSize((int) initialBufferSize.toBytes());
                });
                PropertyMapper.Source sourceWhenNonNull = propertyMapper.from((PropertyMapper) Boolean.valueOf(nettyProperties.isValidateHeaders())).whenNonNull();
                httpRequestDecoderSpec.getClass();
                sourceWhenNonNull.to((v1) -> {
                    r1.validateHeaders(v1);
                });
                return httpRequestDecoderSpec;
            });
        });
    }

    private void customizeIdleTimeout(NettyReactiveWebServerFactory factory, Duration idleTimeout) {
        factory.addServerCustomizers(httpServer -> {
            return httpServer.idleTimeout(idleTimeout);
        });
    }

    private void customizeMaxKeepAliveRequests(NettyReactiveWebServerFactory factory, int maxKeepAliveRequests) {
        factory.addServerCustomizers(httpServer -> {
            return httpServer.maxKeepAliveRequests(maxKeepAliveRequests);
        });
    }
}
