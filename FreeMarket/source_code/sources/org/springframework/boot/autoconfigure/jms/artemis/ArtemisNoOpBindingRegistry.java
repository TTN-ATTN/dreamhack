package org.springframework.boot.autoconfigure.jms.artemis;

import org.apache.activemq.artemis.spi.core.naming.BindingRegistry;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisNoOpBindingRegistry.class */
public class ArtemisNoOpBindingRegistry implements BindingRegistry {
    public Object lookup(String s) {
        return null;
    }

    public boolean bind(String s, Object o) {
        return false;
    }

    public void unbind(String s) {
    }

    public void close() {
    }
}
