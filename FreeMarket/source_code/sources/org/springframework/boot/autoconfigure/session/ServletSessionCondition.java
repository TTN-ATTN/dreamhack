package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.WebApplicationType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/ServletSessionCondition.class */
class ServletSessionCondition extends AbstractSessionCondition {
    ServletSessionCondition() {
        super(WebApplicationType.SERVLET);
    }
}
