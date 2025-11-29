package org.springframework.beans.factory.annotation;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/annotation/Autowire.class */
public enum Autowire {
    NO(0),
    BY_NAME(1),
    BY_TYPE(2);

    private final int value;

    Autowire(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public boolean isAutowire() {
        return this == BY_NAME || this == BY_TYPE;
    }
}
