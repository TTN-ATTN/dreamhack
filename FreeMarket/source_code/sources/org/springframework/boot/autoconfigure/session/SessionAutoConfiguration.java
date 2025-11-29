package org.springframework.boot.autoconfigure.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebSessionIdResolverAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

@EnableConfigurationProperties({ServerProperties.class, SessionProperties.class, WebFluxProperties.class})
@AutoConfiguration(after = {DataSourceAutoConfiguration.class, HazelcastAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class, RedisAutoConfiguration.class, RedisReactiveAutoConfiguration.class, WebSessionIdResolverAutoConfiguration.class}, before = {HttpHandlerAutoConfiguration.class, WebFluxAutoConfiguration.class})
@ConditionalOnClass({Session.class})
@ConditionalOnWebApplication
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration.class */
public class SessionAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Import({ServletSessionRepositoryValidator.class, SessionRepositoryFilterConfiguration.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration.class */
    static class ServletSessionConfiguration {
        ServletSessionConfiguration() {
        }

        @Conditional({DefaultCookieSerializerCondition.class})
        @Bean
        DefaultCookieSerializer cookieSerializer(ServerProperties serverProperties, ObjectProvider<DefaultCookieSerializerCustomizer> cookieSerializerCustomizers) {
            Session.Cookie cookie = serverProperties.getServlet().getSession().getCookie();
            DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            cookie.getClass();
            PropertyMapper.Source sourceFrom = map.from(cookie::getName);
            cookieSerializer.getClass();
            sourceFrom.to(cookieSerializer::setCookieName);
            cookie.getClass();
            PropertyMapper.Source sourceFrom2 = map.from(cookie::getDomain);
            cookieSerializer.getClass();
            sourceFrom2.to(cookieSerializer::setDomainName);
            cookie.getClass();
            PropertyMapper.Source sourceFrom3 = map.from(cookie::getPath);
            cookieSerializer.getClass();
            sourceFrom3.to(cookieSerializer::setCookiePath);
            cookie.getClass();
            PropertyMapper.Source sourceFrom4 = map.from(cookie::getHttpOnly);
            cookieSerializer.getClass();
            sourceFrom4.to((v1) -> {
                r1.setUseHttpOnlyCookie(v1);
            });
            cookie.getClass();
            PropertyMapper.Source sourceFrom5 = map.from(cookie::getSecure);
            cookieSerializer.getClass();
            sourceFrom5.to((v1) -> {
                r1.setUseSecureCookie(v1);
            });
            cookie.getClass();
            PropertyMapper.Source<Integer> sourceAsInt = map.from(cookie::getMaxAge).asInt((v0) -> {
                return v0.getSeconds();
            });
            cookieSerializer.getClass();
            sourceAsInt.to((v1) -> {
                r1.setCookieMaxAge(v1);
            });
            cookie.getClass();
            PropertyMapper.Source sourceAs = map.from(cookie::getSameSite).as((v0) -> {
                return v0.attributeValue();
            });
            cookieSerializer.getClass();
            sourceAs.to(cookieSerializer::setSameSite);
            cookieSerializerCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(cookieSerializer);
            });
            return cookieSerializer;
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({RememberMeServices.class})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration$RememberMeServicesConfiguration.class */
        static class RememberMeServicesConfiguration {
            RememberMeServicesConfiguration() {
            }

            @Bean
            DefaultCookieSerializerCustomizer rememberMeServicesCookieSerializerCustomizer() {
                return cookieSerializer -> {
                    cookieSerializer.setRememberMeRequestAttribute(SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR);
                };
            }
        }

        @ConditionalOnMissingBean({SessionRepository.class})
        @Configuration(proxyBeanMethods = false)
        @Import({ServletSessionRepositoryImplementationValidator.class, ServletSessionConfigurationImportSelector.class})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration$ServletSessionRepositoryConfiguration.class */
        static class ServletSessionRepositoryConfiguration {
            ServletSessionRepositoryConfiguration() {
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Import({ReactiveSessionRepositoryValidator.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionConfiguration.class */
    static class ReactiveSessionConfiguration {
        ReactiveSessionConfiguration() {
        }

        @ConditionalOnMissingBean({ReactiveSessionRepository.class})
        @Configuration(proxyBeanMethods = false)
        @Import({ReactiveSessionRepositoryImplementationValidator.class, ReactiveSessionConfigurationImportSelector.class})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionConfiguration$ReactiveSessionRepositoryConfiguration.class */
        static class ReactiveSessionRepositoryConfiguration {
            ReactiveSessionRepositoryConfiguration() {
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$DefaultCookieSerializerCondition.class */
    static class DefaultCookieSerializerCondition extends AnyNestedCondition {
        DefaultCookieSerializerCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnMissingBean({HttpSessionIdResolver.class, CookieSerializer.class})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$DefaultCookieSerializerCondition$NoComponentsAvailable.class */
        static class NoComponentsAvailable {
            NoComponentsAvailable() {
            }
        }

        @ConditionalOnMissingBean({CookieSerializer.class})
        @ConditionalOnBean({CookieHttpSessionIdResolver.class})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$DefaultCookieSerializerCondition$CookieHttpSessionIdResolverAvailable.class */
        static class CookieHttpSessionIdResolverAvailable {
            CookieHttpSessionIdResolverAvailable() {
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$SessionConfigurationImportSelector.class */
    static abstract class SessionConfigurationImportSelector implements ImportSelector {
        SessionConfigurationImportSelector() {
        }

        protected final String[] selectImports(WebApplicationType webApplicationType) {
            return (String[]) Arrays.stream(StoreType.values()).map(type -> {
                return SessionStoreMappings.getConfigurationClass(webApplicationType, type);
            }).toArray(x$0 -> {
                return new String[x$0];
            });
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionConfigurationImportSelector.class */
    static class ReactiveSessionConfigurationImportSelector extends SessionConfigurationImportSelector {
        ReactiveSessionConfigurationImportSelector() {
        }

        @Override // org.springframework.context.annotation.ImportSelector
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return super.selectImports(WebApplicationType.REACTIVE);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfigurationImportSelector.class */
    static class ServletSessionConfigurationImportSelector extends SessionConfigurationImportSelector {
        ServletSessionConfigurationImportSelector() {
        }

        @Override // org.springframework.context.annotation.ImportSelector
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return super.selectImports(WebApplicationType.SERVLET);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$AbstractSessionRepositoryImplementationValidator.class */
    static abstract class AbstractSessionRepositoryImplementationValidator {
        private final List<String> candidates;
        private final ClassLoader classLoader;
        private final SessionProperties sessionProperties;

        AbstractSessionRepositoryImplementationValidator(ApplicationContext applicationContext, SessionProperties sessionProperties, List<String> candidates) {
            this.classLoader = applicationContext.getClassLoader();
            this.sessionProperties = sessionProperties;
            this.candidates = candidates;
            checkAvailableImplementations();
        }

        private void checkAvailableImplementations() {
            List<Class<?>> availableCandidates = new ArrayList<>();
            for (String candidate : this.candidates) {
                addCandidateIfAvailable(availableCandidates, candidate);
            }
            StoreType storeType = this.sessionProperties.getStoreType();
            if (availableCandidates.size() > 1 && storeType == null) {
                throw new NonUniqueSessionRepositoryException(availableCandidates);
            }
        }

        private void addCandidateIfAvailable(List<Class<?>> candidates, String type) {
            try {
                candidates.add(Class.forName(type, false, this.classLoader));
            } catch (Throwable th) {
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionRepositoryImplementationValidator.class */
    static class ServletSessionRepositoryImplementationValidator extends AbstractSessionRepositoryImplementationValidator {
        ServletSessionRepositoryImplementationValidator(ApplicationContext applicationContext, SessionProperties sessionProperties) {
            super(applicationContext, sessionProperties, Arrays.asList("org.springframework.session.hazelcast.HazelcastIndexedSessionRepository", "org.springframework.session.jdbc.JdbcIndexedSessionRepository", "org.springframework.session.data.mongo.MongoIndexedSessionRepository", "org.springframework.session.data.redis.RedisIndexedSessionRepository"));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionRepositoryImplementationValidator.class */
    static class ReactiveSessionRepositoryImplementationValidator extends AbstractSessionRepositoryImplementationValidator {
        ReactiveSessionRepositoryImplementationValidator(ApplicationContext applicationContext, SessionProperties sessionProperties) {
            super(applicationContext, sessionProperties, Arrays.asList("org.springframework.session.data.redis.ReactiveRedisSessionRepository", "org.springframework.session.data.mongo.ReactiveMongoSessionRepository"));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$AbstractSessionRepositoryValidator.class */
    static abstract class AbstractSessionRepositoryValidator implements InitializingBean {
        private final SessionProperties sessionProperties;
        private final ObjectProvider<?> sessionRepositoryProvider;

        protected AbstractSessionRepositoryValidator(SessionProperties sessionProperties, ObjectProvider<?> sessionRepositoryProvider) {
            this.sessionProperties = sessionProperties;
            this.sessionRepositoryProvider = sessionRepositoryProvider;
        }

        @Override // org.springframework.beans.factory.InitializingBean
        public void afterPropertiesSet() {
            StoreType storeType = this.sessionProperties.getStoreType();
            if (storeType != StoreType.NONE && this.sessionRepositoryProvider.getIfAvailable() == null && storeType != null) {
                throw new SessionRepositoryUnavailableException("No session repository could be auto-configured, check your configuration (session store type is '" + storeType.name().toLowerCase(Locale.ENGLISH) + "')", storeType);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionRepositoryValidator.class */
    static class ServletSessionRepositoryValidator extends AbstractSessionRepositoryValidator {
        ServletSessionRepositoryValidator(SessionProperties sessionProperties, ObjectProvider<SessionRepository<?>> sessionRepositoryProvider) {
            super(sessionProperties, sessionRepositoryProvider);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionRepositoryValidator.class */
    static class ReactiveSessionRepositoryValidator extends AbstractSessionRepositoryValidator {
        ReactiveSessionRepositoryValidator(SessionProperties sessionProperties, ObjectProvider<ReactiveSessionRepository<?>> sessionRepositoryProvider) {
            super(sessionProperties, sessionRepositoryProvider);
        }
    }
}
