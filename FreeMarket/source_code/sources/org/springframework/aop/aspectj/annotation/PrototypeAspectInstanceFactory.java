package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.beans.factory.BeanFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/aspectj/annotation/PrototypeAspectInstanceFactory.class */
public class PrototypeAspectInstanceFactory extends BeanFactoryAspectInstanceFactory implements Serializable {
    public PrototypeAspectInstanceFactory(BeanFactory beanFactory, String name) {
        super(beanFactory, name);
        if (!beanFactory.isPrototype(name)) {
            throw new IllegalArgumentException("Cannot use PrototypeAspectInstanceFactory with bean named '" + name + "': not a prototype");
        }
    }
}
