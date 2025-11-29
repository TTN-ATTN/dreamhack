package org.springframework.boot.autoconfigure.security.oauth2.client;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionException;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientPropertiesRegistrationAdapter.class */
public final class OAuth2ClientPropertiesRegistrationAdapter {
    private OAuth2ClientPropertiesRegistrationAdapter() {
    }

    public static Map<String, ClientRegistration> getClientRegistrations(OAuth2ClientProperties properties) {
        Map<String, ClientRegistration> clientRegistrations = new HashMap<>();
        properties.getRegistration().forEach((key, value) -> {
        });
        return clientRegistrations;
    }

    private static ClientRegistration getClientRegistration(String registrationId, OAuth2ClientProperties.Registration properties, Map<String, OAuth2ClientProperties.Provider> providers) {
        ClientRegistration.Builder builder = getBuilderFromIssuerIfPossible(registrationId, properties.getProvider(), providers);
        if (builder == null) {
            builder = getBuilder(registrationId, properties.getProvider(), providers);
        }
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        properties.getClass();
        PropertyMapper.Source sourceFrom = map.from(properties::getClientId);
        ClientRegistration.Builder builder2 = builder;
        builder2.getClass();
        sourceFrom.to(builder2::clientId);
        properties.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(properties::getClientSecret);
        ClientRegistration.Builder builder3 = builder;
        builder3.getClass();
        sourceFrom2.to(builder3::clientSecret);
        properties.getClass();
        PropertyMapper.Source sourceAs = map.from(properties::getClientAuthenticationMethod).as(ClientAuthenticationMethod::new);
        ClientRegistration.Builder builder4 = builder;
        builder4.getClass();
        sourceAs.to(builder4::clientAuthenticationMethod);
        properties.getClass();
        PropertyMapper.Source sourceAs2 = map.from(properties::getAuthorizationGrantType).as(AuthorizationGrantType::new);
        ClientRegistration.Builder builder5 = builder;
        builder5.getClass();
        sourceAs2.to(builder5::authorizationGrantType);
        properties.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(properties::getRedirectUri);
        ClientRegistration.Builder builder6 = builder;
        builder6.getClass();
        sourceFrom3.to(builder6::redirectUri);
        properties.getClass();
        PropertyMapper.Source sourceAs3 = map.from(properties::getScope).as((v0) -> {
            return StringUtils.toStringArray(v0);
        });
        ClientRegistration.Builder builder7 = builder;
        builder7.getClass();
        sourceAs3.to(builder7::scope);
        properties.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(properties::getClientName);
        ClientRegistration.Builder builder8 = builder;
        builder8.getClass();
        sourceFrom4.to(builder8::clientName);
        return builder.build();
    }

    private static ClientRegistration.Builder getBuilderFromIssuerIfPossible(String registrationId, String configuredProviderId, Map<String, OAuth2ClientProperties.Provider> providers) {
        OAuth2ClientProperties.Provider provider;
        String issuer;
        String providerId = configuredProviderId != null ? configuredProviderId : registrationId;
        if (providers.containsKey(providerId) && (issuer = (provider = providers.get(providerId)).getIssuerUri()) != null) {
            ClientRegistration.Builder builder = ClientRegistrations.fromIssuerLocation(issuer).registrationId(registrationId);
            return getBuilder(builder, provider);
        }
        return null;
    }

    private static ClientRegistration.Builder getBuilder(String registrationId, String configuredProviderId, Map<String, OAuth2ClientProperties.Provider> providers) {
        String providerId = configuredProviderId != null ? configuredProviderId : registrationId;
        CommonOAuth2Provider provider = getCommonProvider(providerId);
        if (provider == null && !providers.containsKey(providerId)) {
            throw new IllegalStateException(getErrorMessage(configuredProviderId, registrationId));
        }
        ClientRegistration.Builder builder = provider != null ? provider.getBuilder(registrationId) : ClientRegistration.withRegistrationId(registrationId);
        if (providers.containsKey(providerId)) {
            return getBuilder(builder, providers.get(providerId));
        }
        return builder;
    }

    private static String getErrorMessage(String configuredProviderId, String registrationId) {
        return configuredProviderId != null ? "Unknown provider ID '" + configuredProviderId + "'" : "Provider ID must be specified for client registration '" + registrationId + "'";
    }

    private static ClientRegistration.Builder getBuilder(ClientRegistration.Builder builder, OAuth2ClientProperties.Provider provider) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        provider.getClass();
        PropertyMapper.Source sourceFrom = map.from(provider::getAuthorizationUri);
        builder.getClass();
        sourceFrom.to(builder::authorizationUri);
        provider.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(provider::getTokenUri);
        builder.getClass();
        sourceFrom2.to(builder::tokenUri);
        provider.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(provider::getUserInfoUri);
        builder.getClass();
        sourceFrom3.to(builder::userInfoUri);
        provider.getClass();
        PropertyMapper.Source sourceAs = map.from(provider::getUserInfoAuthenticationMethod).as(AuthenticationMethod::new);
        builder.getClass();
        sourceAs.to(builder::userInfoAuthenticationMethod);
        provider.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(provider::getJwkSetUri);
        builder.getClass();
        sourceFrom4.to(builder::jwkSetUri);
        provider.getClass();
        PropertyMapper.Source sourceFrom5 = map.from(provider::getUserNameAttribute);
        builder.getClass();
        sourceFrom5.to(builder::userNameAttributeName);
        return builder;
    }

    private static CommonOAuth2Provider getCommonProvider(String providerId) {
        try {
            return (CommonOAuth2Provider) ApplicationConversionService.getSharedInstance().convert(providerId, CommonOAuth2Provider.class);
        } catch (ConversionException e) {
            return null;
        }
    }
}
