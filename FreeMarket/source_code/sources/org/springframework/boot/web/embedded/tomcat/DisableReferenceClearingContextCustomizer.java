package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/tomcat/DisableReferenceClearingContextCustomizer.class */
class DisableReferenceClearingContextCustomizer implements TomcatContextCustomizer {
    DisableReferenceClearingContextCustomizer() {
    }

    @Override // org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
    public void customize(Context context) {
        if (!(context instanceof StandardContext)) {
            return;
        }
        StandardContext standardContext = (StandardContext) context;
        try {
            standardContext.setClearReferencesObjectStreamClassCaches(false);
            standardContext.setClearReferencesRmiTargets(false);
            standardContext.setClearReferencesThreadLocals(false);
        } catch (NoSuchMethodError e) {
        }
    }
}
