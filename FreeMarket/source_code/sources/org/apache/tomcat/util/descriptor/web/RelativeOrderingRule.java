package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/RelativeOrderingRule.class */
final class RelativeOrderingRule extends Rule {
    boolean isRelativeOrderingSet = false;
    private final boolean fragment;

    RelativeOrderingRule(boolean fragment) {
        this.fragment = fragment;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (!this.fragment) {
            this.digester.getLogger().warn(WebRuleSet.sm.getString("webRuleSet.relativeOrdering"));
        }
        if (this.isRelativeOrderingSet) {
            throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.relativeOrderingCount"));
        }
        this.isRelativeOrderingSet = true;
    }
}
