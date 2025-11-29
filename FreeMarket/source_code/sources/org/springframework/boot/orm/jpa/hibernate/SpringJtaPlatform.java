package org.springframework.boot.orm.jpa.hibernate;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/orm/jpa/hibernate/SpringJtaPlatform.class */
public class SpringJtaPlatform extends AbstractJtaPlatform {
    private static final long serialVersionUID = 1;
    private final JtaTransactionManager transactionManager;

    public SpringJtaPlatform(JtaTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "TransactionManager must not be null");
        this.transactionManager = transactionManager;
    }

    protected TransactionManager locateTransactionManager() {
        return this.transactionManager.getTransactionManager();
    }

    protected UserTransaction locateUserTransaction() {
        return this.transactionManager.getUserTransaction();
    }
}
