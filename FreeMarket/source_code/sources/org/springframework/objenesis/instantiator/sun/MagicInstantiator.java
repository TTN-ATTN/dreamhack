package org.springframework.objenesis.instantiator.sun;

import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
import org.springframework.objenesis.instantiator.basic.DelegatingToExoticInstantiator;

@Instantiator(Typology.STANDARD)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/objenesis/instantiator/sun/MagicInstantiator.class */
public class MagicInstantiator<T> extends DelegatingToExoticInstantiator<T> {
    public MagicInstantiator(Class<T> type) {
        super("org.springframework.objenesis.instantiator.exotic.MagicInstantiator", type);
    }
}
