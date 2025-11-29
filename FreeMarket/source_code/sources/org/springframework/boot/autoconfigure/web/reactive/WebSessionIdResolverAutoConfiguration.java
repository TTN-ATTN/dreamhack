package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties({WebFluxProperties.class, ServerProperties.class})
@AutoConfiguration
@ConditionalOnClass({WebSessionManager.class, Mono.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/WebSessionIdResolverAutoConfiguration.class */
public class WebSessionIdResolverAutoConfiguration {
    private final ServerProperties serverProperties;
    private final WebFluxProperties webFluxProperties;

    public WebSessionIdResolverAutoConfiguration(ServerProperties serverProperties, WebFluxProperties webFluxProperties) {
        this.serverProperties = serverProperties;
        this.webFluxProperties = webFluxProperties;
        assertNoMutuallyExclusiveProperties(serverProperties, webFluxProperties);
    }

    private void assertNoMutuallyExclusiveProperties(ServerProperties serverProperties, WebFluxProperties webFluxProperties) {
        MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries -> {
            entries.put("spring.webflux.session.cookie.same-site", webFluxProperties.getSession().getCookie().getSameSite());
            entries.put("server.reactive.session.cookie.same-site", serverProperties.getReactive().getSession().getCookie().getSameSite());
        });
    }

    @ConditionalOnMissingBean
    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        String cookieName = this.serverProperties.getReactive().getSession().getCookie().getName();
        if (StringUtils.hasText(cookieName)) {
            resolver.setCookieName(cookieName);
        }
        resolver.addCookieInitializer(this::initializeCookie);
        return resolver;
    }

    private void initializeCookie(ResponseCookie.ResponseCookieBuilder builder) {
        Cookie cookie = this.serverProperties.getReactive().getSession().getCookie();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        cookie.getClass();
        PropertyMapper.Source sourceFrom = map.from(cookie::getDomain);
        builder.getClass();
        sourceFrom.to(builder::domain);
        cookie.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(cookie::getPath);
        builder.getClass();
        sourceFrom2.to(builder::path);
        cookie.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(cookie::getHttpOnly);
        builder.getClass();
        sourceFrom3.to((v1) -> {
            r1.httpOnly(v1);
        });
        cookie.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(cookie::getSecure);
        builder.getClass();
        sourceFrom4.to((v1) -> {
            r1.secure(v1);
        });
        cookie.getClass();
        PropertyMapper.Source sourceFrom5 = map.from(cookie::getMaxAge);
        builder.getClass();
        sourceFrom5.to(builder::maxAge);
        PropertyMapper.Source sourceFrom6 = map.from((PropertyMapper) getSameSite(cookie));
        builder.getClass();
        sourceFrom6.to(builder::sameSite);
    }

    private String getSameSite(Cookie properties) {
        if (properties.getSameSite() != null) {
            return properties.getSameSite().attributeValue();
        }
        WebFluxProperties.Cookie deprecatedProperties = this.webFluxProperties.getSession().getCookie();
        if (deprecatedProperties.getSameSite() != null) {
            return deprecatedProperties.getSameSite().attribute();
        }
        return null;
    }
}
