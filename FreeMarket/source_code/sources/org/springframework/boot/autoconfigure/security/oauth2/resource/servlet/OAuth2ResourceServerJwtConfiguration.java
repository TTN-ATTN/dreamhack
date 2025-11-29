package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.oauth2.resource.IssuerUriCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.KeyValueCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.SupplierJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.CollectionUtils;

@Configuration(proxyBeanMethods = false)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerJwtConfiguration.class */
class OAuth2ResourceServerJwtConfiguration {
    OAuth2ResourceServerJwtConfiguration() {
    }

    @ConditionalOnMissingBean({JwtDecoder.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerJwtConfiguration$JwtDecoderConfiguration.class */
    static class JwtDecoderConfiguration {
        private final OAuth2ResourceServerProperties.Jwt properties;

        JwtDecoderConfiguration(OAuth2ResourceServerProperties properties) {
            this.properties = properties.getJwt();
        }

        @ConditionalOnProperty(name = {"spring.security.oauth2.resourceserver.jwt.jwk-set-uri"})
        @Bean
        JwtDecoder jwtDecoderByJwkKeySetUri() {
            NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.properties.getJwkSetUri()).jwsAlgorithms(this::jwsAlgorithms).build();
            String issuerUri = this.properties.getIssuerUri();
            Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = issuerUri != null ? () -> {
                return JwtValidators.createDefaultWithIssuer(issuerUri);
            } : JwtValidators::createDefault;
            nimbusJwtDecoder.setJwtValidator(getValidators(defaultValidator));
            return nimbusJwtDecoder;
        }

        private void jwsAlgorithms(Set<SignatureAlgorithm> signatureAlgorithms) {
            for (String algorithm : this.properties.getJwsAlgorithms()) {
                signatureAlgorithms.add(SignatureAlgorithm.from(algorithm));
            }
        }

        private OAuth2TokenValidator<Jwt> getValidators(Supplier<OAuth2TokenValidator<Jwt>> defaultValidator) {
            OAuth2TokenValidator<Jwt> defaultValidators = defaultValidator.get();
            List<String> audiences = this.properties.getAudiences();
            if (CollectionUtils.isEmpty(audiences)) {
                return defaultValidators;
            }
            List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
            validators.add(defaultValidators);
            validators.add(new JwtClaimValidator("aud", aud -> {
                return (aud == null || Collections.disjoint(aud, audiences)) ? false : true;
            }));
            return new DelegatingOAuth2TokenValidator(validators);
        }

        @Conditional({KeyValueCondition.class})
        @Bean
        JwtDecoder jwtDecoderByPublicKeyValue() throws Exception {
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(getKeySpec(this.properties.readPublicKey())));
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).signatureAlgorithm(SignatureAlgorithm.from(exactlyOneAlgorithm())).build();
            jwtDecoder.setJwtValidator(getValidators(JwtValidators::createDefault));
            return jwtDecoder;
        }

        private byte[] getKeySpec(String keyValue) {
            return Base64.getMimeDecoder().decode(keyValue.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", ""));
        }

        private String exactlyOneAlgorithm() {
            List<String> algorithms = this.properties.getJwsAlgorithms();
            int count = algorithms != null ? algorithms.size() : 0;
            if (count != 1) {
                throw new IllegalStateException("Creating a JWT decoder using a public key requires exactly one JWS algorithm but " + count + " were configured");
            }
            return algorithms.get(0);
        }

        @Conditional({IssuerUriCondition.class})
        @Bean
        SupplierJwtDecoder jwtDecoderByIssuerUri() {
            return new SupplierJwtDecoder(() -> {
                String issuerUri = this.properties.getIssuerUri();
                NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
                jwtDecoder.setJwtValidator(getValidators(() -> {
                    return JwtValidators.createDefaultWithIssuer(issuerUri);
                }));
                return jwtDecoder;
            });
        }
    }

    @ConditionalOnDefaultWebSecurity
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerJwtConfiguration$OAuth2SecurityFilterChainConfiguration.class */
    static class OAuth2SecurityFilterChainConfiguration {
        OAuth2SecurityFilterChainConfiguration() {
        }

        @ConditionalOnBean({JwtDecoder.class})
        @Bean
        SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeRequests(requests -> {
                ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) requests.anyRequest()).authenticated();
            });
            http.oauth2ResourceServer((v0) -> {
                v0.jwt();
            });
            return (SecurityFilterChain) http.build();
        }
    }
}
