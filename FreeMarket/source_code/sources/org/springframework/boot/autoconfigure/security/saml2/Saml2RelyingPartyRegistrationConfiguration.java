package org.springframework.boot.autoconfigure.security.saml2;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.log.LogMessage;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@ConditionalOnMissingBean({RelyingPartyRegistrationRepository.class})
@Configuration(proxyBeanMethods = false)
@Conditional({RegistrationConfiguredCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyRegistrationConfiguration.class */
class Saml2RelyingPartyRegistrationConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) Saml2RelyingPartyRegistrationConfiguration.class);

    Saml2RelyingPartyRegistrationConfiguration() {
    }

    @Bean
    RelyingPartyRegistrationRepository relyingPartyRegistrationRepository(Saml2RelyingPartyProperties properties) {
        List<RelyingPartyRegistration> registrations = (List) properties.getRegistration().entrySet().stream().map(this::asRegistration).collect(Collectors.toList());
        return new InMemoryRelyingPartyRegistrationRepository(registrations);
    }

    private RelyingPartyRegistration asRegistration(Map.Entry<String, Saml2RelyingPartyProperties.Registration> entry) {
        return asRegistration(entry.getKey(), entry.getValue());
    }

    private RelyingPartyRegistration asRegistration(String id, Saml2RelyingPartyProperties.Registration properties) {
        RelyingPartyRegistration.Builder builderWithRegistrationId;
        AssertingPartyProperties assertingParty = new AssertingPartyProperties(properties, id);
        boolean usingMetadata = StringUtils.hasText(assertingParty.getMetadataUri());
        if (usingMetadata) {
            builderWithRegistrationId = RelyingPartyRegistrations.fromMetadataLocation(assertingParty.getMetadataUri()).registrationId(id);
        } else {
            builderWithRegistrationId = RelyingPartyRegistration.withRegistrationId(id);
        }
        RelyingPartyRegistration.Builder builder = builderWithRegistrationId;
        builder.assertionConsumerServiceLocation(properties.getAcs().getLocation());
        builder.assertionConsumerServiceBinding(properties.getAcs().getBinding());
        builder.assertingPartyDetails(mapAssertingParty(properties, id, usingMetadata));
        builder.signingX509Credentials(credentials -> {
            Stream<R> map = properties.getSigning().getCredentials().stream().map(this::asSigningCredential);
            credentials.getClass();
            map.forEach((v1) -> {
                r1.add(v1);
            });
        });
        builder.decryptionX509Credentials(credentials2 -> {
            Stream<R> map = properties.getDecryption().getCredentials().stream().map(this::asDecryptionCredential);
            credentials2.getClass();
            map.forEach((v1) -> {
                r1.add(v1);
            });
        });
        builder.assertingPartyDetails(details -> {
            details.verificationX509Credentials(credentials3 -> {
                Stream<R> map = assertingParty.getVerificationCredentials().stream().map(this::asVerificationCredential);
                credentials3.getClass();
                map.forEach((v1) -> {
                    r1.add(v1);
                });
            });
        });
        builder.singleLogoutServiceLocation(properties.getSinglelogout().getUrl());
        builder.singleLogoutServiceResponseLocation(properties.getSinglelogout().getResponseUrl());
        builder.singleLogoutServiceBinding(properties.getSinglelogout().getBinding());
        builder.entityId(properties.getEntityId());
        RelyingPartyRegistration registration = builder.build();
        boolean signRequest = registration.getAssertingPartyDetails().getWantAuthnRequestsSigned();
        validateSigningCredentials(properties, signRequest);
        return registration;
    }

    private Consumer<RelyingPartyRegistration.AssertingPartyDetails.Builder> mapAssertingParty(Saml2RelyingPartyProperties.Registration registration, String id, boolean usingMetadata) {
        return details -> {
            AssertingPartyProperties assertingParty = new AssertingPartyProperties(registration, id);
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            assertingParty.getClass();
            PropertyMapper.Source sourceFrom = map.from(assertingParty::getEntityId);
            details.getClass();
            sourceFrom.to(details::entityId);
            assertingParty.getClass();
            PropertyMapper.Source sourceFrom2 = map.from(assertingParty::getSingleSignonBinding);
            details.getClass();
            sourceFrom2.to(details::singleSignOnServiceBinding);
            assertingParty.getClass();
            PropertyMapper.Source sourceFrom3 = map.from(assertingParty::getSingleSignonUrl);
            details.getClass();
            sourceFrom3.to(details::singleSignOnServiceLocation);
            assertingParty.getClass();
            PropertyMapper.Source sourceWhen = map.from(assertingParty::getSingleSignonSignRequest).when(ignored -> {
                return !usingMetadata;
            });
            details.getClass();
            sourceWhen.to((v1) -> {
                r1.wantAuthnRequestsSigned(v1);
            });
            PropertyMapper.Source sourceFrom4 = map.from((PropertyMapper) assertingParty.getSinglelogoutUrl());
            details.getClass();
            sourceFrom4.to(details::singleLogoutServiceLocation);
            PropertyMapper.Source sourceFrom5 = map.from((PropertyMapper) assertingParty.getSinglelogoutResponseUrl());
            details.getClass();
            sourceFrom5.to(details::singleLogoutServiceResponseLocation);
            PropertyMapper.Source sourceFrom6 = map.from((PropertyMapper) assertingParty.getSinglelogoutBinding());
            details.getClass();
            sourceFrom6.to(details::singleLogoutServiceBinding);
        };
    }

    private void validateSigningCredentials(Saml2RelyingPartyProperties.Registration properties, boolean signRequest) {
        if (signRequest) {
            Assert.state(!properties.getSigning().getCredentials().isEmpty(), "Signing credentials must not be empty when authentication requests require signing.");
        }
    }

    private Saml2X509Credential asSigningCredential(Saml2RelyingPartyProperties.Registration.Signing.Credential properties) throws IOException {
        RSAPrivateKey privateKey = readPrivateKey(properties.getPrivateKeyLocation());
        X509Certificate certificate = readCertificate(properties.getCertificateLocation());
        return new Saml2X509Credential(privateKey, certificate, new Saml2X509Credential.Saml2X509CredentialType[]{Saml2X509Credential.Saml2X509CredentialType.SIGNING});
    }

    private Saml2X509Credential asDecryptionCredential(Saml2RelyingPartyProperties.Decryption.Credential properties) throws IOException {
        RSAPrivateKey privateKey = readPrivateKey(properties.getPrivateKeyLocation());
        X509Certificate certificate = readCertificate(properties.getCertificateLocation());
        return new Saml2X509Credential(privateKey, certificate, new Saml2X509Credential.Saml2X509CredentialType[]{Saml2X509Credential.Saml2X509CredentialType.DECRYPTION});
    }

    private Saml2X509Credential asVerificationCredential(Saml2RelyingPartyProperties.AssertingParty.Verification.Credential properties) throws IOException {
        X509Certificate certificate = readCertificate(properties.getCertificateLocation());
        return new Saml2X509Credential(certificate, new Saml2X509Credential.Saml2X509CredentialType[]{Saml2X509Credential.Saml2X509CredentialType.ENCRYPTION, Saml2X509Credential.Saml2X509CredentialType.VERIFICATION});
    }

    private RSAPrivateKey readPrivateKey(Resource location) throws IOException {
        Assert.state(location != null, "No private key location specified");
        Assert.state(location.exists(), (Supplier<String>) () -> {
            return "Private key location '" + location + "' does not exist";
        });
        try {
            InputStream inputStream = location.getInputStream();
            Throwable th = null;
            try {
                try {
                    RSAPrivateKey rSAPrivateKey = (RSAPrivateKey) RsaKeyConverters.pkcs8().convert(inputStream);
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    return rSAPrivateKey;
                } finally {
                }
            } finally {
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private X509Certificate readCertificate(Resource location) throws IOException {
        Assert.state(location != null, "No certificate location specified");
        Assert.state(location.exists(), (Supplier<String>) () -> {
            return "Certificate  location '" + location + "' does not exist";
        });
        try {
            InputStream inputStream = location.getInputStream();
            Throwable th = null;
            try {
                try {
                    X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(inputStream);
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    return x509Certificate;
                } finally {
                }
            } finally {
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyRegistrationConfiguration$AssertingPartyProperties.class */
    private static class AssertingPartyProperties {
        private final Saml2RelyingPartyProperties.Registration registration;
        private final String id;

        AssertingPartyProperties(Saml2RelyingPartyProperties.Registration registration, String id) {
            this.registration = registration;
            this.id = id;
        }

        String getMetadataUri() {
            return (String) get("metadata-uri", (v0) -> {
                return v0.getMetadataUri();
            });
        }

        List<Saml2RelyingPartyProperties.AssertingParty.Verification.Credential> getVerificationCredentials() {
            return (List) get("verification.credentials", property -> {
                return property.getVerification().getCredentials();
            });
        }

        String getEntityId() {
            return (String) get("entity-id", (v0) -> {
                return v0.getEntityId();
            });
        }

        Saml2MessageBinding getSingleSignonBinding() {
            return (Saml2MessageBinding) get("singlesignon.binding", property -> {
                return property.getSinglesignon().getBinding();
            });
        }

        String getSingleSignonUrl() {
            return (String) get("singlesignon.url", property -> {
                return property.getSinglesignon().getUrl();
            });
        }

        Boolean getSingleSignonSignRequest() {
            return (Boolean) get("singlesignon.sign-request", property -> {
                return property.getSinglesignon().getSignRequest();
            });
        }

        String getSinglelogoutUrl() {
            return this.registration.getAssertingparty().getSinglelogout().getUrl();
        }

        String getSinglelogoutResponseUrl() {
            return this.registration.getAssertingparty().getSinglelogout().getResponseUrl();
        }

        Saml2MessageBinding getSinglelogoutBinding() {
            return this.registration.getAssertingparty().getSinglelogout().getBinding();
        }

        private <T> T get(String name, Function<Saml2RelyingPartyProperties.AssertingParty, T> getter) {
            T newValue = getter.apply(this.registration.getAssertingparty());
            if (!ObjectUtils.isEmpty(newValue)) {
                return newValue;
            }
            T deprecatedValue = getter.apply(this.registration.getIdentityprovider());
            if (deprecatedValue != null) {
                Saml2RelyingPartyRegistrationConfiguration.logger.warn(LogMessage.format("Property 'spring.security.saml2.relyingparty.registration.identityprovider.%1$s.%2$s' is deprecated, please use 'spring.security.saml2.relyingparty.registration.assertingparty.%1$s.%2$s' instead", this.id, name));
                return deprecatedValue;
            }
            return newValue;
        }
    }
}
