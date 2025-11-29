package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/SetSessionConfig.class */
final class SetSessionConfig extends Rule {
    boolean isSessionConfigSet = false;

    SetSessionConfig() {
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.isSessionConfigSet) {
            throw new IllegalArgumentException("<session-config> element is limited to 1 occurrence");
        }
        this.isSessionConfigSet = true;
    }
}
