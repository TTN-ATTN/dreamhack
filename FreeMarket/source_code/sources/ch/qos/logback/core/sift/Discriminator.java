package ch.qos.logback.core.sift;

import ch.qos.logback.core.spi.LifeCycle;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/sift/Discriminator.class */
public interface Discriminator<E> extends LifeCycle {
    String getDiscriminatingValue(E e);

    String getKey();
}
