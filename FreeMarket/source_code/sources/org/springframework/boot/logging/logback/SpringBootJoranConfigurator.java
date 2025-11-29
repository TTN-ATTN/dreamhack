package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.core.env.Environment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/logback/SpringBootJoranConfigurator.class */
class SpringBootJoranConfigurator extends JoranConfigurator {
    private LoggingInitializationContext initializationContext;

    SpringBootJoranConfigurator(LoggingInitializationContext initializationContext) {
        this.initializationContext = initializationContext;
    }

    @Override // ch.qos.logback.classic.joran.JoranConfigurator, ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericConfigurator
    public void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        Environment environment = this.initializationContext.getEnvironment();
        rs.addRule(new ElementSelector("configuration/springProperty"), new SpringPropertyAction(environment));
        rs.addRule(new ElementSelector("*/springProfile"), new SpringProfileAction(environment));
        rs.addRule(new ElementSelector("*/springProfile/*"), new NOPAction());
    }
}
