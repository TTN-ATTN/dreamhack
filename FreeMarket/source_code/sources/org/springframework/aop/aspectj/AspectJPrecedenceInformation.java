package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/aspectj/AspectJPrecedenceInformation.class */
public interface AspectJPrecedenceInformation extends Ordered {
    String getAspectName();

    int getDeclarationOrder();

    boolean isBeforeAdvice();

    boolean isAfterAdvice();
}
