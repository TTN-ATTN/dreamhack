package org.springframework.boot.autoconfigure.web.embedded;

import io.undertow.UndertowOptions;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.xnio.Option;
import org.xnio.Options;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer.class */
public class UndertowWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableUndertowWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public UndertowWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableUndertowWebServerFactory factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerOptions options = new ServerOptions(factory);
        ServerProperties properties = this.serverProperties;
        properties.getClass();
        map.from(properties::getMaxHttpHeaderSize).asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(options.option(UndertowOptions.MAX_HEADER_SIZE));
        mapUndertowProperties(factory, options);
        mapAccessLogProperties(factory);
        PropertyMapper.Source sourceFrom = map.from(this::getOrDeduceUseForwardHeaders);
        factory.getClass();
        sourceFrom.to((v1) -> {
            r1.setUseForwardHeaders(v1);
        });
    }

    private void mapUndertowProperties(ConfigurableUndertowWebServerFactory factory, ServerOptions serverOptions) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerProperties.Undertow properties = this.serverProperties.getUndertow();
        properties.getClass();
        PropertyMapper.Source<Integer> sourceAsInt = map.from(properties::getBufferSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        });
        factory.getClass();
        sourceAsInt.to(factory::setBufferSize);
        ServerProperties.Undertow.Threads threadProperties = properties.getThreads();
        threadProperties.getClass();
        PropertyMapper.Source sourceFrom = map.from(threadProperties::getIo);
        factory.getClass();
        sourceFrom.to(factory::setIoThreads);
        threadProperties.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(threadProperties::getWorker);
        factory.getClass();
        sourceFrom2.to(factory::setWorkerThreads);
        properties.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(properties::getDirectBuffers);
        factory.getClass();
        sourceFrom3.to(factory::setUseDirectBuffers);
        properties.getClass();
        map.from(properties::getMaxHttpPostSize).as((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(serverOptions.option(UndertowOptions.MAX_ENTITY_SIZE));
        properties.getClass();
        map.from(properties::getMaxParameters).to(serverOptions.option(UndertowOptions.MAX_PARAMETERS));
        properties.getClass();
        map.from(properties::getMaxHeaders).to(serverOptions.option(UndertowOptions.MAX_HEADERS));
        properties.getClass();
        map.from(properties::getMaxCookies).to(serverOptions.option(UndertowOptions.MAX_COOKIES));
        properties.getClass();
        map.from(properties::isAllowEncodedSlash).to(serverOptions.option(UndertowOptions.ALLOW_ENCODED_SLASH));
        properties.getClass();
        map.from(properties::isDecodeUrl).to(serverOptions.option(UndertowOptions.DECODE_URL));
        properties.getClass();
        map.from(properties::getUrlCharset).as((v0) -> {
            return v0.name();
        }).to(serverOptions.option(UndertowOptions.URL_CHARSET));
        properties.getClass();
        map.from(properties::isAlwaysSetKeepAlive).to(serverOptions.option(UndertowOptions.ALWAYS_SET_KEEP_ALIVE));
        properties.getClass();
        map.from(properties::getNoRequestTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(serverOptions.option(UndertowOptions.NO_REQUEST_TIMEOUT));
        ServerProperties.Undertow.Options options = properties.getOptions();
        options.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(options::getServer);
        serverOptions.getClass();
        sourceFrom4.to(serverOptions.forEach(serverOptions::option));
        SocketOptions socketOptions = new SocketOptions(factory);
        ServerProperties.Undertow.Options options2 = properties.getOptions();
        options2.getClass();
        PropertyMapper.Source sourceFrom5 = map.from(options2::getSocket);
        socketOptions.getClass();
        sourceFrom5.to(socketOptions.forEach(socketOptions::option));
    }

    private boolean isPositive(Number value) {
        return value.longValue() > 0;
    }

    private void mapAccessLogProperties(ConfigurableUndertowWebServerFactory factory) {
        ServerProperties.Undertow.Accesslog properties = this.serverProperties.getUndertow().getAccesslog();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        properties.getClass();
        PropertyMapper.Source sourceFrom = map.from(properties::isEnabled);
        factory.getClass();
        sourceFrom.to((v1) -> {
            r1.setAccessLogEnabled(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(properties::getDir);
        factory.getClass();
        sourceFrom2.to(factory::setAccessLogDirectory);
        properties.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(properties::getPattern);
        factory.getClass();
        sourceFrom3.to(factory::setAccessLogPattern);
        properties.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(properties::getPrefix);
        factory.getClass();
        sourceFrom4.to(factory::setAccessLogPrefix);
        properties.getClass();
        PropertyMapper.Source sourceFrom5 = map.from(properties::getSuffix);
        factory.getClass();
        sourceFrom5.to(factory::setAccessLogSuffix);
        properties.getClass();
        PropertyMapper.Source sourceFrom6 = map.from(properties::isRotate);
        factory.getClass();
        sourceFrom6.to((v1) -> {
            r1.setAccessLogRotate(v1);
        });
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.getForwardHeadersStrategy() == null) {
            CloudPlatform platform = CloudPlatform.getActive(this.environment);
            return platform != null && platform.isUsingForwardHeaders();
        }
        return this.serverProperties.getForwardHeadersStrategy().equals(ServerProperties.ForwardHeadersStrategy.NATIVE);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer$AbstractOptions.class */
    private static abstract class AbstractOptions {
        private final Class<?> source;
        private final Map<String, Option<?>> nameLookup;
        private final ConfigurableUndertowWebServerFactory factory;

        AbstractOptions(Class<?> source, ConfigurableUndertowWebServerFactory factory) throws IllegalArgumentException {
            Map<String, Option<?>> lookup = new HashMap<>();
            ReflectionUtils.doWithLocalFields(source, field -> {
                int modifiers = field.getModifiers();
                if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Option.class.isAssignableFrom(field.getType())) {
                    try {
                        Option<?> option = (Option) field.get(null);
                        lookup.put(getCanonicalName(field.getName()), option);
                    } catch (IllegalAccessException e) {
                    }
                }
            });
            this.source = source;
            this.nameLookup = Collections.unmodifiableMap(lookup);
            this.factory = factory;
        }

        protected ConfigurableUndertowWebServerFactory getFactory() {
            return this.factory;
        }

        <T> Consumer<Map<String, String>> forEach(Function<Option<T>, Consumer<T>> function) {
            return map -> {
                map.forEach((key, value) -> {
                    Option<?> option = this.nameLookup.get(getCanonicalName(key));
                    Assert.state(option != null, (Supplier<String>) () -> {
                        return "Unable to find '" + key + "' in " + ClassUtils.getShortName(this.source);
                    });
                    ((Consumer) function.apply(option)).accept(option.parseValue(value, getClass().getClassLoader()));
                });
            };
        }

        private static String getCanonicalName(String name) {
            StringBuilder canonicalName = new StringBuilder(name.length());
            name.chars().filter(Character::isLetterOrDigit).map(Character::toLowerCase).forEach(c -> {
                canonicalName.append((char) c);
            });
            return canonicalName.toString();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer$ServerOptions.class */
    private static class ServerOptions extends AbstractOptions {
        ServerOptions(ConfigurableUndertowWebServerFactory factory) {
            super(UndertowOptions.class, factory);
        }

        <T> Consumer<T> option(Option<T> option) {
            return value -> {
                getFactory().addBuilderCustomizers(builder -> {
                    builder.setServerOption(option, value);
                });
            };
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer$SocketOptions.class */
    private static class SocketOptions extends AbstractOptions {
        SocketOptions(ConfigurableUndertowWebServerFactory factory) {
            super(Options.class, factory);
        }

        <T> Consumer<T> option(Option<T> option) {
            return value -> {
                getFactory().addBuilderCustomizers(builder -> {
                    builder.setSocketOption(option, value);
                });
            };
        }
    }
}
