package org.springframework.boot.autoconfigure.ldap;

import java.util.Collections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;

@EnableConfigurationProperties({LdapProperties.class})
@AutoConfiguration
@ConditionalOnClass({ContextSource.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/ldap/LdapAutoConfiguration.class */
public class LdapAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public LdapContextSource ldapContextSource(LdapProperties properties, Environment environment, ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy) throws BeansException {
        LdapContextSource source = new LdapContextSource();
        source.getClass();
        dirContextAuthenticationStrategy.ifUnique(source::setAuthenticationStrategy);
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source sourceFrom = propertyMapper.from((PropertyMapper) properties.getUsername());
        source.getClass();
        sourceFrom.to(source::setUserDn);
        PropertyMapper.Source sourceFrom2 = propertyMapper.from((PropertyMapper) properties.getPassword());
        source.getClass();
        sourceFrom2.to(source::setPassword);
        PropertyMapper.Source sourceFrom3 = propertyMapper.from((PropertyMapper) properties.getAnonymousReadOnly());
        source.getClass();
        sourceFrom3.to((v1) -> {
            r1.setAnonymousReadOnly(v1);
        });
        PropertyMapper.Source sourceFrom4 = propertyMapper.from((PropertyMapper) properties.getBase());
        source.getClass();
        sourceFrom4.to(source::setBase);
        PropertyMapper.Source sourceFrom5 = propertyMapper.from((PropertyMapper) properties.determineUrls(environment));
        source.getClass();
        sourceFrom5.to(source::setUrls);
        propertyMapper.from((PropertyMapper) properties.getBaseEnvironment()).to(baseEnvironment -> {
            source.setBaseEnvironmentProperties(Collections.unmodifiableMap(baseEnvironment));
        });
        return source;
    }

    @ConditionalOnMissingBean({LdapOperations.class})
    @Bean
    public LdapTemplate ldapTemplate(LdapProperties properties, ContextSource contextSource) {
        LdapProperties.Template template = properties.getTemplate();
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        PropertyMapper.Source sourceFrom = propertyMapper.from((PropertyMapper) Boolean.valueOf(template.isIgnorePartialResultException()));
        ldapTemplate.getClass();
        sourceFrom.to((v1) -> {
            r1.setIgnorePartialResultException(v1);
        });
        PropertyMapper.Source sourceFrom2 = propertyMapper.from((PropertyMapper) Boolean.valueOf(template.isIgnoreNameNotFoundException()));
        ldapTemplate.getClass();
        sourceFrom2.to((v1) -> {
            r1.setIgnoreNameNotFoundException(v1);
        });
        PropertyMapper.Source sourceFrom3 = propertyMapper.from((PropertyMapper) Boolean.valueOf(template.isIgnoreSizeLimitExceededException()));
        ldapTemplate.getClass();
        sourceFrom3.to((v1) -> {
            r1.setIgnoreSizeLimitExceededException(v1);
        });
        return ldapTemplate;
    }
}
