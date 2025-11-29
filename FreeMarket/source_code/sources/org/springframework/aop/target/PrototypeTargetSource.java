package org.springframework.aop.target;

import org.springframework.beans.BeansException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/target/PrototypeTargetSource.class */
public class PrototypeTargetSource extends AbstractPrototypeBasedTargetSource {
    @Override // org.springframework.aop.TargetSource
    public Object getTarget() throws BeansException {
        return newPrototypeInstance();
    }

    @Override // org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource, org.springframework.aop.TargetSource
    public void releaseTarget(Object target) {
        destroyPrototypeInstance(target);
    }

    @Override // org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource
    public String toString() {
        return "PrototypeTargetSource for target bean with name '" + getTargetBeanName() + "'";
    }
}
