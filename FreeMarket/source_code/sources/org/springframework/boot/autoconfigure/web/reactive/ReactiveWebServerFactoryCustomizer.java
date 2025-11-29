package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryCustomizer.class */
public class ReactiveWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory>, Ordered {
    private final ServerProperties serverProperties;

    public ReactiveWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableReactiveWebServerFactory factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerProperties serverProperties = this.serverProperties;
        serverProperties.getClass();
        PropertyMapper.Source sourceFrom = map.from(serverProperties::getPort);
        factory.getClass();
        sourceFrom.to((v1) -> {
            r1.setPort(v1);
        });
        ServerProperties serverProperties2 = this.serverProperties;
        serverProperties2.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(serverProperties2::getAddress);
        factory.getClass();
        sourceFrom2.to(factory::setAddress);
        ServerProperties serverProperties3 = this.serverProperties;
        serverProperties3.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(serverProperties3::getSsl);
        factory.getClass();
        sourceFrom3.to(factory::setSsl);
        ServerProperties serverProperties4 = this.serverProperties;
        serverProperties4.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(serverProperties4::getCompression);
        factory.getClass();
        sourceFrom4.to(factory::setCompression);
        ServerProperties serverProperties5 = this.serverProperties;
        serverProperties5.getClass();
        PropertyMapper.Source sourceFrom5 = map.from(serverProperties5::getHttp2);
        factory.getClass();
        sourceFrom5.to(factory::setHttp2);
        PropertyMapper.Source sourceFrom6 = map.from((PropertyMapper) this.serverProperties.getShutdown());
        factory.getClass();
        sourceFrom6.to(factory::setShutdown);
    }
}
