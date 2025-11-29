package org.springframework.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jndi/JndiCallback.class */
public interface JndiCallback<T> {
    @Nullable
    T doInContext(Context ctx) throws NamingException;
}
