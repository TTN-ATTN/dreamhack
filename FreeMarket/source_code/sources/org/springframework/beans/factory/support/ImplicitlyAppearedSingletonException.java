package org.springframework.beans.factory.support;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/ImplicitlyAppearedSingletonException.class */
class ImplicitlyAppearedSingletonException extends IllegalStateException {
    public ImplicitlyAppearedSingletonException() {
        super("About-to-be-created singleton instance implicitly appeared through the creation of the factory bean that its bean definition points to");
    }
}
