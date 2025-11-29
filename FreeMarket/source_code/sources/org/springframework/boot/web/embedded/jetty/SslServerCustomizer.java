package org.springframework.boot.web.embedded.jetty;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.function.Supplier;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslConfigurationValidator;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/SslServerCustomizer.class */
class SslServerCustomizer implements JettyServerCustomizer {
    private final InetSocketAddress address;
    private final Ssl ssl;
    private final SslStoreProvider sslStoreProvider;
    private final Http2 http2;

    SslServerCustomizer(InetSocketAddress address, Ssl ssl, SslStoreProvider sslStoreProvider, Http2 http2) {
        this.address = address;
        this.ssl = ssl;
        this.sslStoreProvider = sslStoreProvider;
        this.http2 = http2;
    }

    @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
    public void customize(Server server) {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setEndpointIdentificationAlgorithm((String) null);
        configureSsl(sslContextFactory, this.ssl, this.sslStoreProvider);
        server.setConnectors(new Connector[]{createConnector(server, sslContextFactory, this.address)});
    }

    private ServerConnector createConnector(Server server, SslContextFactory.Server sslContextFactory, InetSocketAddress address) {
        HttpConfiguration config = new HttpConfiguration();
        config.setSendServerVersion(false);
        config.setSecureScheme("https");
        config.setSecurePort(address.getPort());
        config.addCustomizer(new SecureRequestCustomizer());
        ServerConnector connector = createServerConnector(server, sslContextFactory, config);
        connector.setPort(address.getPort());
        connector.setHost(address.getHostString());
        return connector;
    }

    private ServerConnector createServerConnector(Server server, SslContextFactory.Server sslContextFactory, HttpConfiguration config) {
        if (this.http2 == null || !this.http2.isEnabled()) {
            return createHttp11ServerConnector(server, config, sslContextFactory);
        }
        Assert.state(isJettyAlpnPresent(), (Supplier<String>) () -> {
            return "An 'org.eclipse.jetty:jetty-alpn-*-server' dependency is required for HTTP/2 support.";
        });
        Assert.state(isJettyHttp2Present(), (Supplier<String>) () -> {
            return "The 'org.eclipse.jetty.http2:http2-server' dependency is required for HTTP/2 support.";
        });
        return createHttp2ServerConnector(server, config, sslContextFactory);
    }

    private ServerConnector createHttp11ServerConnector(Server server, HttpConfiguration config, SslContextFactory.Server sslContextFactory) {
        HttpConnectionFactory connectionFactory = new HttpConnectionFactory(config);
        return new SslValidatingServerConnector(server, sslContextFactory, this.ssl.getKeyAlias(), createSslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), connectionFactory);
    }

    private SslConnectionFactory createSslConnectionFactory(SslContextFactory.Server sslContextFactory, String protocol) {
        try {
            return new SslConnectionFactory(sslContextFactory, protocol);
        } catch (NoSuchMethodError e) {
            try {
                return (SslConnectionFactory) SslConnectionFactory.class.getConstructor(SslContextFactory.Server.class, String.class).newInstance(sslContextFactory, protocol);
            } catch (Exception ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    private boolean isJettyAlpnPresent() {
        return ClassUtils.isPresent("org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory", null);
    }

    private boolean isJettyHttp2Present() {
        return ClassUtils.isPresent("org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory", null);
    }

    private ServerConnector createHttp2ServerConnector(Server server, HttpConfiguration config, SslContextFactory.Server sslContextFactory) {
        ConnectionFactory httpConnectionFactory = new HttpConnectionFactory(config);
        ConnectionFactory hTTP2ServerConnectionFactory = new HTTP2ServerConnectionFactory(config);
        ConnectionFactory connectionFactoryCreateAlpnServerConnectionFactory = createAlpnServerConnectionFactory();
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        if (isConscryptPresent()) {
            sslContextFactory.setProvider("Conscrypt");
        }
        return new SslValidatingServerConnector(server, sslContextFactory, this.ssl.getKeyAlias(), createSslConnectionFactory(sslContextFactory, connectionFactoryCreateAlpnServerConnectionFactory.getProtocol()), connectionFactoryCreateAlpnServerConnectionFactory, hTTP2ServerConnectionFactory, httpConnectionFactory);
    }

    private ALPNServerConnectionFactory createAlpnServerConnectionFactory() {
        try {
            return new ALPNServerConnectionFactory(new String[0]);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("An 'org.eclipse.jetty:jetty-alpn-*-server' dependency is required for HTTP/2 support.", ex);
        }
    }

    private boolean isConscryptPresent() {
        return ClassUtils.isPresent("org.conscrypt.Conscrypt", null) && ClassUtils.isPresent("org.eclipse.jetty.alpn.conscrypt.server.ConscryptServerALPNProcessor", null);
    }

    protected void configureSsl(SslContextFactory.Server factory, Ssl ssl, SslStoreProvider sslStoreProvider) {
        factory.setProtocol(ssl.getProtocol());
        configureSslClientAuth(factory, ssl);
        configureSslPasswords(factory, ssl);
        factory.setCertAlias(ssl.getKeyAlias());
        if (!ObjectUtils.isEmpty((Object[]) ssl.getCiphers())) {
            factory.setIncludeCipherSuites(ssl.getCiphers());
            factory.setExcludeCipherSuites(new String[0]);
        }
        if (ssl.getEnabledProtocols() != null) {
            factory.setIncludeProtocols(ssl.getEnabledProtocols());
        }
        if (sslStoreProvider != null) {
            try {
                String keyPassword = sslStoreProvider.getKeyPassword();
                if (keyPassword != null) {
                    factory.setKeyManagerPassword(keyPassword);
                }
                factory.setKeyStore(sslStoreProvider.getKeyStore());
                factory.setTrustStore(sslStoreProvider.getTrustStore());
                return;
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to set SSL store", ex);
            }
        }
        configureSslKeyStore(factory, ssl);
        configureSslTrustStore(factory, ssl);
    }

    private void configureSslClientAuth(SslContextFactory.Server factory, Ssl ssl) {
        if (ssl.getClientAuth() == Ssl.ClientAuth.NEED) {
            factory.setNeedClientAuth(true);
            factory.setWantClientAuth(true);
        } else if (ssl.getClientAuth() == Ssl.ClientAuth.WANT) {
            factory.setWantClientAuth(true);
        }
    }

    private void configureSslPasswords(SslContextFactory.Server factory, Ssl ssl) {
        if (ssl.getKeyStorePassword() != null) {
            factory.setKeyStorePassword(ssl.getKeyStorePassword());
        }
        if (ssl.getKeyPassword() != null) {
            factory.setKeyManagerPassword(ssl.getKeyPassword());
        }
    }

    private void configureSslKeyStore(SslContextFactory.Server factory, Ssl ssl) {
        String keystoreType = ssl.getKeyStoreType() != null ? ssl.getKeyStoreType() : SSL.DEFAULT_KEYSTORE_TYPE;
        String keystoreLocation = ssl.getKeyStore();
        if (keystoreType.equalsIgnoreCase("PKCS11")) {
            Assert.state(!StringUtils.hasText(keystoreLocation), (Supplier<String>) () -> {
                return "Keystore location '" + keystoreLocation + "' must be empty or null for PKCS11 key stores";
            });
        } else {
            try {
                URL url = ResourceUtils.getURL(keystoreLocation);
                factory.setKeyStoreResource(Resource.newResource(url));
            } catch (Exception ex) {
                throw new WebServerException("Could not load key store '" + keystoreLocation + "'", ex);
            }
        }
        factory.setKeyStoreType(keystoreType);
        if (ssl.getKeyStoreProvider() != null) {
            factory.setKeyStoreProvider(this.ssl.getKeyStoreProvider());
        }
    }

    private void configureSslTrustStore(SslContextFactory.Server factory, Ssl ssl) {
        if (ssl.getTrustStorePassword() != null) {
            factory.setTrustStorePassword(ssl.getTrustStorePassword());
        }
        if (ssl.getTrustStore() != null) {
            try {
                URL url = ResourceUtils.getURL(ssl.getTrustStore());
                factory.setTrustStoreResource(Resource.newResource(url));
            } catch (IOException ex) {
                throw new WebServerException("Could not find trust store '" + ssl.getTrustStore() + "'", ex);
            }
        }
        if (ssl.getTrustStoreType() != null) {
            factory.setTrustStoreType(ssl.getTrustStoreType());
        }
        if (ssl.getTrustStoreProvider() != null) {
            factory.setTrustStoreProvider(ssl.getTrustStoreProvider());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/SslServerCustomizer$SslValidatingServerConnector.class */
    static class SslValidatingServerConnector extends ServerConnector {
        private final SslContextFactory sslContextFactory;
        private final String keyAlias;

        /* JADX WARN: Multi-variable type inference failed */
        SslValidatingServerConnector(Server server, SslContextFactory sslContextFactory, String keyAlias, SslConnectionFactory sslConnectionFactory, HttpConnectionFactory connectionFactory) {
            super(server, new ConnectionFactory[]{sslConnectionFactory, connectionFactory});
            this.sslContextFactory = sslContextFactory;
            this.keyAlias = keyAlias;
        }

        SslValidatingServerConnector(Server server, SslContextFactory sslContextFactory, String keyAlias, ConnectionFactory... factories) {
            super(server, factories);
            this.sslContextFactory = sslContextFactory;
            this.keyAlias = keyAlias;
        }

        protected void doStart() throws Exception {
            super.doStart();
            SslConfigurationValidator.validateKeyAlias(this.sslContextFactory.getKeyStore(), this.keyAlias);
        }
    }
}
