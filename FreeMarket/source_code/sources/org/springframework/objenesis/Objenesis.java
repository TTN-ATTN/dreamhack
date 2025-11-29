package org.springframework.objenesis;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/objenesis/Objenesis.class */
public interface Objenesis {
    <T> T newInstance(Class<T> cls);

    <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> cls);
}
