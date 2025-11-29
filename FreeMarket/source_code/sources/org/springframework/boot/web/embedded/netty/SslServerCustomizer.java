package org.springframework.boot.web.embedded.netty;

import ch.qos.logback.core.net.ssl.SSL;
import io.netty.handler.ssl.ClientAuth;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslConfigurationValidator;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.AbstractProtocolSslContextSpec;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/netty/SslServerCustomizer.class */
public class SslServerCustomizer implements NettyServerCustomizer {
    private final Ssl ssl;
    private final Http2 http2;
    private final SslStoreProvider sslStoreProvider;

    public SslServerCustomizer(Ssl ssl, Http2 http2, SslStoreProvider sslStoreProvider) {
        this.ssl = ssl;
        this.http2 = http2;
        this.sslStoreProvider = sslStoreProvider;
    }

    @Override // java.util.function.Function
    public HttpServer apply(HttpServer server) {
        AbstractProtocolSslContextSpec<?> sslContextSpec = createSslContextSpec();
        return server.secure(spec -> {
            spec.sslContext(sslContextSpec);
        });
    }

    protected AbstractProtocolSslContextSpec<?> createSslContextSpec() {
        Http2SslContextSpec http2SslContextSpecForServer;
        if (this.http2 != null && this.http2.isEnabled()) {
            http2SslContextSpecForServer = Http2SslContextSpec.forServer(getKeyManagerFactory(this.ssl, this.sslStoreProvider));
        } else {
            http2SslContextSpecForServer = Http11SslContextSpec.forServer(getKeyManagerFactory(this.ssl, this.sslStoreProvider));
        }
        http2SslContextSpecForServer.configure(builder -> {
            builder.trustManager(getTrustManagerFactory(this.ssl, this.sslStoreProvider));
            if (this.ssl.getEnabledProtocols() != null) {
                builder.protocols(this.ssl.getEnabledProtocols());
            }
            if (this.ssl.getCiphers() != null) {
                builder.ciphers(Arrays.asList(this.ssl.getCiphers()));
            }
            if (this.ssl.getClientAuth() == Ssl.ClientAuth.NEED) {
                builder.clientAuth(ClientAuth.REQUIRE);
            } else if (this.ssl.getClientAuth() == Ssl.ClientAuth.WANT) {
                builder.clientAuth(ClientAuth.OPTIONAL);
            }
        });
        return http2SslContextSpecForServer;
    }

    KeyManagerFactory getKeyManagerFactory(Ssl ssl, SslStoreProvider sslStoreProvider) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyManagerFactory configurableAliasKeyManagerFactory;
        try {
            KeyStore keyStore = getKeyStore(ssl, sslStoreProvider);
            SslConfigurationValidator.validateKeyAlias(keyStore, ssl.getKeyAlias());
            if (ssl.getKeyAlias() == null) {
                configurableAliasKeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            } else {
                configurableAliasKeyManagerFactory = new ConfigurableAliasKeyManagerFactory(ssl.getKeyAlias(), KeyManagerFactory.getDefaultAlgorithm());
            }
            KeyManagerFactory keyManagerFactory = configurableAliasKeyManagerFactory;
            String keyPassword = sslStoreProvider != null ? sslStoreProvider.getKeyPassword() : null;
            if (keyPassword == null) {
                keyPassword = ssl.getKeyPassword() != null ? ssl.getKeyPassword() : ssl.getKeyStorePassword();
            }
            keyManagerFactory.init(keyStore, keyPassword != null ? keyPassword.toCharArray() : null);
            return keyManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private KeyStore getKeyStore(Ssl ssl, SslStoreProvider sslStoreProvider) throws Exception {
        if (sslStoreProvider != null) {
            return sslStoreProvider.getKeyStore();
        }
        return loadKeyStore(ssl.getKeyStoreType(), ssl.getKeyStoreProvider(), ssl.getKeyStore(), ssl.getKeyStorePassword());
    }

    TrustManagerFactory getTrustManagerFactory(Ssl ssl, SslStoreProvider sslStoreProvider) throws NoSuchAlgorithmException, KeyStoreException {
        try {
            KeyStore store = getTrustStore(ssl, sslStoreProvider);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(store);
            return trustManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private KeyStore getTrustStore(Ssl ssl, SslStoreProvider sslStoreProvider) throws Exception {
        if (sslStoreProvider != null) {
            return sslStoreProvider.getTrustStore();
        }
        return loadTrustStore(ssl.getTrustStoreType(), ssl.getTrustStoreProvider(), ssl.getTrustStore(), ssl.getTrustStorePassword());
    }

    private KeyStore loadKeyStore(String type, String provider, String resource, String password) throws Exception {
        return loadStore(type, provider, resource, password);
    }

    private KeyStore loadTrustStore(String type, String provider, String resource, String password) throws Exception {
        if (resource == null) {
            return null;
        }
        return loadStore(type, provider, resource, password);
    }

    private KeyStore loadStore(String keystoreType, String provider, String keystoreLocation, String password) throws Exception {
        String keystoreType2 = keystoreType != null ? keystoreType : SSL.DEFAULT_KEYSTORE_TYPE;
        char[] passwordChars = password != null ? password.toCharArray() : null;
        KeyStore store = provider != null ? KeyStore.getInstance(keystoreType2, provider) : KeyStore.getInstance(keystoreType2);
        if (keystoreType2.equalsIgnoreCase("PKCS11")) {
            Assert.state(!StringUtils.hasText(keystoreLocation), (Supplier<String>) () -> {
                return "Keystore location '" + keystoreLocation + "' must be empty or null for PKCS11 key stores";
            });
            store.load(null, passwordChars);
        } else {
            try {
                URL url = ResourceUtils.getURL(keystoreLocation);
                InputStream stream = url.openStream();
                Throwable th = null;
                try {
                    try {
                        store.load(stream, passwordChars);
                        if (stream != null) {
                            if (0 != 0) {
                                try {
                                    stream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                stream.close();
                            }
                        }
                    } finally {
                    }
                } finally {
                }
            } catch (Exception ex) {
                throw new WebServerException("Could not load key store '" + keystoreLocation + "'", ex);
            }
        }
        return store;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/netty/SslServerCustomizer$ConfigurableAliasKeyManagerFactory.class */
    private static final class ConfigurableAliasKeyManagerFactory extends KeyManagerFactory {
        private ConfigurableAliasKeyManagerFactory(String alias, String algorithm) throws NoSuchAlgorithmException {
            this(KeyManagerFactory.getInstance(algorithm), alias, algorithm);
        }

        private ConfigurableAliasKeyManagerFactory(KeyManagerFactory delegate, String alias, String algorithm) {
            super(new ConfigurableAliasKeyManagerFactorySpi(delegate, alias), delegate.getProvider(), algorithm);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/netty/SslServerCustomizer$ConfigurableAliasKeyManagerFactorySpi.class */
    private static final class ConfigurableAliasKeyManagerFactorySpi extends KeyManagerFactorySpi {
        private final KeyManagerFactory delegate;
        private final String alias;

        private ConfigurableAliasKeyManagerFactorySpi(KeyManagerFactory delegate, String alias) {
            this.delegate = delegate;
            this.alias = alias;
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected void engineInit(KeyStore keyStore, char[] chars) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
            this.delegate.init(keyStore, chars);
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("Unsupported ManagerFactoryParameters");
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected KeyManager[] engineGetKeyManagers() {
            Stream stream = Arrays.stream(this.delegate.getKeyManagers());
            Class<X509ExtendedKeyManager> cls = X509ExtendedKeyManager.class;
            X509ExtendedKeyManager.class.getClass();
            Stream streamFilter = stream.filter((v1) -> {
                return r1.isInstance(v1);
            });
            Class<X509ExtendedKeyManager> cls2 = X509ExtendedKeyManager.class;
            X509ExtendedKeyManager.class.getClass();
            return (KeyManager[]) streamFilter.map((v1) -> {
                return r1.cast(v1);
            }).map(this::wrap).toArray(x$0 -> {
                return new KeyManager[x$0];
            });
        }

        private ConfigurableAliasKeyManager wrap(X509ExtendedKeyManager keyManager) {
            return new ConfigurableAliasKeyManager(keyManager, this.alias);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/netty/SslServerCustomizer$ConfigurableAliasKeyManager.class */
    private static final class ConfigurableAliasKeyManager extends X509ExtendedKeyManager {
        private final X509ExtendedKeyManager delegate;
        private final String alias;

        private ConfigurableAliasKeyManager(X509ExtendedKeyManager keyManager, String alias) {
            this.delegate = keyManager;
            this.alias = alias;
        }

        @Override // javax.net.ssl.X509ExtendedKeyManager
        public String chooseEngineClientAlias(String[] strings, Principal[] principals, SSLEngine sslEngine) {
            return this.delegate.chooseEngineClientAlias(strings, principals, sslEngine);
        }

        @Override // javax.net.ssl.X509ExtendedKeyManager
        public String chooseEngineServerAlias(String s, Principal[] principals, SSLEngine sslEngine) {
            return this.alias != null ? this.alias : this.delegate.chooseEngineServerAlias(s, principals, sslEngine);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
            return this.delegate.chooseClientAlias(keyType, issuers, socket);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return this.delegate.chooseServerAlias(keyType, issuers, socket);
        }

        @Override // javax.net.ssl.X509KeyManager
        public X509Certificate[] getCertificateChain(String alias) {
            return this.delegate.getCertificateChain(alias);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return this.delegate.getClientAliases(keyType, issuers);
        }

        @Override // javax.net.ssl.X509KeyManager
        public PrivateKey getPrivateKey(String alias) {
            return this.delegate.getPrivateKey(alias);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return this.delegate.getServerAliases(keyType, issuers);
        }
    }
}
