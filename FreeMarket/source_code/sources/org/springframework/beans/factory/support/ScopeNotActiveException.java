package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanCreationException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/ScopeNotActiveException.class */
public class ScopeNotActiveException extends BeanCreationException {
    public ScopeNotActiveException(String beanName, String scopeName, IllegalStateException cause) {
        super(beanName, "Scope '" + scopeName + "' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton", cause);
    }
}
