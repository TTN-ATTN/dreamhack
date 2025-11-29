package org.springframework.boot.autoconfigure.transaction.jta;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.config.JtaTransactionManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

@ConditionalOnJndi({"java:comp/UserTransaction", "java:comp/TransactionManager", "java:appserver/TransactionManager", "java:pm/TransactionManager", "java:/TransactionManager"})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({JtaTransactionManager.class})
@ConditionalOnMissingBean({TransactionManager.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/transaction/jta/JndiJtaConfiguration.class */
class JndiJtaConfiguration {
    JndiJtaConfiguration() {
    }

    @Bean
    JtaTransactionManager transactionManager(ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) throws BeansException {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManagerFactoryBean().getObject();
        transactionManagerCustomizers.ifAvailable(customizers -> {
            customizers.customize(jtaTransactionManager);
        });
        return jtaTransactionManager;
    }
}
